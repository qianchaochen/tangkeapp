package com.example.core.payment;

import com.example.core.config.PaymentSecurityProperties;
import com.example.core.config.WechatPayProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WechatPayService {

    private static final Logger log = LoggerFactory.getLogger(WechatPayService.class);

    private final WechatPayProperties properties;
    private final PaymentSecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    public WechatPayService(WechatPayProperties properties, PaymentSecurityProperties securityProperties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.securityProperties = securityProperties;
        this.objectMapper = objectMapper;
    }

    public PaymentInitiation createJsapiRechargeOrder(String orderNo, int amountFen, String payerOpenid, String description) {
        if (!properties.isEnabled()) {
            throw new IllegalStateException("WeChat Pay is not enabled");
        }

        try {
            PrivateKey merchantPrivateKey = loadMerchantPrivateKey();

            AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(
                            properties.getMchId(),
                            new PrivateKeySigner(properties.getMchSerialNumber(), merchantPrivateKey)
                    ),
                    properties.getApiV3Key().getBytes(StandardCharsets.UTF_8)
            );

            try (CloseableHttpClient httpClient = WechatPayHttpClientBuilder.create()
                    .withMerchant(properties.getMchId(), properties.getMchSerialNumber(), merchantPrivateKey)
                    .withValidator(new WechatPay2Validator(verifier))
                    .build()) {

                String url = properties.getApiBaseUrl() + "/v3/pay/transactions/jsapi";
                HttpPost httpPost = new HttpPost(url);
                httpPost.addHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());
                httpPost.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());

                Map<String, Object> body = new LinkedHashMap<>();
                body.put("appid", properties.getAppId());
                body.put("mchid", properties.getMchId());
                body.put("description", description);
                body.put("out_trade_no", orderNo);
                body.put("notify_url", properties.getNotifyUrl());

                Map<String, Object> amount = new LinkedHashMap<>();
                amount.put("total", amountFen);
                amount.put("currency", "CNY");
                body.put("amount", amount);

                Map<String, Object> payer = new LinkedHashMap<>();
                payer.put("openid", payerOpenid);
                body.put("payer", payer);

                String bodyJson = objectMapper.writeValueAsString(body);
                httpPost.setEntity(new StringEntity(bodyJson, ContentType.APPLICATION_JSON));

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode < 200 || statusCode >= 300) {
                        throw new RuntimeException("WeChat unified order failed, status=" + statusCode + ", body=" + responseBody);
                    }

                    JsonNode root = objectMapper.readTree(responseBody);
                    String prepayId = root.path("prepay_id").asText(null);
                    if (prepayId == null || prepayId.isEmpty()) {
                        throw new RuntimeException("WeChat unified order missing prepay_id");
                    }

                    Map<String, String> clientParams = buildJsapiClientParams(prepayId, merchantPrivateKey);
                    return new PaymentInitiation(prepayId, null, clientParams);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("WeChat Pay create order failed: " + e.getMessage(), e);
        }
    }

    public PaymentCallback parseAndVerifyNotification(Map<String, String> headers, String body) {
        try {
            if (!securityProperties.isEnabled()) {
                return parseDecryptedTransaction(body);
            }

            PrivateKey merchantPrivateKey = loadMerchantPrivateKey();
            AutoUpdateCertificatesVerifier verifier = new AutoUpdateCertificatesVerifier(
                    new WechatPay2Credentials(
                            properties.getMchId(),
                            new PrivateKeySigner(properties.getMchSerialNumber(), merchantPrivateKey)
                    ),
                    properties.getApiV3Key().getBytes(StandardCharsets.UTF_8)
            );

            NotificationRequest request = new NotificationRequest.Builder()
                    .withSerialNumber(firstHeader(headers, "Wechatpay-Serial"))
                    .withNonce(firstHeader(headers, "Wechatpay-Nonce"))
                    .withTimestamp(firstHeader(headers, "Wechatpay-Timestamp"))
                    .withSignature(firstHeader(headers, "Wechatpay-Signature"))
                    .withBody(body)
                    .build();

            NotificationHandler handler = new NotificationHandler(verifier, properties.getApiV3Key().getBytes(StandardCharsets.UTF_8));
            Notification notification = handler.parse(request);
            String decrypted = notification.getDecryptData();
            return parseDecryptedTransaction(decrypted);
        } catch (Exception e) {
            log.warn("WeChat Pay notification parse failed: {}", e.getMessage());
            throw new RuntimeException("WeChat Pay notification invalid: " + e.getMessage(), e);
        }
    }

    private String firstHeader(Map<String, String> headers, String name) {
        if (headers == null) {
            return null;
        }
        String v = headers.get(name);
        if (v != null) {
            return v;
        }
        return headers.get(name.toLowerCase());
    }

    private PaymentCallback parseDecryptedTransaction(String decryptedBody) throws Exception {
        JsonNode root = objectMapper.readTree(decryptedBody);
        String orderNo = root.path("out_trade_no").asText(null);
        String transactionId = root.path("transaction_id").asText(null);
        String tradeState = root.path("trade_state").asText("");

        boolean paid = "SUCCESS".equalsIgnoreCase(tradeState);

        if (orderNo == null || orderNo.isEmpty()) {
            orderNo = root.path("orderNo").asText(null);
        }
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = root.path("providerTransactionId").asText(null);
        }

        if (root.has("paid")) {
            paid = root.path("paid").asBoolean(false);
        }

        return new PaymentCallback(orderNo, transactionId, paid, decryptedBody);
    }

    private Map<String, String> buildJsapiClientParams(String prepayId, PrivateKey merchantPrivateKey) throws Exception {
        String timeStamp = String.valueOf(Instant.now().getEpochSecond());
        String nonceStr = UUID.randomUUID().toString().replace("-", "");
        String pkg = "prepay_id=" + prepayId;

        String message = properties.getAppId() + "\n" + timeStamp + "\n" + nonceStr + "\n" + pkg + "\n";

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(merchantPrivateKey);
        signature.update(message.getBytes(StandardCharsets.UTF_8));
        String paySign = Base64.getEncoder().encodeToString(signature.sign());

        Map<String, String> params = new LinkedHashMap<>();
        params.put("timeStamp", timeStamp);
        params.put("nonceStr", nonceStr);
        params.put("package", pkg);
        params.put("signType", "RSA");
        params.put("paySign", paySign);
        return params;
    }

    private PrivateKey loadMerchantPrivateKey() throws Exception {
        if (properties.getPrivateKeyPath() == null || properties.getPrivateKeyPath().isEmpty()) {
            throw new IllegalStateException("wechat.pay.private-key-path is required");
        }
        try (FileInputStream in = new FileInputStream(properties.getPrivateKeyPath())) {
            return PemUtil.loadPrivateKey(in);
        }
    }
}
