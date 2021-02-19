package com.cybertek.service;

import com.cybertek.entity.ConfirmationToken;
import org.springframework.mail.SimpleMailMessage;

public interface ConfirmationTokenService {

    ConfirmationToken save(ConfirmationToken token);
    void sendEmail(SimpleMailMessage mailMessage);
    ConfirmationToken readByToken();
    void delete(ConfirmationToken confirmationToken);
}
