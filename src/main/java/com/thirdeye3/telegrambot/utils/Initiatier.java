package com.thirdeye3.telegrambot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thirdeye3.telegrambot.services.PropertyService;

import jakarta.annotation.PostConstruct;

@Component
public class Initiatier {
	
	@Autowired
	PropertyService propertyService;
	
    private static final Logger logger = LoggerFactory.getLogger(Initiatier.class);
	
	@PostConstruct
    public void init() throws Exception{
        logger.info("Initializing Initiatier...");
        propertyService.fetchProperties();
        logger.info("Initiatier initialized.");
    }

}
