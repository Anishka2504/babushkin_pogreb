package com.tgbot.dto;

import lombok.Builder;

@Builder
public record MailParams(String id, String emailTo) {
}
