package de.fh.dortmund.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.fh.dortmund.models.enums.PostType;
import lombok.Data;


@Data
public class Question extends Post {

    @SerializedName("Title")
    private String title;

    @SerializedName("Views")
    private int views;

    public Question(String userId, String title, String content) {
        super(userId, content, PostType.QUESTION, null);
        this.title = title;
    }
    public Question(JsonObject jsonObject) {
        super(jsonObject.get("idUser").getAsString(), jsonObject.get("content").getAsString(), PostType.QUESTION, null);
        this.title = jsonObject.get("title").getAsString();
    }
    public Question(String userId, String title, String content, String createdAt, String modifiedAt) {
        super(userId, content, PostType.QUESTION, null, createdAt, modifiedAt);
        this.title = title;
    }
    @Override
    public String toString() {
        return "Question{" +
                "id='" + getId().substring(0, 7) + "[...]" + '\'' +
                ", title='" + getTitle() + '\'' +
                ", content='" + getContent().substring(0, 5) + "[...]" + '\'' +
                ", createdAt='" + getCreatedAt() + '\'' +
                ", modifiedAt='" + getModifiedAt() + '\'' +
                '}';
    }
}
