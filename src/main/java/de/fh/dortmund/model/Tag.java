package de.fh.dortmund.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.UUID;

@Data
public class Tag {
    private String idTag = UUID.randomUUID().toString();

    @SerializedName("TagName")
    private String tagName;
    @SerializedName("Info")
    private String info;

    public Tag(String tagName, String info){
        this.tagName = tagName;
        this.info = info;
    }
}
