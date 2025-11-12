package com.ccms.portal.controller;

import com.ccms.portal.dto.request.CardApplicationRequest;
import com.ccms.portal.dto.request.UpdateStatusRequest;
import com.ccms.portal.dto.response.CardApplicationResponse;
import com.ccms.portal.enums.CardApplicationStatus;
import com.ccms.portal.service.CardApplicationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardApplicationControllerTest {

    @Mock
    private CardApplicationService cardApplicationService;

    @InjectMocks
    private CardApplicationController cardApplicationController;

    @Test
    void testApplyForCard() {
        // Arrange
        CardApplicationRequest request = new CardApplicationRequest();
        request.setCardTypeId(10L);
        request.setRequestLimit(5000L);

        CardApplicationResponse saved = new CardApplicationResponse();
        saved.setId(1L);
        saved.setRequestedLimit(5000L);
        saved.setStatus(CardApplicationStatus.PENDING.name());

        when(cardApplicationService.apply(any(CardApplicationRequest.class)))
                .thenReturn(saved);

        // Act
        ResponseEntity<?> response = cardApplicationController.applyForCard(request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof CardApplicationResponse);
        CardApplicationResponse body = (CardApplicationResponse) response.getBody();
        assertEquals(1L, body.getId());
        assertEquals(5000L, body.getRequestedLimit());
        assertEquals("PENDING", body.getStatus());
    }

    @Test
    void testGetApplications() {
        // Arrange
        CardApplicationResponse r1 = new CardApplicationResponse();
        r1.setId(1L);
        r1.setStatus(CardApplicationStatus.PENDING.name());

        List<CardApplicationResponse> list = new ArrayList<>();
        list.add(r1);

        when(cardApplicationService.getApplications()).thenReturn(list);

        // Act
        ResponseEntity<?> response = cardApplicationController.getApplications();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof List);
        List<?> body = (List<?>) response.getBody();
        assertEquals(1, body.size());
    }

    @Test
    void testUpdateApplication() {
        // Arrange
        UpdateStatusRequest request = new UpdateStatusRequest();
        request.setStatus(CardApplicationStatus.ACCEPTED);

        CardApplicationResponse updated = new CardApplicationResponse();
        updated.setId(1L);
        updated.setStatus(CardApplicationStatus.ACCEPTED.name());

        when(cardApplicationService.updateStatus(eq(1L), eq(CardApplicationStatus.ACCEPTED)))
                .thenReturn(updated);

        // Act
        ResponseEntity<?> response = cardApplicationController.updateApplication(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CardApplicationResponse);
        CardApplicationResponse body = (CardApplicationResponse) response.getBody();
        assertEquals(1L, body.getId());
        assertEquals("ACCEPTED", body.getStatus());
    }

    @Test
    void testDeleteApplication() {
        // Arrange
        CardApplicationResponse deleted = new CardApplicationResponse();
        deleted.setId(1L);

        when(cardApplicationService.deleteApplication(1L)).thenReturn(deleted);

        // Act
        ResponseEntity<?> response = cardApplicationController.deleteApplication(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof CardApplicationResponse);
        CardApplicationResponse body = (CardApplicationResponse) response.getBody();
        assertEquals(1L, body.getId());
    }
}
