package com.FinTrace.smartWallet.dao;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.RealCustomer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles({"jpa"})
public class CustomerDaoIntegrationTest {

    @Autowired
    private CustomerDao customerDao;

    @AfterEach
    void cleanUp() {
        customerDao.findAll().forEach(customer -> {
            customerDao.deleteById(customer.getId());
        });
    }

    @Test
    void saveAndFindById() {
        RealCustomer customer = new RealCustomer();
        customer.setName("Jane Doe");
        customer.setFamily("Doe");
        customer.setPhoneNumber("1234567890");

        customer = (RealCustomer) customerDao.save(customer);

        Optional<Customer> foundCustomer = customerDao.findById(customer.getId());
        assertTrue(foundCustomer.isPresent());
        assertEquals("Jane Doe", foundCustomer.get().getName());
        assertEquals("Doe", ((RealCustomer) foundCustomer.get()).getFamily());
        assertEquals("1234567890", ((RealCustomer) foundCustomer.get()).getPhoneNumber());
        assertEquals("REAL", foundCustomer.get().getType().name());
    }

    @Test
    void findAllCustomers() {
        RealCustomer customer1 = new RealCustomer();
        customer1.setName("Alice Smith");
        customer1.setFamily("Smith");
        customer1.setPhoneNumber("1112223333");

        RealCustomer customer2 = new RealCustomer();
        customer2.setName("Bob Johnson");
        customer2.setFamily("Johnson");
        customer2.setPhoneNumber("4445556666");

        customerDao.save(customer1);
        customerDao.save(customer2);

        assertEquals(2, customerDao.findAll().size());
    }

    @Test
    void deleteById() {
        RealCustomer customer = new RealCustomer();
        customer.setName("Charlie Brown");
        customer.setFamily("Brown");
        customer.setPhoneNumber("7778889999");

        customer = (RealCustomer) customerDao.save(customer);
        Long id = customer.getId();

        assertTrue(customerDao.existsById(id));

        customerDao.deleteById(id);

        assertTrue(customerDao.findById(id).isEmpty());
    }

    @Test
    void checkGeneratedId() {
        RealCustomer customer = new RealCustomer();
        customer.setName("Generated ID Test");
        customer.setFamily("ID");
        customer.setPhoneNumber("0001112222");

        customer = (RealCustomer) customerDao.save(customer);

        assertNotNull(customer.getId());
        assertTrue(customer.getId() > 0);
    }

    @Test
    void updateCustomer() {
        RealCustomer customer = new RealCustomer();
        customer.setName("David Wilson");
        customer.setFamily("Wilson");
        customer.setPhoneNumber("3334445555");

        customer = (RealCustomer) customerDao.save(customer);
        Long id = customer.getId();

        customer.setName("Updated David Wilson");
        customer.setFamily("Updated Wilson");

        Customer updatedCustomer = customerDao.save(customer);

        assertNotNull(updatedCustomer);
        assertEquals(id, updatedCustomer.getId());
        assertEquals("Updated David Wilson", updatedCustomer.getName());
        assertEquals("Updated Wilson", ((RealCustomer) updatedCustomer).getFamily());
    }

    @Test
    void findByNameIgnoreCase() {
        RealCustomer customer = new RealCustomer();
        customer.setName("Eve Adams");
        customer.setFamily("Adams");
        customer.setPhoneNumber("9998887777");

        customerDao.save(customer);

        assertEquals(1, customerDao.findByNameIgnoreCase("eve adams").size());
        assertEquals(1, customerDao.findByNameIgnoreCase("EVE ADAMS").size());
        assertEquals(1, customerDao.findByNameIgnoreCase("Eve Adams").size());
    }

}
