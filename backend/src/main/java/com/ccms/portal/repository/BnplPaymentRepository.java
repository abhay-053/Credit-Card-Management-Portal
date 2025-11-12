package com.ccms.portal.repository;

import com.ccms.portal.entity.BnplPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BnplPaymentRepository extends JpaRepository<BnplPayment, Long> {

  // Find all payments for a specific BNPL transaction
  List<BnplPayment> findByTransactionIdOrderByInstallmentNumberAsc(Long transactionId);

  // Find payments by transaction and status
  List<BnplPayment> findByTransactionIdAndStatus(Long transactionId, BnplPayment.PaymentStatus status);

    // Get total paid amount for a transaction
    @Query(value = "SELECT COALESCE(SUM(bp.payment_amount), 0) FROM bnpl_payments bp WHERE bp.transaction_id = :transactionId AND bp.status = 'COMPLETED'", nativeQuery = true)
    BigDecimal getTotalPaidAmount(@Param("transactionId") Long transactionId);

    // Get next installment number for a transaction
    @Query(value = "SELECT COALESCE(MAX(bp.installment_number), 0) + 1 FROM bnpl_payments bp WHERE bp.transaction_id = :transactionId", nativeQuery = true)
    Integer getNextInstallmentNumber(@Param("transactionId") Long transactionId);

  // Find overdue payments
  @Query("SELECT bp FROM BnplPayment bp WHERE bp.paymentDate < :currentDate AND bp.status = 'PENDING'")
  List<BnplPayment> findOverduePayments(@Param("currentDate") LocalDate currentDate);

  // Get payment summary for a transaction
  @Query(value = "SELECT " +
      "bp.transaction_id as transactionId, " +
      "t.amount as totalAmount, " +
      "COALESCE(SUM(CASE WHEN bp.status = 'COMPLETED' THEN bp.payment_amount ELSE 0 END), 0) as paidAmount, " +
      "t.amount - COALESCE(SUM(CASE WHEN bp.status = 'COMPLETED' THEN bp.payment_amount ELSE 0 END), 0) as remainingAmount, " +
      "COUNT(bp.id) as totalPayments, " +
      "COUNT(CASE WHEN bp.status = 'COMPLETED' THEN 1 END) as completedPayments " +
      "FROM bnpl_payments bp " +
      "JOIN transactions t ON bp.transaction_id = t.id " +
      "WHERE bp.transaction_id = :transactionId " +
      "GROUP BY bp.transaction_id, t.amount", nativeQuery = true)
  Object[] getPaymentSummary(@Param("transactionId") Long transactionId);
}
