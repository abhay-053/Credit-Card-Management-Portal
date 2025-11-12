package com.ccms.portal.controller;

import com.ccms.portal.dto.request.UpdateCardStatusRequest;
import com.ccms.portal.dto.response.CreditCardResponse;
import com.ccms.portal.entity.CardTypeEntity;
import com.ccms.portal.enums.CardStatus;
import com.ccms.portal.enums.NetworkType;
import com.ccms.portal.exception.CreditCardNotFoundException;
import com.ccms.portal.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditCardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CreditCardController creditCardController;

    @Test
    void testGetCards() {
        // Create test data using builder pattern
        CreditCardResponse card = CreditCardResponse.builder()
                .id(1L)
                .cardHolderName("Test User")
                .cardNumber("1234567890123456")
                .cardStatus(CardStatus.ACTIVE)
                .creditLimit(5000.0)
                .availableLimit(4500.0)
                .expiryDate(Date.valueOf(LocalDate.now().plusYears(5)))
                .cardType(CreditCardResponse.CardTypeInfo.builder()
                        .name("Gold Card")
                        .networkType(NetworkType.VISA)
                        .description("Premium Card")
                        .build())
                .build();

        List<CreditCardResponse> cards = new ArrayList<>();
        cards.add(card);

        // Mock the service
        when(cardService.getAllCardsByUserId()).thenReturn(cards);

        // Call the controller method
        ResponseEntity<?> response = creditCardController.getCards();

        // Check the result
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetCardsWhenEmpty() {
        // Mock empty list
        when(cardService.getAllCardsByUserId()).thenReturn(new ArrayList<>());

        // Call the controller method
        ResponseEntity<?> response = creditCardController.getCards();

        // Check the result
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testUpdateCardStatus() {
        // Create test data
        UpdateCardStatusRequest request = new UpdateCardStatusRequest();
        request.setCardStatus(CardStatus.BLOCKED);

        CreditCardResponse updatedCard = CreditCardResponse.builder()
                .id(1L)
                .cardStatus(CardStatus.BLOCKED)
                .cardHolderName("Test User")
                .cardNumber("1234567890123456")
                .creditLimit(5000.0)
                .availableLimit(4500.0)
                .expiryDate(Date.valueOf(LocalDate.now().plusYears(5)))
                .build();

        // Mock the service
        when(cardService.updateCardStatus(any(), anyLong())).thenReturn(updatedCard);

        // Call the controller method
        ResponseEntity<?> response = creditCardController.updateCardStatus(request, 1L);

        // Check the result
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testUpdateCardStatusWhenCardNotFound() {
        // Create test data
        UpdateCardStatusRequest request = new UpdateCardStatusRequest();
        request.setCardStatus(CardStatus.BLOCKED);

        // Mock the service to throw exception
        when(cardService.updateCardStatus(any(), anyLong()))
                .thenThrow(new CreditCardNotFoundException("Card not found"));

        // Test that exception is thrown
        assertThrows(CreditCardNotFoundException.class, () -> {
            creditCardController.updateCardStatus(request, 999L);
        });
    }

    @Test
    void testGetCardTypes() {
        // Create test data - just create a simple mock without unnecessary stubbing
        CardTypeEntity cardType = mock(CardTypeEntity.class);
        
        List<CardTypeEntity> cardTypes = new ArrayList<>();
        cardTypes.add(cardType);

        // Mock the service
        when(cardService.getCardTypes()).thenReturn(cardTypes);

        // Call the controller method
        ResponseEntity<?> response = creditCardController.getCardTypes();

        // Check the result
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
} 