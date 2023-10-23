package io.github.alariclightin.predictionstrackerbot.integrationtests;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

}
