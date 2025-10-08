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
            
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("<b>THIRDEYE Notification Service</b>\n")
                          .append("<i>Your communication gateway</i>\n\n")
                          .append("👋 <b>Dear User,</b>\n\n")
                          .append("📂 <b>Chat ID:</b> <code>")
                          .append(chatId)
                          .append("</code>\n\n")
                          .append("💡 Contact support if needed.\n\n")
                          .append("👍 Best regards,\n<b>THIRDEYE Team</b>");

            SendMessage reply = new SendMessage();
            reply.setChatId(chatId);
            reply.setText(messageBuilder.toString());
            reply.setParseMode("HTML");  

            try {
                execute(reply);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
