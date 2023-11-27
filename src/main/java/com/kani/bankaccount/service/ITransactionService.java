package com.kani.bankaccount.service;

import com.kani.bankaccount.dto.TransactionDto;

public interface ITransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
