package com.thirdeye3.telegrambot.services.impl;

import java.time.LocalTime;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import com.thirdeye3.telegrambot.dtos.Response;
import com.thirdeye3.telegrambot.services.PropertyService;
import com.thirdeye3.telegrambot.utils.ApiClient;

@Service
public class PropertyServiceImpl implements PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyServiceImpl.class);
    private Map<String, Object> properties = null;
    private Long maximumMessageReadFromMessageBroker = null;

    @Autowired
    private ApiClient apiClient;

    @Value("${thirdeye.baseUrlForProperties}")
    private String baseUrl;

    @Value("${telegrambot.uniqueId}")
    private Integer uniqueId;

    @Value("${telegrambot.uniqueCode}")
    private String uniqueCode;

    @Override
    public void fetchProperties() {
        Response<Map<String, Object>> response = apiClient.getForObject(
                baseUrl + "/api/properties/webscrapper/" + uniqueId + "/" + uniqueCode,
                new ParameterizedTypeReference<Response<Map<String, Object>>>() {}
        );

        if (response.isSuccess()) {
            logger.info("✅ Properties updated.");
            properties = response.getResponse();
            maximumMessageReadFromMessageBroker = ((Number) properties.getOrDefault("MAXIMUM_MESSAGE_READ_FROM_MESSAGE_BROKER", 50L)).longValue();

            //logger.info("Properties are {}, {}, {}", telegramBotUserName, telegramBotToken);
        } else {
            logger.error("❌ Error ({}): {}", response.getErrorCode(), response.getErrorMessage());
            // propagate error for global handler
            throw new RuntimeException("Property update failed: " + response.getErrorMessage());
        }
    }
    
    @Override
    public Long getMaximumMessageReadFromMessageBroker() {
        return maximumMessageReadFromMessageBroker;
    }
}

