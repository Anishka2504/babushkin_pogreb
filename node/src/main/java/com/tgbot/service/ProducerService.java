package com.tgbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    /**
     * Отправляет ответные сообщения бота из ноды в брокер.
     * @param sendMessage
     */

    void produceAnswer(SendMessage sendMessage);
}
