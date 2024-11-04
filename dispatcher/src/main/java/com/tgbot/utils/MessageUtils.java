package com.tgbot.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Класс для манипуляций с сообщениями от бота в адрес пользователей
 */

@Component
public class MessageUtils {

    /**
     * Создает сообщение для пользователя и наполняет его требуемыми данными
     * @param update - данные и сообщение от пользователя
     * @param text - текст сообщения от бота в адрес пользователя
     * @return sendMessage - объект сообщения от бота
     */

    public SendMessage generateSendMessageWithText(Update update, String text) {
        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        return sendMessage;
    }
}
