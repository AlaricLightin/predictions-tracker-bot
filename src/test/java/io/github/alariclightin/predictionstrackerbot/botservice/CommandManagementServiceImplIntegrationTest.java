package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest
class CommandManagementServiceImplIntegrationTest extends TestWithContainer {
    @Autowired
    private CommandManagementServiceImpl commandManagementService;

    @Test
    void shouldCorrectGetBotCommandList() {
        assertThat(commandManagementService.getBotCommands())
            .extracting("command")
            .containsExactlyInAnyOrder("start", "add", "setresults");
    }
}
