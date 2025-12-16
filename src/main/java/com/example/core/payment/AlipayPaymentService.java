package com.example.core.payment;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.example.core.config.AlipayProperties;
import com.example.core.config.PaymentSecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class AlipayPaymentService {

    private static final Logger log = LoggerFactory.getLogger(AlipayPaymentService.class);

    private final AlipayProperties properties;
    private final PaymentSecurityProperties securityProperties;
    private final ObjectMapper objectMapper;

    public AlipayPaymentService(AlipayProperties properties, PaymentSecurityProperties securityProperties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.securityProperties = securityProperties;
        this.objectMapper = objectMapper;
    }

    public PaymentInitiation createAppRechargeOrder(String orderNo, BigDecimal amountYuan, String subject) {
        if (!properties.isEnabled()) {
            throw new IllegalStateException("Alipay is not enabled");
        }

        try {
            DefaultAlipayClient client = new DefaultAlipayClient(
                    properties.getGatewayUrl(),
                    properties.getAppId(),
                    properties.getMerchantPrivateKey(),
                    "json",
                    properties.getCharset(),
                    properties.getAlipayPublicKey(),
                    properties.getSignType()
            );

            AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
            request.setNotifyUrl(properties.getNotifyUrl());

            AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
            model.setOutTradeNo(orderNo);
            model.setTotalAmount(amountYuan.toPlainString());
            model.setSubject(subject);
            model.setProductCode("QUICK_MSECURITY_PAY");
            request.setBizModel(model);

            AlipayTradeAppPayResponse response = client.sdkExecute(request);
            if (!response.isSuccess()) {
                throw new RuntimeException("Alipay create order failed: " + response.getSubMsg());
            }

            return new PaymentInitiation(null, response.getBody(), null);
        } catch (AlipayApiException e) {
            throw new RuntimeException("Alipay create order failed: " + e.getMessage(), e);
        }
    }

    public PaymentCallback parseAndVerifyNotification(Map<String, String> params) {
        try {
            boolean verified = true;
            if (securityProperties.isEnabled()) {
                verified = AlipaySignature.rsaCheckV1(params, properties.getAlipayPublicKey(), properties.getCharset(), properties.getSignType());
            }

            if (!verified) {
                throw new RuntimeException("Alipay signature verification failed");
            }

            String orderNo = params.get("out_trade_no");
            String tradeNo = params.get("trade_no");
            String tradeStatus = params.get("trade_status");

            boolean paid = "TRADE_SUCCESS".equalsIgnoreCase(tradeStatus) || "TRADE_FINISHED".equalsIgnoreCase(tradeStatus);

            String raw = objectMapper.writeValueAsString(new LinkedHashMap<>(params));
            return new PaymentCallback(orderNo, tradeNo, paid, raw);
        } catch (Exception e) {
            log.warn("Alipay notification parse failed: {}", e.getMessage());
            throw new RuntimeException("Alipay notification invalid: " + e.getMessage(), e);
        }
    }
}
