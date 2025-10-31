package com.thirdeye3.telegrambot.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.thirdeye3.telegrambot.utils.GenericBot;

@Configuration
public class BotConfig {

    @Value("#{'${telegram.bots.username}'.split(',')}")
    private List<String> usernames;

    @Value("#{'${telegram.bots.token}'.split(',')}")
    private List<String> tokens;

    @Bean
    public Map<String, GenericBot> botRegistry() {
        Map<String, GenericBot> map = new HashMap<>();

        if (usernames.size() != tokens.size()) {
            throw new IllegalArgumentException("Mismatch: number of usernames and tokens must be equal!");
        }

        for (int i = 0; i < usernames.size(); i++) {
            String username = usernames.get(i).trim();
            String token = tokens.get(i).trim();
            GenericBot bot = new GenericBot(username, token);
            map.put(username, bot);
        }

        return map;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(Map<String, GenericBot> botRegistry) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        for (GenericBot bot : botRegistry.values()) {
            botsApi.registerBot(bot);
        }
        return botsApi;
    }
}
