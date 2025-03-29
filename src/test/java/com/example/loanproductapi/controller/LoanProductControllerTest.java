package com.example.loanproductapi.controller;

import com.example.loanproductapi.enums.FeeCalculationType;
import com.example.loanproductapi.enums.FeeType;
import com.example.loanproductapi.factories.FeeFactory;
import com.example.loanproductapi.factories.LoanProductFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;



import com.example.loanproductapi.AbstractIntegrationTest;
import com.example.loanproductapi.dto.LoanProductFeeRequest;
import com.example.loanproductapi.dto.LoanProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class LoanProductControllerTest extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void preCleanUp() {
        FeeFactory.deleteAll();
        LoanProductFactory.deleteAll();
    }

    @Test
    void givenInvalidLoanProductRequest_whenCreatingLoanConfiguration_thenThrowBadRequestException() throws Exception {
        LoanProductRequest request = new LoanProductRequest();
        request.setDescription("description");
        request.setLoanProductName("Mortgage Loan");
        request.setTenureType("test");
        request.setTenureUnit("Months");
        request.setTenureValue(1);

        mockMvc.perform(post("/loan-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists()) // Ensure error message exists
                .andExpect(jsonPath("$.message").value("Invalid TenureType: test"));
    }

    @Test
    void givenValidLoanProductRequest_whenCreatingLoanConfiguration_thenSuccess() throws Exception {
        LoanProductRequest request = new LoanProductRequest();
        request.setDescription("Short term personal loan");
        request.setLoanProductName("Personal Loan");
        request.setTenureType("Fixed");
        request.setTenureUnit("Months");
        request.setTenureValue(1);

        MvcResult result = mockMvc.perform(post("/loan-product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.name").value("Personal Loan"))
                .andExpect(jsonPath("$.data.description").value("Short term personal loan"))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        assertThat(response).contains("success", "data");
    }

    @Test
    void givenvalidFeeRequest_whenAddingFeesToLoan_thenReturnSuccess() throws Exception {
        var mockedLoanProduct = LoanProductFactory.create();
        var feeRequest = LoanProductFeeRequest.builder().feeType(FeeType.SERVICE_FEE)
                .calculationType(FeeCalculationType.FIXED)
                .amount(BigDecimal.valueOf(100.0))
                .daysAfterDue(30).build();
        List<LoanProductFeeRequest> fees = List.of(feeRequest);

        mockMvc.perform(post("/loan-product/"+mockedLoanProduct.getId()+"/fee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fees)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fees[0].feeType").value("SERVICE_FEE"))
                .andExpect(jsonPath("$.data.fees[0].amount").value(100.00));
    }
    @Test
    void givenNonExistentLoanProductId_whenAddingFeesToLoan_thenReturnNotFound() throws Exception {
        // Given: A non-existent loan product ID
        Long nonExistentLoanProductId = 9999L;

        var feeRequest = LoanProductFeeRequest.builder()
                .feeType(FeeType.SERVICE_FEE)
                .calculationType(FeeCalculationType.FIXED)
                .amount(BigDecimal.valueOf(100.0))
                .daysAfterDue(30)
                .build();

        List<LoanProductFeeRequest> fees = List.of(feeRequest);

        // When: Sending the request for a non-existent loan product
        mockMvc.perform(post("/loan-product/" + nonExistentLoanProductId + "/fee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fees)))
                // Then: Expect a 404 Not Found response
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Loan not found"));
    }


    @Test
    void givenInvalidFeeRequest_whenAddingFeesToLoan_thenReturnBadRequest() throws Exception {
        // Given: A mock loan product and an invalid fee request (missing fee type)
        var mockedLoanProduct = LoanProductFactory.create();
        var feeRequest = LoanProductFeeRequest.builder()
                .calculationType(FeeCalculationType.FIXED)
                .amount(BigDecimal.valueOf(100.0))
                .daysAfterDue(30)
                .build();

        List<LoanProductFeeRequest> fees = List.of(feeRequest);

        // When: Sending the request with missing required fields
        mockMvc.perform(post("/loan-product/" + mockedLoanProduct.getId() + "/fee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fees)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());
    }


    @Test
    void givenExistentLoanProductId_whenDeactivatingLoanProduct_thenReturnSuccess() throws Exception {
        var mockedLoanProduct = LoanProductFactory.create();

        mockMvc.perform(put("/loan-product/"+mockedLoanProduct.getId()+"/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    @Test
    void givenNonExistentLoanProductId_whenDeactivatingLoanProduct_thenReturnNotFound() throws Exception {
        // Given: A non-existent loan product ID
        Long nonExistentLoanProductId = 9999L;

        // When: Sending the request for a non-existent loan product
        mockMvc.perform(put("/loan-product/" + nonExistentLoanProductId + "/deactivate"))
                // Then: Expect a 404 Not Found response
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Loan Product Does Not Exist."));
    }
    @Test
    void givenAlreadyDeactivatedLoanProduct_whenDeactivatingLoanProduct_thenReturnBadRequest() throws Exception {
        // Given: A loan product that is already deactivated
        var mockedLoanProduct = LoanProductFactory.create(0);

        // When: Sending the request to deactivate an already deactivated loan product
        mockMvc.perform(put("/loan-product/" + mockedLoanProduct.getId() + "/deactivate"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Loan Product is already inactive."));
    }

    @Test
    void givenValidFilterRequest_whenFetchingAllLoans_thenReturnLoansSuccessfully() throws Exception {
        // Given: Some loan products exist
        LoanProductFactory.create();

        mockMvc.perform(MockMvcRequestBuilders.get("/loan-product")
                        .param("page", "1")
                        .param("size", "10")
                        .param("loanProductName", "Mortgage Loan")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Loans retrieved successfully"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.data.content.[0].name").value("Mortgage Loan"))
                .andExpect(jsonPath("$.data.data.content.[0].tenureValue").value(1));
    }


}
