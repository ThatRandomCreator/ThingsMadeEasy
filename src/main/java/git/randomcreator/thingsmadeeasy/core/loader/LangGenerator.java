package git.randomcreator.thingsmadeeasy.core.loader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import git.randomcreator.thingsmadeeasy.core.TMEPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class LangGenerator {

    private static final Gson GSON = new Gson();
    private static final String LANG_FILE = "assets/thingsmadeeasy/lang/en_us.json";

    public static void generate(String itemId, String itemName) {
        try {
            Path langPath = TMEPaths.ROOT.resolve(LANG_FILE);
            Files.createDirectories(langPath.getParent());

            JsonObject langJson;

            // Load existing entries if file exists
            if (Files.exists(langPath)) {
                langJson = JsonParser.parseString(Files.readString(langPath)).getAsJsonObject();
            } else {
                langJson = new JsonObject();
            }

            String key = "item.thingsmadeeasy." + itemId;

            // Only add if it doesn't exist
            if (!langJson.has(key)) {
                langJson.addProperty(key, itemName);
                Files.writeString(langPath, GSON.toJson(langJson));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
