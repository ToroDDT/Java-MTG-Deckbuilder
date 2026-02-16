package com.example.mtg_deckbuilder.model;

import java.time.LocalDate;
import java.util.List;

public class Deck {
    private Long id;
    private String name;
    private List<String> colors;
    private String format;
    private Integer bracket;
    private LocalDate lastUpdated;
    private String url;

    // Constructors
    public Deck() {
    }

    public Deck(Long id, String name, List<String> colors, String format, Integer bracket, LocalDate lastUpdated, String url) {
        this.id = id;
        this.name = name;
        this.colors = colors;
        this.format = format;
        this.bracket = bracket;
        this.lastUpdated = lastUpdated;
        this.url = url;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getBracket() {
        return bracket;
    }

    public void setBracket(Integer bracket) {
        this.bracket = bracket;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Deck{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", colors=" + colors +
                ", format='" + format + '\'' +
                ", bracket=" + bracket +
                ", lastUpdated=" + lastUpdated +
                ", url='" + url + '\'' +
                '}';
    }
}
