package io.github.alariclightin.predictionstrackerbot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

import io.github.alariclightin.predictionstrackerbot.exceptions.UnexpectedMessageException;
import io.github.alariclightin.predictionstrackerbot.states.WaitedResponseState;

public interface HandlersSearchService {

    MessageHandler getHandler(Message message, WaitedResponseState state) throws UnexpectedMessageException;

}
