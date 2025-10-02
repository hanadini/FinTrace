package com.FinTrace.customerSystem.console;

import com.FinTrace.customerSystem.dto.CustomerDto;
import com.FinTrace.customerSystem.dto.FileType;
import com.FinTrace.customerSystem.dto.LegalCustomerDto;
import com.FinTrace.customerSystem.dto.RealCustomerDto;
import com.FinTrace.customerSystem.exception.CustomerNotFoundException;
import com.FinTrace.customerSystem.exception.DuplicateCustomerException;
import com.FinTrace.customerSystem.facade.CustomerFacade;
import com.FinTrace.customerSystem.dto.CustomerType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Profile("console")
public class CustomerConsole extends BaseConsole {
    private final CustomerFacade customerFacade;
    @Autowired
    public CustomerConsole(CustomerFacade customerFacade,
                           ObjectMapper objectMapper) {
        super(objectMapper);
        this.customerFacade = customerFacade;
    }


    public void start() {
        while (true) {
            System.out.println("\nCustomer Management System");
            System.out.println("1. View all customers");
            System.out.println("2. View customer by ID");
            System.out.println("3. Add a new customer");
            System.out.println("4. Update a customer");
            System.out.println("5. Delete a customer");
            System.out.println("6. Find customers by name");
            System.out.println("7. Export customers to file");
            System.out.println("8. Import customers from file");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");

            int choice = Integer.parseInt(scanner.nextLine());

            try {
                switch (choice) {
                    case 1 -> viewAllCustomers();
                    case 2 -> viewCustomerById();
                    case 3 -> addCustomer();
                    case 4 -> updateCustomer();
                    case 5 -> deleteCustomer();
                    case 6 -> findCustomersByName();
                    case 7 -> exportCustomersToFile();
                    case 8 -> importCustomersFromFile();
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

    private void exportCustomersToFile() {
        System.out.print("Enter file name to export: ");
        String name = scanner.nextLine();
        System.out.print("Enter file type (JSON/BINARY): ");
        String type = scanner.nextLine().toUpperCase();
        FileType fileType = null;
        String fileName = "";
        if ("JSON".equals(type)) {
            fileType = FileType.JSON;
            fileName = name + ".json";
        } else if ("BINARY".equals(type)) {
            fileType = FileType.BINARY;
            fileName = name + ".dat";
        } else {
            System.out.println("Invalid file type. Please choose JSON or BINARY.");
        }
        try {
            byte[] fileContent = customerFacade.exportCustomers(fileType);
            try (FileOutputStream fos = new FileOutputStream(fileName)) {
                fos.write(fileContent);
                System.out.println("Customers exported successfully");
            } catch (IOException e) {
                System.err.println("Error writing to file: " + e.getMessage());

            }
        } catch (IOException e) {
            System.err.println("Error exporting customers: " + e.getMessage());
        }
    }

    private void importCustomersFromFile() {
        System.out.print("Enter file name to import: ");
        String name = scanner.nextLine();
        System.out.print("Enter file type (JSON/BINARY): ");
        String type = scanner.nextLine().toUpperCase();
        FileType fileType = null;
        String fileName = "";
        if ("JSON".equals(type)) {
            fileType = FileType.JSON;
            fileName = name + ".json";
        } else if ("BINARY".equals(type)) {
            fileType = FileType.BINARY;
            fileName = name + ".dat";
        } else {
            System.out.println("Invalid file type. Please choose JSON or BINARY.");
        }
        try {
            byte[] fileContent = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(fileName));
            customerFacade.importCustomers(fileContent, fileType);
            System.out.println("Customers imported successfully");
        } catch (IOException e) {
            System.err.println("Error reading from file: " + e.getMessage());
        } catch (DuplicateCustomerException e) {
            System.err.println("Error importing customers: " + e.getMessage());
        }
    }

    private void findCustomersByName() {
        System.out.print("Enter customer name to search: ");
        String name = scanner.nextLine();
        List<CustomerDto> customers = customerFacade.getCustomersByName(name);
        customers.forEach(this::printJsonObject);
    }

    private void viewAllCustomers() {
        List<CustomerDto> customers = customerFacade.getAllCustomers();
        customers.forEach(this::printJsonObject);
    }

    private void printJsonObject(CustomerDto customer) {
        try {
            System.out.println(customer != null ?
                    objectMapper.writeValueAsString(customer) : "Customer not found.");
        } catch (JsonProcessingException e) {
            System.err.println("Error converting customer to JSON: " + e.getMessage());
        }
    }

    private void viewCustomerById() {
        System.out.print("Enter customer ID: ");
        Long id = Long.parseLong(scanner.nextLine());
        CustomerDto customer = customerFacade.getCustomerById(id);
        printJsonObject(customer);
    }

    private void addCustomer() {
        System.out.print("Enter customer type (REAL/LEGAL): ");
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
            System.out.print("Enter fax: ");
            legalCustomer.setFax(scanner.nextLine());
            legalCustomer.setType(CustomerType.LEGAL);
            CustomerDto addedCustomer = customerFacade.addCustomer(legalCustomer);
            System.out.println("Customer added: ");
            printJsonObject(addedCustomer);
        } else {
            System.out.println("Invalid customer type.");
        }
    }

    private void updateCustomer() {
        System.out.print("Enter customer ID to update: ");
        Long id = Long.parseLong(scanner.nextLine());
        CustomerDto customer = customerFacade.getCustomerById(id);
        if (customer == null) {
            System.out.println("Customer not found.");
        } else if (CustomerType.REAL.equals(customer.getType())) {
            RealCustomerDto realCustomer = (RealCustomerDto) customer;
            System.out.print("Enter name: ");
            realCustomer.setName(scanner.nextLine());
            System.out.print("Enter phone number: ");
            realCustomer.setPhoneNumber(scanner.nextLine());
            System.out.print("Enter family: ");
            realCustomer.setFamily(scanner.nextLine());
            realCustomer.setType(CustomerType.REAL);
            CustomerDto updatedCustomer = customerFacade.updateCustomer(id, realCustomer);
            System.out.println("Customer updated: ");
            printJsonObject(updatedCustomer);
        } else if (CustomerType.LEGAL.equals(customer.getType())) {
            LegalCustomerDto legalCustomer = (LegalCustomerDto) customer;
            System.out.print("Enter name: ");
            legalCustomer.setName(scanner.nextLine());
            System.out.print("Enter phone number: ");
            legalCustomer.setPhoneNumber(scanner.nextLine());
            System.out.print("Enter fax: ");
            legalCustomer.setFax(scanner.nextLine());
            legalCustomer.setType(CustomerType.LEGAL);
            CustomerDto updatedCustomer = customerFacade.updateCustomer(id, legalCustomer);
            System.out.println("Customer updated: ");
            printJsonObject(updatedCustomer);
        }
    }

    private void deleteCustomer() {
        System.out.print("Enter customer ID to delete: ");
        Long id = Long.parseLong(scanner.nextLine());
        customerFacade.deleteCustomer(id);
        System.out.println("Customer deleted successfully.");
    }
}