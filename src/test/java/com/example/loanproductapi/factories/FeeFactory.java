package com.example.loanproductapi.factories;

import com.example.loanproductapi.enums.FeeCalculationType;
import com.example.loanproductapi.enums.FeeType;
import com.example.loanproductapi.enums.TenureType;
import com.example.loanproductapi.enums.TenureUnit;
import com.example.loanproductapi.model.Fee;
import com.example.loanproductapi.model.LoanProduct;
import com.example.loanproductapi.repository.FeeRepository;
import com.example.loanproductapi.repository.LoanProductRepository;
import com.example.loanproductapi.testHelpers.SpringContext;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FeeFactory {
    public static Fee create(LoanProduct loanProduct) {
        Fee fee = new Fee();
        fee.setLoanProduct(loanProduct);
        fee.setFeeType(FeeType.SERVICE_FEE);
        fee.setCalculationType(FeeCalculationType.FIXED);
        fee.setAmount(BigDecimal.valueOf(1000));
        fee.setActive(1);
        fee.setDateCreated(LocalDateTime.now());
        fee.setDateModified(LocalDateTime.now());

        return SpringContext.getBean(FeeRepository.class).save(fee);
    }

    public static void deleteAll() {
        SpringContext.getBean(FeeRepository.class).deleteAll();
    }
}
