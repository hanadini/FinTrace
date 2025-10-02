package com.FinTrace.customerSystem.dto;

import com.FinTrace.customerSystem.model.CustomerType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RealCustomerDto.class, name = "REAL"),
        @JsonSubTypes.Type(value = LegalCustomerDto.class, name = "LEGAL")
})
@Schema(description = "Customer entity representing a customer")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public abstract class CustomerDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    @Schema(description = "Unique identifier for the customer", example = "1")
    private Long id;
    @NotEmpty(message = "Name cannot be empty")
    @NotNull(message = "Name cannot be null")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    @Schema(description = "Name of the customer", example = "John Doe")
    private String name;
    @Schema(description = "Phone number of the customer", example = "+1234567890")
    private String phoneNumber;
    private CustomerType type;
}