package com.FinTrace.smartWallet.service;

import com.FinTrace.customerSystem.model.Currency;
import com.FinTrace.customerSystem.exception.InsufficientFundsException;
import com.FinTrace.customerSystem.service.DepositService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles({"jpa"})
public class DepositServiceIntegrationJpaTest {

    @Autowired
    private DepositService depositService;

    private Long customerId;
    private Long depositId;

    @AfterEach
    void cleanUp() {
        depositService.getAllDeposits().forEach(deposit -> {
            depositService.deleteDeposit(deposit.getId());
        });
    }

    @BeforeEach
    void setUp() {
        customerId = 1L;

        depositService.addDeposit(customerId, Currency.EUR);
        depositId = depositService.getDepositsByCustomerId(customerId).getFirst().getId();
    }

    static Stream<org.junit.jupiter.params.provider.Arguments> depositAmountProvider() {
        return Stream.of(
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(BigDecimal.valueOf(100.0),
                                BigDecimal.valueOf(0.1), BigDecimal.valueOf(0.2)),
                        BigDecimal.valueOf(100.3)),
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(BigDecimal.valueOf(0.1),
                                BigDecimal.valueOf(0.2)),
                        BigDecimal.valueOf(0.3)),
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(BigDecimal.valueOf(100.0)),
                        BigDecimal.valueOf(100.0)),
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(BigDecimal.valueOf(50.0),
                                BigDecimal.valueOf(50.0)),
                        BigDecimal.valueOf(100.0)),
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(BigDecimal.valueOf(0.1),
                                BigDecimal.valueOf(0.1),
                                BigDecimal.valueOf(0.1)),
                        BigDecimal.valueOf(0.3)),
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(BigDecimal.valueOf(0.1),
                                BigDecimal.valueOf(0.1),
                                BigDecimal.valueOf(0.2),
                                BigDecimal.valueOf(0.3),
                                BigDecimal.valueOf(0.1)),
                        BigDecimal.valueOf(0.8)),
                org.junit.jupiter.params.provider.Arguments.of(
                        List.of(BigDecimal.valueOf(1),
                                BigDecimal.valueOf(1),
                                BigDecimal.valueOf(2),
                                BigDecimal.valueOf(3),
                                BigDecimal.valueOf(1)),
                        BigDecimal.valueOf(8))
        );
    }

    @ParameterizedTest
    @MethodSource("depositAmountProvider")
    void testDepositAmount(List<BigDecimal> amounts, BigDecimal expectedAmount) {
        BigDecimal initialAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        assertTrue(BigDecimal.ZERO.compareTo(initialAmount) == 0);

        for (BigDecimal amount : amounts) {
            depositService.depositAmount(depositId, amount);
        }

        BigDecimal newAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        assertTrue(expectedAmount.compareTo(newAmount) == 0);
    }

    @Test
    void testWithdrawAmount() {
        BigDecimal initialAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        assertTrue(BigDecimal.ZERO.compareTo(initialAmount) == 0);

        BigDecimal depositAmount = BigDecimal.valueOf(100.0);
        depositService.depositAmount(depositId, depositAmount);

        BigDecimal withdrawAmount = BigDecimal.valueOf(50.0);
        depositService.withdrawAmount(depositId, withdrawAmount);

        BigDecimal newAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        assertTrue(depositAmount.subtract(withdrawAmount).compareTo(newAmount) == 0);
    }

    @Test
    void testWithdrawAmountInsufficientFunds() {
        BigDecimal initialAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        assertTrue(BigDecimal.ZERO.compareTo(initialAmount) == 0);

        BigDecimal depositAmount = BigDecimal.valueOf(100.0);
        depositService.depositAmount(depositId, depositAmount);

        BigDecimal withdrawAmount = BigDecimal.valueOf(150.0);

        try {
            depositService.withdrawAmount(depositId, withdrawAmount);
        } catch (Exception e) {
            assertTrue(e instanceof InsufficientFundsException);
            assertTrue(e.getMessage().contains("Insufficient funds"));
        }
    }

    @Test
    void testWithdrawAmountNoRollback() {
        BigDecimal initialAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        assertTrue(BigDecimal.ZERO.compareTo(initialAmount) == 0);

        BigDecimal depositAmount = BigDecimal.valueOf(100.0);
        depositService.depositAmount(depositId, depositAmount);

        BigDecimal withdrawAmount = BigDecimal.valueOf(150.0);

        try {
            depositService.withdrawAmount(depositId, withdrawAmount);
        } catch (InsufficientFundsException e) {
            // Expected exception
        }

        BigDecimal newAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        assertTrue(depositAmount.compareTo(newAmount) == 0);
    }

    @Test
    void testConcurrentThreadThousandDeposit(){
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                depositService.depositAmount(depositId, BigDecimal.valueOf(1));
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                depositService.depositAmount(depositId, BigDecimal.valueOf(1));
            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        BigDecimal expectedAmount = BigDecimal.valueOf(2000);
        BigDecimal newAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        assertEquals(expectedAmount.setScale(2), newAmount.setScale(2));
    }

    @Test
    void testTransferAmount() {

        Long targetCustomerId = 2L;

        depositService.addDeposit(targetCustomerId, Currency.EUR);
        Long targetDepositId = depositService.getDepositsByCustomerId(targetCustomerId).getFirst().getId();

        BigDecimal transferAmount = BigDecimal.valueOf(50.0);
        depositService.depositAmount(depositId, transferAmount);

        depositService.transferAmount(depositId, targetDepositId, transferAmount);

        BigDecimal sourceNewAmount = depositService.getDepositsByCustomerId(customerId).getFirst().getAmount();
        BigDecimal targetNewAmount = depositService.getDepositsByCustomerId(targetCustomerId).getFirst().getAmount();

        assertEquals(BigDecimal.ZERO.setScale(2), sourceNewAmount.setScale(2));
        assertEquals(transferAmount.setScale(2), targetNewAmount.setScale(2));
    }

}