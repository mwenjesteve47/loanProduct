package com.example.loanproductapi.repository;

import com.example.loanproductapi.enums.FeeType;
import com.example.loanproductapi.model.Fee;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee, Long> {

    @Query("SELECT COUNT(f) > 0 FROM Fee f WHERE f.loanProduct.id = :loanProductId AND f.feeType = :feeType")
    boolean existsByLoanProductIdAndFeeType(@Param("loanProductId") Long loanProductId, @Param("feeType") FeeType feeType);

    Optional<Fee> findByLoanProductIdAndFeeType(Long loanId, @NotNull FeeType feeType);

    List<Fee> findByLoanProductId(long loanId);

    List<Fee> findFeesByLoanProductId(Long id);
}
