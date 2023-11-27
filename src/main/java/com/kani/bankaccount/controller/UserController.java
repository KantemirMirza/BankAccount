package com.kani.bankaccount.controller;

import com.kani.bankaccount.dto.*;
import com.kani.bankaccount.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/")
@Tag(name = "User Account Management APIs")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;

    @Operation(
            summary = "Create New User Account",
            description = "Creating a new user and assigning an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping
    public BankResponse createAccount(@RequestBody UserRequestDto userRequestDto){
        return userService.createAccount(userRequestDto);
    }

    @PostMapping
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }

    @Operation(
            summary = "Balance Enquiry",
            description = "Given an account number, check how much the user has"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("enquiryBalance")
    public BankResponse enquiryBalance(@RequestBody EnquiryBalanceRequest enquiryBalanceRequest){
        return userService.enquiryBalance(enquiryBalanceRequest);
    }


    @GetMapping("enquiry")
    public String enquiry(@RequestBody EnquiryBalanceRequest enquiry){
        return userService.enquiry(enquiry);
    }


    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest creditAccount){
        return userService.creditAccount(creditAccount);
    }

    @PostMapping("debit")
    public BankResponse creditDebitAccount(@RequestBody CreditDebitRequest creditDebitAccount){
        return userService.creditDebitAccount(creditDebitAccount);
    }

    @PostMapping("transfer")
    public BankResponse transfer(@RequestBody TransferRequest transferRequest){
        return userService.transfer(transferRequest);
    }
}
