package com.ccms.portal.repository;

import com.ccms.portal.entity.CardTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardTypeRepository extends JpaRepository<CardTypeEntity, Long> {
}
