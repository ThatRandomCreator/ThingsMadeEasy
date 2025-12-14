package git.randomcreator.thingsmadeeasy.core.loader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import git.randomcreator.thingsmadeeasy.core.TMEPaths;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ItemModelGenerator {

    private static final Gson GSON = new Gson();

    public static void generate(String itemId) {
        try {
            // --- MODEL FILE ---
            Path modelsDir = TMEPaths.ROOT.resolve("assets/thingsmadeeasy/models/item");
            Files.createDirectories(modelsDir);

            Path modelPath = modelsDir.resolve(itemId + ".json");

            if (!Files.exists(modelPath)) {
                JsonObject modelJson = new JsonObject();
                modelJson.addProperty("parent", "minecraft:item/generated");

                JsonObject textures = new JsonObject();
                textures.addProperty("layer0", "thingsmadeeasy:item/" + itemId);
                modelJson.add("textures", textures);

                Files.writeString(modelPath, GSON.toJson(modelJson));
            }

            // --- TEXTURE FILE ---
            Path texturesDir = TMEPaths.ROOT.resolve("assets/thingsmadeeasy/textures/item");
            Files.createDirectories(texturesDir);

            Path texturePath = texturesDir.resolve(itemId + ".png");

            if (!Files.exists(texturePath)) {
                // Create 16x16 white PNG
                BufferedImage img = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, 16, 16);
                g.dispose();

                ImageIO.write(img, "PNG", texturePath.toFile());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
