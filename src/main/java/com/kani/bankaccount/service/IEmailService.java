package com.kani.bankaccount.service;

import com.kani.bankaccount.dto.EmailDetail;

public interface IEmailService {
    void sendEmailAlert(EmailDetail emailDetail);
    void sendEmailWithAttachment(EmailDetail emailDetail);
}
