package com.semmtech.laces.fetch.config;

import com.semmtech.laces.fetch.configuration.dtos.common.AddOnDto;
import com.semmtech.laces.fetch.configuration.facade.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceRegistryConfig {
    @Bean
    public ServiceRegistry<AddOnDto> addOnDtoServices() {
        return new ServiceRegistry<>();
    }


 }

