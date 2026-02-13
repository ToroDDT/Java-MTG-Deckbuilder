package com.example.mtg_deckbuilder.utils;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

@Component
public class Autocomplete {

    public ArrayList<String> readFile() throws IOException {
        ArrayList<String> cards = new ArrayList<>();

        // 1. Get the file from the classpath (src/main/resources)
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("card_names.txt")) {

            if (is == null) {
                throw new FileNotFoundException("File 'card_names.txt' not found in resources folder");
            }

            // 2. Pass the InputStream 'is' into the Scanner
            try (Scanner myReader = new Scanner(is)) {
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    cards.add(data);
                }
            }
        }
        return cards;
    }
}