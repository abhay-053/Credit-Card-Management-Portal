package com.ccms.portal.service;

import com.ccms.portal.dto.request.CardApplicationRequest;
import com.ccms.portal.dto.request.CreateCardRequest;
import com.ccms.portal.dto.response.CardApplicationResponse;
import com.ccms.portal.enums.CardApplicationStatus;
import com.ccms.portal.entity.CardApplicationEntity;
import com.ccms.portal.exception.CardApplicationNotFoundException;
import com.ccms.portal.exception.DuplicateApplicationException;
import com.ccms.portal.exception.UnauthorizedApplicationActionException;
import com.ccms.portal.repository.CardApplicationRepository;
import com.ccms.portal.util.JwtUserDetails;
import com.ccms.portal.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class CardApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(CardApplicationService.class);

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private CardApplicationRepository repository;
    @Autowired
    private CardService cardService;

    public CardApplicationResponse apply(CardApplicationRequest application) {
        JwtUserDetails currentUser = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("User {} is applying for a card of type {}", currentUser.getUserId(), application.getCardTypeId());
        Optional<CardApplicationEntity> existingApplication = repository
                .findByUserIdAndCardTypeId(currentUser.getUserId(), application.getCardTypeId());


        if (existingApplication.isPresent()) {
            logger.warn("User {} already applied for card type {}", currentUser.getUserId(), application.getCardTypeId());
            throw new DuplicateApplicationException("You have already applied for this credit card.");
        }
        CardApplicationEntity applicationEntity = new CardApplicationEntity();
        applicationEntity.setStatus(CardApplicationStatus.PENDING);
        applicationEntity.setCardTypeId(application.getCardTypeId());
        applicationEntity.setRequestedLimit(application.getRequestLimit());
        applicationEntity.setUserId(currentUser.getUserId());

        CardApplicationEntity savedEntity = repository.save(applicationEntity);
        logger.info("Card application saved with ID: {}", savedEntity.getId());

        return new CardApplicationResponse(savedEntity);
    }

    public List<CardApplicationResponse> getApplications() {
        JwtUserDetails currentUser = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Fetching applications for user {} with role {}", currentUser.getUserId(), currentUser.getRole());

        List<CardApplicationEntity> applications = Objects.equals(currentUser.getRole(), "ADMIN")
                ? repository.findAll()
                : repository.findAllByUserId(currentUser.getUserId());

        logger.info("Total applications fetched: {}", applications.size());
        return applications.stream().map(CardApplicationResponse::new).toList();
    }

    public Optional<CardApplicationEntity> getApplicationById(Long id) {
        logger.debug("Fetching application by ID: {}", id);
        return repository.findById(id);
    }

    public CardApplicationResponse updateStatus(Long id, CardApplicationStatus status) {
        JwtUserDetails currentUser = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("User {} attempting to update status of application ID: {} to {}", currentUser.getUserId(), id, status);

        CardApplicationEntity application = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Application with ID {} not found", id);
                    return new CardApplicationNotFoundException("Application with id " + id + " not found");
                });

        if (!Objects.equals(currentUser.getRole(), "ADMIN")) {
            logger.warn("Unauthorized status update attempt by user {}", currentUser.getUserId());
            throw new UnauthorizedApplicationActionException("You are not authorized to update this application");
        }

        CreateCardRequest createCardRequest = new CreateCardRequest();
        createCardRequest.setCardTypeId(application.getCardTypeId());
        createCardRequest.setUserId(application.getUserId());
        createCardRequest.setCreditLimit(application.getRequestedLimit() * 1.0);
        cardService.createCard(createCardRequest);

        application.setReviewDate(LocalDateTime.now());
        application.setReviewerId(currentUser.getUserId());
        application.setStatus(status);

        CardApplicationEntity updatedEntity = repository.save(application);
        logger.info("Application ID: {} updated successfully", id);
        return new CardApplicationResponse(updatedEntity);
    }

    public CardApplicationResponse deleteApplication(Long id) {
        JwtUserDetails currentUser = (JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.warn("User {} attempting to delete application ID: {}", currentUser.getUserId(), id);

        CardApplicationEntity application = repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Application with ID {} not found", id);
                    return new CardApplicationNotFoundException("Card application with id " + id + " not found");
                });

        if (!application.getUserId().equals(currentUser.getUserId())) {
            logger.warn("Unauthorized delete attempt by user {}", currentUser.getUserId());
            throw new UnauthorizedApplicationActionException("You are not authorized to delete this application");
        }

        repository.deleteById(id);
        logger.info("Application ID: {} deleted successfully", id);

        return new CardApplicationResponse(application);
    }
}
