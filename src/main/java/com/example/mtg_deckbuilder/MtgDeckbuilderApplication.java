package com.example.mtg_deckbuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MtgDeckbuilderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MtgDeckbuilderApplication.class, args);
    }

}
