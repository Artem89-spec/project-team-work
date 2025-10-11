package ru.projectteamwork.finance_recommendations.telegram;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@Component
public class BotInitializer {
    @Autowired
    private final LongPollingBot telegramBot; // ваш бин бота

    public BotInitializer(LongPollingBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(telegramBot);
            System.out.println("Бот зарегистрирован успешно");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


