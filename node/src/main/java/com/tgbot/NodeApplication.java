package com.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Сервис для бизнес-логики.
 * Получает сообщения из диспатчера, достает айдишник чата, формирует ответы и передает обратно в диспатчер
 */
@SpringBootApplication
public class NodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NodeApplication.class, args);
    }
}
