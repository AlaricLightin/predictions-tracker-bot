package io.github.alariclightin.predictionstrackerbot.botservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.alariclightin.predictionstrackerbot.testutils.TestConsts;
import io.github.alariclightin.predictionstrackerbot.testutils.TestWithContainer;

@SpringBootTest
class CommandManagementServiceImplIntegrationTest extends TestWithContainer {
    @Autowired
    private CommandManagementServiceImpl commandManagementService;

    @Test
    void shouldCorrectGetBotCommandList() {
        assertThat(commandManagementService.getBotCommands())
            .extracting("command")
            .containsExactly(
                TestConsts.ADD_PREDICTION_COMMAMD,
                TestConsts.CANCEL_COMMAND,
                TestConsts.SET_RESULTS_COMMAND,
                TestConsts.SET_TIMEZONE_COMMAND,
                TestConsts.START_COMMAND 
            );
    }
}
