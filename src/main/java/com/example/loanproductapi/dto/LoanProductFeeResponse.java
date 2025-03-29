package com.example.loanproductapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class LoanProductFeeResponse {
    private Long loanId;
    private String loanName;
    private List<FeeResponse> fees;
}
