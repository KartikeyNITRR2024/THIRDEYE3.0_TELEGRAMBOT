package com.thirdeye3.telegrambot.services;

import java.util.List;

import com.thirdeye3.telegrambot.dtos.Message;
import com.thirdeye3.telegrambot.dtos.TelegramMessage;

public interface MessageBrokerService {
	List<Message<TelegramMessage>> getMessage(String string);
}
