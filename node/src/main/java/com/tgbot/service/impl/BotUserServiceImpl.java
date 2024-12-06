package com.tgbot.service.impl;

import com.tgbot.dto.MailParams;
import com.tgbot.entity.BotUser;
import com.tgbot.enums.UserState;
import com.tgbot.repository.BotUserRepository;
import com.tgbot.service.BotUserService;
import com.tgbot.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotUserServiceImpl implements BotUserService {

    private final BotUserRepository botUserRepository;
    private final CryptoTool cryptoTool;

    @Value("${service.mail.uri}")
    private String mailServiceUri;

    @Override
    public String registerUser(BotUser user) {
        if (user.getIsActive()) {
            return "Вы уже зарегистрированы";
        } else if (user.getEmail() != null) {
            return "На данный электронный адрес было отправлено письмо. Перейдите по ссылке в письме для активации пользователя";
        }
        user.setState(UserState.WAIT_FOR_EMAIL);
        botUserRepository.save(user);
        return "Введите ваш email";
    }

    @Override
    public String setEmail(BotUser user, String email) {
        try {
            InternetAddress emailAddress = new InternetAddress(email);
            emailAddress.validate();
        } catch (AddressException e) {
            return "Введите корректный email. Для отмены команды введите /cancel";
        }
        var optionalUser = botUserRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            user.setEmail(email);
            user.setState(UserState.BASIC);
            botUserRepository.save(user);

            var encryptedId = cryptoTool.hashOf(user.getId());
            var response = sendRequestToMailService(encryptedId, email);
            if (response.getStatusCode() != HttpStatus.OK) {
                var msg = String.format("Отправка письма на электронный адрес %s не удалась", email);
                log.error(msg);
                user.setEmail(null);
                user.setState(UserState.WAIT_FOR_EMAIL);
                botUserRepository.save(user);
                return msg;
            }
            return "Вам на почту было отправлено письмо. Перейдите по ссылке в письме, чтобы завершить регистрацию";
        } else {
            return "Данный email уже используется. Для отмены команды введите /cancel";
        }
    }

    private ResponseEntity<?> sendRequestToMailService(String encryptedId, String email) {
        var restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var mailParams = MailParams.builder()
                .id(encryptedId)
                .emailTo(email)
                .build();
        var request = new HttpEntity<>(mailParams, headers);
        return restTemplate.exchange(mailServiceUri,
                HttpMethod.POST,
                request,
                String.class);
    }
}
