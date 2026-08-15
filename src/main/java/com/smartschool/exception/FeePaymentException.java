package com.smartschool.exception;

public class FeePaymentException extends Exception {
    public FeePaymentException(String message) { super(message); }
    public FeePaymentException(String message, Throwable cause) { super(message, cause); }
}
