package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import io.github.alariclightin.predictionstrackerbot.testutils.TestConsts;

@SpringBootTest
// TODO remove when state will be saved in db
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BaseCommandsIntegrationTest extends AbstractGatewayTest {

    @Test
    void shouldHandleStartCommand() {
        sendTextUpdate("/" + TestConsts.START_COMMAND);
        
        assertResponseTextContainsFragments("Hello");
    }

    @Test
    void shouldHandleUnpredictedTextMessage() {
        sendTextUpdate("test message");

        assertResponseTextContainsFragments("I don't understand you.");
    }

    @Test
    void shouldHandleInvalidCommand() {
        sendTextUpdate("/invalid");
        
        assertResponseTextContainsFragments("invalid", "is not a valid command");
    }

    @Test
    void shouldNotHandleOthersUpdates() {
        Update update = mock(Update.class);
        incomingMessageGateway.handleUpdate(update);

        verify(mockedOutcomingGateway, never()).sendMessage(any());
    }

    @Test
    void shouldHandleUnexpectedButtonCallback() {
        sendButtonCallbackQueryUpdate(TestConsts.SET_RESULTS_COMMAND, "set-result", "YES");

        ArgumentCaptor<AnswerCallbackQuery> response = ArgumentCaptor.forClass(AnswerCallbackQuery.class);
        verify(mockedOutcomingGateway).sendAnswerCallback(response.capture());
        assertThat(response.getValue().getText())
            .isNotEmpty();
    }

}
