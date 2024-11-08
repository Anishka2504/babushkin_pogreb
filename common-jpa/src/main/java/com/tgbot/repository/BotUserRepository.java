package com.tgbot.repository;

import com.tgbot.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotUserRepository extends JpaRepository<BotUser, Long> {

    BotUser findBotUserByTelegramUserId(Long telegramUserId);


}
