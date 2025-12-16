package com.example.core.controller;

import com.example.core.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentWebhookController {

    private static final Logger log = LoggerFactory.getLogger(PaymentWebhookController.class);

    private final WalletService walletService;

    public PaymentWebhookController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping(value = "/wechat/notify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> wechatNotify(@RequestBody String body, @RequestHeader Map<String, String> headers) {
        try {
            walletService.handleWechatPayNotification(headers, body);
            return ResponseEntity.ok(successWechat());
        } catch (Exception e) {
            log.warn("WeChat notify handling failed: {}", e.getMessage());
            return ResponseEntity.ok(failWechat(e.getMessage()));
        }
    }

    @PostMapping(value = "/alipay/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String alipayNotify(@RequestParam Map<String, String> params) {
        try {
            walletService.handleAlipayNotification(params);
            return "success";
        } catch (Exception e) {
            log.warn("Alipay notify handling failed: {}", e.getMessage());
            return "failure";
        }
    }

    private Map<String, String> successWechat() {
        Map<String, String> resp = new LinkedHashMap<>();
        resp.put("code", "SUCCESS");
        resp.put("message", "OK");
        return resp;
    }

    private Map<String, String> failWechat(String message) {
        Map<String, String> resp = new LinkedHashMap<>();
        resp.put("code", "FAIL");
        resp.put("message", message);
        return resp;
    }
}
