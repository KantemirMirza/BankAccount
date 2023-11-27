package com.kani.bankaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class TransactionDto {
    private String transactionType;
    private BigDecimal transactionAmount;
    private String accountNumber;
    private String status;
}
