package com.FinTrace.customerSystem.exception;

public class DepositNotFoundException extends RuntimeException  {
    public DepositNotFoundException(String message) {
        super(message);
    }

    public DepositNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
