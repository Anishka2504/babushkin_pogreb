package com.tgbot.service;

import com.tgbot.entity.BotUser;

public interface BotUserService {

    String registerUser(BotUser user);

    String setEmail(BotUser user, String email);
}
