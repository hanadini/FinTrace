package com.FinTrace.smartWallet.CustomerService.dao.impl;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.dao.DepositDao;
import com.FinTrace.smartWallet.CustomerService.exception.CustomerNotFoundException;
import com.FinTrace.smartWallet.CustomerService.model.Deposit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class DepositJdbcDao implements DepositDao {

    private final JdbcTemplate jdbc;
    private final CustomerDao customerDao;

    @Autowired
    public DepositJdbcDao(JdbcTemplate jdbc, CustomerDao customerDao) {
        this.jdbc = jdbc;
        this.customerDao = customerDao;
    }

    @Override
    public Deposit save(Deposit deposit) {
        if (deposit.getId() == null) {
            String sql = "INSERT INTO deposit (amount, customer_id, version) VALUES (?, ?, 0)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setBigDecimal(1, deposit.getAmount());
                ps.setLong(2, deposit.getCustomer().getId());
                return ps;
            }, keyHolder);
            Long id = keyHolder.getKey().longValue();
            deposit.setId(id);
            deposit.setVersion(0L); // Initialize version to 0 for new deposits
        } else {
            String sql = "UPDATE deposit SET amount = ?, customer_id = ?, version = version + 1 WHERE id = ? and version = ?";
            int update = jdbc.update(sql, deposit.getAmount(), deposit.getCustomer().getId(), deposit.getId(), deposit.getVersion());
            if (update == 0) {
                throw new ObjectOptimisticLockingFailureException(
                        "Deposit with id " + deposit.getId() + " was updated by another transaction.",
                        Deposit.class);
            }
            deposit.setVersion(deposit.getVersion() + 1); // Increment version after successful update
        }
        return deposit; // In a real implementation, you would return the updated deposit with its ID
    }

    @Override
    public Optional<Deposit> findById(Long id) {
        String sql = "SELECT * FROM deposit WHERE id = ?";
        List<Deposit> deposits = jdbc.query(sql, (rs, rowNum) -> {
            Deposit deposit = new Deposit();
            deposit.setId(rs.getLong("id"));
            deposit.setAmount(rs.getBigDecimal("amount"));
            deposit.setVersion(rs.getLong("version"));
            Long customerId = rs.getLong("customer_id");
            deposit.setCustomer(customerDao.findById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " does not exist.")));
            return deposit;
        }, id);
        return deposits.isEmpty() ? Optional.empty() : Optional.of(deposits.get(0));
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM deposit WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public List<Deposit> findByCustomerId(Long id) {
        String sql = "SELECT * FROM deposit WHERE customer_id = ?";
        return jdbc.query(sql, (rs, rowNum) -> {
            Deposit deposit = new Deposit();
            deposit.setId(rs.getLong("id"));
            deposit.setAmount(rs.getBigDecimal("amount"));
            deposit.setVersion(rs.getLong("version"));
            Long customerId = rs.getLong("customer_id");
            deposit.setCustomer(customerDao.findById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " does not exist.")));
            return deposit;
        }, id);
    }

    @Override
    public List<Deposit> findAll() {
        String sql = "SELECT * FROM deposit";
        return jdbc.query(sql, (rs, rowNum) -> {
            Deposit deposit = new Deposit();
            deposit.setId(rs.getLong("id"));
            deposit.setAmount(rs.getBigDecimal("amount"));
            deposit.setVersion(rs.getLong("version"));
            Long customerId = rs.getLong("customer_id");
            deposit.setCustomer(customerDao.findById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " does not exist.")));
            return deposit;
        });
    }
}
