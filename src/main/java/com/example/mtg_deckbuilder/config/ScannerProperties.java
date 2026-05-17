package com.example.mtg_deckbuilder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scanner")
public record ScannerProperties(String host, int port) {
}
