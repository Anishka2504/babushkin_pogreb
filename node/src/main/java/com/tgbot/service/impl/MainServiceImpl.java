package com.tgbot.service.impl;

import com.tgbot.entity.RawData;
import com.tgbot.repository.RawDataRepository;
import com.tgbot.service.MainService;
import com.tgbot.service.ProducerService;
import com.tgbot.entity.BotUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import com.tgbot.repository.BotUserRepository;

import static com.tgbot.enums.UserState.BASIC;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;
    private final BotUserRepository botUserRepository;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var botUser =findOrSaveBotUser(telegramUser);

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Hello from NODE!");
        producerService.produceAnswer(sendMessage);
    }

    private BotUser findOrSaveBotUser(User telegramUser) {
        BotUser persistentUser = botUserRepository.findBotUserByTelegramUserId(telegramUser.getId());
        if (persistentUser == null) {
            BotUser transientUser = BotUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .state(BASIC)
                    //todo изменить значение по умолчанию после добавления регистрации
                    .isActive(true)
                    .build();
            return botUserRepository.save(transientUser);
        }
        return persistentUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }
}
