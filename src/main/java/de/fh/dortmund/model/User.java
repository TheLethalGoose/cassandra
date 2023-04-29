package de.fh.dortmund.model;

import lombok.Data;

import java.util.UUID;

@Data
public class User {
    private String idUser = UUID.randomUUID().toString();

    private String email;

    private String password;

    private int reputation;

    private String username;

    public User(String email, String password, String username){
        this.email = email;
        this.password = password;
        this.username = username;
    }
}