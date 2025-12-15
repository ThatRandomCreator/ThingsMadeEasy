package git.randomcreator.thingsmadeeasy.core;

import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TMEPaths {

    public static final Path ROOT =
            FMLPaths.GAMEDIR.get().resolve("ThingsMadeEasy");

    public static void init() {
        create("items");
        create("blocks");
        create("tool_tiers");
        create("ore_tiers");
        create("multiblocks");
        create("dimensions");
        create("tags");
        create("logs");
        create("assets");
        create("assets/thingsmadeeasy/textures/item");
        create("assets/thingsmadeeasy/models/item");
        create("assets/thingsmadeeasy/lang");

        create("data");
        create("data/minecraft/tags/items");
        create("data/forge/tags/items");
        create("data/thingsmadeeasy/tags/items");

        createPackMcmeta();
    }

    private static void create(String name) {
        try {
            Files.createDirectories(ROOT.resolve(name));
        } catch (IOException e) {
            throw new RuntimeException(
                    "[ThingsMadeEasy] Failed to create folder: " + name, e
            );
        }
    }

    private static void createPackMcmeta() {
        Path packMeta = ROOT.resolve("pack.mcmeta");
        if (!Files.exists(packMeta)) {
            try {
                String content = """
                    {
                      "pack": {
                        "description": "ThingsMadeEasy External Assets",
                        "pack_format": 15
                      }
                    }
                    """;
                Files.writeString(packMeta, content);
            } catch (IOException e) {
                throw new RuntimeException("[ThingsMadeEasy] Failed to create pack.mcmeta", e);
            }
        }
    }
}