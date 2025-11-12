export const BASE_URL = 'http://localhost:8080/api'; 

export const LOCAL_STORAGE = {
    SELECTED_CARD: 'selectedCard'
}

export const APPLICATION_STATUS = {
    PENDING: 'PENDING',
    ACCEPTED: 'ACCEPTED',
    REJECTED: 'REJECTED'
} as const;

export const APPLICATION_STATUS_LABELS = {
    [APPLICATION_STATUS.PENDING]: 'PENDING',
    [APPLICATION_STATUS.ACCEPTED]: 'APPROVED',
    [APPLICATION_STATUS.REJECTED]: 'REJECTED'
} as const;

export const APPLICATION_ACTIONS = {
    APPROVE: 'ACCEPTED',
    REJECT: 'REJECTED'
} as const;

export const CARD_NETWORK_TYPES = {
    VISA: 'VISA',
    MASTERCARD: 'MASTERCARD',
    AMEX: 'AMEX'
} as const;

export const APPLICATION_MESSAGES = {
    LOADING: 'Loading applications...',
    NO_APPLICATIONS: 'No applications found',
    APPROVED_BY: 'Approved by',
    REJECTED_BY: 'Rejected by',
    ON: 'on'
} as const;

export const USER_ROLE = {
   ADMIN: 'Admin',
   USER: 'User'
} as const;