package com.tgbot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Сервис для считывания сообщений из очередей брокера
 */
public interface ConsumerService {

    void consumeTextMessage(Update update);
    void consumePhotoMessage(Update update);
    void consumeDocMessage(Update update);

}
