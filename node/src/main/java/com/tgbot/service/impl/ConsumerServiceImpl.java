package com.tgbot.service.impl;

import com.tgbot.service.ConsumerService;
import com.tgbot.service.ProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.tgbot.model.RabbitQueue.DOC_MESSAGE_UPDATE;
import static com.tgbot.model.RabbitQueue.PHOTO_MESSAGE_UPDATE;
import static com.tgbot.model.RabbitQueue.TEXT_MESSAGE_UPDATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumerServiceImpl implements ConsumerService {

    private final ProducerService producerService;

    @Override
    @RabbitListener(queues = TEXT_MESSAGE_UPDATE)
    public void consumeTextMessage(Update update) {
        log.info("NODE: Received text message {}", update.getMessage().getText());
        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Hello from NODE!");
        producerService.produceAnswer(sendMessage);

    }

    @Override
    @RabbitListener(queues = PHOTO_MESSAGE_UPDATE)
    public void consumePhotoMessage(Update update) {
        log.info("NODE: Received photo message");

    }

    @Override
    @RabbitListener(queues = DOC_MESSAGE_UPDATE)
    public void consumeDocMessage(Update update) {
        log.info("NODE: Received doc message");

    }
}
