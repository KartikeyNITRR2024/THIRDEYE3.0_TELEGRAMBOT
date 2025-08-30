package com.thirdeye3.telegrambot.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.thirdeye3.telegrambot.config.MessageBrokerConfig;
import com.thirdeye3.telegrambot.dtos.Response;
import com.thirdeye3.telegrambot.exceptions.MessageBrokerException;
import com.thirdeye3.telegrambot.dtos.Message;
import com.thirdeye3.telegrambot.dtos.TelegramMessage;
import com.thirdeye3.telegrambot.services.MessageBrokerService;
import com.thirdeye3.telegrambot.services.PropertyService;
import com.thirdeye3.telegrambot.utils.ApiClient;

@Service
public class MessageBrokerServiceImpl implements MessageBrokerService {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageBrokerServiceImpl.class);
	
    @Autowired
    private MessageBrokerConfig messageBrokerConfig;
    
    @Autowired
    private ApiClient apiClient;
    
    @Value("${thirdeye.messageBrokerUrl}")
    private String messageBrokerUrl;
    
    @Autowired
    private PropertyService propertyService;
	
	@Override
	public List<Message<TelegramMessage>> getMessage(String topicName)
	{
		List<Message<TelegramMessage>> messages = new ArrayList<>();
		if(!messageBrokerConfig.getTopics().containsKey(topicName))
    	{
    		throw new MessageBrokerException("Does not have any topic with topic name "+topicName);
    	}
		try {
    		Response<List<Message<TelegramMessage>>> response = apiClient.getForObject(
            		messageBrokerUrl + "/api/message/multiple/" + topicName +"/" +messageBrokerConfig.getTopics().get(topicName).getTopicKey()+"/"+propertyService.getMaximumMessageReadFromMessageBroker(),
                    new ParameterizedTypeReference<Response<List<Message<TelegramMessage>>>>() {}
                    );
    		if (response.isSuccess()) {
    			messages = response.getResponse();
                logger.info("Successfully received messages from message broker with topic name "+topicName);
            }
    		else
    		{
    		    throw new MessageBrokerException("Failed to recive messages from message broker with topic name "+topicName+" "+response.getErrorMessage());
    		}
    	} catch (Exception e) {
    		//throw new MessageBrokerException("Failed to recive messages from message broker with topic name "+topicName);
    		logger.info("Failed to read data");
        }
		return messages;
	}

}
