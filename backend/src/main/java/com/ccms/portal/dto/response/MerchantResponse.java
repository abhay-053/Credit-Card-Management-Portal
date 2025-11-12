package com.ccms.portal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantResponse {
    
    private Long id;
    private String name;
    private String merchantIdentifier;
    private String category;
    private String createdAt;
}
