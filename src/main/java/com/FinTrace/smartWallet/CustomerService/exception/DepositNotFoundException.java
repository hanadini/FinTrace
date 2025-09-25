package com.FinTrace.smartWallet.CustomerService.exception;

public class DepositNotFoundException extends RuntimeException {
    public DepositNotFoundException(String message) {
        super(message);
    }

    public DepositNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
