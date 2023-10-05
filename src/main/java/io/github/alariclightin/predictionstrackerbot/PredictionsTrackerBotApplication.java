package io.github.alariclightin.predictionstrackerbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import io.github.alariclightin.predictionstrackerbot.bot.Bot;
import io.github.alariclightin.predictionstrackerbot.bot.TelegramBotConfig;

@SpringBootApplication
@EnableJdbcRepositories
@EnableConfigurationProperties(TelegramBotConfig.class)
public class PredictionsTrackerBotApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = SpringApplication.run(PredictionsTrackerBotApplication.class, args);

		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			Bot bot = ctx.getBean(Bot.class);
			botsApi.registerBot(bot);
			bot.setCommands();
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}

	}

}
