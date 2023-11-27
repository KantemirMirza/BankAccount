package com.kani.bankaccount.service;

import com.kani.bankaccount.dto.*;

public interface IUserService {
    BankResponse createAccount(UserRequestDto userRequestDto);
    BankResponse enquiryBalance(EnquiryBalanceRequest enquiryBalanceRequest);
    String enquiry(EnquiryBalanceRequest enquiryBalanceRequest);
    BankResponse creditAccount(CreditDebitRequest creditDebitRequest);
    BankResponse creditDebitAccount(CreditDebitRequest creditDebitRequest);
    BankResponse transfer(TransferRequest transferRequest);
    BankResponse login(LoginDto loginDto);
}
