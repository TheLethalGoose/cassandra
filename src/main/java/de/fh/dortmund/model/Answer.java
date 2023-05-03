package de.fh.dortmund.model;

import com.google.gson.annotations.SerializedName;
import de.fh.dortmund.model.enums.PostType;
import lombok.Data;

@Data
public class Answer extends Post {
    @SerializedName("Accepted")
    private boolean accepted;

    public Answer(String userId, String parentId, String answerText) {
        super(userId, answerText, PostType.ANSWER, parentId);
    }
    public Answer(String userId, String parentId, String answerText, boolean accepted, String createdAt, String modifiedAt) {
        super(userId, answerText, PostType.ANSWER, parentId, createdAt, modifiedAt);
        this.accepted = accepted;
    }
}
