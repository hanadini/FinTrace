package com.FinTrace.customerSystem.console;

import com.FinTrace.customerSystem.dto.DepositDto;
import com.FinTrace.customerSystem.exception.CustomerNotFoundException;
import com.FinTrace.customerSystem.exception.DuplicateCustomerException;
import com.FinTrace.customerSystem.exception.InsufficientFundsException;
import com.FinTrace.customerSystem.facade.DepositFacade;
import com.FinTrace.customerSystem.dto.Currency;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("console")
public class DepositConsole extends BaseConsole {
    private final DepositFacade depositFacade;

    @Autowired
    public DepositConsole(DepositFacade depositFacade,
                          ObjectMapper objectMapper) {
        super(objectMapper);
        this.depositFacade = depositFacade;
    }


    public void start() {
        while (true) {
            System.out.println("\nDeposit Management System");
            System.out.println("1. Create deposit for customer");
            System.out.println("2. Deposit amount to deposit");
            System.out.println("3. Withdraw amount from deposit");
            System.out.println("4. Show deposits by customer ID");
            System.out.println("5. Transfer amount between deposits");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            try {
                switch (choice) {
                    case 1 -> createDepositForCustomer();
                    case 2 -> depositMoneyToDeposit();
                    case 3 -> withdrawMoneyFromDeposit();
                    case 4 -> listDepositsForCustomer();
                    case 5 -> TransferAmountBetweenDeposits();
                    case 0 -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (CustomerNotFoundException | DuplicateCustomerException e) {
                System.out.println(e.getMessage());
            } catch (ConstraintViolationException ex) {
                String message = ex.getConstraintViolations().stream()
                        .map(violation -> "Property: " + violation.getPropertyPath() + ", Message: " + violation.getMessage())
                        .collect(Collectors.joining("\n "));
                System.out.println("Validation error: " + message);
            } catch (Exception e) {
                System.out.println("An unexpected error occurred!");
            }

        }
    }

    private void TransferAmountBetweenDeposits() {
        System.out.print("Enter source deposit ID: ");
        Long fromDepositId = Long.parseLong(scanner.nextLine());
        System.out.print("Enter target deposit ID: ");
        Long toDepositId = Long.parseLong(scanner.nextLine());
        System.out.print("Enter amount to transfer: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());

        try {
            depositFacade.transferAmount(fromDepositId, toDepositId, amount);
            System.out.println("Transfer completed successfully.");
        } catch (CustomerNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    private void listDepositsForCustomer() {
        System.out.print("Enter customer ID to list deposits: ");
        Long customerId = Long.parseLong(scanner.nextLine());
        try {
            List<DepositDto> deposits = depositFacade.getDepositsByCustomerId(customerId);
            if (deposits.isEmpty()) {
                System.out.println("No deposits found for customer ID: " + customerId);
            } else {
                deposits.forEach(deposit -> {
                    try {
                        System.out.println(objectMapper.writeValueAsString(deposit));
                    } catch (JsonProcessingException e) {
                        System.err.println("Error converting deposit to JSON: " + e.getMessage());
                    }
                });
            }
        } catch (CustomerNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void withdrawMoneyFromDeposit() {
        System.out.print("Enter deposit ID to withdraw money: ");
        Long depositId = Long.parseLong(scanner.nextLine());
        System.out.print("Enter amount to withdraw: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        try {
            depositFacade.withdrawAmount(depositId, amount);
            System.out.println("Money withdrawn successfully.");
        } catch (CustomerNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());
        }
    }

    private void depositMoneyToDeposit() {
        System.out.print("Enter deposit ID to deposit money: ");
        Long depositId = Long.parseLong(scanner.nextLine());
        System.out.print("Enter amount to deposit: ");
        BigDecimal amount = new BigDecimal(scanner.nextLine());
        try {
            depositFacade.depositAmount(depositId, amount);
            System.out.println("Money deposited successfully.");
        } catch (CustomerNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createDepositForCustomer() {
        System.out.print("Enter deposit currency (e.g., USD, EUR): ");
        String currencyStr = scanner.nextLine().toUpperCase();
        Currency currency = Currency.valueOf(currencyStr);

        System.out.print("Enter customer ID to create deposit: ");
        Long customerId = Long.parseLong(scanner.nextLine());
        try {
            depositFacade.addDeposit(customerId, currency);
            System.out.println("Deposit created successfully.");
        } catch (CustomerNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

}
