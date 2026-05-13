package com.example.mtg_deckbuilder.dto.combo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
}