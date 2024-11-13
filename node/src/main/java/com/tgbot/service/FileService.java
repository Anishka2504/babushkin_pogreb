package com.tgbot.service;

import com.tgbot.entity.AppDocument;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Этот сервис получает телеграммовское сообщение, выполняет все необходимые действия для скачивания файла и сохраняет его в бд
 */
public interface FileService {

    AppDocument processDoc(Message telegramMessage);
}
