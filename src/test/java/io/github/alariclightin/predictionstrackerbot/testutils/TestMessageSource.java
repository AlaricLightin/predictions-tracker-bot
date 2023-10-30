package io.github.alariclightin.predictionstrackerbot.testutils;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class TestMessageSource {
    
    public static ReloadableResourceBundleMessageSource create() {
        var messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
