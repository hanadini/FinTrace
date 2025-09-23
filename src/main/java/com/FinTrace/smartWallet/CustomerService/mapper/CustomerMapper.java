package com.FinTrace.smartWallet.CustomerService.mapper;

import com.FinTrace.smartWallet.CustomerService.dto.LegalCustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.CustomerDto;
import com.FinTrace.smartWallet.CustomerService.dto.RealCustomerDto;
import com.FinTrace.smartWallet.CustomerService.model.LegalCustomer;
import com.FinTrace.smartWallet.CustomerService.model.Customer;
import com.FinTrace.smartWallet.CustomerService.model.RealCustomer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

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

    default byte[] toBytes(List<CustomerDto> customers) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(customers);
        oos.flush();
        return baos.toByteArray();
    }

    default void byteToDtos (byte[] fileContent) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileContent);
             java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais)) {
            List<CustomerDto> customerDtos = (List<CustomerDto>) ois.readObject();
            for (CustomerDto customerDto : customerDtos) {
                customerDto.setId(null);
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to import customers", e);
        }
    }

    default byte[] toJasonBytes(List<CustomerDto> customers) {
        try{
            return new ObjectMapper().writeValueAsBytes(customers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert customers to JSON bytes", e);
        }
    }

    default List<CustomerDto> jsonToDtos(byte[] fileContent) {
        try {
            List<CustomerDto> customerDtos = new ObjectMapper().readValue(fileContent,
                    new ObjectMapper().getTypeFactory()
                            .constructCollectionType(List.class, CustomerDto.class));
            for (CustomerDto customerDto : customerDtos) {
                customerDto.setId(null);
            }
            return customerDtos;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON bytes to customers", e);
        }
    }
}
