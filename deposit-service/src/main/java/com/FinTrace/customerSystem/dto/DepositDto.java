package com.FinTrace.customerSystem.dto;

import com.FinTrace.customerSystem.model.Currency;
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
    private Long customer;
    private Currency currency;
}
