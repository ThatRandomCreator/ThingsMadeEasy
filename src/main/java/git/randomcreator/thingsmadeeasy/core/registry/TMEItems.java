package git.randomcreator.thingsmadeeasy.core.registry;

import git.randomcreator.thingsmadeeasy.ThingsMadeEasy;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TMEItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, ThingsMadeEasy.MOD_ID);

    private static final Map<String, RegistryObject<Item>> REGISTERED = new HashMap<>();

    public static void register(String id, Supplier<Item> supplier) {
        if (REGISTERED.containsKey(id)) return;
        REGISTERED.put(id, ITEMS.register(id, supplier));
    }
}
