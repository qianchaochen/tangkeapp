package com.example.core.config;

import com.example.core.payment.AlipayPaymentService;
import com.example.core.payment.PaymentCallback;
import com.example.core.payment.PaymentInitiation;
import com.example.core.payment.WechatPayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@TestConfiguration
public class TestPaymentConfig {

    @Bean
    @Primary
    public WechatPayService mockWechatPayService(WechatPayProperties wechatPayProperties,
                                                 PaymentSecurityProperties paymentSecurityProperties,
                                                 ObjectMapper objectMapper) {
        return new WechatPayService(wechatPayProperties, paymentSecurityProperties, objectMapper) {
            @Override
            public PaymentInitiation createJsapiRechargeOrder(String orderNo, int amountFen, String payerOpenid, String description) {
                Map<String, String> params = new LinkedHashMap<>();
                params.put("timeStamp", "1700000000");
                params.put("nonceStr", "mock_nonce");
                params.put("package", "prepay_id=mock_prepay_" + orderNo);
                params.put("signType", "RSA");
                params.put("paySign", "mock_sign");
                return new PaymentInitiation("mock_prepay_" + orderNo, null, params);
            }

            @Override
            public PaymentCallback parseAndVerifyNotification(Map<String, String> headers, String body) {
                // In tests, we accept simplified JSON payloads.
                return super.parseAndVerifyNotification(headers, body);
            }
        };
    }

    @Bean
    @Primary
    public AlipayPaymentService mockAlipayPaymentService(AlipayProperties alipayProperties,
                                                         PaymentSecurityProperties paymentSecurityProperties,
                                                         ObjectMapper objectMapper) {
        return new AlipayPaymentService(alipayProperties, paymentSecurityProperties, objectMapper) {
            @Override
            public PaymentInitiation createAppRechargeOrder(String orderNo, BigDecimal amountYuan, String subject) {
                return new PaymentInitiation(null, "mock_alipay_order_string_" + orderNo, null);
            }

            @Override
            public PaymentCallback parseAndVerifyNotification(Map<String, String> params) {
                return super.parseAndVerifyNotification(params);
            }
        };
    }
}
