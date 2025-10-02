package com.FinTrace.customerSystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty
    @NotNull
    @Column(nullable = false)
    private String family;

    public RealCustomer() {
        this.setType(CustomerType.REAL);
    }
}
