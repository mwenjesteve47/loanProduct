package com.example.loanproductapi.dto;

import com.example.loanproductapi.enums.TenureType;
import com.example.loanproductapi.enums.TenureUnit;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@AllArgsConstructor
@Builder
public class LoanProductResponse {
    private Long id;
    private String name;
    private String description;
    private String tenureType;
    private Integer tenureValue;
    private String tenureUnit;
    private Integer active;
    private List<FeeResponse> fees;
}
