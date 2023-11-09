package io.github.alariclightin.predictionstrackerbot.data.settings;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

interface UserSettingsRepository extends CrudRepository<UserSettings, Long> {
    
    @Query("SELECT timezone FROM users.user_settings WHERE user_id = :userId")
    Optional<String> getTimezoneByUserId(long userId);

    @Modifying
    @Query(
        """
            INSERT INTO users.user_settings (user_id, timezone) 
                VALUES (:userId, :timezone)
            ON CONFLICT(user_id) DO
                UPDATE SET timezone = :timezone
        """)
    void setTimezoneByUserId(long userId, String timezone);

    @Modifying
    @Query(
        """
            INSERT INTO users.user_settings (user_id, language_code) 
                VALUES (:userId, :languageCode)
            ON CONFLICT(user_id) DO
                UPDATE SET language_code = :languageCode
        """
    )
    void setLanguageCodeByUserId(long userId, String languageCode);

    @Query("SELECT language_code FROM users.user_settings WHERE user_id = :userId")
    Optional<String> getLanguageCodeByUserId(long userId);


}
