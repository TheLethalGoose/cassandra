package de.fh.dortmund.model;

import de.fh.dortmund.model.enums.PostType;
import lombok.Data;

@Data
public class Comment extends Post {
    public Comment(String userId, String content, String parentPostId) {
        super(userId, content, PostType.COMMENT, parentPostId);
    }
}
