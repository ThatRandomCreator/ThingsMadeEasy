package git.randomcreator.thingsmadeeasy.core.loader;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import git.randomcreator.thingsmadeeasy.ThingsMadeEasy;
import git.randomcreator.thingsmadeeasy.core.TMEPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TagGenerator {

    private static final Gson GSON = new Gson();
    private static final Map<String, JsonArray> TAG_CACHE = new HashMap<>();

    public static void generate(String itemId, JsonArray tags) {
        for (JsonElement tagElement : tags) {
            String fullTagPath = tagElement.getAsString();
            addItemToTag(itemId, fullTagPath);
        }
    }

    private static void addItemToTag(String itemId, String fullTagPath) {
        try {
            String[] parts = fullTagPath.split(":", 2);
            if (parts.length != 2) {
                System.err.println("[ThingsMadeEasy] Invalid tag format: " + fullTagPath);
                return;
            }

            String namespace = parts[0];
            String tagPath = parts[1];


            String tagType = "items";


            Path tagDir = TMEPaths.ROOT.resolve("data")
                    .resolve(namespace)
                    .resolve("tags")
                    .resolve(tagType);

            Files.createDirectories(tagDir);

            Path tagFile = tagDir.resolve(tagPath + ".json");

            JsonObject tagJson;
            if (Files.exists(tagFile)) {
                tagJson = JsonParser.parseString(Files.readString(tagFile)).getAsJsonObject();
            } else {
                tagJson = new JsonObject();
                tagJson.addProperty("replace", false);
                tagJson.add("values", new JsonArray());
            }

            JsonArray values = tagJson.getAsJsonArray("values");
            String itemResourceLocation = ThingsMadeEasy.MOD_ID + ":" + itemId;

            boolean alreadyExists = false;
            for (JsonElement element : values) {
                if (element.getAsString().equals(itemResourceLocation)) {
                    alreadyExists = true;
                    break;
                }
            }

            if (!alreadyExists) {
                values.add(itemResourceLocation);
                Files.writeString(tagFile, GSON.toJson(tagJson));
            }

        } catch (IOException e) {
            System.err.println("[ThingsMadeEasy] Failed to create tag file: " + fullTagPath);
            e.printStackTrace();
        }
    }
}
