package com.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Сервис, распределяющий сообщения от пользователя по очередям
 * Получает сообщения из телеграма и распределяет его в соответствующую очередь.
 * Далее передает в node service для последующей обработки.
 * После обработки нодой, принимает ответ от ноды, помещенный также в очередь брокера, и передает его в телеграм
 */

@SpringBootApplication
public class DispatcherApplication {

    public static void main(String[] args) {
        SpringApplication.run(DispatcherApplication.class, args);

    }
}
