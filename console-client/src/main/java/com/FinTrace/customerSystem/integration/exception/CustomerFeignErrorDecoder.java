package com.FinTrace.customerSystem.integration.exception;

import com.FinTrace.customerSystem.exception.CustomerNotFoundException;
import com.FinTrace.customerSystem.exception.DuplicateCustomerException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomerFeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            return new CustomerNotFoundException(
                    "Customer not found. Please check the customer ID or Name and try again."
            );
        } else if (response.status() == 409) {
            return new DuplicateCustomerException(
                    "Conflict error: A customer with the same details already exists."
            );
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }
}
