package com.tgbot.service.impl;

import com.tgbot.dto.MailParams;
import com.tgbot.service.MailSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailSenderServiceImpl implements MailSenderService {

    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String emailFrom;
    @Value("${service.activation.uri}")
    private String activationServiceUri;

    @Override
    public void sendMail(MailParams mailParams) {
        var subject = "Активация учётной записи";
        var messageBody = getActivationMailBody(mailParams.id());
        var emailTo = mailParams.emailTo();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(emailTo);
        message.setSubject(subject);
        message.setText(messageBody);

        mailSender.send(message);
    }

    private String getActivationMailBody(String id) {
        var msg = String.format("Для завершения активации перейдите по ссылке: \n%s", activationServiceUri);
        return msg.replace("{id}", id);
    }
}
