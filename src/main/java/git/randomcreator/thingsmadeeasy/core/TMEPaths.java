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
}
