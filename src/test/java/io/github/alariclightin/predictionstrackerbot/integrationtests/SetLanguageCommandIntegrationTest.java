package io.github.alariclightin.predictionstrackerbot.integrationtests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.alariclightin.predictionstrackerbot.testutils.TestConsts;

@SpringBootTest
class SetLanguageCommandIntegrationTest extends AbstractGatewayTest {
    
    @Test
    void shouldHandleSetLanguageCommand() {
        sendTextUpdate("/" + TestConsts.SET_LANGUAGE);

        assertResponseTextContainsFragments("language");
    }

    @Nested
    class AfterCommandWasSent {
        
        @BeforeEach
        void setUp() {
            sendTextUpdate("/" + TestConsts.SET_LANGUAGE);
        }

        @Test
        void shouldHandleCallbackFromButton() {
            sendButtonCallbackQueryUpdate(TestConsts.SET_LANGUAGE, "setlanguage-input", "RUSSIAN");

            assertResponseTextContainsFragments("язык", "Русский");
        }

    }
}
