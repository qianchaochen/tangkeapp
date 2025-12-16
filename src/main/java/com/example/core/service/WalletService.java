package com.example.core.service;

import com.example.core.dto.RechargeInitiationDTO;
import com.example.core.entity.Account;
import com.example.core.entity.Customer;
import com.example.core.entity.RechargeOrder;
import com.example.core.entity.RechargeOrderEvent;
import com.example.core.entity.RechargePromotion;
import com.example.core.entity.Transaction;
import com.example.core.enums.PaymentChannel;
import com.example.core.enums.ProjectType;
import com.example.core.enums.RechargeOrderStatus;
import com.example.core.enums.TransactionType;
import com.example.core.exception.InsufficientBalanceException;
import com.example.core.exception.PaymentProcessingException;
import com.example.core.payment.AlipayPaymentService;
import com.example.core.payment.PaymentCallback;
import com.example.core.payment.PaymentInitiation;
import com.example.core.payment.WechatPayService;
import com.example.core.repository.AccountRepository;
import com.example.core.repository.CustomerRepository;
import com.example.core.repository.RechargeOrderEventRepository;
import com.example.core.repository.RechargeOrderRepository;
import com.example.core.repository.RechargePromotionRepository;
import com.example.core.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private static final int OPTIMISTIC_LOCK_RETRY = 3;

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final RechargePromotionRepository rechargePromotionRepository;
    private final RechargeOrderRepository rechargeOrderRepository;
    private final RechargeOrderEventRepository rechargeOrderEventRepository;

    private final WechatPayService wechatPayService;
    private final AlipayPaymentService alipayPaymentService;

    private final ObjectMapper objectMapper;
    private final TransactionTemplate requiresNewTx;

    public WalletService(CustomerRepository customerRepository,
                         AccountRepository accountRepository,
                         TransactionRepository transactionRepository,
                         RechargePromotionRepository rechargePromotionRepository,
                         RechargeOrderRepository rechargeOrderRepository,
                         RechargeOrderEventRepository rechargeOrderEventRepository,
                         WechatPayService wechatPayService,
                         AlipayPaymentService alipayPaymentService,
                         ObjectMapper objectMapper,
                         PlatformTransactionManager transactionManager) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.rechargePromotionRepository = rechargePromotionRepository;
        this.rechargeOrderRepository = rechargeOrderRepository;
        this.rechargeOrderEventRepository = rechargeOrderEventRepository;
        this.wechatPayService = wechatPayService;
        this.alipayPaymentService = alipayPaymentService;
        this.objectMapper = objectMapper;

        this.requiresNewTx = new TransactionTemplate(transactionManager);
        this.requiresNewTx.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
    }

    public Account getOrCreateAccount(Long customerId) {
        return requiresNewTx.execute(status -> {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            return accountRepository.findByCustomerId(customerId)
                    .orElseGet(() -> accountRepository.save(new Account(customer)));
        });
    }

    public RechargeInitiationDTO initiateRecharge(Long customerId, PaymentChannel channel, BigDecimal amount, Long promotionId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        return requiresNewTx.execute(status -> {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            Account account = accountRepository.findByCustomerId(customerId)
                    .orElseGet(() -> accountRepository.save(new Account(customer)));

            BigDecimal bonusAmount = BigDecimal.ZERO;
            if (promotionId != null) {
                RechargePromotion promotion = rechargePromotionRepository.findById(promotionId)
                        .orElseThrow(() -> new IllegalArgumentException("Promotion not found"));
                bonusAmount = promotion.getBonusAmount();
            }

            String orderNo = generateOrderNo(channel);
            RechargeOrder order = new RechargeOrder(customer, account, orderNo, channel, amount);
            order.setPromotionId(promotionId);
            order.setBonusAmount(bonusAmount);
            order.setStatus(RechargeOrderStatus.PENDING);

            Map<String, Object> requestMeta = new LinkedHashMap<>();
            requestMeta.put("customerId", customerId);
            requestMeta.put("channel", channel.name());
            requestMeta.put("amount", amount);
            requestMeta.put("bonusAmount", bonusAmount);
            if (promotionId != null) {
                requestMeta.put("promotionId", promotionId);
            }
            order.setRequestMetadata(toJsonSafe(requestMeta));

            order = rechargeOrderRepository.save(order);
            rechargeOrderEventRepository.save(new RechargeOrderEvent(order, null, RechargeOrderStatus.PENDING.name(), "Order created", order.getRequestMetadata()));

            PaymentInitiation initiation;
            if (channel == PaymentChannel.WECHAT_PAY) {
                int amountFen = amount.multiply(BigDecimal.valueOf(100)).intValueExact();
                initiation = wechatPayService.createJsapiRechargeOrder(orderNo, amountFen, customer.getWechatOpenid(), "Wallet recharge");
                order.setProviderPrepayId(initiation.getProviderPrepayId());
                order = rechargeOrderRepository.save(order);
            } else if (channel == PaymentChannel.ALIPAY) {
                initiation = alipayPaymentService.createAppRechargeOrder(orderNo, amount, "Wallet recharge");
            } else {
                throw new IllegalArgumentException("Unsupported payment channel");
            }

            RechargeInitiationDTO dto = new RechargeInitiationDTO();
            dto.setOrderNo(orderNo);
            dto.setChannel(channel);
            dto.setAmount(amount);
            dto.setBonusAmount(bonusAmount);
            dto.setPromotionId(promotionId);
            dto.setWechatPayParams(initiation.getClientParams());
            dto.setAlipayOrderString(initiation.getOrderString());
            return dto;
        });
    }

    public void handleWechatPayNotification(Map<String, String> headers, String body) {
        PaymentCallback callback = wechatPayService.parseAndVerifyNotification(headers, body);
        confirmRechargePaid(PaymentChannel.WECHAT_PAY, callback);
    }

    public void handleAlipayNotification(Map<String, String> params) {
        PaymentCallback callback = alipayPaymentService.parseAndVerifyNotification(params);
        confirmRechargePaid(PaymentChannel.ALIPAY, callback);
    }

    public Transaction deduct(Long customerId, BigDecimal amount, ProjectType projectType, String metadata, String referenceNo) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        return executeWithOptimisticLockRetry(() -> requiresNewTx.execute(status -> {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

            Account account = accountRepository.findByCustomerId(customerId)
                    .orElseGet(() -> accountRepository.save(new Account(customer)));

            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }

            account.setBalance(account.getBalance().subtract(amount));
            account.setTotalSpend(account.getTotalSpend().add(amount));
            account = accountRepository.save(account);

            Transaction transaction = new Transaction(customer, account, TransactionType.SPEND, amount, projectType);
            transaction.setMetadata(metadata);
            transaction.setReferenceNo(referenceNo);
            return transactionRepository.save(transaction);
        }));
    }

    private void confirmRechargePaid(PaymentChannel channel, PaymentCallback callback) {
        if (callback.getOrderNo() == null || callback.getOrderNo().isEmpty()) {
            throw new PaymentProcessingException("Missing orderNo in callback");
        }

        if (!callback.isPaid()) {
            log.info("Payment callback not paid (channel={}, orderNo={})", channel, callback.getOrderNo());
            return;
        }

        executeWithOptimisticLockRetry(() -> {
            requiresNewTx.execute(status -> {
                RechargeOrder order = rechargeOrderRepository.findByOrderNo(callback.getOrderNo())
                        .orElseThrow(() -> new PaymentProcessingException("Recharge order not found: " + callback.getOrderNo()));

                if (order.getStatus() == RechargeOrderStatus.PAID) {
                    log.info("Recharge order already paid (orderNo={})", order.getOrderNo());
                    return null;
                }

                RechargeOrderStatus from = order.getStatus();

                order.setStatus(RechargeOrderStatus.PAID);
                order.setProviderTransactionId(callback.getProviderTransactionId());
                order.setCallbackMetadata(callback.getRawPayload());
                order.setPaidAt(LocalDateTime.now());
                rechargeOrderRepository.save(order);
                rechargeOrderEventRepository.save(new RechargeOrderEvent(order, from.name(), RechargeOrderStatus.PAID.name(), "Payment confirmed", callback.getRawPayload()));

                Account account = order.getAccount();
                BigDecimal credit = order.getAmount().add(order.getBonusAmount() != null ? order.getBonusAmount() : BigDecimal.ZERO);

                account.setBalance(account.getBalance().add(credit));
                account.setTotalRecharge(account.getTotalRecharge().add(order.getAmount()));
                accountRepository.save(account);

                Map<String, Object> txMeta = new LinkedHashMap<>();
                txMeta.put("orderNo", order.getOrderNo());
                txMeta.put("channel", channel.name());
                txMeta.put("providerTransactionId", callback.getProviderTransactionId());
                txMeta.put("bonusAmount", order.getBonusAmount());
                if (order.getPromotionId() != null) {
                    txMeta.put("promotionId", order.getPromotionId());
                }

                Transaction transaction = new Transaction(order.getCustomer(), account, TransactionType.RECHARGE, order.getAmount(),
                        order.getPromotionId() != null ? ProjectType.PROMOTION : ProjectType.GENERAL);
                transaction.setReferenceNo(order.getOrderNo());
                transaction.setMetadata(toJsonSafe(txMeta));

                transactionRepository.save(transaction);
                return null;
            });
            return null;
        });
    }

    private <T> T executeWithOptimisticLockRetry(java.util.function.Supplier<T> supplier) {
        ObjectOptimisticLockingFailureException last = null;
        for (int attempt = 1; attempt <= OPTIMISTIC_LOCK_RETRY; attempt++) {
            try {
                return supplier.get();
            } catch (ObjectOptimisticLockingFailureException e) {
                last = e;
                log.warn("Optimistic lock conflict, retry {}/{}", attempt, OPTIMISTIC_LOCK_RETRY);
            }
        }
        throw last;
    }

    private String generateOrderNo(PaymentChannel channel) {
        return channel.name() + "_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String toJsonSafe(Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            return null;
        }
    }
}
