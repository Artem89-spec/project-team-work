package ru.projectteamwork.finance_recommendations.telegram;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Инициализатор Telegram-бота приложения
 *
 * <p>Класс автоматически создаётся Spring-контейнером при запуске приложения
 * После инициализации контекста создаётся экземпляр {@link TelegramBotsApi},
 * и в него регистрируется реализация {@link LongPollingBot}, например
 * {@link ru.projectteamwork.finance_recommendations.telegram.RecommendationTelegramBot}.</p>
 *
 * <h3>Назначение:</h3>
 * <ul>
 *   <li>Создание сессии Telegram Bots API через {@link DefaultBotSession};</li>
 *   <li>Регистрация экземпляра Telegram-бота;</li>
 *   <li>Вывод информации о результате регистрации в консоль.</li>
 * </ul>
 *
 * <h3>Особенности:</h3>
 * <ul>
 *   <li>Инициализация выполняется один раз, после старта приложения, благодаря аннотации {@link PostConstruct};</li>
 *   <li>При ошибке регистрации исключение перехватывается и выводится в консоль;</li>
 *   <li>Не требует ручного вызова — выполняется автоматически при запуске Spring Boot.</li>
 * </ul>
 *
 * @author Project Team
 * @see org.telegram.telegrambots.meta.TelegramBotsApi
 * @see org.telegram.telegrambots.meta.generics.LongPollingBot
 * @see ru.projectteamwork.finance_recommendations.telegram.RecommendationTelegramBot
 */
@Component
public class BotInitializer {

    @Autowired
    private final LongPollingBot telegramBot;

    /**
     * Конструктор инициализатора
     *
     * @param telegramBot реализация {@link LongPollingBot}, обрабатывающая события Telegram
     */
    public BotInitializer(LongPollingBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    /**
     * Метод инициализации, выполняющий регистрацию Telegram-бота
     * через {@link TelegramBotsApi} после запуска контекста Spring
     * <p>Вызывается автоматически благодаря аннотации {@link PostConstruct}.</p>
     */
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


