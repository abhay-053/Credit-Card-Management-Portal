package com.ccms.portal.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {
    private String name;
    private String phoneNumber;
    private String address;
    private Double annualIncome;
}
