package com.FinTrace.smartWallet.CustomerService.console;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Scanner;

public abstract class BaseConsole {
    protected final Scanner scanner;
    protected final ObjectMapper objectMapper;

    protected BaseConsole(ObjectMapper objectMapper) {
        this.scanner = new Scanner(System.in);
        this.objectMapper = objectMapper;
    }
}
