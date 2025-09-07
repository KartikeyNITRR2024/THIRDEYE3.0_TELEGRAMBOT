package com.thirdeye3.telegrambot.services;

import org.springframework.beans.factory.annotation.Autowired;

import com.thirdeye3.telegrambot.config.MessageBrokerConfig;

public interface MessageSenderService {
	void readAndSendMessages(String type);

}
