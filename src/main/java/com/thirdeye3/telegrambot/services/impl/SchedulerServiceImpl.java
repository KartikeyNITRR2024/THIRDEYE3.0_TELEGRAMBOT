package com.thirdeye3.telegrambot.services.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.thirdeye3.telegrambot.services.MessageSenderService;
import com.thirdeye3.telegrambot.services.SchedulerService;
import com.thirdeye3.telegrambot.exceptions.TelegramBotException;
import com.thirdeye3.telegrambot.utils.Initiatier;
import com.thirdeye3.telegrambot.utils.TimeManager;
import com.thirdeye3.telegrambot.utils.ApiClient;
import com.thirdeye3.telegrambot.dtos.Response;

@Service
public class SchedulerServiceImpl implements SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

    @Autowired
    private Initiatier initiatier;
    
    @Autowired
    private TimeManager timeManager;
    
    @Autowired
    private ApiClient apiClient;
    
    @Autowired
    private MessageSenderService messageSenderService;
    
    @Value("${telegrambot.baseUrlForCheckStatus}")
    private String baseUrl;

    @Value("${telegrambot.uniqueId}")
    private Integer uniqueId;

    @Value("${telegrambot.uniqueCode}")
    private String uniqueCode;
    
    @Value("${telegrambot.priority}")
    private Integer priority;

    @Override
    @Scheduled(cron = "${telegrambot.scheduler.cronToRefreshData}", zone = "${telegrambot.timezone}")
    public void runToRefreshdata() {
        try {
        	TimeUnit.SECONDS.sleep(priority * 3); 
            initiatier.init();
            logger.info("🔄 Data refreshed at {}", timeManager.getCurrentTime());
        } catch (Exception e) {
            logger.error("❌ Failed to refresh data at {}: {}", timeManager.getCurrentTime(), e.getMessage());
            throw new TelegramBotException("Scheduler runToRefreshdata failed: " + e.getMessage());
        }
    }
    
    @Override
    @Scheduled(fixedRateString = "${telegrambot.scheduler.runToCheckStatus}")
    public void statusChecker() {
        Response<String> response = apiClient.getForObject(
                baseUrl + "/api/statuschecker/" + uniqueId + "/" + uniqueCode,
                new ParameterizedTypeReference<Response<String>>() {}
                );
        logger.info("🔄 Checking status. result is {}", response.getResponse());

    }
    
    @Override
    @Scheduled(fixedRateString = "${telegrambot.scheduler.runToSendMessage}")
    public void readAndSendMessage() {
    	messageSenderService.readAndSendMessages("users");
    	messageSenderService.readAndSendMessages("telegramthresold");
        logger.info("Going to read and send messages");

    }
}

