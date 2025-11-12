package com.ccms.portal.service.factory;

import com.ccms.portal.dto.request.CreateCardRequest;
import com.ccms.portal.entity.CardTypeEntity;
import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.entity.UserEntity;
import com.ccms.portal.enums.CardStatus;
import com.ccms.portal.util.CreditCardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("defaultCardFactory")
public class DefaultCardFactory implements CardFactory {

    @Autowired
    private CreditCardUtil cardHelper;

    @Override
    public CreditCardEntity createCard(UserEntity user, CardTypeEntity cardType, CreateCardRequest request) {
        return CreditCardEntity.builder()
                .user(user)
                .cardType(cardType)
                .creditLimit(request.getCreditLimit())
                .availableLimit(request.getCreditLimit())
                .cardStatus(CardStatus.ACTIVE)
                .cardNumber(cardHelper.generateCardNumber())
                .expiryDate(cardHelper.generateExpiryDate(5))
                .build();
    }
}