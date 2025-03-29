package com.example.loanproductapi.model;

import com.example.loanproductapi.enums.FeeCalculationType;
import com.example.loanproductapi.enums.FeeType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "fees")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Fee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private LoanProduct loanProduct;

    @Enumerated(EnumType.STRING)
    private FeeType feeType;

    @Enumerated(EnumType.STRING)
    private FeeCalculationType calculationType;

    private BigDecimal amount;

    private Integer daysAfterDue;
    private Integer active;
    private LocalDateTime dateModified;
    private LocalDateTime dateCreated;


}
