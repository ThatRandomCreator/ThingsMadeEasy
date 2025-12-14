package git.randomcreator.thingsmadeeasy;

import git.randomcreator.thingsmadeeasy.core.TMEPaths;
import git.randomcreator.thingsmadeeasy.core.loader.ItemJsonLoader;
import git.randomcreator.thingsmadeeasy.core.registry.TMEItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ThingsMadeEasy.MOD_ID)
public class ThingsMadeEasy {

    public static final String MOD_ID = "thingsmadeeasy";

    public ThingsMadeEasy() {
        TMEPaths.init();

        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        TMEItems.ITEMS.register(bus);

        ItemJsonLoader.load();
    }
}
