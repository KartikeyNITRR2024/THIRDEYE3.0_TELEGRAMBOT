package com.thirdeye3.telegrambot.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.thirdeye3.telegrambot.dtos.Message;
import com.thirdeye3.telegrambot.dtos.TelegramMessage;
import com.thirdeye3.telegrambot.services.MessageBrokerService;
import com.thirdeye3.telegrambot.services.MessageSenderService;
import com.thirdeye3.telegrambot.utils.Bot;

@Service
public class MessageSenderServiceImpl implements MessageSenderService {

    private static final Logger logger = LoggerFactory.getLogger(MessageSenderServiceImpl.class);

    @Autowired
    private MessageBrokerService messageBrokerService;

    @Autowired
    private Bot bot;

    @Override
    public void readAndSendMessages(String type) {
        List<Message<TelegramMessage>> messages = messageBrokerService.getMessage(type);

        if (messages.isEmpty()) {
            return;
        }
        int count1 = 0;
        int count2 = 0;
        for (Message<TelegramMessage> message : messages) {
            TelegramMessage telegramMessage = message.getMessage();

            for (String chat : telegramMessage.getChats()) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(telegramMessage.getChatId());
                sendMessage.setText(chat);
                count1++;
                try {
                    bot.execute(sendMessage);
                    count2++;
                } catch (TelegramApiException e) {
                    logger.error("❌ Failed to send message to chat: {}", e.getMessage());
                }
            }
        }
        logger.info("Successfully send {} out of {} messages", count2, count1);
    }
}
