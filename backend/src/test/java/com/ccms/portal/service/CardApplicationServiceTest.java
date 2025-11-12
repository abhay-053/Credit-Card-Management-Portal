package com.ccms.portal.service;

import com.ccms.portal.dto.request.CardApplicationRequest;
import com.ccms.portal.dto.request.CreateCardRequest;
import com.ccms.portal.dto.response.CardApplicationResponse;
import com.ccms.portal.entity.CardApplicationEntity;
import com.ccms.portal.enums.CardApplicationStatus;
import com.ccms.portal.exception.CardApplicationNotFoundException;
import com.ccms.portal.exception.UnauthorizedApplicationActionException;
import com.ccms.portal.repository.CardApplicationRepository;
import com.ccms.portal.util.JwtUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardApplicationServiceTest {

    @Mock
    private CardApplicationRepository repository;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardApplicationService cardApplicationService;

    // ---- mock security context ----
    private JwtUserDetails user;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setupSecurity() {
        user = new JwtUserDetails(1L, "demoUser", "USER");
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);

        when(authentication.getPrincipal()).thenReturn(user);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testApply() {
        // Arrange
        CardApplicationRequest req = new CardApplicationRequest();
        req.setCardTypeId(10L);
        req.setRequestLimit(20_000L);

        CardApplicationEntity saved = new CardApplicationEntity();
        saved.setId(101L);
        saved.setUserId(1L);
        saved.setCardTypeId(10L);
        saved.setRequestedLimit(20_000L);
        saved.setStatus(CardApplicationStatus.PENDING);

        when(repository.save(any(CardApplicationEntity.class))).thenReturn(saved);

        // Act
        CardApplicationResponse result = cardApplicationService.apply(req);

        // Assert
        assertNotNull(result);
        assertEquals(101L, result.getId());
        assertEquals(20_000L, result.getRequestedLimit());
        assertEquals("PENDING", result.getStatus());
        verify(repository, times(1)).save(any(CardApplicationEntity.class));
    }

    @Test
    void testGetApplicationsForNormalUser() {
        // Arrange
        CardApplicationEntity e1 = new CardApplicationEntity();
        e1.setId(1L);
        e1.setUserId(1L);
        e1.setStatus(CardApplicationStatus.PENDING);

        when(repository.findAllByUserId(1L)).thenReturn(List.of(e1));

        // Act
        List<CardApplicationResponse> list = cardApplicationService.getApplications();

        // Assert
        assertEquals(1, list.size());
        assertEquals("PENDING", list.get(0).getStatus());
        verify(repository).findAllByUserId(1L);
    }

    @Test
    void testGetApplicationsForAdmin() {
        // Arrange – switch user role to ADMIN
        user = new JwtUserDetails(2L, "adminUser", "ADMIN");
        when(authentication.getPrincipal()).thenReturn(user);

        CardApplicationEntity e1 = new CardApplicationEntity();
        e1.setId(10L);
        e1.setStatus(CardApplicationStatus.ACCEPTED);
        when(repository.findAll()).thenReturn(List.of(e1));

        // Act
        List<CardApplicationResponse> list = cardApplicationService.getApplications();

        // Assert
        assertEquals(1, list.size());
        assertEquals("ACCEPTED", list.get(0).getStatus());
        verify(repository).findAll();
    }

    @Test
    void testUpdateStatusByAdmin() {
        // Arrange – set current user as ADMIN
        user = new JwtUserDetails(99L, "adminUser", "ADMIN");
        when(authentication.getPrincipal()).thenReturn(user);

        CardApplicationEntity app = new CardApplicationEntity();
        app.setId(7L);
        app.setUserId(5L);
        app.setCardTypeId(22L);
        app.setRequestedLimit(50_000L);
        app.setStatus(CardApplicationStatus.PENDING);

        when(repository.findById(7L)).thenReturn(Optional.of(app));
        when(repository.save(any(CardApplicationEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        CardApplicationResponse resp = cardApplicationService.updateStatus(7L, CardApplicationStatus.ACCEPTED);

        // Assert
        assertEquals("ACCEPTED", resp.getStatus());
        assertNotNull(resp.getReviewDate());
        verify(cardService, times(1)).createCard(any(CreateCardRequest.class));
        verify(repository, times(1)).save(any(CardApplicationEntity.class));
    }

    @Test
    void testUpdateStatusUnauthorizedForNormalUser() {
        // Arrange – user stays as USER
        CardApplicationEntity app = new CardApplicationEntity();
        app.setId(5L);
        app.setUserId(1L);
        when(repository.findById(5L)).thenReturn(Optional.of(app));

        // Act / Assert
        assertThrows(UnauthorizedApplicationActionException.class,
                () -> cardApplicationService.updateStatus(5L, CardApplicationStatus.ACCEPTED));
    }

    @Test
    void testUpdateStatusWhenNotFound() {
        when(repository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(CardApplicationNotFoundException.class,
                () -> cardApplicationService.updateStatus(100L, CardApplicationStatus.ACCEPTED));
    }

    @Test
    void testDeleteApplicationByOwner() {
        // Arrange
        CardApplicationEntity app = new CardApplicationEntity();
        app.setId(3L);
        app.setUserId(1L);  // same as current user
        app.setStatus(CardApplicationStatus.PENDING); 

        when(repository.findById(3L)).thenReturn(Optional.of(app));

        // Act
        CardApplicationResponse resp = cardApplicationService.deleteApplication(3L);

        // Assert
        assertEquals(3L, resp.getId());
        verify(repository).deleteById(3L);
    }

    @Test
    void testDeleteApplicationUnauthorizedForDifferentUser() {
        // Arrange
        CardApplicationEntity app = new CardApplicationEntity();
        app.setId(4L);
        app.setUserId(99L); // different user
        app.setStatus(CardApplicationStatus.PENDING); 

        when(repository.findById(4L)).thenReturn(Optional.of(app));

        // Act / Assert
        assertThrows(UnauthorizedApplicationActionException.class,
                () -> cardApplicationService.deleteApplication(4L));
        verify(repository, never()).deleteById(anyLong());
    }

    @Test
    void testDeleteApplicationWhenNotFound() {
        when(repository.findById(111L)).thenReturn(Optional.empty());
        assertThrows(CardApplicationNotFoundException.class,
                () -> cardApplicationService.deleteApplication(111L));
    }
}
