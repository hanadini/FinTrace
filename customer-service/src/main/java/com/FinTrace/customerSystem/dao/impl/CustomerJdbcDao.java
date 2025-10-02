package com.FinTrace.customerSystem.dao.impl;

import com.FinTrace.customerSystem.dao.CustomerDao;
import com.FinTrace.customerSystem.model.Customer;
import com.FinTrace.customerSystem.model.CustomerType;
import com.FinTrace.customerSystem.model.LegalCustomer;
import com.FinTrace.customerSystem.model.RealCustomer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import jakarta.validation.Validator;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
@Primary
@Profile("jdbc")
public class CustomerJdbcDao implements CustomerDao {

    private final JdbcTemplate jdbc;
    private final Validator validator;

    @Autowired
    public CustomerJdbcDao(JdbcTemplate jdbc, Validator validator) {
        this.jdbc = jdbc;
        this.validator = validator;
    }

    public Customer save(Customer customer) {
        Set<ConstraintViolation<Customer>> violations = validator.validate(customer);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        if (existsById(customer.getId())) {
            return update(customer);
        } else {
            return insert(customer);
        }
    }

    private Customer insert(Customer customer) {
        String customerSql = "INSERT INTO customer (name, phone_number, type) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(customerSql, new String[]{"id"});
            ps.setString(1, customer.getName());
            ps.setString(2, customer.getPhoneNumber());
            ps.setString(3, customer.getType().name());
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKey().longValue();
        customer.setId(id);

        if (customer instanceof RealCustomer realCustomer) {
            String realCustomerSql = "INSERT INTO real_customer (id, family) VALUES (?, ?)";
            jdbc.update(realCustomerSql, id, realCustomer.getFamily());
        } else if (customer instanceof LegalCustomer legalCustomer) {
            String legalCustomerSql = "INSERT INTO legal_customer (id, fax) VALUES (?, ?)";
            jdbc.update(legalCustomerSql, id, legalCustomer.getFax());
        }

        return customer;
    }

    private Customer update(Customer customer) {
        String customerSql = "UPDATE customer SET name = ?, phone_number = ?, type = ? WHERE id = ?";
        jdbc.update(customerSql, customer.getName(),
                customer.getPhoneNumber(), customer.getType().name(), customer.getId());

        if (customer instanceof RealCustomer realCustomer) {
            String realCustomerSql = "UPDATE real_customer SET family = ? WHERE id = ?";
            jdbc.update(realCustomerSql, realCustomer.getFamily(), customer.getId());
        } else if (customer instanceof LegalCustomer legalCustomer) {
            String legalCustomerSql = "UPDATE legal_customer SET fax = ? WHERE id = ?";
            jdbc.update(legalCustomerSql, legalCustomer.getFax(), customer.getId());
        }

        return customer;
    }

    public void deleteById(Long id) {
        String customerSql = "DELETE FROM real_customer WHERE id = ?";
        jdbc.update(customerSql, id);
        customerSql = "DELETE FROM legal_customer WHERE id = ?";
        jdbc.update(customerSql, id);
        customerSql = "DELETE FROM customer WHERE id = ?";
        jdbc.update(customerSql, id);
    }

    public Optional<Customer> findById(Long id) {
        if(!existsById(id)) {
            return Optional.empty();
        }
        String customerSql = "SELECT * FROM customer WHERE id = ?";
        Map<String, Object> customerRow = jdbc.queryForMap(customerSql, id);

        CustomerType type = CustomerType.valueOf((String) customerRow.get("type"));
        Customer customer;

        if (type == CustomerType.REAL) {
            String realCustomerSql = "SELECT * FROM real_customer WHERE id = ?";
            Map<String, Object> realCustomerRow = jdbc.queryForMap(realCustomerSql, id);
            customer = RealCustomer.builder()
                    .id(id)
                    .name((String) customerRow.get("name"))
                    .phoneNumber((String) customerRow.get("phone_number"))
                    .type(type)
                    .family((String) realCustomerRow.get("family"))
                    .build();
        } else {
            String legalCustomerSql = "SELECT * FROM legal_customer WHERE id = ?";
            Map<String, Object> legalCustomerRow = jdbc.queryForMap(legalCustomerSql, id);
            customer = LegalCustomer.builder()
                    .id(id)
                    .name((String) customerRow.get("name"))
                    .phoneNumber((String) customerRow.get("phone_number"))
                    .type(type)
                    .fax((String) legalCustomerRow.get("fax"))
                    .build();
        }

        return Optional.of(customer);
    }

    public List<Customer> findAll() {
        String customerSql = "SELECT * FROM customer";
        return jdbc.query(customerSql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            CustomerType type = CustomerType.valueOf(rs.getString("type"));

            if (type == CustomerType.REAL) {
                String realCustomerSql = "SELECT * FROM real_customer WHERE id = ?";
                Map<String, Object> realCustomerRow = jdbc.queryForMap(realCustomerSql, id);
                return RealCustomer.builder()
                        .id(id)
                        .name(rs.getString("name"))
                        .phoneNumber(rs.getString("phone_number"))
                        .type(type)
                        .family((String) realCustomerRow.get("family"))
                        .build();
            } else {
                String legalCustomerSql = "SELECT * FROM legal_customer WHERE id = ?";
                Map<String, Object> legalCustomerRow = jdbc.queryForMap(legalCustomerSql, id);
                return LegalCustomer.builder()
                        .id(id)
                        .name(rs.getString("name"))
                        .phoneNumber(rs.getString("phone_number"))
                        .type(type)
                        .fax((String) legalCustomerRow.get("fax"))
                        .build();
            }
        });
    }

    public boolean existsById(Long id) {
        String customerSql = "SELECT COUNT(*) FROM customer WHERE id = ?";
        Integer count = jdbc.queryForObject(customerSql, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public List<Customer> findByNameIgnoreCase(String name) {
        String customerSql = "SELECT * FROM customer WHERE LOWER(name) = LOWER(?)";
        return jdbc.query(customerSql, (rs, rowNum) -> {
            Long id = rs.getLong("id");
            CustomerType type = CustomerType.valueOf(rs.getString("type"));

            if (type == CustomerType.REAL) {
                String realCustomerSql = "SELECT * FROM real_customer WHERE id = ?";
                Map<String, Object> realCustomerRow = jdbc.queryForMap(realCustomerSql, id);
                return RealCustomer.builder()
                        .id(id)
                        .name(rs.getString("name"))
                        .phoneNumber(rs.getString("phone_number"))
                        .type(type)
                        .family((String) realCustomerRow.get("family"))
                        .build();
            } else {
                String legalCustomerSql = "SELECT * FROM legal_customer WHERE id = ?";
                Map<String, Object> legalCustomerRow = jdbc.queryForMap(legalCustomerSql, id);
                return LegalCustomer.builder()
                        .id(id)
                        .name(rs.getString("name"))
                        .phoneNumber(rs.getString("phone_number"))
                        .type(type)
                        .fax((String) legalCustomerRow.get("fax"))
                        .build();
            }
        }, name);
    }

    @Override
    public boolean realCustomerExists(String name, String family) {
        String sql = "SELECT COUNT(*) FROM customer c JOIN real_customer rc ON c.id = rc.id WHERE LOWER(c.name) = LOWER(?) AND LOWER(rc.family) = LOWER(?)";
        Integer count = jdbc.queryForObject(sql, Integer.class, name, family);
        return count > 0;
    }

    @Override
    public boolean legalCustomerExists(String name) {
        String sql = "SELECT COUNT(*) FROM customer c JOIN legal_customer lc ON c.id = lc.id WHERE LOWER(c.name) = LOWER(?)";
        Integer count = jdbc.queryForObject(sql, Integer.class, name);
        return count > 0;
    }

}


