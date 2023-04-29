package de.fh.dortmund.model;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Question extends Post{
    @SerializedName("Title")
    private String title;

    @SerializedName("Views")
    private int views;

    public Question(String idUser, String content, String title){
        super(PType.QUESTION, idUser, content, "0");
        this.title = title;
    }
    public Question(JsonElement jsonElement){
        super(jsonElement);
        this.title = jsonElement.getAsJsonObject().get("Title").getAsString();
        this.views = jsonElement.getAsJsonObject().get("Views").getAsInt();
    }
}
