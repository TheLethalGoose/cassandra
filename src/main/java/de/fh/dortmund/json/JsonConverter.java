package de.fh.dortmund.json;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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

                if(row.getObject(def.getName()) instanceof Set) {
                    Gson gson = new Gson();
                    Set<String> set = row.getSet(def.getName(), String.class);
                    obj.add(def.getName(),  gson.toJsonTree(set));
                    continue;
                }
                obj.add(def.getName(), (JsonElement) row.getObject(def.getName()));

            }
            result.add(obj);
        }
        return result;
    }

}
