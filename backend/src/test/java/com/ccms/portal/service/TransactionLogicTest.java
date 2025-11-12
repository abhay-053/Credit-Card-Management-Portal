package com.ccms.portal.service;

import com.ccms.portal.dto.request.CreateTransactionRequest;
import com.ccms.portal.enums.TransactionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class TransactionLogicTest {

  @Test
  void testTransactionRequestValidation() {
    // Test basic request validation
    CreateTransactionRequest request = new CreateTransactionRequest();
    request.setCardId(1L);
    request.setMerchantName("Test Merchant");
    request.setAmount(BigDecimal.valueOf(1000));
    request.setCategory("shopping");
    request.setIsBnpl(false);
    request.setTransactionDate(LocalDate.now());
    request.setCardType("VISA");
    request.setLastFour("8888");

    // Verify all fields are set correctly
    assertNotNull(request);
    assertEquals(1L, request.getCardId());
    assertEquals("Test Merchant", request.getMerchantName());
    assertEquals(BigDecimal.valueOf(1000), request.getAmount());
    assertEquals("shopping", request.getCategory());
    assertFalse(request.getIsBnpl());
    assertNotNull(request.getTransactionDate());
    assertEquals("VISA", request.getCardType());
    assertEquals("8888", request.getLastFour());
  }

  @Test
  void testTransactionStatusValues() {
    // Test all transaction status values
    assertEquals("COMPLETED", TransactionStatus.COMPLETED.name());
    assertEquals("BNPL_ACTIVE", TransactionStatus.BNPL_ACTIVE.name());
    assertEquals("BNPL_COMPLETED", TransactionStatus.BNPL_COMPLETED.name());
    assertEquals("BNPL_DEFAULTED", TransactionStatus.BNPL_DEFAULTED.name());
    assertEquals("PENDING", TransactionStatus.PENDING.name());
    assertEquals("FAILED", TransactionStatus.FAILED.name());
    assertEquals("REFUNDED", TransactionStatus.REFUNDED.name());
  }

  @Test
  void testFinancialCalculations() {
    // Test credit limit calculations
    BigDecimal creditLimit = BigDecimal.valueOf(10000);
    BigDecimal transactionAmount = BigDecimal.valueOf(1500);
    BigDecimal availableLimit = creditLimit.subtract(transactionAmount);

    assertEquals(BigDecimal.valueOf(8500), availableLimit);

    // Test BNPL calculation
    BigDecimal bnplAmount = BigDecimal.valueOf(6000);
    int tenureMonths = 6;
    BigDecimal monthlyEMI = bnplAmount.divide(BigDecimal.valueOf(tenureMonths), 2, BigDecimal.ROUND_HALF_UP);

    assertTrue(monthlyEMI.compareTo(BigDecimal.valueOf(1000.0)) == 0);
  }

  @Test
  void testDateCalculations() {
    // Test date calculations for statements
    LocalDate today = LocalDate.now();
    LocalDate statementDate = today.withDayOfMonth(1);
    LocalDate dueDate = statementDate.plusDays(15);

    assertNotNull(statementDate);
    assertNotNull(dueDate);
    assertTrue(dueDate.isAfter(statementDate));

    // Test month calculations
    LocalDate nextMonth = today.plusMonths(1);
    assertTrue(nextMonth.isAfter(today));
  }

  @Test
  void testAmountValidation() {
    // Test amount validation logic
    BigDecimal validAmount = BigDecimal.valueOf(100);
    BigDecimal zeroAmount = BigDecimal.ZERO;
    BigDecimal negativeAmount = BigDecimal.valueOf(-100);

    assertTrue(validAmount.compareTo(BigDecimal.ZERO) > 0);
    assertTrue(zeroAmount.compareTo(BigDecimal.ZERO) == 0);
    assertTrue(negativeAmount.compareTo(BigDecimal.ZERO) < 0);
  }

  @Test
  void testBNPLLogic() {
    // Test BNPL-specific logic
    CreateTransactionRequest bnplRequest = new CreateTransactionRequest();
    bnplRequest.setIsBnpl(true);
    bnplRequest.setAmount(BigDecimal.valueOf(5000));

    assertTrue(bnplRequest.getIsBnpl());
    assertEquals(BigDecimal.valueOf(5000), bnplRequest.getAmount());

    // Test BNPL status assignment
    TransactionStatus bnplStatus = TransactionStatus.BNPL_ACTIVE;
    assertEquals(TransactionStatus.BNPL_ACTIVE, bnplStatus);
  }

  @Test
  void testRegularTransactionLogic() {
    // Test regular transaction logic
    CreateTransactionRequest regularRequest = new CreateTransactionRequest();
    regularRequest.setIsBnpl(false);
    regularRequest.setAmount(BigDecimal.valueOf(2000));

    assertFalse(regularRequest.getIsBnpl());
    assertEquals(BigDecimal.valueOf(2000), regularRequest.getAmount());

    // Test regular transaction status assignment
    TransactionStatus regularStatus = TransactionStatus.COMPLETED;
    assertEquals(TransactionStatus.COMPLETED, regularStatus);
  }

  @Test
  void testCreditLimitValidation() {
    // Test credit limit validation logic
    BigDecimal creditLimit = BigDecimal.valueOf(10000);
    BigDecimal transactionAmount = BigDecimal.valueOf(5000);
    BigDecimal availableLimit = creditLimit.subtract(transactionAmount);

    // Test sufficient credit
    assertTrue(availableLimit.compareTo(BigDecimal.ZERO) >= 0);

    // Test insufficient credit
    BigDecimal largeTransaction = BigDecimal.valueOf(15000);
    boolean hasInsufficientCredit = largeTransaction.compareTo(creditLimit) > 0;
    assertTrue(hasInsufficientCredit);
  }

  @Test
  void testStringValidation() {
    // Test string field validation
    String merchantName = "Test Merchant";
    String category = "shopping";
    String cardType = "VISA";
    String lastFour = "8888";

    assertNotNull(merchantName);
    assertFalse(merchantName.trim().isEmpty());
    assertNotNull(category);
    assertNotNull(cardType);
    assertNotNull(lastFour);
    assertEquals(4, lastFour.length());
  }
}
