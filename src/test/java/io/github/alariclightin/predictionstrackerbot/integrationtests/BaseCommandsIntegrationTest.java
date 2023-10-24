package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.api.objects.Update;

@SpringBootTest
class BaseCommandsIntegrationTest extends AbstractGatewayTest {

    @Test
    void shouldHandleStartCommand() {
        sendTextUpdate("/start");
        
        assertResponse("Hello");
    }

    @Test
    void shouldHandleUnpredictedTextMessage() {
        sendTextUpdate("test message");

        assertResponse("I don't understand you.");
    }

    @Test
    void shouldHandleInvalidCommand() {
        sendTextUpdate("/invalid");
        
        assertResponse("invalid", "is not a valid command");
    }

    @Test
    void shouldNotHandleOthersUpdates() {
        Update update = mock(Update.class);
        incomingMessageGateway.handleUpdate(update);

        verify(outcomingMessageGateway, never()).sendMessage(any());
    }

}
