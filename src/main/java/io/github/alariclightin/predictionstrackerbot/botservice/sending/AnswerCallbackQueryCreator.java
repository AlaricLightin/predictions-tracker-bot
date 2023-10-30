package io.github.alariclightin.predictionstrackerbot.botservice.sending;

import java.util.Locale;
import java.util.function.Supplier;

import org.springframework.context.MessageSource;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotCallbackAnswer;

class AnswerCallbackQueryCreator implements Supplier<AnswerCallbackQuery> {
    private final MessageSource messageSource;
    private final String callbackQueryId;
    private final String languageCode;
    private final BotCallbackAnswer botCallbackAnswer;

    AnswerCallbackQueryCreator(
        MessageSource messageSource,
        String callbackQueryId,
        String languageCode,
        BotCallbackAnswer botCallbackAnswer) {
        
        this.messageSource = messageSource;
        this.callbackQueryId = callbackQueryId;
        this.languageCode = languageCode;
        this.botCallbackAnswer = botCallbackAnswer;
    }

    @Override
    public AnswerCallbackQuery get() {
        String textId = botCallbackAnswer.messageId();
        String text = textId != null && !textId.isEmpty()
            ? messageSource.getMessage(textId, null, Locale.forLanguageTag(languageCode))
            : "";

        return AnswerCallbackQuery.builder()
            .callbackQueryId(callbackQueryId)
            .text(text)
            .build();
    }
    
}
