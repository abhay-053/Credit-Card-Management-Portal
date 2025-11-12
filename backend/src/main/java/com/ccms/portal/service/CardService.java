package com.ccms.portal.service;

import com.ccms.portal.dto.request.CreateCardRequest;
import com.ccms.portal.dto.request.UpdateCardStatusRequest;

import com.ccms.portal.dto.response.CreditCardResponse;

import com.ccms.portal.enums.CardStatus;
import com.ccms.portal.exception.CardTypeNotFoundException;
import com.ccms.portal.exception.CreditCardNotFoundException;
import com.ccms.portal.exception.UnauthorizedApplicationActionException;
import com.ccms.portal.exception.UserNotFoundException;
import com.ccms.portal.service.factory.CardFactory;
import com.ccms.portal.service.factory.DefaultCardFactory;
import com.ccms.portal.util.CreditCardUtil;
import com.ccms.portal.entity.CardTypeEntity;
import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.entity.UserEntity;
import com.ccms.portal.repository.CreditCardRepository;
import com.ccms.portal.repository.CardTypeRepository;
import com.ccms.portal.repository.TransactionRepository;
import com.ccms.portal.repository.UserRepository;
import com.ccms.portal.util.JwtUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class CardService {

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);

    @Autowired
    CreditCardRepository cardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CardTypeRepository cardTypeRepository;

    @Autowired
    CreditCardUtil cardHelper;

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    @Qualifier("defaultCardFactory")
    private CardFactory defaultCardFactory;

    public List<CreditCardResponse> getAllCardsByUserId(){
        JwtUserDetails currentUser = (JwtUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        final Long userId = currentUser.getUserId();
        logger.info("Fetching cards for user ID: {}", userId);
        
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        return cardRepository.findAllByUserIdWithCardType(userId).stream()
                .map(cardHelper::buildCreditCardResponse)
                .toList();
    }

    public CreditCardResponse createCard(CreateCardRequest cardRequest){
        logger.info("Creating card for user ID: {}", cardRequest.getUserId());
        
        UserEntity user = userRepository.findById(cardRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + cardRequest.getUserId()));

        CardTypeEntity cardType = cardTypeRepository.findById(cardRequest.getCardTypeId())
                .orElseThrow(() -> new CardTypeNotFoundException("Card type not found with ID: " + cardRequest.getCardTypeId()));

        /**
         * Implement Factory Design Pattern
         */
        CreditCardEntity entity = defaultCardFactory.createCard(user, cardType, cardRequest);

        CreditCardEntity saved = cardRepository.save(entity);
        logger.info("Card created successfully with ID: {}", saved.getId());
        
        return cardHelper.buildCreditCardResponse(saved);
    }

    public CreditCardResponse updateCardStatus(UpdateCardStatusRequest cardStatusRequest, Long cardId){
        logger.info("Updating card status for card ID: {} to {}", cardId, cardStatusRequest.getCardStatus());
        
        JwtUserDetails currentUser = (JwtUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        CreditCardEntity creditCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new CreditCardNotFoundException("Credit Card not found with card Id: " + cardId));

        if(creditCard.getUser() == null || !currentUser.getUserId().equals(creditCard.getUser().getId())){
            logger.warn("Unauthorized card update attempt for card ID: {} by user ID: {}", cardId, currentUser.getUserId());
            throw new UnauthorizedApplicationActionException("You are not allowed to update the status of this card");
        }

        CreditCardEntity savedEntity = creditCard;

        if (!creditCard.getCardStatus().equals(cardStatusRequest.getCardStatus())) {
            creditCard.setCardStatus(cardStatusRequest.getCardStatus());
            savedEntity = cardRepository.save(creditCard); // save only if changed
            logger.info("Card status updated successfully for card ID: {}", cardId);
        } else {
            logger.info("Card status is already '{}' for card ID: {}, skipping update", cardStatusRequest.getCardStatus(), cardId);
        }

        return cardHelper.buildCreditCardResponse(savedEntity);

    }

    public List<CardTypeEntity> getCardTypes() {
        logger.info("Fetching card types");
        return cardTypeRepository.findAll();
    }
}