package com.ccms.portal.controller;

import com.ccms.portal.dto.response.MerchantResponse;
import com.ccms.portal.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class MerchantController {
    
    private final MerchantService merchantService;
    
    /**
     * Get all merchants
     * GET /api/merchants
     */
    @GetMapping
    public ResponseEntity<List<MerchantResponse>> getAllMerchants() {
        log.info("GET /api/merchants - Fetching all merchants");
        
        try {
            List<MerchantResponse> merchants = merchantService.getAllMerchants();
            log.info("Successfully fetched {} merchants", merchants.size());
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            log.error("Error fetching merchants: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get merchants by category
     * GET /api/merchants/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MerchantResponse>> getMerchantsByCategory(@PathVariable String category) {
        log.info("GET /api/merchants/category/{} - Fetching merchants by category", category);
        
        try {
            List<MerchantResponse> merchants = merchantService.getMerchantsByCategory(category);
            log.info("Successfully fetched {} merchants for category: {}", merchants.size(), category);
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            log.error("Error fetching merchants by category {}: {}", category, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Search merchants by name
     * GET /api/merchants/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<MerchantResponse>> searchMerchants(@RequestParam String name) {
        log.info("GET /api/merchants/search?name={} - Searching merchants", name);
        
        try {
            List<MerchantResponse> merchants = merchantService.searchMerchants(name);
            log.info("Successfully found {} merchants matching: {}", merchants.size(), name);
            return ResponseEntity.ok(merchants);
        } catch (Exception e) {
            log.error("Error searching merchants with name {}: {}", name, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
