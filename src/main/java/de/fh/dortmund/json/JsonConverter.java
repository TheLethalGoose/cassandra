package de.fh.dortmund.json;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.gson.*;

import java.util.Set;

public class JsonConverter {

    public static JsonArray executeQuery(Session session, String query) {
        ResultSet resultSet = session.execute(query);
        return resultSetToJsonArray(resultSet);
    }
    public static JsonArray resultSetToJsonArray(ResultSet resultSet){
        JsonArray result = new JsonArray();
        for (Row row : resultSet) {
            JsonObject obj = new JsonObject();
            for (ColumnDefinitions.Definition def : resultSet.getColumnDefinitions().asList()) {
                obj.add(def.getName(), new Gson().toJsonTree(row.getObject(def.getName())));
            }
            result.add(obj);
        }
        return result;
    }
    public static String jsonArrayToString(JsonArray jsonArray){
        return new Gson().toJson(jsonArray);
    }

}
