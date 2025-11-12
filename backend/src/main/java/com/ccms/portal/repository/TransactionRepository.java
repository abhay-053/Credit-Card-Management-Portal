package com.ccms.portal.repository;

import com.ccms.portal.entity.Transaction;
import com.ccms.portal.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.card.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findTop10ByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.card.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findAllByUserId(@Param("userId") Long userId);

        /**
         * Find all transactions for a specific card ordered by transaction date
         * descending
         */
        Page<Transaction> findByCardIdOrderByTransactionDateDesc(Long cardId, Pageable pageable);

        /**
         * Find all transactions for a specific card with filters
         */
        @Query("SELECT t FROM Transaction t WHERE t.card.id = :cardId " +
                        "AND (:category IS NULL OR t.category = :category) " +
                        "AND (:isBnpl IS NULL OR t.isBnpl = :isBnpl) " +
                        "AND (:merchantName IS NULL OR LOWER(t.merchantName) LIKE LOWER(CONCAT('%', :merchantName, '%'))) "
                        +
                        "ORDER BY t.transactionDate DESC")
        Page<Transaction> findByCardIdWithFilters(
                        @Param("cardId") Long cardId,
                        @Param("category") String category,
                        @Param("isBnpl") Boolean isBnpl,
                        @Param("merchantName") String merchantName,
                        Pageable pageable);

        /**
         * Find transactions by card ID and date range
         */
        @Query("SELECT t FROM Transaction t WHERE t.card.id = :cardId " +
                        "AND t.transactionDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY t.transactionDate DESC")
        List<Transaction> findByCardIdAndDateRange(
                        @Param("cardId") Long cardId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Get total spending by category for a card
         */
        @Query("SELECT t.category, SUM(t.amount) FROM Transaction t " +
                        "WHERE t.card.id = :cardId " +
                        "GROUP BY t.category " +
                        "ORDER BY SUM(t.amount) DESC")
        List<Object[]> getSpendingByCategory(@Param("cardId") Long cardId);

        /**
         * Get monthly spending trend for a card
         */
        @Query("SELECT YEAR(t.transactionDate) as year, MONTH(t.transactionDate) as month, SUM(t.amount) as total " +
                        "FROM Transaction t " +
                        "WHERE t.card.id = :cardId " +
                        "AND t.transactionDate >= :startDate " +
                        "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate) " +
                        "ORDER BY year, month")
        List<Object[]> getMonthlySpendingTrend(
                        @Param("cardId") Long cardId,
                        @Param("startDate") LocalDate startDate);

        /**
         * Get total amount spent on a card
         */
        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.card.id = :cardId")
        Double getTotalSpentByCardId(@Param("cardId") Long cardId);

        /**
         * Get transaction count for a card
         */
        Long countByCardId(Long cardId);

        /**
         * Get BNPL transactions for a card
         */
        List<Transaction> findByCardIdAndIsBnplTrueOrderByTransactionDateDesc(Long cardId);

        /**
         * Get transactions by status for a card
         */
        List<Transaction> findByCardIdAndStatusOrderByTransactionDateDesc(Long cardId, TransactionStatus status);

        /**
         * Get recent transactions for a card (last N transactions)
         */
        @Query("SELECT t FROM Transaction t WHERE t.card.id = :cardId " +
                        "ORDER BY t.transactionDate DESC, t.createdAt DESC")
        List<Transaction> findTopNByCardIdOrderByTransactionDateDesc(@Param("cardId") Long cardId, Pageable pageable);

        /**
         * Find normal transactions by card ID, status, and date range
         */
        @Query("SELECT t FROM Transaction t WHERE t.card.id = :cardId " +
                        "AND t.isBnpl = false " +
                        "AND t.status = :status " +
                        "AND t.transactionDate BETWEEN :startDate AND :endDate " +
                        "ORDER BY t.transactionDate DESC")
        List<Transaction> findByCardIdAndIsBnplFalseAndStatusAndTransactionDateBetween(
                        @Param("cardId") Long cardId,
                        @Param("status") TransactionStatus status,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        /**
         * Find BNPL transactions by card ID and status list
         */
        @Query("SELECT t FROM Transaction t WHERE t.card.id = :cardId " +
                        "AND t.isBnpl = true " +
                        "AND t.status IN :statuses " +
                        "ORDER BY t.transactionDate DESC")
        List<Transaction> findByCardIdAndIsBnplTrueAndStatusIn(
                        @Param("cardId") Long cardId,
                        @Param("statuses") List<TransactionStatus> statuses);

        /**
         * Count normal transactions by card ID and status
         */
        Long countByCardIdAndIsBnplFalseAndStatus(Long cardId, TransactionStatus status);
}
