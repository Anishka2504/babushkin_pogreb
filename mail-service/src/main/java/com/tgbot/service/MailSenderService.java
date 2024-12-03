package com.tgbot.service;

import com.tgbot.dto.MailParams;

public interface MailSenderService {

    void sendMail(MailParams mailParams);
}
