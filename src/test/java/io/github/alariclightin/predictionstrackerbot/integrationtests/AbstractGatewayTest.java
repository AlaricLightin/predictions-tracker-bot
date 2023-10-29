package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import io.github.alariclightin.predictionstrackerbot.integration.IncomingMessageGateway;
import io.github.alariclightin.predictionstrackerbot.messages.ButtonsConsts;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

public abstract class AbstractGatewayTest extends TestWithContainer {
    @MockBean
    private TelegramLongPollingBot bot;
    
    @Autowired
    protected IncomingMessageGateway incomingMessageGateway;

    @Autowired
    private PublishSubscribeChannel outcomingMessagesChannel;

    protected OutcomingMessageGateway mockedOutcomingGateway = mock(OutcomingMessageGateway.class);

    private ChannelInterceptor outcomingChannelInterceptor = new ChannelInterceptor() {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if (message.getPayload() instanceof SendMessage sendMessage) {
                mockedOutcomingGateway.sendMessage(sendMessage);
            } else if (message.getPayload() instanceof AnswerCallbackQuery answerCallbackQuery) {
                mockedOutcomingGateway.sendAnswerCallback(answerCallbackQuery);
            }
            return message;
        }
    };

    @BeforeEach
    void setUp() {
        outcomingMessagesChannel.addInterceptor(outcomingChannelInterceptor);
    }

    protected void sendTextUpdate(String text) {
        incomingMessageGateway.handleUpdate(BotTestUtils.createTextUpdate(text));
    }

    protected void sendButtonCallbackQueryUpdate(String command, String phase, String data) {
        incomingMessageGateway.handleUpdate(BotTestUtils.createCallbackQueryUpdate(
            String.join(ButtonsConsts.ID_DELIMITER, 
                ButtonsConsts.BUTTON_PREFIX, command, phase, data)
        ));
    }

    protected void assertResponseTextContainsFragments(CharSequence... expectedFragments) {
        ArgumentCaptor<SendMessage> response = ArgumentCaptor.forClass(SendMessage.class);
        verify(mockedOutcomingGateway, atLeastOnce()).sendMessage(response.capture());
        assertSendMessageContainsFragments(response.getValue(), expectedFragments);
    }

    protected void assertSendMessageContainsFragments(SendMessage message, CharSequence... expectedFragments) {
        assertThat(message)
            .extracting(SendMessage::getText)
            .asString()
            .contains(expectedFragments);
    }

    protected void assertSendMessageContainsButtons(SendMessage message, List<ButtonData> expectedButtons) {
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
                    .map(button -> Tuple.tuple(button.text(), button.callbackData()))
                    .toList()
            );
    }

    protected static record ButtonData(String text, String callbackData) {
    }

    protected void assertAnswerCallbackQueryTextIsEmpty() {
        ArgumentCaptor<AnswerCallbackQuery> response = ArgumentCaptor.forClass(AnswerCallbackQuery.class);
        verify(mockedOutcomingGateway, atLeastOnce()).sendAnswerCallback(response.capture());
        assertThat(response.getValue().getText())
            .isEmpty();
    }

    protected void assertAnswerCallbackQueryContainsFragments(CharSequence... expectedFragments) {
        ArgumentCaptor<AnswerCallbackQuery> response = ArgumentCaptor.forClass(AnswerCallbackQuery.class);
        verify(mockedOutcomingGateway, atLeastOnce()).sendAnswerCallback(response.capture());
        assertThat(response.getValue().getText())
            .contains(expectedFragments);
    }
}
