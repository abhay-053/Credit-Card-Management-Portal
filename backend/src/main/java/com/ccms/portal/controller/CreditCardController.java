package com.ccms.portal.controller;

import com.ccms.portal.dto.request.CreateCardRequest;
import com.ccms.portal.dto.request.UpdateCardStatusRequest;
import com.ccms.portal.dto.response.CreditCardResponse;
import com.ccms.portal.entity.CardTypeEntity;
import com.ccms.portal.service.CardService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling Credit Card related operations.
 *
 * Base path: /cards
 *
 * Responsibilities:
 * - Fetch all cards of the logged-in user
 * - Update the status of a specific card
 * - Fetch available card types
 */
@RestController
@RequestMapping("/cards")
public class CreditCardController {

    // Logger instance for request tracking and debugging
    private static final Logger logger = LoggerFactory.getLogger(CreditCardController.class);

    // Service layer to handle business logic
    @Autowired
    private CardService cardService;

    /**
     * Fetch all credit cards of the currently logged-in user.
     *
     * Endpoint: GET /cards
     *
     * @return 200 OK with list of cards if found,
     *         204 No Content if user has no cards.
     */
    @GetMapping
    public ResponseEntity<?> getCards() {
        logger.info("GET /cards - Fetching user cards");

        List<CreditCardResponse> userCards = cardService.getAllCardsByUserId();
        if (userCards.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(userCards);
    }

    /**
     * Update the status of a credit card (e.g., ACTIVE, BLOCKED).
     *
     * Endpoint: PUT /cards/{cardId}/status
     *
     * @param cardStatusRequest request body containing the new card status
     * @param cardId            path variable representing the card ID
     * @return 200 OK with updated card details
     */
    @PutMapping("/{cardId}/status")
    public ResponseEntity<?> updateCardStatus(
            @Valid @RequestBody UpdateCardStatusRequest cardStatusRequest,
            @PathVariable Long cardId) {

        logger.info("PUT /cards/{}/status - Updating card status to {}",
                cardId, cardStatusRequest.getCardStatus());

        CreditCardResponse updatedCard = cardService.updateCardStatus(cardStatusRequest, cardId);
        return ResponseEntity.ok(updatedCard);
    }

    /**
     * Fetch all available card types (e.g., Platinum, Gold, Silver).
     *
     * Endpoint: GET /cards/type
     *
     * @return 200 OK with list of card types
     */
    @GetMapping("/type")
    public ResponseEntity<?> getCardTypes() {
        logger.info("GET /cards/type - Fetching card types");

        List<CardTypeEntity> allCardType = cardService.getCardTypes();
        return ResponseEntity.ok(allCardType);
    }
}
