package com.example.loanproductapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ApplicationConfig {
    @Value("${loanProduct.late-fee-default-days}")
    private int lateFeeDefaultDays;
}
