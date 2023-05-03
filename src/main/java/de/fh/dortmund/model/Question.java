package de.fh.dortmund.model;

import com.google.gson.annotations.SerializedName;
import de.fh.dortmund.model.enums.PostType;
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
