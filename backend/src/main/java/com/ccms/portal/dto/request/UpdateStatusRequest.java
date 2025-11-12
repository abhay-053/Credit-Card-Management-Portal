package com.ccms.portal.dto.request;

import com.ccms.portal.enums.CardApplicationStatus;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    private CardApplicationStatus status;
}
