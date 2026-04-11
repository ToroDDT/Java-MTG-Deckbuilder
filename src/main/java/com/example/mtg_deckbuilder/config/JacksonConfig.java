package com.example.mtg_deckbuilder.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .addModule(new AfterburnerModule())

                // 1. Support Lombok @Getter classes
                .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)
                .visibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.PUBLIC_ONLY)

                // 2. Support Java Records
                .visibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.ANY)

                // 3. Efficiency: Skip raw field scanning since we use Getters/Creators
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.NONE)

                // 4. Performance & Safety
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }
}