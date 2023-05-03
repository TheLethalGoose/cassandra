package de.fh.dortmund.model;

import com.google.gson.annotations.SerializedName;
import de.fh.dortmund.model.enums.PostType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Data
public abstract class Post {
    @SerializedName("Id")
    private String id;

    @SerializedName("IdUser")
    private String userId;

    @SerializedName("Content")
    private String content;

    @SerializedName("CreatedAt")
    private String createdAt;

    @SerializedName("ModifiedAt")
    private String modifiedAt;

    @SerializedName("PostType")
    private PostType postType;

    @SerializedName("IdParent")
    private String idParent;

    public Post(String userId, String content, PostType postType, String idParent) {
        this.id = String.valueOf(UUID.randomUUID());
        this.userId = userId;
        this.content = content;
        this.createdAt = LocalDateTime.now().toString();
        this.modifiedAt = LocalDateTime.now().toString();
        this.postType = postType;
        this.idParent = idParent;
    }

}
