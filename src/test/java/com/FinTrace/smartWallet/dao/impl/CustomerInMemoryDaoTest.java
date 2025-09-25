package com.FinTrace.smartWallet.dao.impl;

import com.FinTrace.smartWallet.CustomerService.dao.impl.CustomerInMemoryDao;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.LegalCustomer;
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
        customer.setEmail("a@gmail.com");
        customer = (LegalCustomer) customerInMemoryDao.save(customer);
        Optional<Customer> byId = customerInMemoryDao.findById(customer.getId());
        assert byId.isPresent();
    }
}
