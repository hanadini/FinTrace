package com.FinTrace.smartWallet;

import com.FinTrace.smartWallet.model.Contact;
import com.FinTrace.smartWallet.service.ContactService;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContactServiceTest {

    @Test
    public void  testRaceConditionWthMultipleThreads() {
        ContactService contactService = new ContactService();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        int numThreads = 100;
        CountDownLatch latch = new CountDownLatch(numThreads);
        for (int i = 0; i < numThreads; i++)  {
            final int index = i;
            executor.submit(() -> {
                contactService.addContact(new Contact(null, "John Doe","JD@gmail.com", "1234567890"));
                latch.countDown();
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        executor.shutdown();

        List<Contact> allContacts = contactService.getAllContacts();
        Set<Long> ids= new HashSet<>();
        for (Contact contact : allContacts) {
            if (!ids.add(contact.getId())) {
                System.out.println("Duplicate ID found: " + contact.getId());
            }
        }
        assertEquals(allContacts.size(), ids.size(), "Duplicate IDs detected!");
    }
}
