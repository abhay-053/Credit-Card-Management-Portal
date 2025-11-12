package com.ccms.portal.service;

import com.ccms.portal.dto.request.UpdateCardLimitRequest;
import com.ccms.portal.dto.response.CardLimitResponse;
import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.exception.CardLimitException;
import com.ccms.portal.exception.CreditCardNotFoundException;
import com.ccms.portal.exception.UnauthorizedApplicationActionException;
import com.ccms.portal.repository.CreditCardRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CardLimitService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Transactional
    public CardLimitResponse updateLimit(Long cardId, UpdateCardLimitRequest request, String callerUsername) {
        log.info("updateLimit called - cardId={}, callerUsername={}", cardId, callerUsername);

        if (request == null || request.getNewCreditLimit() == null) {
            log.error("Invalid request: request or newCreditLimit is null - cardId={}, callerUsername={}", cardId, callerUsername);
            throw new CardLimitException("Request or new credit limit must not be null");
        }

        double requested = request.getNewCreditLimit();
        log.debug("Requested new credit limit: {} for cardId={}", requested, cardId);

        CreditCardEntity card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> {
                    log.error("Card not found for id: {}", cardId);
                    return new CreditCardNotFoundException("Card not found for id: " + cardId);
                });

        if (!isOwner(card, callerUsername)) {
            log.warn("Unauthorized update attempt - cardId={}, callerUsername={}, ownerEmail={}",
                    cardId,
                    callerUsername,
                    card.getUser() != null ? card.getUser().getEmail() : "null");
            throw new UnauthorizedApplicationActionException("You are not allowed to update this card");
        }

        double minCardLimit = card.getCardType() != null ? card.getCardType().getMinCardLimit() : 0.0;
        double maxCardLimit = card.getCardType() != null ? card.getCardType().getMaxCardLimit() : Double.MAX_VALUE;

        log.debug("Card limits for cardId={}: min={}, max={}", cardId, minCardLimit, maxCardLimit);

        if (requested < minCardLimit || requested > maxCardLimit) {
            log.warn("Requested limit out of bounds - cardId={}, requested={}, min={}, max={}",
                    cardId, requested, minCardLimit, maxCardLimit);
            throw new CardLimitException("Requested limit must be between " + minCardLimit + " and " + maxCardLimit);
        }

        double oldLimit = card.getCreditLimit();
        double available = card.getAvailableLimit();
        double outstanding = oldLimit - available;

        log.debug("Old limit={}, available={}, outstanding={} for cardId={}", oldLimit, available, outstanding, cardId);

        if (requested < outstanding) {
            log.warn("Requested limit less than outstanding - cardId={}, requested={}, outstanding={}", cardId, requested, outstanding);
            throw new CardLimitException("New limit cannot be lower than outstanding balance (" + outstanding + ")");
        }

        double newAvailable = requested - outstanding;

        card.setCreditLimit(requested);
        card.setAvailableLimit(newAvailable);
        creditCardRepository.save(card);

        log.info("Credit limit updated successfully - cardId={}, oldLimit={}, newLimit={}, newAvailable={}",
                cardId, oldLimit, requested, newAvailable);

        return CardLimitResponse.builder()
                .cardId(card.getId())
                .oldLimit(oldLimit)
                .newLimit(requested)
                .availableLimit(newAvailable)
                .build();
    }

    private boolean isOwner(CreditCardEntity card, String callerUsername) {
        if (callerUsername == null || card.getUser() == null) return false;
        String ownerEmail = card.getUser().getEmail();
        return ownerEmail != null && ownerEmail.equalsIgnoreCase(callerUsername);
    }
}
