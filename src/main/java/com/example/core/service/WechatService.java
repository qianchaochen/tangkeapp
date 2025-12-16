package com.example.core.service;

import com.example.core.config.WechatMiniappProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class WechatService {

    private final WechatMiniappProperties wechatProperties;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public WechatService(WechatMiniappProperties wechatProperties, WebClient.Builder webClientBuilder,
                         ObjectMapper objectMapper) {
        this.wechatProperties = wechatProperties;
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public Code2SessionResponse code2session(String code) {
        try {
            String url = wechatProperties.getCode2sessionUrl() + "?appid=" + wechatProperties.getAppid()
                    + "&secret=" + wechatProperties.getSecret()
                    + "&js_code=" + code
                    + "&grant_type=authorization_code";

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return objectMapper.readValue(response, Code2SessionResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get session from WeChat: " + e.getMessage(), e);
        }
    }

    public static class Code2SessionResponse {
        private String openid;
        private String session_key;
        private Integer errcode;
        private String errmsg;

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public String getSession_key() {
            return session_key;
        }

        public void setSession_key(String session_key) {
            this.session_key = session_key;
        }

        public Integer getErrcode() {
            return errcode;
        }

        public void setErrcode(Integer errcode) {
            this.errcode = errcode;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        public boolean isSuccess() {
            return errcode == null || errcode == 0;
        }
    }
}
