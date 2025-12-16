package com.example.core.dto;

public class LoginResponseDTO {
    private Long customerId;
    private String token;
    private String openid;
    private String name;
    private String phone;
    private String source;
    private boolean isNewCustomer;

    public LoginResponseDTO(Long customerId, String token, String openid, String name, String phone,
                           String source, boolean isNewCustomer) {
        this.customerId = customerId;
        this.token = token;
        this.openid = openid;
        this.name = name;
        this.phone = phone;
        this.source = source;
        this.isNewCustomer = isNewCustomer;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isNewCustomer() {
        return isNewCustomer;
    }

    public void setNewCustomer(boolean newCustomer) {
        isNewCustomer = newCustomer;
    }
}
