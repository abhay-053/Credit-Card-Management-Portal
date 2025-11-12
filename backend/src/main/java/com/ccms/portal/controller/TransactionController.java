package com.ccms.portal.controller;

import com.ccms.portal.dto.request.CreateTransactionRequest;
import com.ccms.portal.dto.response.AnalyticsResponse;
import com.ccms.portal.dto.response.TransactionHistoryResponse;
import com.ccms.portal.dto.response.TransactionResponse;
import com.ccms.portal.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Create a new transaction
     * POST /api/transactions
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        log.info("Received request to create transaction for card: {}", request.getCardId());

        try {
            TransactionResponse response = transactionService.createTransaction(request);
            log.info("Transaction created successfully with ID: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating transaction: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get transaction history for a card
     * GET /api/transactions/{cardId}
     */
    @GetMapping("/{cardId}")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(
            @PathVariable Long cardId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isBnpl,
            @RequestParam(required = false) String merchant) {

        log.info("Received request for transaction history - cardId: {}, page: {}, size: {}", cardId, page, size);

        try {
            TransactionHistoryResponse response = transactionService.getTransactionHistory(
                    cardId, page, size, category, isBnpl, merchant);
            log.info("Retrieved {} transactions for card: {}", response.getTotalElements(), cardId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching transaction history: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get spending analytics for a card
     * GET /api/transactions/{cardId}/analytics
     */
    @GetMapping("/{cardId}/analytics")
    public ResponseEntity<AnalyticsResponse> getSpendingAnalytics(@PathVariable Long cardId) {
        log.info("Received request for spending analytics - cardId: {}", cardId);

        try {
            AnalyticsResponse response = transactionService.getSpendingAnalytics(cardId);
            log.info("Retrieved analytics for card: {}", cardId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching spending analytics: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get spending analytics by category
     * GET /api/transactions/{cardId}/analytics/category
     */
    @GetMapping("/{cardId}/analytics/category")
    public ResponseEntity<List<Object[]>> getSpendingByCategory(@PathVariable Long cardId) {
        log.info("Received request for spending by category - cardId: {}", cardId);

        try {
            List<Object[]> response = transactionService.getSpendingAnalytics(cardId).getCategoryBreakdown()
                    .stream()
                    .map(cat -> new Object[] { cat.getCategory(), cat.getAmount() })
                    .collect(java.util.stream.Collectors.toList());
            log.info("Retrieved spending by category for card: {}", cardId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching spending by category: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get monthly spending trend
     * GET /api/transactions/{cardId}/analytics/trends
     */
    @GetMapping("/{cardId}/analytics/trends")
    public ResponseEntity<List<Object[]>> getMonthlySpendingTrend(
            @PathVariable Long cardId,
            @RequestParam(required = false) LocalDate startDate) {

        log.info("Received request for monthly spending trend - cardId: {}, startDate: {}", cardId, startDate);

        try {
            // Use custom startDate or default to 12 months ago
            LocalDate effectiveStartDate = startDate != null ? startDate : LocalDate.now().minusMonths(12);

            List<Object[]> response = transactionService.getMonthlySpendingTrends(cardId, effectiveStartDate);
            log.info("Retrieved monthly spending trend for card: {}", cardId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching monthly spending trend: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get total amount spent on a card
     * GET /api/transactions/{cardId}/total
     */
    @GetMapping("/{cardId}/total")
    public ResponseEntity<Map<String, Object>> getTotalSpent(@PathVariable Long cardId) {
        log.info("Received request for total spent - cardId: {}", cardId);

        try {
            Double totalSpent = transactionService.getTotalSpentByCardId(cardId);
            Long transactionCount = transactionService.getTransactionCountByCardId(cardId);

            Map<String, Object> response = Map.of(
                    "totalSpent", totalSpent,
                    "transactionCount", transactionCount);

            log.info("Retrieved total spent for card: {} - Amount: {}, Count: {}", cardId, totalSpent,
                    transactionCount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching total spent: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get BNPL transactions for a card
     * GET /api/transactions/{cardId}/bnpl
     */
    @GetMapping("/{cardId}/bnpl")
    public ResponseEntity<List<TransactionResponse>> getBnplTransactions(@PathVariable Long cardId) {
        log.info("Received request for BNPL transactions - cardId: {}", cardId);

        try {
            List<TransactionResponse> response = transactionService.getBnplTransactions(cardId);
            log.info("Retrieved {} BNPL transactions for card: {}", response.size(), cardId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching BNPL transactions: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get transaction by ID
     * GET /api/transactions/details/{transactionId}
     */
    @GetMapping("/details/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long transactionId) {
        log.info("Received request for transaction details - transactionId: {}", transactionId);

        try {
            TransactionResponse response = transactionService.getTransactionById(transactionId);
            log.info("Retrieved transaction details for ID: {}", transactionId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching transaction details: {}", e.getMessage());
            throw e;
        }
    }
}
