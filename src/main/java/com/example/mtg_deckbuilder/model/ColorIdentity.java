package com.example.mtg_deckbuilder.model;


import lombok.Getter;

@Getter
public enum ColorIdentity {
    WHITE("W"),
    BLUE("U"),
    BLACK("B"),
    RED("R"),
    GREEN("G"),
    COLORLESS("C"),
 ;
    private final String colorIdentity;

 ColorIdentity(String s) {
     this.colorIdentity = s;
 }
}
