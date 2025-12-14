package git.randomcreator.thingsmadeeasy.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class JsonUtil {

    private static final Gson GSON = new Gson();

    public static JsonObject read(Path path) throws Exception {
        try (Reader reader = Files.newBufferedReader(path)) {
            return GSON.fromJson(reader, JsonObject.class);
        }
    }

    public static int getInt(JsonObject json, String key, int def) {
        return json.has(key) ? json.get(key).getAsInt() : def;
    }
}
