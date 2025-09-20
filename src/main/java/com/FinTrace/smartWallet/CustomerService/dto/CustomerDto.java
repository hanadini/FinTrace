package com.FinTrace.smartWallet.CustomerService.dto;

import com.FinTrace.smartWallet.CustomerService.model.CustomerType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)

@JsonSubTypes({
        @JsonSubTypes.Type(value = RealCustomerDto.class, name = "REAL"),
        @JsonSubTypes.Type(value = LegalCustomerDto.class, name = "LEGAL")
})

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Schema(description = "Customer Data Transfer Object representing a contact in the smart wallet application")
public abstract class CustomerDto {
    @Schema(description = "Unique identifier of the customer", example = "1")
    private Long id;
    @Schema(description = "Name of the customer", example = "Ed")
    private String name;
    @Schema(description = "Email address of the customer", example = "a@gmail.com")
    private String email;
    @Schema(description = "Phone number of the customer", example = "+905397251111")
    private String phoneNumber;
    private CustomerType type;


}
