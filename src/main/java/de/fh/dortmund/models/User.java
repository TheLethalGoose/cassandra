package de.fh.dortmund.models;

import com.google.gson.JsonObject;
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
    public User(JsonObject jsonObject) {
        this.email = jsonObject.get("email").getAsString();
        this.password = jsonObject.get("password").getAsString();
        this.username = jsonObject.get("username").getAsString();
        this.reputation = jsonObject.get("reputation").getAsInt();
    }
}
