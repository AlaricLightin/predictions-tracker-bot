package io.github.alariclightin.predictionstrackerbot.commands.export;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;

@Configuration
class ExportConfig {
    
    @Bean
    CsvMapper csvMapper() {
        var csvMapper = new CsvMapper();
        csvMapper.findAndRegisterModules();
        return csvMapper;
    }
}
