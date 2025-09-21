package com.thirdeye3.telegrambot.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thirdeye3.telegrambot.config.MessageBrokerConfig;
import com.thirdeye3.telegrambot.dtos.Response;
import com.thirdeye3.telegrambot.dtos.Message;
import com.thirdeye3.telegrambot.dtos.TelegramMessage;
import com.thirdeye3.telegrambot.exceptions.MessageBrokerException;
import com.thirdeye3.telegrambot.services.MessageBrokerService;
import com.thirdeye3.telegrambot.services.PropertyService;

@Service
public class MessageBrokerServiceImpl implements MessageBrokerService {

    private static final Logger logger = LoggerFactory.getLogger(MessageBrokerServiceImpl.class);

    @Autowired
    private MessageBrokerConfig messageBrokerConfig;

    @Autowired
    private PropertyService propertyService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${thirdeye.baseUrlForGateway}")
    private String messageBrokerUrl;

    @Value("${telegrambot.uniqueId}")
    private Integer uniqueId;

    @Value("${telegrambot.uniqueCode}")
    private String uniqueCode;

    @Value("${telegrambot.api.key}")
    private String telegramApiKey;

    @Override
    public List<Message<TelegramMessage>> getMessage(String topicName) {
        List<Message<TelegramMessage>> messages = new ArrayList<>();

        if (!messageBrokerConfig.getTopics().containsKey(topicName)) {
            throw new MessageBrokerException("Does not have any topic with topic name " + topicName);
        }

        try {
            String url = messageBrokerUrl + "/mb/message/telegrambot/multiple/" + uniqueId + "/" + uniqueCode + "/"
                    + topicName + "/" + messageBrokerConfig.getTopics().get(topicName).getTopicKey() + "/"
                    + propertyService.getMaximumMessageReadFromMessageBroker();

            HttpHeaders headers = new HttpHeaders();
            headers.set("telegrambot-api-key", telegramApiKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Response<List<Message<TelegramMessage>>>> responseEntity =
                    restTemplate.exchange(url, HttpMethod.GET, entity,
                            new ParameterizedTypeReference<Response<List<Message<TelegramMessage>>>>() {});

            Response<List<Message<TelegramMessage>>> response = responseEntity.getBody();

            if (response != null && response.isSuccess()) {
                messages = response.getResponse();
                logger.info("Successfully received messages from message broker with topic name {}", topicName);
            } else {
                throw new MessageBrokerException("Failed to receive messages from message broker with topic name "
                        + topicName);
            }
        } catch (Exception e) {
            logger.error("Failed to read data from message broker for topic {}", topicName, e);
        }

        return messages;
    }
}
