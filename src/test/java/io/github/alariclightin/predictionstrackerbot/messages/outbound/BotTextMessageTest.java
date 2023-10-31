package io.github.alariclightin.predictionstrackerbot.messages.outbound;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BotTextMessageTest {

    @ParameterizedTest
    @MethodSource("dataForShouldAddTextMessages")
    void shouldAddTextMessages(BotTextMessage message1, BotTextMessage message2, BotTextMessage expected) {
        BotTextMessage result = message1.add(message2);

        assertThat(result)
            .usingRecursiveComparison()
            .isEqualTo(expected);
    }

    private static final BotKeyboard BOT_KEYBOARD = mock(BotKeyboard.class);

    private static Stream<Arguments> dataForShouldAddTextMessages() {
        return Stream.of(
            Arguments.of(
                new BotTextMessage("messageId1", "text1"),
                new BotTextMessage("messageId2", "text2"),
                new BotTextMessage(
                    List.of(
                        new BotTextMessage.TextData("messageId1", new Object[]{"text1"}), 
                        new BotTextMessage.TextData("messageId2", new Object[]{"text2"})
                    ), 
                    null
                )
            ),

            Arguments.of(
                new BotTextMessage(BOT_KEYBOARD, "message1"),
                new BotTextMessage("message2"),
                new BotTextMessage(
                    List.of(
                        new BotTextMessage.TextData("message1", new Object[0]), 
                        new BotTextMessage.TextData("message2", new Object[0])
                    ), 
                    BOT_KEYBOARD
                )
            ),

            Arguments.of(
                new BotTextMessage(BOT_KEYBOARD, "message1"),
                new BotTextMessage("message2"),
                new BotTextMessage(
                    List.of(
                        new BotTextMessage.TextData("message1", new Object[0]), 
                        new BotTextMessage.TextData("message2", new Object[0])
                    ), 
                    BOT_KEYBOARD
                )
            )
        );
    }
}
