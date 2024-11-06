package com.tgbot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Читает ответы, которые были отправлены из ноды (модуль node)
 */
public interface AnswerConsumer {

    void consume(SendMessage sendMessage);
}
