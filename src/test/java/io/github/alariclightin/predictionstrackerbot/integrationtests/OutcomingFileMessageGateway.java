package io.github.alariclightin.predictionstrackerbot.integrationtests;

interface OutcomingFileMessageGateway {
    
    void sendFile(String chatId, String fileName, byte[] fileData);
}
