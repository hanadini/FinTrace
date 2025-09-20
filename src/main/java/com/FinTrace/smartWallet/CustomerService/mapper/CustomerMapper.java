package com.FinTrace.smartWallet.CustomerService.mapper;

import com.FinTrace.smartWallet.CustomerService.dto.LegalCustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.CustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.RealCustomerDto;
import com.FinTrace.smartWallet.CustomerService.model.LegalCustomer;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.RealCustomer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    RealCustomer toEntity(RealCustomerDto dto);

    RealCustomerDto toDto(RealCustomer entity);

    LegalCustomer toEntity(LegalCustomerDto dto);

    LegalCustomerDto toDto(LegalCustomer entity);

    default Customer toEntity(CustomerDto dto) {
        if (dto instanceof RealCustomerDto) {
            return toEntity((RealCustomerDto) dto);
        } else if (dto instanceof LegalCustomerDto) {
            return toEntity((LegalCustomerDto) dto);
        }
        throw new IllegalArgumentException("Unknown customerDto type: " + dto.getClass());
    }

    default CustomerDto toDto(Customer entity) {
        if (entity instanceof RealCustomer) {
            return toDto((RealCustomer) entity);
        } else if (entity instanceof LegalCustomer) {
            return toDto((LegalCustomer) entity);
        }
        throw new IllegalArgumentException("Unknown customer type: " + entity.getClass());
    }
}
