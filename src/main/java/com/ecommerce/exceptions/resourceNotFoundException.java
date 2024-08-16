package com.ecommerce.exceptions;

public class resourceNotFoundException extends RuntimeException {

    public resourceNotFoundException(String message){
        super(message);
    }
}
