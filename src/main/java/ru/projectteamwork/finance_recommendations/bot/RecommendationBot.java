//package ru.projectteamwork.finance_recommendations.bot;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import ru.projectteamwork.finance_recommendations.dto.RecommendationDTO;
//import ru.projectteamwork.finance_recommendations.repository.UserLookupRepository;
//import ru.projectteamwork.finance_recommendations.service.RecommendationsService;
//
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class RecommendationBot extends TelegramLongPollingBot {
//    private static final Logger log = LoggerFactory.getLogger(RecommendationBot.class);
//
//    private final String username;
//    private final String token;
//    private final RecommendationsService recommendationsService;
//    private final UserLookupRepository userLookupRepository;
//
//    public RecommendationBot(
//            @Value("${telegram.bot.username}") String username,
//            @Value("${telegram.bot.token}") String token,
//            RecommendationsService recommendationsService,
//            UserLookupRepository userLookupRepository
//    ) {
//        super(token);
//        this.username = username;
//        this.token = token;
//        this.recommendationsService = recommendationsService;
//        this.userLookupRepository = userLookupRepository;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (!update.hasMessage() || !update.getMessage().hasText()) return;
//
//        Message msg = update.getMessage();
//        String text = msg.getText().trim();
//        long chatId = msg.getChatId();
//
//        if (text.equalsIgnoreCase("/start")) {
//            sendText(chatId, """
//                    👋 Hello! I'm your Finance Recommendations Bot.
//
//                    Use the command:
//                    /recommend FirstName LastName
//
//                    Example:
//                    /recommend Ivan Petrov
//                    """);
//            return;
//        }
//
//        if (text.toLowerCase().startsWith("/recommend")) {
//            handleRecommend(chatId, text);
//        }
//    }
//
//    private void handleRecommend(long chatId, String text) {
//        try {
//            String[] parts = text.split("\\s+", 3);
//            if (parts.length < 3) {
//                sendText(chatId, "❗ Please provide your first and last name: /recommend FirstName LastName");
//                return;
//            }
//
//            String fullName = parts[1] + " " + parts[2];
//
//            var userOpt = userLookupRepository.findSingleUserIdByFullName(fullName);
//            if (userOpt.isEmpty()) {
//                sendText(chatId, "🙁 Пользователь не найден.");
//                return;
//            }
//
//            UUID userId = userOpt.get();
//
//            List<RecommendationDTO> recommendations = recommendationsService.getRecommendationsForUser(userId.toString());
//            if (recommendations.isEmpty()) {
//                sendText(chatId, "Пока нет новых рекомендаций для вас.");
//            } else {
//                StringBuilder sb = new StringBuilder("Здравствуйте, " + fullName + "!\n\nНовые продукты для вас:\n\n");
//                for (RecommendationDTO rec : recommendations) {
//                    sb.append("• ").append(rec).append("\n");
//                }
//                sendText(chatId, sb.toString());
//            }
//
//        } catch (Exception e) {
//            log.error("Ошибка обработки команды /recommend", e);
//            sendText(chatId, "⚠️ Произошла ошибка при обработке команды. Попробуйте позже.");
//        }
//    }
//
//    private void sendText(long chatId, String text) {
//        try {
//            execute(SendMessage.builder().chatId(String.valueOf(chatId)).text(text).build());
//        } catch (TelegramApiException e) {
//            log.error("Ошибка при отправке сообщения пользователю", e);
//        }
//    }
//
//    @Override
//    public String getBotUsername() {
//        return username;
//    }
//}
