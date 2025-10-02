package com.FinTrace.customerSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DepositDto {
    private Long id;
    private BigDecimal amount;
    private CustomerDto customer;
    private Currency currency;
}