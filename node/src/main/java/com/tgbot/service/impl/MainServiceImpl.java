package com.tgbot.service.impl;

import com.tgbot.entity.RawData;
import com.tgbot.repository.RawDataRepository;
import com.tgbot.service.MainService;
import com.tgbot.service.ProducerService;
import com.tgbot.entity.BotUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.tgbot.repository.BotUserRepository;

import static com.tgbot.enums.UserState.BASIC;
import static com.tgbot.enums.UserState.WAIT_FOR_EMAIL;
import static com.tgbot.service.enums.ServiceCommands.CANCEL;
import static com.tgbot.service.enums.ServiceCommands.HELP;
import static com.tgbot.service.enums.ServiceCommands.REGISTRATION;
import static com.tgbot.service.enums.ServiceCommands.START;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainServiceImpl implements MainService {

    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;
    private final BotUserRepository botUserRepository;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var botUser = findOrSaveBotUser(update);
        var userState = botUser.getState();
        var text = update.getMessage().getText();
        var answer = "";

        //обработка статуса пользователя
        if (CANCEL.equals(text)) {
            answer = processCancel(botUser);
        } else if (BASIC.equals(userState)) {
            answer = processServiceCommand(botUser, text);
        } else if (WAIT_FOR_EMAIL.equals(userState)) {
            //todo добавить обработку имейла
        } else {
            log.error("Unknown state: {}", userState);
            answer = "Неизвестная ошибка! Введите /cancel и попробуйте снова";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(answer, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);

        var botUser = findOrSaveBotUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowedToSendContent(botUser, chatId)) {
            return;
        }
        //todo добавить сохранение документа в бд
        var answer = "Документ успешно загружен! Ссылка для скачивания: http://tgbot.com/get-doc/777";
        sendAnswer(answer, chatId);

    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);

        var botUser = findOrSaveBotUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowedToSendContent(botUser, chatId)) {
            return;
        }
        //todo добавить сохранение документа в бд
        var answer = "Фото успешно загружено! Ссылка для скачивания: http://tgbot.com/get-photo/777";
        sendAnswer(answer, chatId);
    }

    private boolean isNotAllowedToSendContent(BotUser botUser, Long chatId) {
        var userState = botUser.getState();
        if (!botUser.getIsActive()) {
            var errorAnswer = "Зарегистрируйтесь или подтвердите свою учётную запись для загрузки файлов";
            sendAnswer(errorAnswer, chatId);
            return true;
        } else if (!BASIC.equals(userState)) {
            var errorAnswer = "Для отправки файлов отмените текущую команду с помощью /cancel";
            sendAnswer(errorAnswer, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String answer, Long chatId) {
        var sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(answer);
        producerService.produceAnswer(sendMessage);
    }

    private String processServiceCommand(BotUser botUser, String cmd) {
        if (REGISTRATION.equals(cmd)) {
            //todo добавить регистрацию
            return "Временно недоступно";
        } else if (HELP.equals(cmd)) {
            return help();
        } else if (START.equals(cmd)) {
            return "Приветствую! Чтобы просмотреть список доступных команд, введите /help";
        } else {
            return "Неизвестная команда! Чтобы просмотреть список доступных команд, введите /help";
        }
    }

    private String help() {
        return """
                Список доступных команд:
                /cancel - отмена выполнения текущей команды
                /registration - регистрация пользователя
                """;
    }

    private String processCancel(BotUser botUser) {
        botUser.setState(BASIC);
        botUserRepository.save(botUser);
        return "Команда отменена!";
    }

    private BotUser findOrSaveBotUser(Update update) {
        var telegramUser = update.getMessage().getFrom();
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
