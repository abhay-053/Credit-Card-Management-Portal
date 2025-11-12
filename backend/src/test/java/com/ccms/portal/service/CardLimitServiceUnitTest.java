package com.ccms.portal.service;

import com.ccms.portal.dto.request.UpdateCardLimitRequest;
import com.ccms.portal.dto.response.CardLimitResponse;
import com.ccms.portal.entity.CardTypeEntity;
import com.ccms.portal.entity.CreditCardEntity;
import com.ccms.portal.entity.UserEntity;
import com.ccms.portal.exception.CardLimitException;
import com.ccms.portal.exception.CreditCardNotFoundException;
import com.ccms.portal.exception.UnauthorizedApplicationActionException;
import com.ccms.portal.repository.CreditCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardLimitServiceUnitTest {

    @InjectMocks
    private CardLimitService cardLimitService;

    @Mock
    private CreditCardRepository creditCardRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private CreditCardEntity makeCard(Long id,
                                      double creditLimit,
                                      double availableLimit,
                                      double minLimit,
                                      double maxLimit,
                                      String ownerEmail) {
        CreditCardEntity card = new CreditCardEntity();
        card.setId(id);
        card.setCreditLimit(creditLimit);
        card.setAvailableLimit(availableLimit);

        CardTypeEntity cardType = new CardTypeEntity();
        cardType.setMinCardLimit(minLimit);
        cardType.setMaxCardLimit(maxLimit);
        card.setCardType(cardType);

        UserEntity user = new UserEntity();
        user.setEmail(ownerEmail);
        card.setUser(user);

        return card;
    }

    @Test
    void validIncreaseWithinRange_updatesAvailableLimit() {
        String ownerEmail = "alice@example.com";
        CreditCardEntity card = makeCard(1L, 5000.0, 4000.0, 1000.0, 10000.0, ownerEmail);

        when(creditCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(creditCardRepository.save(any(CreditCardEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCardLimitRequest req = new UpdateCardLimitRequest();
        req.setNewCreditLimit(7000.0);

        CardLimitResponse resp = cardLimitService.updateLimit(1L, req, ownerEmail);

        assertNotNull(resp);
        assertEquals(1L, resp.getCardId());
        assertEquals(5000.0, resp.getOldLimit(), 1e-6);
        assertEquals(7000.0, resp.getNewLimit(), 1e-6);
        assertEquals(6000.0, resp.getAvailableLimit(), 1e-6);

        verify(creditCardRepository).findById(1L);
        verify(creditCardRepository).save(any(CreditCardEntity.class));
    }

    @Test
    void decreaseBelowOutstanding_throwsCardLimitException() {
        String ownerEmail = "bob@example.com";
        CreditCardEntity card = makeCard(2L, 3000.0, 1000.0, 500.0, 8000.0, ownerEmail);

        when(creditCardRepository.findById(2L)).thenReturn(Optional.of(card));

        UpdateCardLimitRequest req = new UpdateCardLimitRequest();
        req.setNewCreditLimit(1500.0); // < outstanding

        CardLimitException ex = assertThrows(CardLimitException.class,
                () -> cardLimitService.updateLimit(2L, req, ownerEmail));

        assertTrue(ex.getMessage().toLowerCase().contains("outstanding")
                        || ex.getMessage().toLowerCase().contains("cannot"),
                "Exception should reference outstanding constraint");

        verify(creditCardRepository).findById(2L);
        verify(creditCardRepository, never()).save(any());
    }

    @Test
    void outsideProductRange_throwsCardLimitException() {
        String ownerEmail = "carol@example.com";
        CreditCardEntity card = makeCard(3L, 3000.0, 2500.0, 1000.0, 5000.0, ownerEmail);

        when(creditCardRepository.findById(3L)).thenReturn(Optional.of(card));

        UpdateCardLimitRequest req = new UpdateCardLimitRequest();
        req.setNewCreditLimit(6000.0); // > max

        CardLimitException ex = assertThrows(CardLimitException.class,
                () -> cardLimitService.updateLimit(3L, req, ownerEmail));

        assertTrue(ex.getMessage().toLowerCase().contains("between")
                        || ex.getMessage().toLowerCase().contains("max"),
                "Exception should indicate allowed range");

        verify(creditCardRepository).findById(3L);
        verify(creditCardRepository, never()).save(any());
    }

    @Test
    void unauthorizedUser_throwsUnauthorizedApplicationActionException() {
        String ownerEmail = "dave@example.com";
        CreditCardEntity card = makeCard(4L, 4000.0, 3500.0, 1000.0, 10000.0, ownerEmail);

        when(creditCardRepository.findById(4L)).thenReturn(Optional.of(card));

        UpdateCardLimitRequest req = new UpdateCardLimitRequest();
        req.setNewCreditLimit(4500.0);

        UnauthorizedApplicationActionException ex = assertThrows(UnauthorizedApplicationActionException.class,
                () -> cardLimitService.updateLimit(4L, req, "eve@example.com"));

        assertTrue(ex.getMessage().toLowerCase().contains("not allowed")
                        || ex.getMessage().toLowerCase().contains("not allowed to update"),
                "Expected unauthorized message");

        verify(creditCardRepository).findById(4L);
        verify(creditCardRepository, never()).save(any());
    }

    @Test
    void nullRequest_throwsCardLimitException() {
        CardLimitException ex = assertThrows(CardLimitException.class,
                () -> cardLimitService.updateLimit(5L, null, "any@example.com"));

        assertTrue(ex.getMessage().toLowerCase().contains("must not be null")
                || ex.getMessage().toLowerCase().contains("request"));
        verifyNoInteractions(creditCardRepository);
    }

    @Test
    void cardNotFound_throwsCreditCardNotFoundException() {
        when(creditCardRepository.findById(999L)).thenReturn(Optional.empty());
        UpdateCardLimitRequest req = new UpdateCardLimitRequest();
        req.setNewCreditLimit(5000.0);

        CreditCardNotFoundException ex = assertThrows(CreditCardNotFoundException.class,
                () -> cardLimitService.updateLimit(999L, req, "someone@example.com"));

        assertTrue(ex.getMessage().toLowerCase().contains("card not found")
                || ex.getMessage().toLowerCase().contains("not found"));
        verify(creditCardRepository).findById(999L);
        verify(creditCardRepository, never()).save(any());
    }
}
