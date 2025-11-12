package com.ccms.portal.service;

import com.ccms.portal.dto.response.MerchantResponse;
import com.ccms.portal.entity.Merchant;
import com.ccms.portal.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MerchantService {
    
    private final MerchantRepository merchantRepository;
    
    /**
     * Get all merchants
     */
    public List<MerchantResponse> getAllMerchants() {
        log.info("Fetching all merchants");
        
        List<Merchant> merchants = merchantRepository.findAllByOrderByNameAsc();
        log.info("Found {} merchants", merchants.size());
        
        return merchants.stream()
                .map(this::mapToMerchantResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get merchants by category
     */
    public List<MerchantResponse> getMerchantsByCategory(String category) {
        log.info("Fetching merchants by category: {}", category);
        
        List<Merchant> merchants = merchantRepository.findByCategoryOrderByNameAsc(category);
        log.info("Found {} merchants for category: {}", merchants.size(), category);
        
        return merchants.stream()
                .map(this::mapToMerchantResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Search merchants by name
     */
    public List<MerchantResponse> searchMerchants(String name) {
        log.info("Searching merchants by name: {}", name);
        
        List<Merchant> merchants = merchantRepository.findByNameContainingIgnoreCaseOrderByNameAsc(name);
        log.info("Found {} merchants matching: {}", merchants.size(), name);
        
        return merchants.stream()
                .map(this::mapToMerchantResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Map Merchant entity to MerchantResponse DTO
     */
    private MerchantResponse mapToMerchantResponse(Merchant merchant) {
        return MerchantResponse.builder()
                .id(merchant.getId())
                .name(merchant.getName())
                .merchantIdentifier(merchant.getMerchantIdentifier())
                .category(merchant.getCategory())
                .createdAt(merchant.getCreatedAt() != null ? 
                    merchant.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null)
                .build();
    }
}
