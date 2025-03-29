package com.example.loanproductapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class FeeResponse {
    private String feeType;
    private String calculationType;
    private BigDecimal amount;
    private Integer active;
    private Integer daysAfterDue;
}
