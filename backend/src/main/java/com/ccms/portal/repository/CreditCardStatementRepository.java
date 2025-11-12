package com.ccms.portal.repository;

import com.ccms.portal.entity.CreditCardStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardStatementRepository extends JpaRepository<CreditCardStatement, Long> {

  // Find current statement for a card (most recent)
  @Query("SELECT s FROM CreditCardStatement s WHERE s.cardId = :cardId ORDER BY s.statementDate DESC")
  List<CreditCardStatement> findByCardIdOrderByStatementDateDesc(@Param("cardId") Long cardId);

  // Find current statement for a card
  Optional<CreditCardStatement> findFirstByCardIdOrderByStatementDateDesc(Long cardId);

  // Find statements for a card within date range
  @Query("SELECT s FROM CreditCardStatement s WHERE s.cardId = :cardId AND s.statementDate BETWEEN :startDate AND :endDate ORDER BY s.statementDate DESC")
  List<CreditCardStatement> findByCardIdAndStatementDateBetween(
      @Param("cardId") Long cardId,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  // Find overdue statements
  @Query("SELECT s FROM CreditCardStatement s WHERE s.cardId = :cardId AND s.dueDate < :currentDate AND s.status != 'PAID'")
  List<CreditCardStatement> findOverdueStatements(@Param("cardId") Long cardId,
      @Param("currentDate") LocalDate currentDate);

  // Find statements by status
  List<CreditCardStatement> findByCardIdAndStatus(Long cardId, String status);

  // Check if statement exists for a card on a specific date
  boolean existsByCardIdAndStatementDate(Long cardId, LocalDate statementDate);
}
