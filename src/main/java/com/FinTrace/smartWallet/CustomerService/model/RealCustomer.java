package com.FinTrace.smartWallet.CustomerService.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "real_customers")
@PrimaryKeyJoinColumn(name = "id")
@DiscriminatorValue( "REAL" )
public class RealCustomer extends Customer {
    private String family;

    public RealCustomer() {
        this.setType(CustomerType.REAL);
    }
}
