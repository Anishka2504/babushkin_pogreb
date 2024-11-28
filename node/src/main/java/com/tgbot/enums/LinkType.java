package com.tgbot.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LinkType {
    GET_DOC("/file/getDoc"),
    GET_PHOTO("/file/getPhoto");

    private final String link;

    @Override
    public String toString() {
        return link;
    }
}
