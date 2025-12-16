package com.example.core.controller;

import com.example.core.dto.CustomerDTO;
import com.example.core.entity.Customer;
import com.example.core.mapper.CustomerMapper;
import com.example.core.repository.CustomerRepository;
import com.example.core.security.AuthenticationContext;
import com.example.core.service.CustomerAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CustomerAuthService customerAuthService;
    private final CustomerRepository customerRepository;
    private final AuthenticationContext authenticationContext;

    public AuthController(CustomerAuthService customerAuthService, CustomerRepository customerRepository,
                         AuthenticationContext authenticationContext) {
        this.customerAuthService = customerAuthService;
        this.customerRepository = customerRepository;
        this.authenticationContext = authenticationContext;
    }

    @PostMapping("/wechat/login")
    public ResponseEntity<?> wechatLogin(@RequestBody LoginRequest request) {
        try {
            CustomerAuthService.LoginResponse response = customerAuthService.handleWechatLogin(
                    request.getCode(),
                    request.getSource()
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/profile/complete")
    public ResponseEntity<?> completeProfile(@RequestBody ProfileCompleteRequest request) {
        try {
            Long customerId = authenticationContext.getCurrentCustomerId();
            if (customerId == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Not authenticated"));
            }

            Customer customer = customerAuthService.completeProfile(
                    customerId,
                    request.getName(),
                    request.getPhone(),
                    request.getSource()
            );

            return ResponseEntity.ok(CustomerMapper.toDTO(customer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Long customerId = authenticationContext.getCurrentCustomerId();
            if (customerId == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Not authenticated"));
            }

            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            return ResponseEntity.ok(CustomerMapper.toDTO(customer));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    public static class LoginRequest {
        private String code;
        private String source;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }

    public static class ProfileCompleteRequest {
        private String name;
        private String phone;
        private String source;

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
    }

    public static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}
