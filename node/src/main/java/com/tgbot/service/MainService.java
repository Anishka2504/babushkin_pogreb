package com.tgbot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Осуществляет обработку входящих сообщений и вызов специфических сервисов в зависимости от типа сообщений
 * Связующее звено между БД и {@link com.tgbot.service.ConsumerService}, который передает сообщения из брокера
 */
public interface MainService {

    void processTextMessage(Update update);
    void processDocMessage(Update update);
    void processPhotoMessage(Update update);
}
