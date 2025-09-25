package com.FinTrace.smartWallet.CustomerService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DepositDto {
    private Long id;
    private Double amount;
    private CustomerDto customer;
}
