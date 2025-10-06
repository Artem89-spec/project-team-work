package ru.projectteamwork.finance_recommendations.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.util.WebhookUtils;
import ru.projectteamwork.finance_recommendations.telegram.RecommendationTelegramBot;

@Configuration
public class TelegramBotConfig {

    private final RecommendationTelegramBot recommendationTelegramBot;

    public TelegramBotConfig(RecommendationTelegramBot recommendationTelegramBot) {
        this.recommendationTelegramBot = recommendationTelegramBot;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi() throws Exception {
        // Перед регистрацией бота пытаемся очистить webhook
        try {
            recommendationTelegramBot.clearWebhook();
        } catch (Exception e) {
            // Игнорируем ошибки, связанные с отсутствием webhook
            System.out.println("Webhook уже не установлен или не удалось его очистить: " + e.getMessage());
        }

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(recommendationTelegramBot);
        return botsApi;
    }
}