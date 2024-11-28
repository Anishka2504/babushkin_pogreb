package com.tgbot.service;

import com.tgbot.entity.AppDocument;
import com.tgbot.entity.AppPhoto;
import com.tgbot.enums.LinkType;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Этот сервис получает телеграммовское сообщение, выполняет все необходимые действия для скачивания файла и сохраняет его в бд
 */
public interface FileService {

    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long id, LinkType linkType);
}
