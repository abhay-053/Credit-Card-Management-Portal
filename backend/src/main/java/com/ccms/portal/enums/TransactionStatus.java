package com.ccms.portal.enums;

import lombok.Getter;

@Getter
public enum TransactionStatus {
  PENDING,
  COMPLETED,
  FAILED,
  REFUNDED,
  BNPL_ACTIVE,
  BNPL_COMPLETED,
  BNPL_DEFAULTED
}
