package com.example.core.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthenticationContext {

    public Long getCurrentCustomerId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                Object customerId = attributes.getRequest().getAttribute("customerId");
                if (customerId != null) {
                    return (Long) customerId;
                }
            }
        } catch (Exception e) {
            // No request context available
        }
        return null;
    }

    public boolean isAuthenticated() {
        return getCurrentCustomerId() != null;
    }
}
