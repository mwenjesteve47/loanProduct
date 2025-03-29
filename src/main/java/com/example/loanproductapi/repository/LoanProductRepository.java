package com.example.loanproductapi.repository;

import com.example.loanproductapi.dto.LoanProductFilterRequest;
import com.example.loanproductapi.enums.TenureType;
import com.example.loanproductapi.enums.TenureUnit;
import com.example.loanproductapi.model.LoanProduct;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoanProductRepository extends JpaRepository<LoanProduct,Long> {
    Optional<LoanProduct> findByIdAndActive(Long loanId, int active);


    @Query("""
     SELECT lp FROM LoanProduct lp
     LEFT JOIN FETCH Fee f ON f.loanProduct.id = lp.id
     WHERE (:loanProductName IS NULL OR lp.name = :loanProductName)
     AND (:tenureType IS NULL OR lp.tenureType = :tenureType)
     AND (:loanProductId IS NULL OR lp.id = :loanProductId)
     AND (:active IS NULL OR lp.active = :active)
    """)
    Page<LoanProduct> getLoanProductConfigurations(
            @Param("loanProductName") String loanProductName,
            @Param("tenureType") String tenureType,
            @Param("loanProductId") Integer loanProductId,
            @Param("active") int active,
            Pageable pageable);

    Optional<LoanProduct> findByNameAndTenureTypeAndTenureValueAndTenureUnit(@NotBlank String loanProductName,
                                                                             TenureType tenureType,
                                                                             @NotNull @Positive Integer tenureValue,
                                                                             TenureUnit tenureUnit);

}
