package com.ccms.portal.repository;

import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.enums.CardStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCardEntity, Long> {

    @Query("SELECT c FROM CreditCardEntity c JOIN FETCH c.cardType WHERE c.user.id = :userId")
    List<CreditCardEntity> findAllByUserIdWithCardType(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(c.creditLimit), 0) FROM CreditCardEntity c WHERE c.user.id = :userId AND c.cardStatus = 'ACTIVE'")
    Double sumCreditLimitByUserId(@Param("userId") Long userId);

    @Query("SELECT COALESCE(SUM(c.availableLimit), 0) FROM CreditCardEntity c WHERE c.user.id = :userId AND c.cardStatus = 'ACTIVE'")
    Double sumAvailableLimitByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM CreditCardEntity c WHERE c.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(c) FROM CreditCardEntity c WHERE c.user.id = :userId AND c.cardStatus = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") CardStatus status);

    @Query("SELECT COUNT(c) > 0 FROM CreditCardEntity c WHERE c.id = :cardId AND c.user.id = :userId")
    boolean existsByIdAndUserId(@Param("cardId") Long cardId, @Param("userId") Long userId);
}
