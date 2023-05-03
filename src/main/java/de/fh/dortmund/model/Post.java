package de.fh.dortmund.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class Post {
    private String idPost = UUID.randomUUID().toString();

    private String idUser;

    @SerializedName("Content")
    private String content;

    @SerializedName("CreatedAt")
    private LocalDateTime createdAt;

    @SerializedName("ModifiedAt")
    private LocalDateTime modifiedAt;

    @SerializedName("PType")
    private PType type;

    @SerializedName("ParentPostId")
    private String parentPostId;

    public Post(){}

    public Post(PType type, String idUser, String content, String parentPostId){
        this.type = type;
        this.idUser = idUser;
        this.content = content;
        this.parentPostId = parentPostId;
        this.createdAt = LocalDateTime.now();
    }

    public Post(JsonElement jsonElement) {
        this.idPost = jsonElement.getAsJsonObject().get("idPost").getAsString();
        this.idUser = jsonElement.getAsJsonObject().get("idUser").getAsString();
        this.content = jsonElement.getAsJsonObject().get("Content").getAsString();
        this.createdAt = LocalDateTime.parse(jsonElement.getAsJsonObject().get("CreatedAt").getAsString());
        this.modifiedAt = LocalDateTime.parse(jsonElement.getAsJsonObject().get("ModifiedAt").getAsString());
        this.type = PType.valueOf(jsonElement.getAsJsonObject().get("PType").getAsString());
        this.parentPostId = jsonElement.getAsJsonObject().get("ParentPostId").getAsString();
    }
}
