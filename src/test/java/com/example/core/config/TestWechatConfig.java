package com.example.core.config;

import com.example.core.service.WechatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Test configuration for mocking WeChat service
 */
@TestConfiguration
public class TestWechatConfig {

    private static int codeCounter = 0;

    @Bean
    @Primary
    public WechatService mockWechatService(WechatMiniappProperties properties, 
                                          WebClient.Builder webClientBuilder,
                                          ObjectMapper objectMapper) {
        return new WechatService(properties, webClientBuilder, objectMapper) {
            @Override
            public Code2SessionResponse code2session(String code) {
                // Mock response for testing
                Code2SessionResponse response = new Code2SessionResponse();
                response.setOpenid("mock_openid_" + code + "_" + (codeCounter++));
                response.setSession_key("mock_session_key_" + System.nanoTime());
                response.setErrcode(0);
                response.setErrmsg("ok");
                return response;
            }
        };
    }
}
