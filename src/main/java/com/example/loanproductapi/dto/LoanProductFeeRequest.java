package com.example.loanproductapi.dto;

import com.example.loanproductapi.enums.FeeCalculationType;
import com.example.loanproductapi.enums.FeeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class LoanProductFeeRequest {
    @NotNull
    private FeeType feeType;

    @NotNull
    private FeeCalculationType calculationType;

    @NotNull
    @Positive
    private BigDecimal amount; // Fixed amount or percentage

    @Min(0)
    private Integer daysAfterDue;
}
