package com.example.loanproductapi.model;

import com.example.loanproductapi.enums.TenureType;
import com.example.loanproductapi.enums.TenureUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loanProducts")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private TenureType tenureType;

    private Integer tenureValue;

    @Enumerated(EnumType.STRING)
    private TenureUnit tenureUnit;
    private Integer active;
    private LocalDateTime dateModified;
    private LocalDateTime dateCreated;


}
