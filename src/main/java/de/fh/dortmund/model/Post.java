package de.fh.dortmund.model;

import com.google.gson.annotations.SerializedName;
import de.fh.dortmund.model.enums.PostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
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

    public Post(String userId, String content, PostType postType, String idParent, String createdAt, String modifiedAt){
        this.id = String.valueOf(UUID.randomUUID());
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.postType = postType;
        this.idParent = idParent;
    }
    public Post(String userId, String content, PostType postType, String idParent){
        this.id = String.valueOf(UUID.randomUUID());
        this.userId = userId;
        this.content = content;
        this.createdAt = String.valueOf(LocalDateTime.now());
        this.modifiedAt = String.valueOf(LocalDateTime.now());
        this.postType = postType;
        this.idParent = idParent;
    }

}
