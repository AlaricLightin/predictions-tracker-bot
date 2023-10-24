package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import io.github.alariclightin.predictionstrackerbot.integration.IncomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.integration.OutcomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.messages.outbound.InlineButton;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

public abstract class AbstractGatewayTest extends TestWithContainer {
    @MockBean
    private TelegramLongPollingBot bot;
    
    @Autowired
    protected IncomingMessageGateway incomingMessageGateway;

    @SpyBean
    protected OutcomingMessageGateway outcomingMessageGateway;

    protected void assertResponseTextContainsFragments(CharSequence... expectedFragments) {
        ArgumentCaptor<SendMessage> response = ArgumentCaptor.forClass(SendMessage.class);
        verify(outcomingMessageGateway, atLeastOnce()).sendMessage(response.capture());
        assertSendMessageContainsFragments(response.getValue(), expectedFragments);
    }

    protected void assertSendMessageContainsFragments(SendMessage message, CharSequence... expectedFragments) {
        assertThat(message)
            .extracting(SendMessage::getText)
            .asString()
            .contains(expectedFragments);
    }

    protected void assertSendMessageContainsButtons(SendMessage message, List<InlineButton> expectedButtons) {
        assertThat(message)
            .extracting(SendMessage::getReplyMarkup)
            .isNotNull()
            .isInstanceOf(InlineKeyboardMarkup.class);

        List<InlineKeyboardButton> actualButtons = ((InlineKeyboardMarkup) message.getReplyMarkup()).getKeyboard().stream()
            .flatMap(List::stream)
            .toList();

        assertThat(actualButtons)
            .extracting(InlineKeyboardButton::getText, InlineKeyboardButton::getCallbackData)
            .containsExactlyElementsOf(
                expectedButtons.stream()
                    .map(button -> Tuple.tuple(button.messageId(), button.callbackString()))
                    .toList()
            );
    }

    protected void sendTextUpdate(String text) {
        incomingMessageGateway.handleUpdate(BotTestUtils.createTextUpdate(text));
    }

    protected void sendCallbackQueryUpdate(String command, String phase, String data) {
        incomingMessageGateway.handleUpdate(BotTestUtils.createCallbackQueryUpdate(
            String.join("::", command, phase, data)
        ));
    }

    protected void assertAnswerCallbackQueryTextIsEmpty() {
        ArgumentCaptor<AnswerCallbackQuery> response = ArgumentCaptor.forClass(AnswerCallbackQuery.class);
        verify(outcomingMessageGateway, atLeastOnce()).sendAnswerCallback(response.capture());
        assertThat(response.getValue().getText())
            .isEmpty();
    }

}
