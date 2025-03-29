package com.example.loanproductapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoanProductFilterRequest {
    private String loanProductName;
    private String tenureType;
    private Integer active;
    private Integer id;

    @Builder.Default
    @NotNull(message = "Page number is not supposed to be empty.")
    @Min(1)
    private int page = 1;

    @Builder.Default
    @NotNull(message = "Limit is not supposed to be empty.")
    @Min(5)
    private int size = 10;

}
