package com.kani.bankaccount.service.impl;

import com.kani.bankaccount.dto.TransactionDto;
import com.kani.bankaccount.entity.Transaction;
import com.kani.bankaccount.repository.ITransactionRepository;
import com.kani.bankaccount.service.ITransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionService implements ITransactionService {
    private final ITransactionRepository transactionRepository;
    @Override
    public void saveTransaction(TransactionDto transactionDto) {
            Transaction transaction = Transaction.builder()
                    .transactionType(transactionDto.getTransactionType())
                    .accountNumber(transactionDto.getAccountNumber())
                    .transactionAmount(transactionDto.getTransactionAmount())
                    .status("SUCCESS")
                    .build();
            transactionRepository.save(transaction);
            System.out.println("Transaction saved successfully");
        }
}
