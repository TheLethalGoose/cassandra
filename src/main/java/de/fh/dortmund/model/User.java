package de.fh.dortmund.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class User {
    @SerializedName("Id")
    private String id = UUID.randomUUID().toString();

    @SerializedName("Email")
    private String email;

    @SerializedName("Password")
    private String password;

    @SerializedName("Reputation")
    private int reputation;

    @SerializedName("Username")
    private String username;

    private List<Tag> tagWatches;

    public User(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }
    public User(String email, String password, String username, int reputation) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.reputation = reputation;
    }
}
