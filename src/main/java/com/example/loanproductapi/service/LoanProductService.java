package com.example.loanproductapi.service;

import com.example.loanproductapi.config.ApplicationConfig;
import com.example.loanproductapi.dto.*;
import com.example.loanproductapi.enums.FeeCalculationType;
import com.example.loanproductapi.enums.TenureType;
import com.example.loanproductapi.enums.TenureUnit;
import com.example.loanproductapi.exception.DuplicateResourceException;
import com.example.loanproductapi.exception.ResourceNotFoundException;
import com.example.loanproductapi.model.Fee;
import com.example.loanproductapi.model.LoanProduct;
import com.example.loanproductapi.repository.FeeRepository;
import com.example.loanproductapi.repository.LoanProductRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class LoanProductService {

    private final FeeRepository feeRepository;
    private final ApplicationConfig applicationConfig;
    private final LoanProductRepository loanProductRepository;
    public static final String SORT_BY_DATE_CREATED = "dateCreated";

    public LoanProductService(FeeRepository feeRepository, ApplicationConfig applicationConfig, LoanProductRepository loanProductRepository) {
        this.feeRepository = feeRepository;
        this.applicationConfig = applicationConfig;
        this.loanProductRepository = loanProductRepository;
    }

    public ApiResponse<?> createLoanCofiguration(@Valid LoanProductRequest request) {
        log.info("Creating loan configuration for request: {}", request);
        validateLoanTenure(request);

        // Check if the loan product already exists
        Optional<LoanProduct> existingLoanProduct = loanProductRepository
                .findByNameAndTenureTypeAndTenureValueAndTenureUnit(
                        request.getLoanProductName(),
                        TenureType.fromValue(request.getTenureType()),
                        request.getTenureValue(),
                        TenureUnit.fromValue(request.getTenureUnit())
                );

        if (existingLoanProduct.isPresent()) {
            throw new DuplicateResourceException(
                    "A loan product with the name '" + request.getLoanProductName() +
                            "', tenure type '" + request.getTenureType() +
                            "', tenure value '" + request.getTenureValue() +
                            "', and tenure unit '" + request.getTenureUnit() + "' already exists."
            );
        }
        LoanProduct loanProduct = LoanProduct.builder()
                .name(request.getLoanProductName())
                .description(request.getDescription())
                .tenureType(TenureType.fromValue(request.getTenureType()))
                .tenureValue(request.getTenureValue())
                .tenureUnit(TenureUnit.fromValue(request.getTenureUnit()))
                .active(1)
                .dateCreated(LocalDateTime.now())
                .dateModified(LocalDateTime.now())
                .build();

        LoanProduct savedLoanProduct = loanProductRepository.save(loanProduct);

        // Return response
        return new ApiResponse<>(true, "Loan created successfully", savedLoanProduct);
    }

    private void validateLoanTenure(LoanProductRequest request) {
        if (TenureType.FIXED.name().equalsIgnoreCase(request.getTenureType()) && request.getTenureUnit() == null) {
            throw new IllegalArgumentException("Fixed tenure requires a unit (DAYS or MONTHS).");
        }

        if (request.getTenureValue() == null || request.getTenureValue() <= 0) {
            throw new IllegalArgumentException("Tenure value must be greater than zero.");
        }
    }

    public ApiResponse<?> addOrUpdateFeesToLoan(Long loanId, List<LoanProductFeeRequest> fees) {
        var loanProduct = loanProductRepository.findByIdAndActive(loanId,1)
                .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        List<Fee> updatedFees = new ArrayList<>();
        for (LoanProductFeeRequest feeRequest : fees) {
            validateFee(feeRequest, loanId);

            Optional<Fee> existingFee = feeRepository.findByLoanProductIdAndFeeType(loanId, feeRequest.getFeeType());

            if (existingFee.isPresent()) {
                // Update existing fee
                Fee fee = existingFee.get();
                fee.setCalculationType(feeRequest.getCalculationType());
                fee.setAmount(feeRequest.getAmount());
                fee.setDaysAfterDue(feeRequest.getDaysAfterDue());
                fee.setDateModified(LocalDateTime.now());
                updatedFees.add(fee);
            } else {
                // Create new fee
                Fee newFee = Fee.builder()
                        .loanProduct(loanProduct)
                        .feeType(feeRequest.getFeeType())
                        .calculationType(feeRequest.getCalculationType())
                        .amount(feeRequest.getAmount())
                        .daysAfterDue(feeRequest.getDaysAfterDue())
                        .dateCreated(LocalDateTime.now())
                        .active(1)
                        .build();
                updatedFees.add(newFee);
            }
        }

        feeRepository.saveAll(updatedFees);

        // Convert Fee objects to FeeResponse DTOs
        List<FeeResponse> feeResponses = updatedFees.stream()
                .map(fee -> new FeeResponse(
                        fee.getFeeType().name(),
                        fee.getCalculationType().name(),
                        fee.getAmount(),
                        fee.getActive(),
                        fee.getDaysAfterDue()
                )).toList();

        // Construct response without redundant loanProduct data
        Map<String, Object> responseData = new LinkedHashMap<>();
        responseData.put("loanId", loanProduct.getId());
        responseData.put("loanName", loanProduct.getName());
        responseData.put("fees", feeResponses);

        return new ApiResponse<>(true, "Fees updated successfully", responseData);
    }



    private void validateFee(LoanProductFeeRequest feeRequest, Long loanProductId) {
        // Check if the same fee type already exists for this loan product
        boolean feeExists = feeRepository.existsByLoanProductIdAndFeeType(loanProductId, feeRequest.getFeeType());

        if (feeExists) {
            throw new IllegalArgumentException("Fee of type " + feeRequest.getFeeType() + " already exists for this loan.");
        }

        if (feeRequest.getDaysAfterDue() == null) {
            feeRequest.setDaysAfterDue(applicationConfig.getLateFeeDefaultDays());
        }

        if (feeRequest.getCalculationType() == FeeCalculationType.PERCENTAGE &&
                feeRequest.getAmount().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Percentage-based fees cannot exceed 100%.");
        }
    }

    public ApiResponse<?> deactivateLoan(long loanId) {
        // Check if loan product exists
        var loanProduct = loanProductRepository.findById(loanId).orElseThrow(
                () -> new ResourceNotFoundException("Loan Product Does Not Exist.")
        );

        if (loanProduct.getActive() == 0) {
            return new ApiResponse<>(false, "Loan Product is already inactive.");
        }

        List<Fee> fees = feeRepository.findByLoanProductId(loanId);
        for(Fee fee : fees) {
            fee.setActive(0);
            fee.setDateModified(LocalDateTime.now());
        }
        loanProduct.setActive(0);
        loanProduct.setDateModified(LocalDateTime.now());
        // Save updated fees and loan product
        feeRepository.saveAll(fees);
        loanProductRepository.save(loanProduct);

        return new ApiResponse<>(true, "successfully deactivated loan product: "+loanProduct.getName());
    }

    public ApiResponse<?> getAllLoans(LoanProductFilterRequest request) {
        // Formulate paginated args to query data.
        Sort sort = Sort.by(SORT_BY_DATE_CREATED).descending();
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);

        // Log query parameters
        log.info("Fetching loan products with filters - Name: {}, Tenure Type: {}, ID: {}, Page: {}, Size: {}",
                request.getLoanProductName(),
                request.getTenureType(),
                request.getId(),
                request.getPage(),
                request.getSize()
        );
        // Filter
        Page<LoanProduct> loanProductPage = loanProductRepository.getLoanProductConfigurations(
                request.getLoanProductName(),
                request.getTenureType(),
                request.getId(),
                1,
                pageable
        );
        // Map loan products and fetch their fees
        List<LoanProductResponse> loanResponses = loanProductPage.getContent().stream()
                .map(lp -> {
                    List<FeeResponse> fees = feeRepository.findFeesByLoanProductId(lp.getId())
                            .stream()
                            .map(fee -> new FeeResponse(
                                    fee.getFeeType().name(),
                                    fee.getCalculationType().name(),
                                    fee.getAmount(),
                                    fee.getActive(),
                                    fee.getDaysAfterDue()
                            ))
                            .collect(Collectors.toList());

                    return new LoanProductResponse(
                            lp.getId(),
                            lp.getName(),
                            lp.getDescription(),
                            lp.getTenureType().name(),
                            lp.getTenureValue(),
                            lp.getTenureUnit().name(),
                            lp.getActive(),
                            fees // Add fees to response
                    );
                })
                .collect(Collectors.toList());
        return new ApiResponse<>(true, "Successfully fetched loan products", new PaginateObjectResponse<>(
                loanResponses,
                loanProductPage.getNumber(),
                loanProductPage.getSize(),
                loanProductPage.getTotalElements(),
                loanProductPage.getTotalPages(),
                loanProductPage.isLast()
        ));
    }
}
