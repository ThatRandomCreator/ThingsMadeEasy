package git.randomcreator.thingsmadeeasy.core.loader;

import com.google.gson.JsonObject;
import git.randomcreator.thingsmadeeasy.core.TMEPaths;
import git.randomcreator.thingsmadeeasy.core.registry.TMEItems;
import git.randomcreator.thingsmadeeasy.util.JsonUtil;
import net.minecraft.world.item.Item;

import java.nio.file.Files;
import java.nio.file.Path;

public class ItemJsonLoader {

    public static void load() {
        Path dir = TMEPaths.ROOT.resolve("items");

        if (!Files.exists(dir)) return;

        try {
            Files.list(dir)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(ItemJsonLoader::loadItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadItem(Path path) {
        try {
            JsonObject json = JsonUtil.read(path);

            String id = json.get("id").getAsString();
            int stackSize = JsonUtil.getInt(json, "stack_size", 64);
            String name = json.has("name") ? json.get("name").getAsString() : id;

            // Auto-generate model + placeholder PNG
            ItemModelGenerator.generate(id);

            // Auto-generate lang entry
            LangGenerator.generate(id, name);

            TMEItems.register(id, () ->
                    new Item(new Item.Properties().stacksTo(stackSize))
            );

        } catch (Exception e) {
            System.err.println("[ThingsMadeEasy] Failed to load item: " + path);
            e.printStackTrace();
        }
    }



}
