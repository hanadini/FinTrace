package com.FinTrace.customerSystem.mapper;

import com.FinTrace.customerSystem.dto.DepositDto;
import com.FinTrace.customerSystem.model.Deposit;
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
public interface DepositMapper {

    DepositDto toDto(Deposit deposit);

    Deposit toEntity(DepositDto depositDto);

    default byte[] toBytes(List<DepositDto> deposits) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(deposits);
        oos.flush();
        return baos.toByteArray();
    }

    default byte[] toJsonBytes(List<DepositDto> deposits){
        try {
            return new ObjectMapper().writeValueAsBytes(deposits);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert deposits to JSON bytes", e);
        }
    }

    default List<DepositDto> byteToDtos(byte[] fileContent) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(fileContent);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            List<DepositDto> depositDtos = (List<DepositDto>) ois.readObject();
            for (DepositDto depositDto : depositDtos) {
                depositDto.setId(null); // Reset ID for new entities
            }
            return depositDtos;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Failed to import customers", e);
        }
    }

    default List<DepositDto> jsonToDtos(byte[] fileContent){
        try {
            List<DepositDto> depositDtos = new ObjectMapper().readValue(fileContent, new ObjectMapper().getTypeFactory()
                    .constructCollectionType(List.class, DepositDto.class));
            for (DepositDto depositDto : depositDtos) {
                depositDto.setId(null); // Reset ID for new entities
            }
            return depositDtos;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON bytes to deposits", e);
        }
    }
}
