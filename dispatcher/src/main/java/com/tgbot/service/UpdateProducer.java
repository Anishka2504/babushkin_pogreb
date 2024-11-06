package com.tgbot.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducer {

    /**
     * Распределяет сообщения по очередям брокера в соответствии с типом сообщения
     * @param rabbitQueue имя очереди
     * @param update объект сообщения
     */
    void produce(String rabbitQueue, Update update);
}
