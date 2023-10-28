package io.github.alariclightin.predictionstrackerbot.commands.addprediction;

import io.github.alariclightin.predictionstrackerbot.messages.outbound.BotMessage;

interface DeadlinePromptService {
    
    BotMessage getDeadlinePromptMessage();
}
