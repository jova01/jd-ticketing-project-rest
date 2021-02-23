package com.cybertek.service;

import com.cybertek.entity.ConfirmationToken;
import com.cybertek.exception.TicketingProjectException;
import org.springframework.mail.SimpleMailMessage;

public interface ConfirmationTokenService {

    ConfirmationToken save(ConfirmationToken token);
    void sendEmail(SimpleMailMessage mailMessage);
    ConfirmationToken readByToken(String token) throws TicketingProjectException;
    void delete(ConfirmationToken confirmationToken);
}
