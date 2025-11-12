package com.ccms.portal.controller;

import com.ccms.portal.dto.request.PaymentRequest;
import com.ccms.portal.dto.response.StatementResponse;
import com.ccms.portal.service.CreditCardStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/statements")
@RequiredArgsConstructor
public class CreditCardStatementController {

  private final CreditCardStatementService statementService;

  @GetMapping("/card/{cardId}/current")
  public ResponseEntity<StatementResponse> getCurrentStatement(@PathVariable Long cardId) {
    StatementResponse statement = statementService.getCurrentStatement(cardId);
    return ResponseEntity.ok(statement);
  }

  @GetMapping("/card/{cardId}/history")
  public ResponseEntity<List<StatementResponse>> getStatementHistory(@PathVariable Long cardId) {
    List<StatementResponse> statements = statementService.getStatementHistory(cardId);
    return ResponseEntity.ok(statements);
  }

  @PostMapping("/payment")
  public ResponseEntity<StatementResponse> makePayment(@Valid @RequestBody PaymentRequest paymentRequest) {
    StatementResponse statement = statementService.makePayment(paymentRequest);
    return ResponseEntity.ok(statement);
  }

  @PostMapping("/card/{cardId}/create")
  public ResponseEntity<StatementResponse> createCurrentStatement(@PathVariable Long cardId) {
    StatementResponse statement = statementService.getCurrentStatement(cardId);
    return ResponseEntity.ok(statement);
  }

  @GetMapping("/card/{cardId}/debug")
  public ResponseEntity<String> debugStatement(@PathVariable Long cardId) {
    try {
      // Force create statement with debug info
      statementService.createCurrentStatement(cardId);
      return ResponseEntity.ok("Statement created/updated successfully");
    } catch (Exception e) {
      return ResponseEntity.ok("Error: " + e.getMessage());
    }
  }
}
