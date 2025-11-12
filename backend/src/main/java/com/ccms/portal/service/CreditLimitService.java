package com.ccms.portal.service;

import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.entity.Transaction;
import com.ccms.portal.entity.CreditCardStatement;
import com.ccms.portal.enums.TransactionStatus;
import com.ccms.portal.repository.CreditCardRepository;
import com.ccms.portal.repository.TransactionRepository;
import com.ccms.portal.repository.CreditCardStatementRepository;
import com.ccms.portal.repository.BnplPaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditLimitService {

  private final CreditCardRepository creditCardRepository;
  private final TransactionRepository transactionRepository;
  private final CreditCardStatementRepository statementRepository;
  private final BnplPaymentRepository bnplPaymentRepository;

  /**
   * Calculate current available credit limit
   */
  public BigDecimal calculateCurrentAvailableLimit(Long cardId) {
    return calculateCurrentAvailableLimit(cardId, false);
  }

  /**
   * Calculate current available credit limit
   * 
   * @param cardId        The card ID
   * @param isBnplPayment Whether this is a BNPL payment (affects calculation
   *                      logic)
   */
  public BigDecimal calculateCurrentAvailableLimit(Long cardId, boolean isBnplPayment) {
    CreditCardEntity card = creditCardRepository.findById(cardId).orElse(null);
    if (card == null) {
      log.warn("Card not found for ID: {}", cardId);
      return BigDecimal.ZERO;
    }

    if (isBnplPayment) {
      log.info("BNPL PAYMENT DEBUG - Card {}: Credit Limit: {}, BNPL Outstanding: {}", cardId, card.getCreditLimit(),
          getBnplOutstanding(cardId));
      return BigDecimal.valueOf(card.getCreditLimit()).subtract(getBnplOutstanding(cardId));
    }
    // 1. Get statement amount due (includes all normal transactions)
    BigDecimal statementAmountDue = getStatementAmountDue(cardId);

    // 2. Get BNPL outstanding (BNPL transactions minus EMI payments)
    BigDecimal bnplOutstanding = getBnplOutstanding(cardId);

    // 3. Calculate total outstanding
    // Normal outstanding = statement amount due (includes current month + previous
    // unpaid)
    // BNPL outstanding = BNPL transactions - EMI payments
    BigDecimal totalOutstanding = statementAmountDue.add(bnplOutstanding);

    // 5. Available limit = Credit limit - Total outstanding
    BigDecimal availableLimit = BigDecimal.valueOf(card.getCreditLimit()).subtract(totalOutstanding);

    // Additional debugging for the calculation
    log.info(
        "CALCULATION DEBUG - Card {}: Credit Limit: {}, Statement Due: {}, BNPL Outstanding: {}, Total Outstanding: {}, Available: {} (BNPL Payment: {})",
        cardId, card.getCreditLimit(), statementAmountDue, bnplOutstanding, totalOutstanding, availableLimit,
        isBnplPayment);

    // Validation: Check if calculation makes sense
    BigDecimal expectedAvailable = BigDecimal.valueOf(card.getCreditLimit()).subtract(statementAmountDue)
        .subtract(bnplOutstanding);
    if (!availableLimit.equals(expectedAvailable)) {
      log.error(
          "CALCULATION ERROR - Card {}: Expected: {}, Actual: {}, Statement Due: {}, BNPL Outstanding: {} (BNPL Payment: {})",
          cardId, expectedAvailable, availableLimit, statementAmountDue, bnplOutstanding, isBnplPayment);
    }

    log.info(
        "Available limit calculation for card {}: Credit Limit: {}, Statement Amount Due: {}, BNPL Outstanding: {}, Total Outstanding: {}, Available: {} (BNPL Payment: {})",
        cardId, card.getCreditLimit(), statementAmountDue, bnplOutstanding, totalOutstanding, availableLimit,
        isBnplPayment);

    return availableLimit.max(BigDecimal.ZERO); // Can't go below 0
  }

  /**
   * Get statement amount due (includes both normal and BNPL payments)
   */
  private BigDecimal getStatementAmountDue(Long cardId) {
    CreditCardStatement latestStatement = statementRepository
        .findFirstByCardIdOrderByStatementDateDesc(cardId)
        .orElse(null);

    if (latestStatement == null) {
      log.info("No statement found for card {}", cardId);
      return BigDecimal.ZERO;
    }

    BigDecimal amountDue = latestStatement.getAmountDue();
    log.info("Statement amount due for card {}: Statement Amount: {}, Paid Amount: {}, Amount Due: {}",
        cardId, latestStatement.getStatementAmount(), latestStatement.getPaidAmount(), amountDue);
    return amountDue;
  }

  /**
   * Recalculate and update available limit for a card
   */
  public void recalculateAvailableLimit(Long cardId) {
    recalculateAvailableLimit(cardId, false);
  }

  /**
   * Recalculate and update available limit for a card
   * 
   * @param cardId        The card ID
   * @param isBnplPayment Whether this is a BNPL payment (affects calculation
   *                      logic)
   */
  public void recalculateAvailableLimit(Long cardId, boolean isBnplPayment) {
    try {
      log.info("Starting available limit recalculation for card {} (BNPL Payment: {})", cardId, isBnplPayment);

      CreditCardEntity card = creditCardRepository.findById(cardId).orElse(null);

      // Note: BNPL payments are now handled directly in BnplService
      // This method is only used for normal transactions and statement payments

      if (card == null) {
        log.warn("Card not found for recalculation: {}", cardId);
        return;
      }

      // Get OLD available limit BEFORE calculating new one
      BigDecimal oldAvailableLimit = BigDecimal.valueOf(card.getAvailableLimit());

      // Calculate NEW available limit
      BigDecimal newAvailableLimit = calculateCurrentAvailableLimit(cardId, isBnplPayment);

      // Calculate the change
      BigDecimal change = newAvailableLimit.subtract(oldAvailableLimit);
      // Update the card with new available limit
      card.setAvailableLimit(newAvailableLimit.doubleValue());
      creditCardRepository.save(card);

      log.info("Recalculated available limit for card {}: Old: {}, New: {}, Change: {} (BNPL Payment: {})",
          cardId, oldAvailableLimit, newAvailableLimit, change, isBnplPayment);

      // Additional debugging for BNPL payment issue
      if (change.compareTo(BigDecimal.ZERO) > 0) {
        log.info(
            "AVAILABLE LIMIT INCREASE DEBUG - Card {}: Available limit increased by {} from {} to {} (BNPL Payment: {})",
            cardId, change, oldAvailableLimit, newAvailableLimit, isBnplPayment);
      } else if (change.compareTo(BigDecimal.ZERO) < 0) {
        log.info(
            "AVAILABLE LIMIT DECREASE DEBUG - Card {}: Available limit decreased by {} from {} to {} (BNPL Payment: {})",
            cardId, change.abs(), oldAvailableLimit, newAvailableLimit, isBnplPayment);
      }

    } catch (Exception e) {
      log.error("Failed to recalculate available limit for card {}: {}", cardId, e.getMessage(), e);
    }
  }

  /**
   * Check if user has sufficient credit limit for a transaction
   */
  public boolean hasSufficientCreditLimit(Long cardId, BigDecimal transactionAmount) {
    BigDecimal availableLimit = calculateCurrentAvailableLimit(cardId);
    boolean sufficient = availableLimit.compareTo(transactionAmount) >= 0;

    log.info("Credit limit check for card {}: Available: {}, Required: {}, Sufficient: {}",
        cardId, availableLimit, transactionAmount, sufficient);

    return sufficient;
  }

  /**
   * Get BNPL outstanding amount (BNPL transactions minus EMI payments)
   */
  private BigDecimal getBnplOutstanding(Long cardId) {
    // Get all active BNPL transactions
    List<Transaction> bnplTransactions = transactionRepository
        .findByCardIdAndIsBnplTrueAndStatusIn(cardId,
            List.of(TransactionStatus.BNPL_ACTIVE, TransactionStatus.BNPL_DEFAULTED));

    log.info("Found {} BNPL transactions for card {}: {}", bnplTransactions.size(), cardId,
        bnplTransactions.stream().map(t -> t.getId() + ":" + t.getAmount() + ":" + t.getStatus()).toList());

    if (bnplTransactions.isEmpty()) {
      return BigDecimal.ZERO;
    }

    // Calculate total BNPL amount
    BigDecimal totalBnplAmount = bnplTransactions.stream()
        .map(Transaction::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // Calculate total EMI payments made
    BigDecimal totalEmiPaid = bnplTransactions.stream()
        .mapToLong(Transaction::getId)
        .mapToObj(transactionId -> {
          try {
            // Get total paid amount for this BNPL transaction
            BigDecimal paid = bnplPaymentRepository.getTotalPaidAmount(transactionId);
            log.info("BNPL transaction {} has paid amount: {}", transactionId, paid);
            return paid;
          } catch (Exception e) {
            log.warn("Error getting BNPL payment amount for transaction {}: {}", transactionId, e.getMessage());
            return BigDecimal.ZERO;
          }
        })
        .filter(Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal bnplOutstanding = totalBnplAmount.subtract(totalEmiPaid);
    log.info("BNPL outstanding for card {}: Total: {}, Paid: {}, Outstanding: {}",
        cardId, totalBnplAmount, totalEmiPaid, bnplOutstanding);

    // Additional debugging for BNPL payment issue
    if (totalEmiPaid.compareTo(BigDecimal.ZERO) > 0) {
      log.info(
          "BNPL PAYMENT DEBUG - Card {}: BNPL transactions found: {}, Total BNPL amount: {}, Total EMI paid: {}, BNPL outstanding: {}",
          cardId, bnplTransactions.size(), totalBnplAmount, totalEmiPaid, bnplOutstanding);

      // Debug each BNPL transaction individually
      for (Transaction bnplTransaction : bnplTransactions) {
        try {
          BigDecimal transactionPaid = bnplPaymentRepository.getTotalPaidAmount(bnplTransaction.getId());
          log.info("BNPL TRANSACTION DEBUG - Transaction {}: Amount: {}, Paid: {}, Outstanding: {}",
              bnplTransaction.getId(), bnplTransaction.getAmount(), transactionPaid,
              bnplTransaction.getAmount().subtract(transactionPaid));
        } catch (Exception e) {
          log.warn("Error getting payment details for BNPL transaction {}: {}", bnplTransaction.getId(),
              e.getMessage());
        }
      }
    }

    return bnplOutstanding.max(BigDecimal.ZERO); // Can't be negative
  }
}
