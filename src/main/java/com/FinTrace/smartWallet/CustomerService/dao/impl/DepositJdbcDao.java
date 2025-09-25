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
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@Profile( "jdbc" )
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
            String sql = "INSERT INTO deposit (amount, customer_id) VALUES (?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbc.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                ps.setDouble(1, deposit.getAmount());
                ps.setLong(2, deposit.getCustomer().getId());
                return ps;
            }, keyHolder);
            Long id = keyHolder.getKey().longValue();
            deposit.setId(id);
        } else {
            String sql = "UPDATE deposit SET amount = ?, customer_id = ? WHERE id = ?";
            jdbc.update(sql, deposit.getAmount(), deposit.getCustomer().getId(), deposit.getId());
        }
        return deposit; // In a real implementation, you would return the updated deposit with its ID
    }

    @Override
    public Optional<Deposit> findById(Long id) {
        String sql = "SELECT * FROM deposit WHERE id = ?";
        List<Deposit> deposits = jdbc.query(sql, (rs, rowNum) -> {
            Deposit deposit = new Deposit();
            deposit.setId(rs.getLong("id"));
            deposit.setAmount(rs.getDouble("amount"));
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
            deposit.setAmount(rs.getDouble("amount"));
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
            deposit.setAmount(rs.getDouble("amount"));
            Long customerId = rs.getLong("customer_id");
            deposit.setCustomer(customerDao.findById(customerId)
                    .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + customerId + " does not exist.")));
            return deposit;
        });
    }

}
