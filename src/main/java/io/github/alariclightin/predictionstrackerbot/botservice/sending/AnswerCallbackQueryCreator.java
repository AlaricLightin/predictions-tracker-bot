package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.util.Locale;
import java.util.function.Supplier;

import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import io.github.alariclightin.predictionstrackerbot.data.settings.UserLanguageService;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;

class AnswerCallbackQueryCreator implements Supplier<AnswerCallbackQuery> {
    private final MessageSource messageSource;
    private final UserLanguageService userLanguageService;
    private final String callbackQueryId;
    private final long userId;
    private final BotCallbackAnswer botCallbackAnswer;

    AnswerCallbackQueryCreator(
        MessageSource messageSource,
        UserLanguageService userLanguageService,
        String callbackQueryId,
        long userId,
        BotCallbackAnswer botCallbackAnswer) {
        
        this.messageSource = messageSource;
        this.userLanguageService = userLanguageService;
        this.callbackQueryId = callbackQueryId;
        this.userId = userId;
        this.botCallbackAnswer = botCallbackAnswer;
    }

    @Override
    public AnswerCallbackQuery get() {
        String textId = botCallbackAnswer.messageId();
        String text = textId != null && !textId.isEmpty()
            ? messageSource.getMessage(textId, null, 
                Locale.forLanguageTag(userLanguageService.getLanguageCode(userId)))
            : "";

        return AnswerCallbackQuery.builder()
            .callbackQueryId(callbackQueryId)
            .text(text)
            .build();
    }
    
}
