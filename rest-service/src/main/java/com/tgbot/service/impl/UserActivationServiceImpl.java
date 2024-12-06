package com.tgbot.service.impl;

import com.tgbot.repository.BotUserRepository;
import com.tgbot.service.UserActivationService;
import com.tgbot.utils.CryptoTool;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserActivationServiceImpl implements UserActivationService {

    private final BotUserRepository botUserRepository;
    private final CryptoTool cryptoTool;

    @Override
    public boolean activate(String encryptedUserId) {
        var userId = cryptoTool.idOf(encryptedUserId);
        var optionalUser = botUserRepository.findById(userId);
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();
            user.setIsActive(true);
            botUserRepository.save(user);
            return true;
        }
        return false;
    }
}
