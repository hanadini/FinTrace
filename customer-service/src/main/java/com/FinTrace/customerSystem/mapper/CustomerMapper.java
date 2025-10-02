package com.FinTrace.customerSystem.mapper;

import com.FinTrace.customerSystem.dto.LegalCustomerDto;
import com.FinTrace.customerSystem.dto.CustomerDto;
import com.FinTrace.customerSystem.dto.RealCustomerDto;
import com.FinTrace.customerSystem.model.LegalCustomer;
import com.FinTrace.customerSystem.model.Customer;
import com.FinTrace.customerSystem.model.RealCustomer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    RealCustomer toEntity(RealCustomerDto dto);

    RealCustomerDto toDto(RealCustomer entity);

    LegalCustomer toEntity(LegalCustomerDto dto);

    LegalCustomerDto toDto(LegalCustomer entity);

    default Customer toEntity(Object dto) {
        if (dto instanceof RealCustomerDto) {
            return toEntity((RealCustomerDto) dto);
        } else if (dto instanceof LegalCustomerDto) {
            return toEntity((LegalCustomerDto) dto);
        }
        throw new IllegalArgumentException("Unsupported DTO type: " + dto.getClass());
    }

    default CustomerDto toDto(Customer entity) {
        if (entity instanceof RealCustomer) {
            return toDto((RealCustomer) entity);
        } else if (entity instanceof LegalCustomer) {
            return toDto((LegalCustomer) entity);
        }
        throw new IllegalArgumentException("Unsupported entity type: " + entity.getClass());
    }

    default byte[] toBytes(List<CustomerDto> customers) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(customers);
        oos.flush();
        return baos.toByteArray();
    }

    default List<CustomerDto> byteToDtos(byte[] fileContent) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileContent);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            List<CustomerDto> customerDtos = (List<CustomerDto>) ois.readObject();
            for (CustomerDto customerDto : customerDtos) {
                customerDto.setId(null); // Reset ID for new entities
            }
            return customerDtos;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to import customers", e);
        }
    }

    default byte[] toJsonBytes(List<CustomerDto> customers){
        try {
            return new ObjectMapper().writeValueAsBytes(customers);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert customers to JSON bytes", e);
        }
    }

    default List<CustomerDto> jsonToDtos(byte[] fileContent){
        try {
            List<CustomerDto> customerDtos = new ObjectMapper().readValue(fileContent, new ObjectMapper().getTypeFactory()
                    .constructCollectionType(List.class, CustomerDto.class));
            for (CustomerDto customerDto : customerDtos) {
                customerDto.setId(null); // Reset ID for new entities
            }
            return customerDtos;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON bytes to customers", e);
        }
    }
}