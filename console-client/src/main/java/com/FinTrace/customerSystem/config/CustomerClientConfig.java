package com.FinTrace.customerSystem.config;

import com.FinTrace.customerSystem.integration.AuthClient;
import com.FinTrace.customerSystem.integration.ClientAuthInterceptor;
import com.FinTrace.customerSystem.integration.exception.CustomerFeignErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectFactory;

@Configuration
public class CustomerClientConfig {
    @Bean
    public ErrorDecoder customerErrorDecoder() {
        return new CustomerFeignErrorDecoder();
    }

    @Bean
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean("customerClientAuthInterceptor")
    public RequestInterceptor customerClientAuthInterceptor(AuthClient authClient) {
        return new ClientAuthInterceptor(authClient);
    }
}