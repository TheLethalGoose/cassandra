package de.fh.dortmund.json;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonConverter {

    Session session;
    public JsonConverter(Session session) {
        this.session = session;
    }

    public JsonArray executeQuery(String query) {
        ResultSet rs = session.execute(query);
        JsonArray result = new JsonArray();
        for (Row row : rs) {
            JsonObject obj = new JsonObject();
            for (ColumnDefinitions.Definition def : rs.getColumnDefinitions().asList()) {
                obj.add(def.getName(), (JsonElement) row.getObject(def.getName()));
            }
            result.add(obj);
        }
        return result;
    }

}
