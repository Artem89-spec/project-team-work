package ru.projectteamwork.finance_recommendations.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.util.WebhookUtils;
import ru.projectteamwork.finance_recommendations.service.RecommendationsService;

/**
 * Класс Telegram-бота, реализующий получение и отправку финансовых рекомендаций пользователям.
 * <p>
 * Бот принимает сообщения в Telegram, анализирует команды и обращается к сервису {@link RecommendationsService}
 * для получения рекомендаций по имени пользователя.
 * </p>
 * <p>
 * Использует режим long polling для обработки входящих сообщений.
 * </p>
 */
@Component
public class RecommendationTelegramBot extends TelegramLongPollingBot {

    private final RecommendationsService recommendationsService;
    private final String botUsername;
    private final String botToken;

    /**
     * Конструктор Telegram-бота.
     *
     * @param recommendationsService сервис, отвечающий за получение рекомендаций
     * @param botUsername имя Telegram-бота, заданное в настройках приложения
     * @param botToken токен доступа к Telegram Bot API
     */
    public RecommendationTelegramBot(
            RecommendationsService recommendationsService,
            @Value("${telegram.bot.username}") String botUsername,
            @Value("${telegram.bot.token}") String botToken) {
        this.recommendationsService = recommendationsService;
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    /**
     * Возвращает имя Telegram-бота
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return botUsername;
    }

    /**
     * Возвращает токен доступа Telegram-бота
     *
     * @return токен доступа
     */
    @Override
    public String getBotToken() {
        return botToken;
    }

    /**
     * Обрабатывает входящие сообщения от пользователей Telegram
     * <p>
     * Поддерживает команду <b>/recommend Имя Фамилия</b> для получения рекомендаций
     * В ответ бот отправляет список рекомендаций или сообщение об ошибке, если данные не найдены
     * </p>
     *
     * @param update объект обновления, содержащий входящее сообщение
     */
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

    /**
     * Отправляет текстовое сообщение пользователю в Telegram-чат
     *
     * @param chatId идентификатор чата
     * @param text текст сообщения
     */
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

    /**
     * Очищает webhook (если ранее был установлен), чтобы бот мог работать в режиме long polling
     * <p>
     * Метод используется чтобы при необходимости принудительно удалить старую конфигурацию webhook
     * </p>
     *
     * @throws TelegramApiRequestException если возникает ошибка при удалении webhook
     */
    public void clearWebhook() throws TelegramApiRequestException {
        try {
            WebhookUtils.clearWebhook(this);
        } catch (TelegramApiRequestException e) {
            if (e.getMessage() != null && e.getMessage().contains("404")) {
                // Webhook уже отсутствует — игнорируем
            } else {
                e.printStackTrace();
            }
        }
    }
}

