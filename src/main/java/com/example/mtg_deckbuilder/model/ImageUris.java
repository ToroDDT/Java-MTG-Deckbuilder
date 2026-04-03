package com.example.mtg_deckbuilder.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ImageUris {
    private String small;
    private String normal;
    private String large;
    private String png;
    @JsonProperty("border_crop")
    private String borderCrop;
    @JsonProperty("art_crop")
    private String artCrop;

    public ImageUris() {
    }


}

