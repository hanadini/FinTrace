package com.FinTrace.customerSystem.dao.impl;

import com.FinTrace.customerSystem.model.Customer;
import com.FinTrace.customerSystem.model.LegalCustomer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;


@SpringBootTest
@ActiveProfiles({"memo"})
public class CustomerInMemoryDaoTest {

    @Autowired
    private CustomerInMemoryDao customerInMemoryDao;

    @Test
    void saveAndFindById() {
        LegalCustomer customer = new LegalCustomer();
        customer.setName("Test Company");
        customer.setPhoneNumber("1234567890");
        customer.setFax("987654321");
        customer = (LegalCustomer) customerInMemoryDao.save(customer);
        Optional<Customer> byId = customerInMemoryDao.findById(customer.getId());
        assert byId.isPresent();
    }
}
