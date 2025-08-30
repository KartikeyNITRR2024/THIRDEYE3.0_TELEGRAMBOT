package com.thirdeye3.telegrambot.utils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//import com.thirdeye3.webscrapper.services.PropertyService;

@Component
public class TimeManager {
    
    @Value("${telegrambot.timezone}")
    private String timeZone;
    
//    @Autowired
//    private PropertyService propertyService;

    public Timestamp getCurrentTime() {
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.of(timeZone));
        LocalDateTime localDateTime = currentTime.toLocalDateTime();
        return Timestamp.valueOf(localDateTime);
    }

}

