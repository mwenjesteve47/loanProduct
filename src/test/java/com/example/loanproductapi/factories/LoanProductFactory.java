package com.example.loanproductapi.factories;

import com.example.loanproductapi.enums.TenureType;
import com.example.loanproductapi.enums.TenureUnit;
import com.example.loanproductapi.model.LoanProduct;
import com.example.loanproductapi.repository.LoanProductRepository;
import com.example.loanproductapi.testHelpers.SpringContext;

import java.time.LocalDateTime;

public class LoanProductFactory {
    public static LoanProduct create() {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setDescription("description");
        loanProduct.setName("Mortgage Loan");
        loanProduct.setTenureType(TenureType.fromValue("fixed"));
        loanProduct.setTenureUnit(TenureUnit.fromValue("Months"));
        loanProduct.setActive(1);
        loanProduct.setDateCreated(LocalDateTime.now());
        loanProduct.setDateModified(LocalDateTime.now());
        loanProduct.setTenureValue(1);

        return SpringContext.getBean(LoanProductRepository.class).save(loanProduct);
    }
    public static LoanProduct create(Integer active) {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setDescription("description");
        loanProduct.setName("Mortgage Loan");
        loanProduct.setTenureType(TenureType.fromValue("fixed"));
        loanProduct.setTenureUnit(TenureUnit.fromValue("Months"));
        loanProduct.setActive(active);
        loanProduct.setDateCreated(LocalDateTime.now());
        loanProduct.setDateModified(LocalDateTime.now());
        loanProduct.setTenureValue(1);

        return SpringContext.getBean(LoanProductRepository.class).save(loanProduct);
    }

    public static void deleteAll() {
        SpringContext.getBean(LoanProductRepository.class).deleteAll();
    }
}
