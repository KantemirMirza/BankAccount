package com.kani.bankaccount.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserRequestDto {
    private String firstName;
    private String lastName;
    private String gender;
    private String address;
    private String email;
    private String phoneNumber;
    private String password;
}
