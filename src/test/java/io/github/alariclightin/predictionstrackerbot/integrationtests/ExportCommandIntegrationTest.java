package io.github.alariclightin.predictionstrackerbot.integrationtests;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest
class ExportCommandIntegrationTest extends AbstractGatewayTest {

    @Test
    @Sql(scripts = { "classpath:sql/questions-with-predictions.sql" }, 
        executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
    void shouldExportPredictionsIfTheyExists() {
        sendTextUpdate("/export");

        assertFileSended(
            Long.toString(BotTestUtils.CHAT_ID), 
            "export.csv", 
            "Question 1", "Question 2"
        );
    }
    
    @Test
    void shouldNotExportPredictionsIfTheyNotExists() {
        sendTextUpdate("/export");

        assertResponseTextContainsFragments("no predictions");
        verify(mockedOutcomingFileGateway, never()).sendFile(any(), any(), any());
    }
}
