package de.fh.dortmund.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.UUID;

@Data
public class Tag {
    @SerializedName("Id")
    private String id = UUID.randomUUID().toString();

    @SerializedName("Name")
    private String name;

    @SerializedName("Info")
    private String info;

    private String questionId;

    public Tag(String tagName, String info) {
        this.name = tagName;
        this.info = info;
    }

    public Tag() {
    }

}
