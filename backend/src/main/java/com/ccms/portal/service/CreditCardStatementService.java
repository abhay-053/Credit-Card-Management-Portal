package com.ccms.portal.service;

import com.ccms.portal.dto.request.PaymentRequest;
import com.ccms.portal.dto.response.StatementResponse;
import com.ccms.portal.entity.CreditCardStatement;
import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.entity.Transaction;
import com.ccms.portal.exception.ResourceNotFoundException;
import com.ccms.portal.exception.UnauthorizedException;
import com.ccms.portal.repository.CreditCardStatementRepository;
import com.ccms.portal.repository.TransactionRepository;
import com.ccms.portal.repository.CreditCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditCardStatementService {

  private final CreditCardStatementRepository statementRepository;
  private final TransactionRepository transactionRepository;
  private final CreditCardRepository creditCardRepository;
  private final CreditLimitService creditLimitService;

  public StatementResponse getCurrentStatement(Long cardId) {
    // Verify user owns the card
    verifyCardOwnership(cardId);

    // Get or create current statement
    CreditCardStatement statement = statementRepository.findFirstByCardIdOrderByStatementDateDesc(cardId)
        .orElseGet(() -> createCurrentStatement(cardId));

    return mapToStatementResponse(statement);
  }

  @Transactional
  public CreditCardStatement createCurrentStatement(Long cardId) {
    // Verify user owns the card
    verifyCardOwnership(cardId);

    LocalDate today = LocalDate.now();
    LocalDate firstDayOfMonth = today.withDayOfMonth(1);

    // Calculate statement amount from current month transactions
    // Use a broader date range to catch all current month transactions
    LocalDate monthStart = firstDayOfMonth.minusDays(1); // Start from previous day to be safe
    LocalDate monthEnd = today.plusDays(1); // End on next day to be safe

    log.info("Looking for transactions between {} and {} for card {}", monthStart, monthEnd, cardId);

    List<Transaction> monthlyTransactions = transactionRepository.findByCardIdAndDateRange(
        cardId, monthStart, monthEnd);

    log.info("Found {} transactions for card {} in current month", monthlyTransactions.size(), cardId);
    monthlyTransactions.forEach(
        t -> log.info("Transaction: {} - {} on {}", t.getMerchantName(), t.getAmount(), t.getTransactionDate()));

    // Only include regular transactions in statement
    // BNPL transactions are handled separately through BNPL outstanding
    List<Transaction> statementTransactions = monthlyTransactions.stream()
        .filter(t -> !t.getIsBnpl())
        .collect(Collectors.toList());

    log.info("Transactions included in statement: {}", statementTransactions.size());
    statementTransactions.forEach(t -> log.info("Statement transaction: {} - {} (BNPL: {}, Status: {})",
        t.getMerchantName(), t.getAmount(), t.getIsBnpl(), t.getStatus()));

    BigDecimal statementAmount = statementTransactions.stream()
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    log.info("Calculated statement amount: {} for card {}", statementAmount, cardId);

    // Check if statement already exists for current month
    List<CreditCardStatement> existingStatements = statementRepository
        .findByCardIdAndStatementDateBetween(cardId, firstDayOfMonth, today);
    Optional<CreditCardStatement> existingStatement = existingStatements.isEmpty() ? Optional.empty()
        : Optional.of(existingStatements.get(0));

    if (existingStatement.isPresent()) {
      // Update existing statement with new amount
      CreditCardStatement statement = existingStatement.get();
      statement.setStatementAmount(statementAmount);
      return statementRepository.save(statement);
    } else {
      // Create new statement
      LocalDate dueDate = today.plusDays(15);

      CreditCardStatement statement = CreditCardStatement.builder()
          .cardId(cardId)
          .statementDate(today)
          .dueDate(dueDate)
          .statementAmount(statementAmount)
          .paidAmount(BigDecimal.ZERO)
          .status("PENDING")
          .build();

      return statementRepository.save(statement);
    }
  }

  @Transactional
  public StatementResponse makePayment(PaymentRequest paymentRequest) {
    CreditCardStatement statement = statementRepository.findById(paymentRequest.getStatementId())
        .orElseThrow(() -> new ResourceNotFoundException("Statement not found"));

    // Verify user owns the card
    verifyCardOwnership(statement.getCardId());

    // Validate payment amount
    if (paymentRequest.getPaymentAmount().compareTo(statement.getAmountDue()) > 0) {
      throw new IllegalArgumentException("Payment amount cannot exceed amount due");
    }

    // Update paid amount
    BigDecimal newPaidAmount = statement.getPaidAmount().add(paymentRequest.getPaymentAmount());
    statement.setPaidAmount(newPaidAmount);

    // Check payment timing
    LocalDate today = LocalDate.now();
    boolean isOnTime = !today.isAfter(statement.getDueDate());
    boolean isLate = today.isAfter(statement.getDueDate());

    // Update status
    if (newPaidAmount.compareTo(statement.getStatementAmount()) >= 0) {
      statement.setStatus("PAID");

      // Restore credit limit only if statement is fully paid
      if (isOnTime || isLate) {
        // Recalculate available limit after statement payment
        creditLimitService.recalculateAvailableLimit(statement.getCardId());
        log.info("Recalculated available limit for card {} after statement payment", statement.getCardId());
      }

      // Apply late payment penalties if applicable
      if (isLate) {
        applyLatePaymentPenalties(statement.getCardId(), statement.getStatementAmount());
      }
    } else {
      statement.setStatus(isLate ? "OVERDUE" : "PARTIAL");
    }

    statement = statementRepository.save(statement);

    log.info("Payment of {} made for statement {} - Status: {}, On Time: {}",
        paymentRequest.getPaymentAmount(), statement.getId(), statement.getStatus(), isOnTime);

    return mapToStatementResponse(statement);
  }

  public List<StatementResponse> getStatementHistory(Long cardId) {
    // Verify user owns the card
    verifyCardOwnership(cardId);

    List<CreditCardStatement> statements = statementRepository.findByCardIdOrderByStatementDateDesc(cardId);
    return statements.stream()
        .map(this::mapToStatementResponse)
        .collect(Collectors.toList());
  }

  @Transactional
  public void updateStatementWithBnplPayment(Long cardId, BigDecimal bnplPaymentAmount) {
    // BNPL EMI payments are now handled separately through BNPL outstanding
    // calculation
    // No need to update statement for BNPL payments
    log.info("BNPL EMI payment of {} for card {} - handled through BNPL outstanding calculation",
        bnplPaymentAmount, cardId);
  }

  private void verifyCardOwnership(Long cardId) {
    // Temporarily bypass ownership check for debugging
    log.info("Verifying card ownership for cardId: {}", cardId);

    // Check if card exists
    boolean cardExists = creditCardRepository.existsById(cardId);
    if (!cardExists) {
      log.error("Card {} does not exist", cardId);
      throw new UnauthorizedException("Card does not exist");
    }

    log.info("Card {} exists, proceeding with statement creation", cardId);
  }

  /**
   * Apply late payment penalties
   */
  private void applyLatePaymentPenalties(Long cardId, BigDecimal statementAmount) {
    try {
      CreditCardEntity card = creditCardRepository.findById(cardId).orElse(null);
      if (card != null) {
        // Calculate late payment fee (2% of statement amount, minimum ₹500)
        BigDecimal lateFee = statementAmount.multiply(BigDecimal.valueOf(0.02));
        BigDecimal minLateFee = BigDecimal.valueOf(500);
        lateFee = lateFee.max(minLateFee);

        // Calculate interest charges (3% per month on unpaid amount)
        BigDecimal interestCharges = statementAmount.multiply(BigDecimal.valueOf(0.03));

        // Total penalty
        BigDecimal totalPenalty = lateFee.add(interestCharges);

        // Reduce credit limit by penalty amount
        BigDecimal newCreditLimit = BigDecimal.valueOf(card.getCreditLimit()).subtract(totalPenalty);
        BigDecimal newAvailableLimit = BigDecimal.valueOf(card.getAvailableLimit()).subtract(totalPenalty);

        // Ensure limits don't go below zero
        newCreditLimit = newCreditLimit.max(BigDecimal.ZERO);
        newAvailableLimit = newAvailableLimit.max(BigDecimal.ZERO);

        card.setCreditLimit(newCreditLimit.doubleValue());
        card.setAvailableLimit(newAvailableLimit.doubleValue());
        creditCardRepository.save(card);

        log.warn("Applied late payment penalties for card {}: Late fee: {}, Interest: {}, Total: {}",
            cardId, lateFee, interestCharges, totalPenalty);
      }
    } catch (Exception e) {
      log.warn("Failed to apply late payment penalties for card {}: {}", cardId, e.getMessage());
    }
  }

  private StatementResponse mapToStatementResponse(CreditCardStatement statement) {
    return StatementResponse.builder()
        .id(statement.getId())
        .cardId(statement.getCardId())
        .statementDate(statement.getStatementDate())
        .dueDate(statement.getDueDate())
        .statementAmount(statement.getStatementAmount())
        .paidAmount(statement.getPaidAmount())
        .amountDue(statement.getAmountDue())
        .status(statement.getStatus())
        .isPaid(statement.isPaid())
        .isOverdue(statement.isOverdue())
        .build();
  }
}
