package com.example.core.controller;

import com.example.core.dto.AccountDTO;
import com.example.core.dto.DeductResponseDTO;
import com.example.core.dto.RechargeInitiationDTO;
import com.example.core.entity.RechargePromotion;
import com.example.core.entity.Transaction;
import com.example.core.enums.PaymentChannel;
import com.example.core.enums.ProjectType;
import com.example.core.exception.InsufficientBalanceException;
import com.example.core.mapper.AccountMapper;
import com.example.core.repository.RechargePromotionRepository;
import com.example.core.security.AuthenticationContext;
import com.example.core.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final AuthenticationContext authenticationContext;
    private final RechargePromotionRepository rechargePromotionRepository;

    public WalletController(WalletService walletService,
                            AuthenticationContext authenticationContext,
                            RechargePromotionRepository rechargePromotionRepository) {
        this.walletService = walletService;
        this.authenticationContext = authenticationContext;
        this.rechargePromotionRepository = rechargePromotionRepository;
    }

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance() {
        try {
            Long customerId = authenticationContext.getCurrentCustomerId();
            if (customerId == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Not authenticated"));
            }

            AccountDTO dto = AccountMapper.toDTO(walletService.getOrCreateAccount(customerId));
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/promotions")
    public ResponseEntity<?> getActivePromotions() {
        try {
            List<RechargePromotion> promotions = rechargePromotionRepository.findActivePromotionsAt(LocalDateTime.now());
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/recharge/initiate")
    public ResponseEntity<?> initiateRecharge(@RequestBody RechargeInitiateRequest request) {
        try {
            Long customerId = authenticationContext.getCurrentCustomerId();
            if (customerId == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Not authenticated"));
            }

            RechargeInitiationDTO result = walletService.initiateRecharge(
                    customerId,
                    request.getChannel(),
                    request.getAmount(),
                    request.getPromotionId()
            );

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/deduct")
    public ResponseEntity<?> deduct(@RequestBody DeductRequest request) {
        try {
            Long customerId = authenticationContext.getCurrentCustomerId();
            if (customerId == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Not authenticated"));
            }

            Transaction transaction = walletService.deduct(
                    customerId,
                    request.getAmount(),
                    request.getProjectType(),
                    request.getMetadata(),
                    request.getReferenceNo()
            );

            return ResponseEntity.ok(new DeductResponseDTO(transaction.getId(), transaction.getAccount().getBalance()));
        } catch (InsufficientBalanceException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    public static class RechargeInitiateRequest {
        private PaymentChannel channel;
        private BigDecimal amount;
        private Long promotionId;

        public PaymentChannel getChannel() {
            return channel;
        }

        public void setChannel(PaymentChannel channel) {
            this.channel = channel;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public Long getPromotionId() {
            return promotionId;
        }

        public void setPromotionId(Long promotionId) {
            this.promotionId = promotionId;
        }
    }

    public static class DeductRequest {
        private BigDecimal amount;
        private ProjectType projectType = ProjectType.GENERAL;
        private String metadata;
        private String referenceNo;

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public ProjectType getProjectType() {
            return projectType;
        }

        public void setProjectType(ProjectType projectType) {
            this.projectType = projectType;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }

        public String getReferenceNo() {
            return referenceNo;
        }

        public void setReferenceNo(String referenceNo) {
            this.referenceNo = referenceNo;
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
