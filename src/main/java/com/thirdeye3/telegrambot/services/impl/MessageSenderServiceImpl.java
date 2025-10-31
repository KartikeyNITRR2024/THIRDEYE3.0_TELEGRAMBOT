package com.thirdeye3.telegrambot.services.impl;

import java.util.List;
import java.util.Map;

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
import com.thirdeye3.telegrambot.utils.GenericBot;

@Service
public class MessageSenderServiceImpl implements MessageSenderService {

    private static final Logger logger = LoggerFactory.getLogger(MessageSenderServiceImpl.class);

    @Autowired
    private MessageBrokerService messageBrokerService;

    @Autowired
    private Map<String, GenericBot> botRegistry;

    @Override
    public void readAndSendMessages(String type) {
        List<Message<TelegramMessage>> messages = messageBrokerService.getMessage(type);

        if (messages.isEmpty()) {
            return;
        }

        int total = 0;
        int success = 0;

        for (Message<TelegramMessage> message : messages) {
            TelegramMessage telegramMessage = message.getMessage();

            String chatName = telegramMessage.getChatName();
            GenericBot bot = botRegistry.get(chatName);

            if (bot == null) {
                logger.error("❌ No bot available to send messages.");
                return;
            }

            for (String chatText : telegramMessage.getChats()) {
                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(telegramMessage.getChatId());
                sendMessage.setText(chatText);

                if (chatText.contains("<b>") || chatText.contains("<i>") || chatText.contains("<code>")) {
                    sendMessage.setParseMode("HTML");
                }

                total++;
                try {
                    bot.execute(sendMessage);
                    success++;
                } catch (TelegramApiException e) {
                    logger.error("❌ Failed to send message: {}", e.getMessage());
                }
            }
        }

        logger.info("✅ Successfully sent {} out of {} messages", success, total);
    }
}
