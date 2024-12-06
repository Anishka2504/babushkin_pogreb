package com.tgbot.service.impl;

import com.tgbot.entity.AppDocument;
import com.tgbot.entity.AppPhoto;
import com.tgbot.entity.RawData;
import com.tgbot.enums.LinkType;
import com.tgbot.exception.UploadFileException;
import com.tgbot.repository.RawDataRepository;
import com.tgbot.service.BotUserService;
import com.tgbot.service.FileService;
import com.tgbot.service.MainService;
import com.tgbot.service.ProducerService;
import com.tgbot.entity.BotUser;
import com.tgbot.service.enums.ServiceCommands;
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

    public static final String DOC_UPLOAD_SUCCESSFULLY_ANSWER = "Документ успешно загружен! Ссылка для скачивания: %s";
    public static final String PHOTO_UPLOAD_SUCCESSFULLY_ANSWER = "Изображение успешно загружено! Ссылка для скачивания: %s";
    public static final String ERROR_UPLOAD_MESSAGE_ANSWER = "Произошла ошибка во время загрузки файла. Повторите попытку позже";
    public static final String UNKNOWN_COMMAND_ERROR_ANSWER = "Неизвестная ошибка! Введите /cancel и попробуйте снова";
    public static final String REGISTRATION_ERROR_ANSWER = "Зарегистрируйтесь или подтвердите свою учётную запись для загрузки файлов";
    public static final String CANCEL_PROCESSING_ANSWER = "Для отправки файлов отмените текущую команду с помощью /cancel";

    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;
    private final BotUserRepository botUserRepository;
    private final FileService fileService;
    private final BotUserService botUserService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var botUser = findOrSaveBotUser(update);
        var userState = botUser.getState();
        var text = update.getMessage().getText();
        var serviceCommand = ServiceCommands.fromValue(text);

        var answer = "";

        //обработка статуса пользователя
        if (CANCEL.equals(serviceCommand)) {
            answer = processCancel(botUser);
        } else if (BASIC.equals(userState)) {
            answer = processServiceCommand(botUser, text);
        } else if (WAIT_FOR_EMAIL.equals(userState)) {
            answer = botUserService.setEmail(botUser, text);
        } else {
            log.error("Unknown state: {}", userState);
            answer = UNKNOWN_COMMAND_ERROR_ANSWER;
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

        try {
            AppDocument document = fileService.processDoc(update.getMessage());
            var link = fileService.generateLink(document.getId(), LinkType.GET_DOC);
            sendAnswer(String.format(DOC_UPLOAD_SUCCESSFULLY_ANSWER, link), chatId);
        } catch (UploadFileException ex) {
            log.error(ex.getMessage());
            sendAnswer(ERROR_UPLOAD_MESSAGE_ANSWER, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);

        var botUser = findOrSaveBotUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowedToSendContent(botUser, chatId)) {
            return;
        }

        try {
            AppPhoto appPhoto = fileService.processPhoto(update.getMessage());
            var link = fileService.generateLink(appPhoto.getId(), LinkType.GET_PHOTO);
            sendAnswer(String.format(PHOTO_UPLOAD_SUCCESSFULLY_ANSWER, link), chatId);
        } catch (UploadFileException ex) {
            log.error(ex.getMessage());
            sendAnswer(ERROR_UPLOAD_MESSAGE_ANSWER, chatId);
        }
    }

    private boolean isNotAllowedToSendContent(BotUser botUser, Long chatId) {
        var userState = botUser.getState();
        if (!botUser.getIsActive()) {
            sendAnswer(REGISTRATION_ERROR_ANSWER, chatId);
            return true;
        } else if (!BASIC.equals(userState)) {
            sendAnswer(CANCEL_PROCESSING_ANSWER, chatId);
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
        ServiceCommands serviceCommand = ServiceCommands.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommand)) {
            return botUserService.registerUser(botUser);
        } else if (HELP.equals(serviceCommand)) {
            return help();
        } else if (START.equals(serviceCommand)) {
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
        var optionalUser = botUserRepository.findByTelegramUserId(telegramUser.getId());
        if (optionalUser.isEmpty()) {
            BotUser transientUser = BotUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .state(BASIC)
                    .isActive(false)
                    .build();
            return botUserRepository.save(transientUser);
        }
        return optionalUser.get();
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }
}
