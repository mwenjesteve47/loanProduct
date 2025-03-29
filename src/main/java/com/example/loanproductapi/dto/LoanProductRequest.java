package com.example.loanproductapi.dto;

import com.example.loanproductapi.enums.TenureType;
import com.example.loanproductapi.enums.TenureUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanProductRequest {
    @NotBlank(message = "loanProductName cannot be blank")
    private String loanProductName;

    @NotBlank(message = "description cannot be blank")
    private String description;

    @NotBlank(message = "tenureType cannot be blank")
    private String tenureType;

    @NotNull(message = "tenureValue cannot be null")
    @Positive
    private Integer tenureValue; // 12 months or 30 days

    @NotNull(message = "tenureUnit cannot be null")
    private String tenureUnit;

}
