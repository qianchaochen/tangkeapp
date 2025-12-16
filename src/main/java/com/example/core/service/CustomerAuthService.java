package com.example.core.service;

import com.example.core.entity.Customer;
import com.example.core.repository.CustomerRepository;
import com.example.core.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CustomerAuthService {

    private final CustomerRepository customerRepository;
    private final WechatService wechatService;
    private final JwtTokenProvider jwtTokenProvider;

    public CustomerAuthService(CustomerRepository customerRepository, WechatService wechatService,
                               JwtTokenProvider jwtTokenProvider) {
        this.customerRepository = customerRepository;
        this.wechatService = wechatService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public LoginResponse handleWechatLogin(String code, String source) {
        WechatService.Code2SessionResponse response = wechatService.code2session(code);

        if (!response.isSuccess()) {
            throw new RuntimeException("WeChat login failed: " + response.getErrmsg());
        }

        String openid = response.getOpenid();
        Optional<Customer> existingCustomer = customerRepository.findByWechatOpenid(openid);

        Customer customer;
        if (existingCustomer.isPresent()) {
            customer = existingCustomer.get();
            customer.setUpdatedAt(LocalDateTime.now());
            customerRepository.save(customer);
        } else {
            customer = new Customer();
            customer.setWechatOpenid(openid);
            customer.setName("");
            customer.setSource(source != null ? source : "weixin");
            customer.setFirstVisitAt(LocalDateTime.now());
            customerRepository.save(customer);
        }

        String token = jwtTokenProvider.generateToken(customer.getId());

        return new LoginResponse(
                customer.getId(),
                token,
                openid,
                customer.getName(),
                customer.getPhone(),
                customer.getSource(),
                existingCustomer.isEmpty()
        );
    }

    @Transactional
    public Customer completeProfile(Long customerId, String name, String phone, String source) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        if (name != null && !name.isEmpty()) {
            customer.setName(name);
        }
        if (phone != null && !phone.isEmpty()) {
            customer.setPhone(phone);
        }
        if (source != null && !source.isEmpty()) {
            customer.setSource(source);
        }

        customer.setUpdatedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public static class LoginResponse {
        private Long customerId;
        private String token;
        private String openid;
        private String name;
        private String phone;
        private String source;
        private boolean isNewCustomer;

        public LoginResponse(Long customerId, String token, String openid, String name, String phone,
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
}
