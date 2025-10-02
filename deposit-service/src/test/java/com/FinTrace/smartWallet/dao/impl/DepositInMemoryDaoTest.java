package com.FinTrace.smartWallet.dao.impl;

import com.FinTrace.customerSystem.dao.impl.DepositInMemoryDao;
import com.FinTrace.customerSystem.model.Deposit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles({"memo"})
public class DepositInMemoryDaoTest {

    @Autowired
    private DepositInMemoryDao depositInMemoryDao;

    @AfterEach
    void cleanUp() {
        depositInMemoryDao.findAll().forEach(deposit -> {
            depositInMemoryDao.deleteById(deposit.getId());
        });
    }

    @Test
    void checkInitialVersion() {

        Deposit deposit = new Deposit();
        deposit.setAmount(BigDecimal.valueOf(1000.0));
        deposit.setCustomerId(1L);
        deposit = depositInMemoryDao.save(deposit);

        assertEquals(0L, deposit.getVersion(), "Initial version should be 0");
    }


    @Test
    void checkWrongVersion() {
        // This test should create a deposit and then try to update it with a wrong version.

        Deposit deposit = new Deposit();
        deposit.setAmount(BigDecimal.valueOf(1500.0));
        deposit.setCustomerId(1L);
        deposit = depositInMemoryDao.save(deposit);

        assert deposit.getId() != null : "Deposit ID should not be null after saving";

        // Simulate a wrong version update
        try {
            deposit.setVersion(999L); // Set an incorrect version
            depositInMemoryDao.save(deposit);
            assert false : "Expected an ObjectOptimisticLockingFailureException due to wrong version";
        } catch (ObjectOptimisticLockingFailureException e) {
            assert true;
        }
    }

    @Test
    void checkVersionUpdate() {
        // This test should create a deposit, update it, and check the version field.

        Deposit deposit = new Deposit();
        deposit.setAmount(BigDecimal.valueOf(2500.0));
        deposit.setCustomerId(1L);
        deposit = depositInMemoryDao.save(deposit);

        assert deposit.getId() != null : "Deposit ID should not be null after saving";
        Long initialVersion = deposit.getVersion();

        // Update the deposit amount
        deposit.setAmount(BigDecimal.valueOf(3000.0));
        deposit = depositInMemoryDao.save(deposit);

        assertEquals(1L, deposit.getVersion(), "Version should be incremented to 2 after update");
    }

    @Test
    void checkVersionUpdatedAfterFindById() {
        // This test should create a deposit, retrieve it, and check the version field after retrieval.

        Deposit deposit = new Deposit();
        deposit.setAmount(BigDecimal.valueOf(3500.0));
        deposit.setCustomerId(1L);
        deposit = depositInMemoryDao.save(deposit);

        assert deposit.getId() != null : "Deposit ID should not be null after saving";
        Long initialVersion = deposit.getVersion();

        // Retrieve the deposit by ID
        Deposit retrievedDeposit = depositInMemoryDao.findById(deposit.getId())
                .orElseThrow(() -> new RuntimeException("Deposit not found after saving"));

        assertEquals(initialVersion, retrievedDeposit.getVersion(), "Version should remain the same after retrieval");
    }
}