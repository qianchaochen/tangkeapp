package com.example.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappProperties {

    private String appid;
    private String secret;
    private String code2sessionUrl;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getCode2sessionUrl() {
        return code2sessionUrl;
    }

    public void setCode2sessionUrl(String code2sessionUrl) {
        this.code2sessionUrl = code2sessionUrl;
    }
}
