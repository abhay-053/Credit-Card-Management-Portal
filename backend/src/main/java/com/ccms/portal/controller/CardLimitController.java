package com.ccms.portal.controller;

import com.ccms.portal.dto.request.UpdateCardLimitRequest;
import com.ccms.portal.dto.response.CardLimitResponse;
import com.ccms.portal.service.CardLimitService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cards")
public class CardLimitController {
    @Autowired
    CardLimitService cardLimitService;

    @PutMapping("/{cardId}/limit")
    public ResponseEntity<CardLimitResponse> updateLimit(
            @PathVariable Long cardId,
            @Valid @RequestBody UpdateCardLimitRequest request,
            Authentication authentication) {

        String callerUsername = authentication == null ? null : authentication.getName();
        CardLimitResponse response = cardLimitService.updateLimit(cardId, request, callerUsername);
        return ResponseEntity.ok(response);
    }
}
