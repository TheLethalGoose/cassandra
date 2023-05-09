package de.fh.dortmund.models;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.fh.dortmund.models.enums.PostType;
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
    public Answer(JsonObject jsonObject) {
        super(jsonObject.get("idUser").getAsString(), jsonObject.get("content").getAsString(), PostType.ANSWER, jsonObject.get("idParent").getAsString());
        this.accepted = jsonObject.get("accepted").getAsBoolean();
    }
}
