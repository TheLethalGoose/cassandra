package de.fh.dortmund.model;

import lombok.Data;

@Data
public class Comment extends Post{
    public Comment(String idParent, String idUser, String content){
        super(PType.COMMENT, idUser, content, idParent);
    }
}
