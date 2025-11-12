package com.ccms.portal.repository;

import com.ccms.portal.entity.CardApplicationEntity;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CardApplicationRepository extends JpaRepository<CardApplicationEntity, Long> {

    List<CardApplicationEntity> findAllByUserId(Long userId);

    Optional<CardApplicationEntity> findByUserIdAndCardTypeId(Long userId, @NotNull(message = "cardTypeId is required") Long cardTypeId);
}
