package com.tgbot.service.impl;

import com.tgbot.repository.RawDataRepository;
import com.tgbot.service.MainService;
import com.tgbot.service.ProducerService;
import com.tgbot.entity.RawData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class MainServiceImpl implements MainService {

    private final RawDataRepository rawDataRepository;
    private final ProducerService producerService;

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Hello from NODE!");
        producerService.produceAnswer(sendMessage);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataRepository.save(rawData);
    }
}
