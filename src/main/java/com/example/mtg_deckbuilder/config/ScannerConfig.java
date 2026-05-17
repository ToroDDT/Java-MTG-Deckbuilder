package com.example.mtg_deckbuilder.config;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ScannerProperties.class)
public class ScannerConfig {

    @Bean(destroyMethod = "shutdownNow")
    ManagedChannel scannerChannel(ScannerProperties properties) {
        return ManagedChannelBuilder.forAddress(properties.host(), properties.port())
                .usePlaintext()
                .build();
    }
}
