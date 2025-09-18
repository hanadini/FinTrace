package com.FinTrace.smartWallet.CustomerService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class RealCustomer extends Customer {
    private String family;

    public RealCustomer() {
        this.setType(CustomerType.REAL);
    }
}
