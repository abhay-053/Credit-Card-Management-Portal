package com.ccms.portal.controller;

import com.ccms.portal.dto.request.CardApplicationRequest;
import com.ccms.portal.dto.request.UpdateStatusRequest;
import com.ccms.portal.dto.response.CardApplicationResponse;
import com.ccms.portal.service.CardApplicationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("card/application")
public class CardApplicationController {

    private static final Logger logger = LoggerFactory.getLogger(CardApplicationController.class);

    @Autowired
    private CardApplicationService cardApplicationService;

    @PostMapping
    public ResponseEntity<?> applyForCard(@Valid @RequestBody CardApplicationRequest application) {
        logger.info("Received request to apply for card: {}", application);
        CardApplicationResponse savedApplication = cardApplicationService.apply(application);
        logger.info("Card application created successfully with ID: {}", savedApplication.getId());
        return new ResponseEntity<>(savedApplication, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CardApplicationResponse>> getApplications() {
        logger.info("Fetching all card applications");
        List<CardApplicationResponse> savedApplications = cardApplicationService.getApplications();
        logger.info("Fetched {} applications", savedApplications.size());
        return new ResponseEntity<>(savedApplications, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateApplication(@PathVariable Long id, @RequestBody UpdateStatusRequest request) {
        logger.info("Updating application with ID: {} to status: {}", id, request.getStatus());
        CardApplicationResponse updatedApplication = cardApplicationService.updateStatus(id, request.getStatus());
        logger.info("Application with ID: {} updated successfully", id);
        return ResponseEntity.ok(updatedApplication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApplication(@PathVariable Long id) {
        logger.warn("Deleting application with ID: {}", id);
        CardApplicationResponse response = cardApplicationService.deleteApplication(id);
        logger.info("Application with ID: {} deleted successfully", id);
        return ResponseEntity.ok(response);
    }
}
