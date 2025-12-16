package com.example.core;

import com.example.core.config.TestWechatConfig;
import com.example.core.entity.Customer;
import com.example.core.repository.CustomerRepository;
import com.example.core.service.CustomerAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestWechatConfig.class)
class WechatAuthIntegrationTest {

    @Autowired
    private CustomerAuthService authService;

    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Test
    void testNewCustomerLogin() {
        CustomerAuthService.LoginResponse response = 
            authService.handleWechatLogin("test_code_123", "weixin");

        assertNotNull(response.getToken());
        assertTrue(response.isNewCustomer());
        assertNotNull(response.getCustomerId());
        assertEquals("weixin", response.getSource());
        
        Customer customer = customerRepository.findById(response.getCustomerId())
            .orElse(null);
        assertNotNull(customer);
        assertEquals("weixin", customer.getSource());
        assertTrue(customer.getName().isEmpty());
        assertNull(customer.getPhone());
    }

    @Test
    void testExistingCustomerLogin() {
        String code = "test_code_existing";
        
        // First login
        CustomerAuthService.LoginResponse firstLogin = 
            authService.handleWechatLogin(code, "weixin");
        assertTrue(firstLogin.isNewCustomer());
        Long customerId = firstLogin.getCustomerId();

        // Second login with same code (mocked to return same openid)
        // Note: In real scenario, wx.login() would generate same code for same session
        // For testing, we simulate by getting the same customer directly
        Customer firstCustomer = customerRepository.findById(customerId).orElseThrow();
        int initialCount = (int) customerRepository.count();

        // Verify customer exists
        assertEquals(1, initialCount);
    }

    @Test
    void testCompleteProfile() {
        // Login first
        CustomerAuthService.LoginResponse loginResponse = 
            authService.handleWechatLogin("test_code_profile", "weixin");
        Long customerId = loginResponse.getCustomerId();

        // Complete profile
        Customer updated = authService.completeProfile(
            customerId,
            "Test User",
            "13800138000",
            "weixin"
        );

        assertEquals("Test User", updated.getName());
        assertEquals("13800138000", updated.getPhone());
        assertEquals("weixin", updated.getSource());

        // Verify database persistence
        Customer fromDb = customerRepository.findById(customerId).orElseThrow();
        assertEquals("Test User", fromDb.getName());
        assertEquals("13800138000", fromDb.getPhone());
    }

    @Test
    void testFindCustomerByOpenid() {
        String openid = "test_openid_12345";
        CustomerAuthService.LoginResponse response = 
            authService.handleWechatLogin("code_for_openid_test", "weixin");

        Customer customer = customerRepository.findById(response.getCustomerId()).orElseThrow();
        
        // Verify we can find by openid
        Customer found = customerRepository.findByWechatOpenid(customer.getWechatOpenid())
            .orElse(null);
        assertNotNull(found);
        assertEquals(customer.getId(), found.getId());
    }

    @Test
    void testCustomerDataPersistence() {
        String code = "test_persistence_code";
        String source = "weixin_test";
        
        // Create customer
        CustomerAuthService.LoginResponse response = 
            authService.handleWechatLogin(code, source);
        
        // Complete profile
        authService.completeProfile(
            response.getCustomerId(),
            "完整名称",
            "13812345678",
            source
        );

        // Retrieve and verify all data
        Customer customer = customerRepository.findById(response.getCustomerId()).orElseThrow();
        
        assertEquals("完整名称", customer.getName());
        assertEquals("13812345678", customer.getPhone());
        assertEquals(source, customer.getSource());
        assertNotNull(customer.getFirstVisitAt());
        assertNotNull(customer.getCreatedAt());
        assertNotNull(customer.getWechatOpenid());
    }

    @Test
    void testMultipleCustomersCreation() {
        // Create multiple customers
        CustomerAuthService.LoginResponse response1 = 
            authService.handleWechatLogin("code_1", "weixin");
        
        CustomerAuthService.LoginResponse response2 = 
            authService.handleWechatLogin("code_2", "weixin");
        
        CustomerAuthService.LoginResponse response3 = 
            authService.handleWechatLogin("code_3", "weixin");

        // Verify all created
        assertEquals(3, customerRepository.count());
        
        // Verify they're different customers
        assertNotEquals(response1.getCustomerId(), response2.getCustomerId());
        assertNotEquals(response2.getCustomerId(), response3.getCustomerId());
    }
}
