package com.FinTrace.smartWallet.service;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.exception.DuplicateCustomerException;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.CustomerType;
import com.FinTrace.smartWallet.CustomerService.model.LegalCustomer;
import com.FinTrace.smartWallet.CustomerService.model.RealCustomer;
import com.FinTrace.smartWallet.CustomerService.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith( MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void addValidRealCustomer(){
        RealCustomer customer = new RealCustomer();
        customer.setName("Ed");
        customer.setFamily("Din");
        customer.setPhoneNumber("1234567890");
        customer.setEmail("a@gmail.com");
        when(customerDao.save(any())).thenReturn(customer);

        RealCustomer savedCustomer = (RealCustomer) customerService.addCustomer(customer);

        assertNotNull(savedCustomer);
        assertEquals("Ed", savedCustomer.getName());
        assertEquals("Din", savedCustomer.getFamily());
        assertEquals("1234567890", savedCustomer.getPhoneNumber());
        assertEquals("a@gmail.com", savedCustomer.getEmail());
        assertEquals(CustomerType.REAL, savedCustomer.getType());
    }

    @Test
    void addValidLegalCustomer() {
        Customer customer = new LegalCustomer();
        customer.setName("FinTrace");
        customer.setPhoneNumber("1234567890");
        customer.setEmail("b@gmail.com");
        when(customerDao.save(any())).thenReturn(customer);

        LegalCustomer savedCustomer = (LegalCustomer) customerService.addCustomer(customer);

        assertNotNull(savedCustomer);
        assertEquals("FinTrace", savedCustomer.getName());
        assertEquals("1234567890", savedCustomer.getPhoneNumber());
        assertEquals("b@gmail.com", savedCustomer.getEmail());
        assertEquals(CustomerType.LEGAL, savedCustomer.getType());
    }

    @Test
    void UpdateCustomer(){
        RealCustomer customer = new RealCustomer();
        customer.setId(1L);
        customer.setName("Ed");
        customer.setFamily("Din");
        customer.setPhoneNumber("1234567890");
        customer.setEmail("a@gmail.com" );

        when(customerDao.existsById(1L)).thenReturn(true);
        when(customerDao.save(any())).thenReturn(customer);

        customer.setName("Edd");
        customer.setFamily("Dinn");
        customer.setPhoneNumber("0987654321");
        customer.setEmail("b@gmail.com");

        Customer result = customerService.updateCustomer(1L, customer);
        assertNotNull(result);
        assertEquals("Edd", ((RealCustomer) result).getName());
        assertEquals("Dinn", ((RealCustomer) result).getFamily());
        assertEquals("0987654321", ((RealCustomer) result).getPhoneNumber());
        assertEquals("b@gmail.com", ((RealCustomer) result).getEmail());
    }

    @Test
    void deleteCustomer(){
        Long customerId = 1L;
        when(customerDao.existsById(customerId)).thenReturn(true);

        customerService.deleteCustomer(customerId);

        verify(customerDao).deleteById(customerId);
    }

    @Test
    void deleteNonExistentCustomer() {
        Long customerId = 1L;
        when(customerDao.existsById(customerId)).thenReturn(false);

        try {
            customerService.deleteCustomer(customerId);
        } catch (Exception e) {
            assertEquals("Customer not found with id: " + customerId, e.getMessage());
        }
    }

    @Test
    void addDuplicateRealCustomer() {
        RealCustomer customer = new RealCustomer();
        customer.setName("Ann");
        customer.setFamily("Dn");
        customer.setPhoneNumber("1234567890");
        when(customerDao.realCustomerExists(customer.getName(), customer.getFamily())).thenReturn(true);

        assertThrows(DuplicateCustomerException.class, () -> {
            customerService.addCustomer(customer);
        });
    }

    @Test
    void getCustomerById() {
        Long customerId = 1L;
        RealCustomer customer = new RealCustomer();
        customer.setId(customerId);
        customer.setName("Pdr");
        customer.setFamily("Dn");
        customer.setPhoneNumber("1234567890");
        when(customerDao.findById(customerId)).thenReturn(java.util.Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(customerId);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("Pdr", ((RealCustomer) result.get()).getName());
    }

    @Test
    void getCustomerByIdNotFound() {
        Long customerId = 1L;
        when(customerDao.findById(customerId)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.getCustomerById(customerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByName() {
        String name = "Ali";
        RealCustomer customer = new RealCustomer();
        customer.setName(name);
        customer.setFamily("Rn");
        customer.setPhoneNumber("1234567890");

        when(customerDao.findByNameIgnoreCase(name)).thenReturn(List.of(customer));

        List<Customer> result = customerService.findByName(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ali", ((RealCustomer) result.get(0)).getName());
    }

    @Test
    void findByNameNotFound() {
        String name = "Non Existent";
        when(customerDao.findByNameIgnoreCase(name)).thenReturn(List.of());

        try {
            customerService.findByName(name);
        } catch (Exception e) {
            assertEquals("Customer not found with name: " + name, e.getMessage());
        }
    }

    @Test
    void findByNameIgnoreCase() {
        String name = "ryn";
        RealCustomer customer = new RealCustomer();
        customer.setName("ryn");
        customer.setFamily("Rn");
        customer.setPhoneNumber("1234567890");

        when(customerDao.findByNameIgnoreCase(name)).thenReturn(List.of(customer));

        List<Customer> result = customerService.findByName(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ryn", ((RealCustomer) result.get(0)).getName());
    }

    // Test for multiple customers with the same name
    // This test checks if the service can handle multiple customers with the same name correctly.
    // It ensures that the service returns all customers with the specified name.
    // This is important for scenarios where multiple customers might share the same name, such as in large databases or systems with many users.
    @Test
    void findByNameTwoCustomers() {
        String name = "Ryn";
        RealCustomer customer1 = new RealCustomer();
        customer1.setName(name);
        customer1.setFamily("Rn");
        customer1.setPhoneNumber("1234567890");

        LegalCustomer customer2 = new LegalCustomer();
        customer2.setName(name);
        customer2.setPhoneNumber("0987654321");
        customer2.setEmail("c@gmail.com");

        when(customerDao.findByNameIgnoreCase(name)).thenReturn(List.of(customer1, customer2));

        List<Customer> result = customerService.findByName(name);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
