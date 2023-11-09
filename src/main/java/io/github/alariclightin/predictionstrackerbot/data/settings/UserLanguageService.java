package io.github.alariclightin.predictionstrackerbot.data.settings;

public interface UserLanguageService {

    String getLanguageCode(long userId);

    void setLanguageCode(long userId, String languageCode);
    
}
