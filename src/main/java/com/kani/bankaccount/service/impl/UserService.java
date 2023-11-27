package com.kani.bankaccount.service.impl;

import com.kani.bankaccount.config.JwtTokenProvider;
import com.kani.bankaccount.dto.*;
import com.kani.bankaccount.entity.Role;
import com.kani.bankaccount.entity.User;
import com.kani.bankaccount.repository.IUserRepository;
import com.kani.bankaccount.service.IEmailService;
import com.kani.bankaccount.service.ITransactionService;
import com.kani.bankaccount.service.IUserService;
import com.kani.bankaccount.utils.AccountUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepository;
    private final IEmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final ITransactionService transactionService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public BankResponse createAccount(UserRequestDto userRequestDto) {
        if (userRepository.existsByEmail(userRequestDto.getEmail())){
            return BankResponse.builder()
                    .responseCode(AccountUtil.ACCOUNT_EXISTS_CODE)
                    .responseMessage(AccountUtil.ACCOUNT_EXISTS_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User newUser = User.builder()
                .firstName(userRequestDto.getFirstName())
                .lastName(userRequestDto.getLastName())
                .gender(userRequestDto.getGender())
                .address(userRequestDto.getAddress())
                .accountNumber(AccountUtil.generateAccountNumber())
                .accountBalance(BigDecimal.ZERO)
                .email(userRequestDto.getEmail())
                .password(passwordEncoder.encode(userRequestDto.getPassword()))
                .phoneNumber(userRequestDto.getPhoneNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_USER"))
                .build();

        User savedUser = userRepository.save(newUser);
        //Send email Alert
        EmailDetail emailDetail = EmailDetail.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations! Your Account Has been Successfully Created.\nYour Account Details: \n" +
                        "Account Name: " + savedUser.getFirstName() + " " + savedUser.getLastName() + " " + "\nAccount Number: " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetail);

        return BankResponse.builder()
                .responseCode(AccountUtil.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtil.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountOwner(savedUser.getFirstName() + " " + savedUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public BankResponse enquiryBalance(EnquiryBalanceRequest enquiryBalanceRequest) {
        boolean isAccountExist = userRepository.existsByAccountNumber(enquiryBalanceRequest.getEnquiryBalanceAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtil.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtil.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(enquiryBalanceRequest.getEnquiryBalanceAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtil.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtil.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(enquiryBalanceRequest.getEnquiryBalanceAccountNumber())
                        .accountOwner(foundUser.getFirstName() + " " + foundUser.getLastName())
                        .build())
                .build();
    }

    @Override
    public String enquiry(EnquiryBalanceRequest enquiryBalanceRequest) {
            boolean isAccountExist = userRepository.existsByAccountNumber(enquiryBalanceRequest.getEnquiryBalanceAccountNumber());
            if (!isAccountExist){
                return AccountUtil.ACCOUNT_NOT_EXIST_MESSAGE;
            }
            User foundUser = userRepository.findByAccountNumber(enquiryBalanceRequest.getEnquiryBalanceAccountNumber());
            return foundUser.getFirstName() + " " + foundUser.getLastName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest creditDebitRequest) {
            boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getCreditDebitAccountNumber());
            if (!isAccountExist){
                return BankResponse.builder()
                        .responseCode(AccountUtil.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtil.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }

            User userToCredit = userRepository.findByAccountNumber(creditDebitRequest.getCreditDebitAccountNumber());
            userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(creditDebitRequest.getCreditDebitAmount()));
            userRepository.save(userToCredit);

            //Save transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToCredit.getAccountNumber())
                    .transactionType("CREDIT")
                    .transactionAmount(creditDebitRequest.getCreditDebitAmount())
                    .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtil.ACCOUNT_CREDITED_SUCCESS)
                    .responseMessage(AccountUtil.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountOwner(userToCredit.getFirstName() + " " + userToCredit.getLastName())
                            .accountBalance(userToCredit.getAccountBalance())
                            .accountNumber(creditDebitRequest.getCreditDebitAccountNumber())
                            .build())
                    .build();
        }

    @Override
    public BankResponse creditDebitAccount(CreditDebitRequest creditDebitRequest) {

            boolean isAccountExist = userRepository.existsByAccountNumber(creditDebitRequest.getCreditDebitAccountNumber());
            if (!isAccountExist){
                return BankResponse.builder()
                        .responseCode(AccountUtil.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtil.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }

            User userToDebit = userRepository.findByAccountNumber(creditDebitRequest.getCreditDebitAccountNumber());
            BigInteger availableBalance =userToDebit.getAccountBalance().toBigInteger();
            BigInteger debitAmount = creditDebitRequest.getCreditDebitAmount().toBigInteger();
            if ( availableBalance.intValue() < debitAmount.intValue()){
                return BankResponse.builder()
                        .responseCode(AccountUtil.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtil.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();
            } else {
                userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(creditDebitRequest.getCreditDebitAmount()));
                userRepository.save(userToDebit);
                TransactionDto transactionDto = TransactionDto.builder()
                        .accountNumber(userToDebit.getAccountNumber())
                        .transactionType("CREDIT")
                        .transactionAmount(creditDebitRequest.getCreditDebitAmount())
                        .build();

                transactionService.saveTransaction(transactionDto);
                return BankResponse.builder()
                        .responseCode(AccountUtil.ACCOUNT_DEBITED_SUCCESS)
                        .responseMessage(AccountUtil.ACCOUNT_DEBITED_MESSAGE)
                        .accountInfo(AccountInfo.builder()
                                .accountNumber(creditDebitRequest.getCreditDebitAccountNumber())
                                .accountOwner(userToDebit.getFirstName() + " " + userToDebit.getLastName())
                                .accountBalance(userToDebit.getAccountBalance())
                                .build())
                        .build();
            }
    }

    @Override
    public BankResponse transfer(TransferRequest transferRequest) {

            boolean isDestinationAccountExist = userRepository.existsByAccountNumber(transferRequest.getTransferDestinationAccountNumber());
            if (!isDestinationAccountExist){
                return BankResponse.builder()
                        .responseCode(AccountUtil.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtil.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }

            User sourceAccountUser = userRepository.findByAccountNumber(transferRequest.getTransferSourceAccountNumber());
            if (transferRequest.getTransferAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
                return BankResponse.builder()
                        .responseCode(AccountUtil.INSUFFICIENT_BALANCE_CODE)
                        .responseMessage(AccountUtil.INSUFFICIENT_BALANCE_MESSAGE)
                        .accountInfo(null)
                        .build();
            }

            sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(transferRequest.getTransferAmount()));
            String sourceUsername = sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName();

            userRepository.save(sourceAccountUser);

            EmailDetail debitAlert = EmailDetail.builder()
                    .subject("DEBIT ALERT")
                    .recipient(sourceAccountUser.getEmail())
                    .messageBody("The sum of " + transferRequest.getTransferAmount() + " has been deducted from your account! Your current balance is " + sourceAccountUser.getAccountBalance())
                    .build();

            emailService.sendEmailAlert(debitAlert);

            User destinationAccountUser = userRepository.findByAccountNumber(transferRequest.getTransferDestinationAccountNumber());
            destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(transferRequest.getTransferAmount()));
            userRepository.save(destinationAccountUser);
            EmailDetail creditAlert = EmailDetail.builder()
                    .subject("CREDIT ALERT")
                    .recipient(sourceAccountUser.getEmail())
                    .messageBody("The sum of " + transferRequest.getTransferAmount() + " has been sent to your account from " + sourceUsername + " Your current balance is " + sourceAccountUser.getAccountBalance())
                    .build();

            emailService.sendEmailAlert(creditAlert);

            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(destinationAccountUser.getAccountNumber())
                    .transactionType("CREDIT")
                    .transactionAmount(transferRequest.getTransferAmount())
                    .build();

            transactionService.saveTransaction(transactionDto);

            return BankResponse.builder()
                    .responseCode(AccountUtil.TRANSFER_SUCCESSFUL_CODE)
                    .responseMessage(AccountUtil.TRANSFER_SUCCESSFUL_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

    @Override
    public BankResponse login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        EmailDetail loginAlert = EmailDetail.builder()
                .subject("You're logged in")
                .recipient(loginDto.getEmail())
                .messageBody("You logged in to your account!!")
                .build();
        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login Successfully")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .accountInfo(null)
                .build();
    }
}
