package com.example.core;

import com.example.core.config.TestPaymentConfig;
import com.example.core.config.TestWechatConfig;
import com.example.core.dto.RechargeInitiationDTO;
import com.example.core.entity.Account;
import com.example.core.entity.Transaction;
import com.example.core.enums.PaymentChannel;
import com.example.core.enums.ProjectType;
import com.example.core.enums.TransactionType;
import com.example.core.exception.InsufficientBalanceException;
import com.example.core.repository.*;
import com.example.core.service.CustomerAuthService;
import com.example.core.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestWechatConfig.class, TestPaymentConfig.class})
class WalletServiceIntegrationTest {

    @Autowired
    private CustomerAuthService authService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RechargeOrderRepository rechargeOrderRepository;

    @Autowired
    private RechargeOrderEventRepository rechargeOrderEventRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        rechargeOrderEventRepository.deleteAll();
        rechargeOrderRepository.deleteAll();
        accountRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    void testWechatRechargeIsIdempotentAndCreatesTransaction() {
        Long customerId = authService.handleWechatLogin("wallet_test_code", "weixin").getCustomerId();
        Account account = walletService.getOrCreateAccount(customerId);
        BigDecimal initial = account.getBalance();

        RechargeInitiationDTO initiation = walletService.initiateRecharge(customerId, PaymentChannel.WECHAT_PAY, new BigDecimal("10.00"), null);
        assertNotNull(initiation.getOrderNo());

        String callbackBody = "{\"orderNo\":\"" + initiation.getOrderNo() + "\",\"providerTransactionId\":\"wx_tx_001\",\"paid\":true}";

        walletService.handleWechatPayNotification(Map.of(), callbackBody);
        walletService.handleWechatPayNotification(Map.of(), callbackBody); // duplicate

        Account after = accountRepository.findByCustomerId(customerId).orElseThrow();
        assertEquals(initial.add(new BigDecimal("10.00")), after.getBalance());

        Transaction tx = transactionRepository.findByTypeAndReferenceNo(TransactionType.RECHARGE, initiation.getOrderNo()).orElseThrow();
        assertEquals(new BigDecimal("10.00"), tx.getAmount());

        assertEquals(1, transactionRepository.countByTypeAndReferenceNo(TransactionType.RECHARGE, initiation.getOrderNo()));
    }

    @Test
    void testDeductPreventsOverdraft() {
        Long customerId = authService.handleWechatLogin("wallet_deduct_code", "weixin").getCustomerId();

        // credit via mock callback
        RechargeInitiationDTO initiation = walletService.initiateRecharge(customerId, PaymentChannel.WECHAT_PAY, new BigDecimal("20.00"), null);
        String callbackBody = "{\"orderNo\":\"" + initiation.getOrderNo() + "\",\"providerTransactionId\":\"wx_tx_002\",\"paid\":true}";
        walletService.handleWechatPayNotification(Map.of(), callbackBody);

        Transaction spend = walletService.deduct(customerId, new BigDecimal("5.00"), ProjectType.GENERAL, "{}", "spend_001");
        assertEquals(TransactionType.SPEND, spend.getType());

        Account afterSpend = accountRepository.findByCustomerId(customerId).orElseThrow();
        assertEquals(new BigDecimal("15.00"), afterSpend.getBalance());

        assertThrows(InsufficientBalanceException.class, () ->
                walletService.deduct(customerId, new BigDecimal("100.00"), ProjectType.GENERAL, "{}", "spend_002")
        );

        Account afterFailed = accountRepository.findByCustomerId(customerId).orElseThrow();
        assertEquals(new BigDecimal("15.00"), afterFailed.getBalance());
    }
}
