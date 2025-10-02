package com.FinTrace.smartWallet.dao.impl;

import com.FinTrace.customerSystem.dao.impl.DepositJdbcDao;
import com.FinTrace.customerSystem.model.Currency;
import com.FinTrace.customerSystem.model.Deposit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles({"jdbc"})
public class DepositJdbcDaoIntegrationTest {

    @Autowired
    private DepositJdbcDao depositJdbcDao;

    @AfterEach
    void cleanUp() {
        depositJdbcDao.findAll().forEach(deposit -> {
            depositJdbcDao.deleteById(deposit.getId());
        });
    }

    private Deposit createDeposit(double amount, Long customer, Currency currency) {
        Deposit deposit = new Deposit();
        deposit.setCustomerId(customer);
        deposit.setAmount(BigDecimal.valueOf(amount));
        deposit.setCurrency(currency);
        return deposit;
    }

    private Deposit createDeposit(double amount) {
        return createDeposit(amount, 1L, Currency.EUR);
    }

    @Test
    void saveAndFindById() {
        // This test should create a deposit, save it, and then retrieve it by ID.
        // Implement the logic to create a deposit and verify its retrieval.
        Deposit deposit = createDeposit(1000.0);
        deposit = depositJdbcDao.save(deposit);
        Deposit foundDeposit = depositJdbcDao.findById(deposit.getId())
                .orElseThrow(() -> new RuntimeException("Deposit not found"));
        assert foundDeposit != null;
        assert foundDeposit.getAmount().compareTo(BigDecimal.valueOf(1000.0)) == 0;
    }

    @Test
    void deleteById(){
        Deposit deposit = createDeposit(500.0);
        deposit.setCustomerId(1L);
        deposit = depositJdbcDao.save(deposit);
        assert deposit.getId() != null : "Deposit ID should not be null after saving";
        Long depositId = deposit.getId();
        depositJdbcDao.deleteById(depositId);
        Optional<Deposit> byId = depositJdbcDao.findById(depositId);
        assert byId.isEmpty() : "Deposit should not be found after deletion";
    }

    @Test
    void findByCustomerId() {
        // This test should create a deposit for a specific customer and then retrieve it by customer ID.

        Deposit deposit = createDeposit(2000.0);
        deposit = depositJdbcDao.save(deposit);

        assert deposit.getId() != null : "Deposit ID should not be null after saving";

        var deposits = depositJdbcDao.findByCustomerId(1L);
        assert !deposits.isEmpty() : "Deposits should not be empty for the given customer ID";
        assert deposits.get(0).getAmount().compareTo(BigDecimal.valueOf(2000.0)) == 0 : "Deposit amount should match the saved amount";
    }

    @Test
    void findAll() {
        // This test should create multiple deposits and verify that they can be retrieved.

        Deposit deposit1 = createDeposit(3000.0, 1L, Currency.EUR);
        depositJdbcDao.save(deposit1);

        Deposit deposit2 = createDeposit(4000.0, 2L, Currency.EUR);
        depositJdbcDao.save(deposit2);

        var deposits = depositJdbcDao.findAll();
        assert deposits.size() == 2 : "There should be two deposits in the database";
    }

    @Test
    void updateDeposit() {
        // This test should create a deposit, update it, and verify the changes.

        Deposit deposit = createDeposit(1500.0);
        deposit = depositJdbcDao.save(deposit);

        assert deposit.getId() != null : "Deposit ID should not be null after saving";

        // Update the deposit amount
        deposit.setAmount(BigDecimal.valueOf(2000.0));
        deposit = depositJdbcDao.save(deposit);

        Deposit updatedDeposit = depositJdbcDao.findById(deposit.getId())
                .orElseThrow(() -> new RuntimeException("Updated Deposit not found"));

        assert updatedDeposit.getAmount().compareTo(BigDecimal.valueOf(2000.0)) == 0 : "Updated amount should be 2000.0";
    }

    @Test
    void checkInitialVersion() {
        // This test should create a deposit and check the initial version field.

        Deposit deposit = createDeposit(1000.0);
        deposit = depositJdbcDao.save(deposit);

        assertEquals(0L, deposit.getVersion(), "Initial version should be 0");
    }

    @Test
    void checkWrongVersion() {
        // This test should create a deposit and then try to update it with a wrong version.

        Deposit deposit = createDeposit(1500.0);
        deposit = depositJdbcDao.save(deposit);

        assert deposit.getId() != null : "Deposit ID should not be null after saving";

        // Simulate a wrong version update
        try {
            deposit.setVersion(999L); // Set an incorrect version
            depositJdbcDao.save(deposit);
            assert false : "Expected an ObjectOptimisticLockingFailureException due to wrong version";
        } catch (ObjectOptimisticLockingFailureException e) {
            assert true;
        }
    }

    @Test
    void checkVersionUpdate() {
        // This test should create a deposit, update it, and check the version field.

        Deposit deposit = createDeposit(2000.0);
        deposit = depositJdbcDao.save(deposit);

        assert deposit.getId() != null : "Deposit ID should not be null after saving";
        Long initialVersion = deposit.getVersion();

        // Update the deposit amount
        deposit.setAmount(BigDecimal.valueOf(3000.0));
        deposit = depositJdbcDao.save(deposit);

        assertEquals(1L, deposit.getVersion(), "Version should be incremented to 2 after update");
    }

    @Test
    void checkVersionUpdatedAfterFindById() {
        // This test should create a deposit, retrieve it, and check the version field after retrieval.

        Deposit deposit = createDeposit(3500.0);
        deposit = depositJdbcDao.save(deposit);

        assert deposit.getId() != null : "Deposit ID should not be null after saving";
        Long initialVersion = deposit.getVersion();

        // Retrieve the deposit by ID
        Deposit retrievedDeposit = depositJdbcDao.findById(deposit.getId())
                .orElseThrow(() -> new RuntimeException("Deposit not found after saving"));

        assertEquals(initialVersion, retrievedDeposit.getVersion(), "Version should remain the same after retrieval");
    }

}