package com.FinTrace.customerSystem.console;

import com.FinTrace.customerSystem.exception.CustomerNotFoundException;
import com.FinTrace.customerSystem.exception.DuplicateCustomerException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.stream.Collectors;

@Component
@Profile("console")
public class MainConsoleInterface {
    private final CustomerConsole customerConsole;
    private final DepositConsole depositConsole;
    private final Scanner scanner;

    @Autowired
    public MainConsoleInterface(CustomerConsole customerConsole,
                                DepositConsole depositConsole) {
        this.customerConsole = customerConsole;
        this.depositConsole = depositConsole;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            System.out.println("\nBanking System");
            System.out.println("1. Customer Menu");
            System.out.println("2. Deposit Menu");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            try {
                switch (choice) {
                    case 1 -> customerConsole.start();
                    case 2 -> depositConsole.start();
                    case 0 -> {
                        System.out.println("Exiting...");
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

}