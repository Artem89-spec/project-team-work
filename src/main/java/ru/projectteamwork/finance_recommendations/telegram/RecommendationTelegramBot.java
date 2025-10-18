package ru.projectteamwork.finance_recommendations.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.projectteamwork.finance_recommendations.service.RecommendationsService;

@Component
public class RecommendationTelegramBot extends TelegramLongPollingBot {

    private final RecommendationsService recommendationsService;

    private final String botUsername;
    private final String botToken;

    // 👇 конструктор, где Spring внедрит зависимости
    public RecommendationTelegramBot(
            RecommendationsService recommendationsService,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken) {
        this.recommendationsService = recommendationsService;
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();

            if (text.startsWith("/recommend")) {
                String[] parts = text.split(" ", 2);
                if (parts.length < 2) {
                    send(chatId, "Укажите имя и фамилию пользователя.");
                    return;
                }

                String name = parts[1].trim();
                var recommendations = recommendationsService.getRecommendationsForUser(name);

                if (recommendations.isEmpty()) {
                    send(chatId, "Пользователь не найден или нет рекомендаций.");
                } else {
                    StringBuilder response = new StringBuilder("Рекомендации для " + name + ":\n");
                    recommendations.forEach(r ->
                            response.append("- ").append(r.getName()).append(": ").append(r.getText()).append("\n")
                    );
                    send(chatId, response.toString());
                }
            } else {
                send(chatId, "Используй команду: /recommend Имя Фамилия");
            }
        }
    }

    private void send(String chatId, String text) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

