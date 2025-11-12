package com.ccms.portal.service;

import com.ccms.portal.dto.response.DashboardResponse;
import com.ccms.portal.dto.response.SummaryResponse;
import com.ccms.portal.dto.response.TransactionResponse;
import com.ccms.portal.entity.Transaction;
import com.ccms.portal.entity.UserEntity;
import com.ccms.portal.enums.CardStatus;
import com.ccms.portal.exception.UserNotFoundException;
import com.ccms.portal.repository.CreditCardRepository;
import com.ccms.portal.repository.TransactionRepository;
import com.ccms.portal.repository.UserRepository;
import com.ccms.portal.util.JwtUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
@Slf4j
public class DashboardService {

    @Autowired
    private CreditCardRepository cardRepo;

    @Autowired
    private TransactionRepository txRepo;

    @Autowired
    private UserRepository userRepo;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        log.info("getDashboard called");

        Object principal = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getPrincipal()
                : null;

        Long userId;
        if (principal instanceof JwtUserDetails) {
            userId = ((JwtUserDetails) principal).getUserId();
            log.debug("Authenticated userId from JwtUserDetails: {}", userId);
        } else {
            userId = null;
            log.warn("Could not obtain JwtUserDetails principal from security context - principalType={}", principal != null ? principal.getClass().getName() : "null");
        }

        if (userId == null) {
            log.error("Authenticated user not found in security context");
            throw new IllegalStateException("Authenticated user not found. Make sure request contains a valid JWT.");
        }

        UserEntity userEntity = userRepo.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id {}", userId);
                    return new UserNotFoundException("User not found with id " + userId);
                });

        Double totalLimitObj = cardRepo.sumCreditLimitByUserId(userId);
        double totalLimit = totalLimitObj == null ? 0.0 : totalLimitObj;

        Double availableObj = cardRepo.sumAvailableLimitByUserId(userId);
        double availableCredit = availableObj == null ? 0.0 : availableObj;

        double outstanding = totalLimit - availableCredit;

        int totalCards = (int) cardRepo.countByUserId(userId);
        int activeCards = (int) cardRepo.countByUserIdAndStatus(userId, CardStatus.ACTIVE);

        log.debug("Summary for userId={}: totalCards={}, activeCards={}, totalLimit={}, availableCredit={}, outstanding={}",
                userId, totalCards, activeCards, totalLimit, availableCredit, outstanding);

        SummaryResponse summary = new SummaryResponse();
        summary.setActiveCards(activeCards);
        summary.setTotalCards(totalCards);
        summary.setTotalLimit(totalLimit);
        summary.setAvailableCredit(availableCredit);
        summary.setOutstanding(outstanding);

        List<Transaction> txEntities = txRepo.findTop10ByUserId(userId);
        log.debug("Transactions fetched for userId={} - count={}", userId, txEntities != null ? txEntities.size() : 0);

        List<TransactionResponse> transactions = txEntities.stream()
                .map(t -> {
                    TransactionResponse tr = new TransactionResponse();

                    tr.setId(t.getId());

                    if (t.getCard() != null && t.getCard().getId() != null) {
                        tr.setCardId(t.getCard().getId());
                    } else {
                        tr.setCardId(null);
                    }

                    tr.setMerchantName(t.getMerchantName());

                    BigDecimal amt = t.getAmount();
                    tr.setAmount(amt != null ? amt : BigDecimal.ZERO);

                    tr.setTransactionDate(t.getTransactionDate());

                    tr.setCategory(t.getCategory());

                    tr.setIsBnpl(t.getIsBnpl());

                    tr.setCardType(t.getCardType());
                    tr.setLastFour(t.getLastFour());

                    tr.setStatus(t.getStatus());
                    tr.setCreatedAt(t.getCreatedAt());

                    return tr;
                })
                .sorted(Comparator.comparing(TransactionResponse::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .collect(Collectors.toList());

        DashboardResponse dashboard = new DashboardResponse();
        dashboard.setUserName(userEntity.getName());
        dashboard.setSummary(summary);
        dashboard.setTransactions(transactions);
        dashboard.setLastUpdated(Instant.now());

        log.info("Dashboard built successfully for userId={} userName={} transactionsCount={}",
                userId, userEntity.getName(), transactions.size());

        return dashboard;
    }
}
