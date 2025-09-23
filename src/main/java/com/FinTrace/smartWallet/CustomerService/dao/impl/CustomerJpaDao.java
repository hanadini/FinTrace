package com.FinTrace.smartWallet.CustomerService.dao.impl;

import com.FinTrace.smartWallet.CustomerService.dao.CustomerDao;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Primary
@Profile("jpa")
public interface CustomerJpaDao extends JpaRepository<Customer, Long>, CustomerDao {
    @Query("SELECT CASE WHEN COUNT(rc) > 0 THEN TRUE ELSE FALSE END FROM RealCustomer rc WHERE LOWER(rc.name) = LOWER(:name) AND LOWER(rc.family) = LOWER(:family)")
    boolean realCustomerExists(@Param("name") String name, @Param("family") String family);

    @Query("SELECT CASE WHEN COUNT(lc) > 0 THEN TRUE ELSE FALSE END FROM LegalCustomer lc WHERE LOWER(lc.name) = LOWER(:name)")
    boolean legalCustomerExists(@Param("name") String name);

}
