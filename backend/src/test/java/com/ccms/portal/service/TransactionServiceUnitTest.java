package com.ccms.portal.service;

import com.ccms.portal.dto.request.CreateTransactionRequest;
import com.ccms.portal.dto.response.TransactionResponse;
import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.enums.TransactionStatus;
import com.ccms.portal.repository.CreditCardRepository;
import com.ccms.portal.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceUnitTest {

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private CreditCardRepository creditCardRepository;

  @Mock
  private CreditCardStatementService creditCardStatementService;

  @InjectMocks
  private TransactionService transactionService;

  private CreditCardEntity testCard;
  private CreateTransactionRequest testRequest;

  @BeforeEach
  void setUp() {
    testCard = CreditCardEntity.builder()
        .id(1L)
        .cardNumber("1234-5678-9012-3456")
        .creditLimit(10000.0)
        .availableLimit(10000.0)
        .cardStatus(com.ccms.portal.enums.CardStatus.ACTIVE)
        .expiryDate(java.sql.Date.valueOf(LocalDate.now().plusYears(2)))
        .build();

    testRequest = new CreateTransactionRequest();
    testRequest.setCardId(1L);
    testRequest.setMerchantName("Test Merchant");
    testRequest.setAmount(BigDecimal.valueOf(1000));
    testRequest.setCategory("shopping");
    testRequest.setIsBnpl(false);
    testRequest.setTransactionDate(LocalDate.now());
    testRequest.setCardType("VISA");
    testRequest.setLastFour("3456");
  }

  @Test
  void testCreateTransaction_Success() {
    // Given
    when(creditCardRepository.findById(1L)).thenReturn(Optional.of(testCard));
    when(transactionRepository.save(any())).thenAnswer(invocation -> {
      var transaction = invocation.getArgument(0);
      return transaction;
    });

    // When
    TransactionResponse response = transactionService.createTransaction(testRequest);

    // Then
    assertNotNull(response);
    assertEquals("Test Merchant", response.getMerchantName());
    assertEquals(BigDecimal.valueOf(1000), response.getAmount());
    assertEquals("shopping", response.getCategory());
    assertFalse(response.getIsBnpl());
    assertEquals(TransactionStatus.COMPLETED, response.getStatus());

    verify(creditCardRepository).findById(1L);
    verify(transactionRepository).save(any());
    verify(creditCardRepository).save(any());
  }

  @Test
  void testCreateTransaction_CardNotFound() {
    // Given
    when(creditCardRepository.findById(1L)).thenReturn(Optional.empty());

    // When & Then
    assertThrows(Exception.class, () -> {
      transactionService.createTransaction(testRequest);
    });

    verify(creditCardRepository).findById(1L);
    verify(transactionRepository, never()).save(any());
  }

  @Test
  void testCreateTransaction_InsufficientLimit() {
    // Given
    testRequest.setAmount(BigDecimal.valueOf(15000)); // More than available limit
    when(creditCardRepository.findById(1L)).thenReturn(Optional.of(testCard));

    // When & Then
    assertThrows(Exception.class, () -> {
      transactionService.createTransaction(testRequest);
    });

    verify(creditCardRepository).findById(1L);
    verify(transactionRepository, never()).save(any());
  }

  @Test
  void testCreateBnplTransaction_DoesNotDeductCreditLimit() {
    // Given
    testRequest.setIsBnpl(true);
    when(creditCardRepository.findById(1L)).thenReturn(Optional.of(testCard));
    when(transactionRepository.save(any())).thenAnswer(invocation -> {
      var transaction = invocation.getArgument(0);
      return transaction;
    });

    // When
    TransactionResponse response = transactionService.createTransaction(testRequest);

    // Then
    assertNotNull(response);
    assertTrue(response.getIsBnpl());
    assertEquals(TransactionStatus.BNPL_ACTIVE, response.getStatus());

    // Verify credit limit was NOT deducted
    verify(creditCardRepository, never()).save(any());
  }

  @Test
  void testCreateTransaction_InvalidAmount() {
    // Given
    testRequest.setAmount(BigDecimal.ZERO);
    when(creditCardRepository.findById(1L)).thenReturn(Optional.of(testCard));

    // When & Then
    assertThrows(Exception.class, () -> {
      transactionService.createTransaction(testRequest);
    });

    verify(creditCardRepository).findById(1L);
    verify(transactionRepository, never()).save(any());
  }
}
