package com.ccms.portal.util;

import com.ccms.portal.dto.response.CreditCardResponse;
import com.ccms.portal.entity.CardTypeEntity;
import com.ccms.portal.entity.CreditCardEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Random;

@Component
public class CreditCardUtil {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardUtil.class);
    private static final int CARD_NUMBER_LENGTH = 16;

    /*
     * Generates a random 16-digit card number.
     * Digits are purely random and not validated
     * against Luhn algorithm.
     */
    public String generateCardNumber() {
        logger.debug("Generating new card number");
        StringBuilder cardNumber = new StringBuilder(CARD_NUMBER_LENGTH);
        for (int i = 0; i < CARD_NUMBER_LENGTH; i++) {
            cardNumber.append(new Random().nextInt(10));
        }
        return cardNumber.toString();
    }

    /*
     * Generates an expiry date by adding the given
     * number of years to the current date.
     */
    public Date generateExpiryDate(int yearsFromNow) {
        logger.debug("Generating expiry date {} years from now", yearsFromNow);
        LocalDate expiry = LocalDate.now().plusYears(yearsFromNow);
        return Date.valueOf(expiry);
    }

    /*
     * Builds a CreditCardResponse DTO from a CreditCardEntity.
     * Includes card details, limits, status, expiry date, and
     * associated card type information.
     */
    public CreditCardResponse buildCreditCardResponse(CreditCardEntity cardEntity){
        logger.debug("Building response for card ID: {}", cardEntity.getId());

        CardTypeEntity cardType = cardEntity.getCardType();

        CreditCardResponse.CardTypeInfo cardTypeInfo = CreditCardResponse.CardTypeInfo.builder()
                .name(cardType.getName())
                .networkType(cardType.getNetworkType())
                .description(cardType.getDescription())
                .build();

        return CreditCardResponse.builder()
                .id(cardEntity.getId())
                .cardHolderName(cardEntity.getUser().getName())
                .cardNumber(cardEntity.getCardNumber())
                .cardStatus(cardEntity.getCardStatus())
                .creditLimit(cardEntity.getCreditLimit())
                .availableLimit(cardEntity.getAvailableLimit())
                .expiryDate(cardEntity.getExpiryDate())
                .cardType(cardTypeInfo)
                .build();
    }
}
