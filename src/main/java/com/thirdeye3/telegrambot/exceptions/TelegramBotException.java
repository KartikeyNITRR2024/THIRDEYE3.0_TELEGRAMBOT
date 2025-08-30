package com.thirdeye3.telegrambot.exceptions;

public class TelegramBotException extends RuntimeException {
    public TelegramBotException(String message) {
        super(message);
    }
}
