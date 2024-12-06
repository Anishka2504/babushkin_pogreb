package com.tgbot.repository;

import com.tgbot.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BotUserRepository extends JpaRepository<BotUser, Long> {

    Optional<BotUser> findByTelegramUserId(Long telegramUserId);
    Optional<BotUser> findById(Long id);
    Optional<BotUser> findByEmail(String email);


}
