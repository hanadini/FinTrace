package com.FinTrace.smartWallet.CustomerService.dao.impl;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.CustomerType;
import com.FinTrace.smartWallet.CustomerService.model.LegalCustomer;
import com.FinTrace.smartWallet.CustomerService.model.RealCustomer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Primary
@Profile("jdbc")
public class CustomerJdbcDao implements CustomerDao {

    private final JdbcTemplate jdbc;

    @Autowired
    public CustomerJdbcDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Customer save(Customer customer) {
        if (existsById(customer.getId())) {
            return update(customer);
        } else {
            return insert(customer);
        }
    }

    public Customer insert(Customer customer) {
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
            String legalCustomerSql = "INSERT INTO legal_customer (id, email, businnes_address) VALUES (?, ?, ?)";
            jdbc.update(legalCustomerSql, id, legalCustomer.getEmail(), legalCustomer.getBusinessAddress());
        }

        return customer;
    }

    public Customer update(Customer customer) {
        String customerSql = "UPDATE customer SET name = ?, phone_number = ?, type = ? WHERE id = ?";
        jdbc.update(customerSql, customer.getName(), customer.getPhoneNumber(), customer.getType().name(), customer.getId());

        if (customer instanceof RealCustomer realCustomer) {
            String realCustomerSql = "UPDATE real_customer SET family = ? WHERE id = ?";
            jdbc.update(realCustomerSql, realCustomer.getFamily(), customer.getId());
        } else if (customer instanceof LegalCustomer legalCustomer) {
            String legalCustomerSql = "UPDATE legal_customer SET fax = ? WHERE id = ?";
            jdbc.update(legalCustomerSql, legalCustomer.getBusinessAddress(), customer.getId());
        }
        return customer;
    }

    public void deleteById(Long id) {
        String customerSql = "DELETE FROM customer WHERE id = ?";
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
                    .email((String) legalCustomerRow.get("email"))
                    .businessAddress((String) legalCustomerRow.get("businnes_address"))
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
                        .businessAddress((String) legalCustomerRow.get("fax"))
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
        return jdbc.query(customerSql, new Object[]{name}, (rs, rowNum) -> {
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
                        .email( (String) realCustomerRow.get("email"))
                        .build();
            } else {
                String legalCustomerSql = "SELECT * FROM legal_customer WHERE id = ?";
                Map<String, Object> legalCustomerRow = jdbc.queryForMap(legalCustomerSql, id);
                return LegalCustomer.builder()
                        .id(id)
                        .name(rs.getString("name"))
                        .phoneNumber(rs.getString("phone_number"))
                        .type(type)
                        .businessAddress((String) legalCustomerRow.get("businnes_address"))
                        .email( (String) legalCustomerRow.get("email"))
                        .build();
            }
        });
    }
}


