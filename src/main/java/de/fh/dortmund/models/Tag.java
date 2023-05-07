package de.fh.dortmund.models;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag tag)) return false;

        return getName() != null ? getName().equals(tag.getName()) : tag.getName() == null;
    }

    @Override
    public int hashCode() {
        return getName() != null ? getName().hashCode() : 0;
    }
}
