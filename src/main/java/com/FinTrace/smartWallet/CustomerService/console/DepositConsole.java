package com.FinTrace.smartWallet.CustomerService.console;

import com.FinTrace.smartWallet.CustomerService.dto.DepositDto;
import com.FinTrace.smartWallet.CustomerService.exception.CustomerNotFoundException;
import com.FinTrace.smartWallet.CustomerService.exception.DuplicateCustomerException;
import com.FinTrace.smartWallet.CustomerService.exception.InsufficientFundException;
import com.FinTrace.smartWallet.CustomerService.facade.DepositFacade;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

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
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            try {
                switch (choice) {
                    case 1 -> createDepositForCustomer();
                    case 2 -> depositMoneyToDeposit();
                    case 3 -> withdrawMoneyFromDeposit();
                    case 4 -> listDepositsForCustomer();
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
        Double amount = Double.parseDouble(scanner.nextLine());
        try {
            depositFacade.withdrawAmount(depositId, amount);
            System.out.println("Money withdrawn successfully.");
        } catch (CustomerNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (InsufficientFundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void depositMoneyToDeposit() {
        System.out.print("Enter deposit ID to deposit money: ");
        Long depositId = Long.parseLong(scanner.nextLine());
        System.out.print("Enter amount to deposit: ");
        Double amount = Double.parseDouble(scanner.nextLine());
        try {
            depositFacade.depositAmount(depositId, amount);
            System.out.println("Money deposited successfully.");
        } catch (CustomerNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createDepositForCustomer() {
        System.out.print("Enter customer ID to create deposit: ");
        Long customerId = Long.parseLong(scanner.nextLine());
        try {
            depositFacade.addDeposit(customerId);
            System.out.println("Deposit created successfully.");
        } catch (CustomerNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
