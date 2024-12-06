package com.tgbot.service;

public interface UserActivationService {

    boolean activate(String encryptedUserId);
}
