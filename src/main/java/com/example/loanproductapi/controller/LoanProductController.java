package com.example.loanproductapi.controller;

import com.example.loanproductapi.dto.*;
import com.example.loanproductapi.service.LoanProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/loan-product")
public class LoanProductController {

    private final LoanProductService loanProductService;

    public LoanProductController(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }

    @PostMapping
    public ResponseEntity<?> createLoanConfiguration(@Valid @RequestBody LoanProductRequest request) {
        var response = loanProductService.createLoanCofiguration(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{loanId}/fee")
    public ResponseEntity<ApiResponse<?>> addFeesToLoan(
            @PathVariable Long loanId,
            @RequestBody @Valid List<LoanProductFeeRequest> fees) {

        var response = loanProductService.addOrUpdateFeesToLoan(loanId, fees);
        return ResponseEntity.ok(response);
    }

    // Get all loans with filters
    @GetMapping
    public ResponseEntity<?> getAllLoans(@Valid LoanProductFilterRequest request) {
        var response = loanProductService.getAllLoans(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Loans retrieved successfully", response));
    }

    @PutMapping( "/{loanId}/deactivate")
    public ResponseEntity<?> deactivateLoanProduct(@PathVariable long loanId) {
        var response = loanProductService.deactivateLoan(loanId);
        return ResponseEntity.ok(response);
    }

}
