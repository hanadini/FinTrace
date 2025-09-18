package com.FinTrace.smartWallet.CustomerService.dto;

import com.FinTrace.smartWallet.CustomerService.model.CustomerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class RealCustomerDto extends CustomerDto {
    private String family;

    public RealCustomerDto() {
        this.setType(CustomerType.REAL);
    }
}
