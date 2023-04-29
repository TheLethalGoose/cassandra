package de.fh.dortmund.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Answer extends Post{
    private boolean accepted;

    public Answer(String idQuestion, String idUser, String content){
        super(PType.ANSWER, idUser, content, idQuestion);
    }
}
