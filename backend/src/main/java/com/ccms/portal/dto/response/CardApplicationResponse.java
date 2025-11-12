package com.ccms.portal.dto.response;

import com.ccms.portal.entity.CardApplicationEntity;
import com.ccms.portal.enums.CardApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardApplicationResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String cardTypeName;
    private String cardNetworkType;
    private Long requestedLimit;
    private LocalDateTime applicationDate;
    private String status;

    private String reviewerName;
    private LocalDateTime reviewDate;
    public CardApplicationResponse() {
        // no-argument constructor
    }

    public CardApplicationResponse(CardApplicationEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.userName = entity.getUser() != null ? entity.getUser().getName() : null;
        this.cardTypeName = entity.getCardType() != null ? entity.getCardType().getName() : null;
        this.cardNetworkType = entity.getCardType() != null ? entity.getCardType().getNetworkType().name() : null;
        this.requestedLimit = entity.getRequestedLimit();
        this.applicationDate = entity.getApplicationDate();
        this.status = entity.getStatus().name();
        this.reviewerName = entity.getReviewer() != null ? entity.getReviewer().getName() : null;
        this.reviewDate = entity.getReviewDate();
    }
}


