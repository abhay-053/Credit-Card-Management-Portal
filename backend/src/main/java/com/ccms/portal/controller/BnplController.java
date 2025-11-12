package com.ccms.portal.controller;

import com.ccms.portal.dto.request.BnplPaymentRequest;
import com.ccms.portal.dto.response.BnplOverviewResponse;
import com.ccms.portal.service.BnplService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/bnpl")
@RequiredArgsConstructor
public class BnplController {

  private final BnplService bnplService;

  @GetMapping("/overview/card/{cardId}")
  public ResponseEntity<BnplOverviewResponse> getBnplOverview(@PathVariable Long cardId) {
    BnplOverviewResponse overview = bnplService.getBnplOverview(cardId);
    return ResponseEntity.ok(overview);
  }

  @PostMapping("/payment")
  public ResponseEntity<String> makeBnplPayment(@Valid @RequestBody BnplPaymentRequest request) {
    bnplService.makeBnplPayment(request);
    return ResponseEntity.ok("BNPL payment processed successfully");
  }
}
