package com.ccms.portal.exception;

public class CreditCardNotFoundException extends RuntimeException{
    public CreditCardNotFoundException(String message){
        super(message);
    }
}
