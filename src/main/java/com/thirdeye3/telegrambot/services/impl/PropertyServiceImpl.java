package com.thirdeye3.telegrambot.services.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.thirdeye3.telegrambot.dtos.Response;
import com.thirdeye3.telegrambot.services.PropertyService;

@Service
public class PropertyServiceImpl implements PropertyService {

    private static final Logger logger = LoggerFactory.getLogger(PropertyServiceImpl.class);

    private Map<String, Object> properties;
    private Long maximumMessageReadFromMessageBroker;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${thirdeye.baseUrlForGateway}")
    private String baseUrl;

    @Value("${telegrambot.uniqueId}")
    private Integer uniqueId;

    @Value("${telegrambot.uniqueCode}")
    private String uniqueCode;

    @Value("${telegrambot.api.key}")
    private String telegramApiKey;

    @Override
    public void fetchProperties() {
        String url = baseUrl + "/pm/properties/webscrapper/" + uniqueId + "/" + uniqueCode;

        HttpHeaders headers = new HttpHeaders();
        headers.set("THIRDEYE-API-KEY", telegramApiKey);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Response<Map<String, Object>>> responseEntity =
                restTemplate.exchange(url, HttpMethod.GET, entity,
                        new ParameterizedTypeReference<Response<Map<String, Object>>>() {});

        Response<Map<String, Object>> response = responseEntity.getBody();

        if (response != null && response.isSuccess()) {
            properties = response.getResponse();
            maximumMessageReadFromMessageBroker =
                    ((Number) properties.getOrDefault("MAXIMUM_MESSAGE_READ_FROM_MESSAGE_BROKER", 50L)).longValue();
            logger.info("✅ Properties updated.");
        } else {
            throw new RuntimeException("Property update failed: " + (response != null ? response.getErrorMessage() : "null response"));
        }
    }

    @Override
    public Long getMaximumMessageReadFromMessageBroker() {
        return maximumMessageReadFromMessageBroker;
    }
}
