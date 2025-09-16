package com.FinTrace.smartWallet.service;

import com.FinTrace.smartWallet.model.Contact;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ContactService {
    private final Map<Long, Contact> contacts = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(0);

    @PostConstruct
    public void init() {
        // Initialize with some dummy contacts
        addContact(new Contact(null, "John Doe", "Jd@gmail.com", "1234567890"));
        addContact(new Contact(null, "Jane Smith", "JS@gmail.com", "0987654321"));
    }

    public Contact addContact(Contact contact) {
        long id = currentId.incrementAndGet();
        contact.setId(id);
        contacts.put(id, contact);
        return contact;
    }

    public Contact updateContact(Long id, Contact contact) {
        if (contacts.containsKey(id)) {
            contact.setId(id);
            contacts.put(id, contact);
            return contact;
        }
        return null;
    }

    public boolean deleteContact(Long id) {
        if (contacts.containsKey(id)) {
            contacts.remove(id);
            return true;
        }
        return false;
    }

    public Contact getContact(Long id) {
        return contacts.get(id);
    }

    public List<Contact> getAllContacts() {
        return contacts.values().stream().toList();
    }

}
