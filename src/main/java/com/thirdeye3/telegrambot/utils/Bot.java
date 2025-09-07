package com.thirdeye3.telegrambot.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${telegrambots.bots[0].username}")
    private String botUsername;

    @Value("${telegrambots.bots[0].token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String receivedText = update.getMessage().getText();

            SendMessage reply = new SendMessage();
            reply.setChatId(chatId);
            reply.setText(String.format(
            	    "<b>THIRDEYE Notification Service</b>\n" +
            	    "<i>Your communication gateway</i>\n\n" +
            	    "📂 <b>Chat ID:</b> <code>%s</code>\n" +
            	    "🟢 Status: <b>Active</b>\n\n" +
            	    "Please save this ID to receive all updates and notifications.\n\n" +
            	    "For assistance, reach out to our support team. We're here to assist you!",
            	    chatId
            	));
            	reply.setParseMode("HTML");

            try {
                execute(reply);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
