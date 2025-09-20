package com.FinTrace.smartWallet.CustomerService.console;

import com.FinTrace.smartWallet.CustomerService.dto.CustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.LegalCustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.RealCustomerDto;
import com.FinTrace.smartWallet.CustomerService.facade.CustomerFacade;
import com.FinTrace.smartWallet.CustomerService.model.CustomerType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
@Profile("console")
public class ConsoleInterface {

    private final CustomerFacade customerFacade;
    private final Scanner scanner;
    private final ObjectMapper objectMapper;

    @Autowired
    public ConsoleInterface(CustomerFacade customerFacade, ObjectMapper ObjectMapper) {
        this.customerFacade = customerFacade;
        this.scanner = new Scanner(System.in);
        this.objectMapper = ObjectMapper;
    }

    public void start() {
        while (true) {
            System.out.println("\nCustomer Management System");
            System.out.println("1. View all customers");
            System.out.println("2. View customer by ID");
            System.out.println("3. Add a new customer");
            System.out.println("4. Update a customer");
            System.out.println("5. Delete a customer");
            System.out.println("6. Find a customer by Name");
            System.out.println("0. Exit");
            System.out.println("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            switch (choice) {
                case 1 -> viewAllCustomers();
                case 2 -> viewCustomerById();
                case 3 -> addCustomer();
                case 4 -> updateCustomer();
                case 5 -> deleteCustomer();
                case 6 -> findCustomerByName();
                case 0 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void findCustomerByName() {
        System.out.println("Enter customer name: ");
        String name = scanner.nextLine();
        List<CustomerDto> customers = customerFacade.getCustomersByName(name);
        if (customers.isEmpty()) {
            System.out.println("No customers found with the name: " + name);
        } else {
            customers.forEach(this::printJsonObject);
        }
    }

    private void viewAllCustomers() {
        List<CustomerDto> customers = customerFacade.getAllCustomers();
        customers.forEach(this::printJsonObject);
    }

    private void printJsonObject(CustomerDto customer) {
        try {
            System.out.println(customer != null ? objectMapper.writeValueAsString(customer) : "Customer not found");
        } catch (JsonProcessingException e) {
            System.err.println("Error converting to JSON: " + e.getMessage());
        }
    }

    private void viewCustomerById() {
        System.out.println("Enter customer ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        CustomerDto customer = customerFacade.getCustomerById(id);
        printJsonObject(customer);
    }

    private void addCustomer() {
        System.out.println("Enter customer type (REAL/LEGAL): ");
        String type = scanner.nextLine().toUpperCase();

        if ("REAL".equals(type)) {
            RealCustomerDto realCustomer = new RealCustomerDto();
            System.out.print("Enter name: ");
            realCustomer.setName(scanner.nextLine());
            System.out.print("Enter phone number: ");
            realCustomer.setPhoneNumber(scanner.nextLine());
            System.out.print("Enter family: ");
            realCustomer.setFamily(scanner.nextLine());
            realCustomer.setType(CustomerType.REAL);
            CustomerDto addedCustomer = customerFacade.addCustomer(realCustomer);
            System.out.println("Customer added: ");
            printJsonObject(addedCustomer);
        } else if ("LEGAL".equals(type)) {
            LegalCustomerDto legalCustomer = new LegalCustomerDto();
            System.out.print("Enter name: ");
            legalCustomer.setName(scanner.nextLine());
            System.out.print("Enter phone number: ");
            legalCustomer.setPhoneNumber(scanner.nextLine());
            System.out.print("Enter email: ");
            legalCustomer.setEmail(scanner.nextLine());
            legalCustomer.setType(CustomerType.LEGAL);
            CustomerDto addedCustomer = customerFacade.addCustomer(legalCustomer);
            System.out.println("Customer added: ");
            printJsonObject(addedCustomer);
        } else {
            System.out.println("Invalid customer type. Must be REAL or LEGAL.");
        }
    }

    private void updateCustomer() {
        System.out.println("Enter customer ID to update: ");
        Long id = Long.parseLong(scanner.nextLine());
        CustomerDto existingCustomer = customerFacade.getCustomerById(id);

        if (existingCustomer == null) {
            System.out.println("Customer not found.");
            return;
        }

        System.out.println("Existing customer details: ");
        printJsonObject(existingCustomer);

        if (existingCustomer instanceof RealCustomerDto) {
            RealCustomerDto realCustomer = (RealCustomerDto) existingCustomer;
            System.out.print("Enter new name (current: " + realCustomer.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.isBlank()) realCustomer.setName(name);

            System.out.print("Enter new phone number (current: " + realCustomer.getPhoneNumber() + "): ");
            String phoneNumber = scanner.nextLine();
            if (!phoneNumber.isBlank()) realCustomer.setPhoneNumber(phoneNumber);

            System.out.print("Enter new family (current: " + realCustomer.getFamily() + "): ");
            String family = scanner.nextLine();
            if (!family.isBlank()) realCustomer.setFamily(family);

            CustomerDto updatedCustomer = customerFacade.updateCustomer(id, realCustomer);
            System.out.println("Customer updated: ");
            printJsonObject(updatedCustomer);

        } else if (existingCustomer instanceof LegalCustomerDto) {
            LegalCustomerDto legalCustomer = (LegalCustomerDto) existingCustomer;
            System.out.print("Enter new name (current: " + legalCustomer.getName() + "): ");
            String name = scanner.nextLine();
            if (!name.isBlank()) legalCustomer.setName(name);

            System.out.print("Enter new phone number (current: " + legalCustomer.getPhoneNumber() + "): ");
            String phoneNumber = scanner.nextLine();
            if (!phoneNumber.isBlank()) legalCustomer.setPhoneNumber(phoneNumber);

            System.out.print("Enter new email (current: " + legalCustomer.getEmail() + "): ");
            String email = scanner.nextLine();
            if (!email.isBlank()) legalCustomer.setEmail(email);

            CustomerDto updatedCustomer = customerFacade.updateCustomer(id, legalCustomer);
            System.out.println("Customer updated: ");
            printJsonObject(updatedCustomer);
        } else {
            System.out.println("Unknown customer type.");
        }
    }

    private void deleteCustomer() {
        System.out.println("Enter customer ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine());
        boolean deleted = customerFacade.deleteCustomer(id);
        System.out.println(deleted ? "Customer deleted successfully." : "Customer not found.");
    }
}
