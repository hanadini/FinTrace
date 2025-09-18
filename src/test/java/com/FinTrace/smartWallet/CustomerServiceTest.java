package com.FinTrace.smartWallet;

import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.RealCustomer;
import com.FinTrace.smartWallet.CustomerService.service.CustomerService;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerServiceTest {

    @Test
    public void  testRaceConditionWthMultipleThreads() {
        CustomerService customerService = new CustomerService();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int numThreads = 100;
        CountDownLatch latch = new CountDownLatch(numThreads);
        for (int i = 0; i < numThreads; i++)  {
            final int index = i;
            executor.submit(() -> {
                customerService.addCustomer(
                        RealCustomer.builder()
                                .id(null)
                                .name("Eda")
                                .email("ED@gmail.com")
                                .phoneNumber("5397255710")
                                .family("Din")
                                .build()
                );
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        executor.shutdown();

        List<Customer> allCustomers = customerService.getAllCustomers();
        Set<Long> ids= new HashSet<>();
        for (Customer customer : allCustomers) {
            if (!ids.add(customer.getId())) {
                System.out.println("Duplicate ID found: " + customer.getId());
            }
        }
        assertEquals(allCustomers.size(), ids.size(), "Duplicate IDs detected!");
    }
}
