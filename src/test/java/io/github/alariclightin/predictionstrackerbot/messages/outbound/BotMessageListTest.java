package io.github.alariclightin.predictionstrackerbot.messages.outbound;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

public class BotMessageListTest {

    @Test
    void shouldDoFlatListFromList() {
        BotMessageList botMessageList = new BotMessageList(List.of(
            new BotMessageList(List.of(
                new BotTextMessage("1"),
                new BotTextMessage("2")
            )),
            new BotMessageList(
                new BotTextMessage("3"),
                new BotTextMessage("4")
            )
        ));

        assertThat(botMessageList.botMessages())
            .extracting("messageId")
            .containsExactly("1", "2", "3", "4");
    }

    @Test
    void shouldDoFlatListFromVarargs() {
        BotMessageList botMessageList = new BotMessageList(
            new BotMessageList(
                new BotTextMessage("1"),
                new BotTextMessage("2")
            ),
            new BotMessageList(
                new BotTextMessage("3"),
                new BotTextMessage("4")
            )
        );

        assertThat(botMessageList.botMessages())
            .extracting("messageId")
            .containsExactly("1", "2", "3", "4");
    }
}
