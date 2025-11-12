package com.ccms.portal.service.factory;

import com.ccms.portal.dto.request.CreateCardRequest;
import com.ccms.portal.entity.CardTypeEntity;
import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.entity.UserEntity;

public interface CardFactory {
    CreditCardEntity createCard(UserEntity user, CardTypeEntity cardType, CreateCardRequest request);
}

