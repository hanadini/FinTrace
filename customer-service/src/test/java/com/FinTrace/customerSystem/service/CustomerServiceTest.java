package com.FinTrace.customerSystem.service;

import com.FinTrace.customerSystem.dao.CustomerDao;
import com.FinTrace.customerSystem.exception.DuplicateCustomerException;
import com.FinTrace.customerSystem.model.Customer;
import com.FinTrace.customerSystem.model.CustomerType;
import com.FinTrace.customerSystem.model.LegalCustomer;
import com.FinTrace.customerSystem.model.RealCustomer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void addValidRealCustomer() {
        RealCustomer customer = new RealCustomer();
        customer.setName("John Doe");
        customer.setFamily("Doe");
        customer.setPhoneNumber("1234567890");
        when(customerDao.save(any())).thenReturn(customer);

        RealCustomer savedCustomer = (RealCustomer) customerService.addCustomer(customer);

        assertNotNull(savedCustomer);
        assertEquals("John Doe", savedCustomer.getName());
        assertEquals("Doe", savedCustomer.getFamily());
        assertEquals("1234567890", savedCustomer.getPhoneNumber());
        assertEquals(CustomerType.REAL, savedCustomer.getType());
    }

    @Test
    void addValidLegalCustomer() {
        LegalCustomer customer = new LegalCustomer();
        customer.setName("Company Inc.");
        customer.setPhoneNumber("0987654321");
        customer.setFax("123456789");
        when(customerDao.save(any())).thenReturn(customer);

        LegalCustomer savedCustomer = (LegalCustomer) customerService.addCustomer(customer);

        assertNotNull(savedCustomer);
        assertEquals("Company Inc.", savedCustomer.getName());
        assertEquals("0987654321", savedCustomer.getPhoneNumber());
        assertEquals("123456789", savedCustomer.getFax());
        assertEquals(CustomerType.LEGAL, savedCustomer.getType());
    }

    @Test
    void updateCustomer() {
        RealCustomer customer = new RealCustomer();
        customer.setId(1L);
        customer.setName("Jane Doe");
        customer.setFamily("Doe");
        customer.setPhoneNumber("1234567890");

        when(customerDao.existsById(1L)).thenReturn(true);
        when(customerDao.save(any())).thenReturn(customer);

        customer.setName("Jane Smith");
        customer.setFamily("Smith");
        customer.setPhoneNumber("0987654321");

        Customer result = customerService.updateCustomer(1L, customer);
        assertNotNull(result);
        assertEquals("Jane Smith", ((RealCustomer) result).getName());
        assertEquals("Smith", ((RealCustomer) result).getFamily());
    }

    @Test
    void deleteCustomer() {
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
        customer.setName("John Doe");
        customer.setFamily("Doe");
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
        customer.setName("John Doe");
        customer.setFamily("Doe");
        customer.setPhoneNumber("1234567890");
        when(customerDao.findById(customerId)).thenReturn(java.util.Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById(customerId);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals("John Doe", ((RealCustomer) result.get()).getName());
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
        String name = "John Doe";
        RealCustomer customer = new RealCustomer();
        customer.setName(name);
        customer.setFamily("Doe");
        customer.setPhoneNumber("1234567890");

        when(customerDao.findByNameIgnoreCase(name)).thenReturn(List.of(customer));

        List<Customer> result = customerService.findByName(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", ((RealCustomer) result.get(0)).getName());
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
        String name = "john doe";
        RealCustomer customer = new RealCustomer();
        customer.setName("John Doe");
        customer.setFamily("Doe");
        customer.setPhoneNumber("1234567890");

        when(customerDao.findByNameIgnoreCase(name)).thenReturn(List.of(customer));

        List<Customer> result = customerService.findByName(name);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", ((RealCustomer) result.get(0)).getName());
    }

    @Test
    void findByNameTwoCustomers() {
        String name = "John Doe";
        RealCustomer customer1 = new RealCustomer();
        customer1.setName(name);
        customer1.setFamily("Doe");
        customer1.setPhoneNumber("1234567890");

        LegalCustomer customer2 = new LegalCustomer();
        customer2.setName(name);
        customer2.setPhoneNumber("0987654321");
        customer2.setFax("123456789");

        when(customerDao.findByNameIgnoreCase(name)).thenReturn(List.of(customer1, customer2));

        List<Customer> result = customerService.findByName(name);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
