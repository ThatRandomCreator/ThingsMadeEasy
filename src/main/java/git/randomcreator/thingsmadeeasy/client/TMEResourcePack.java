package git.randomcreator.thingsmadeeasy.client;

import git.randomcreator.thingsmadeeasy.ThingsMadeEasy;
import git.randomcreator.thingsmadeeasy.core.TMEPaths;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.resource.PathPackResources;

import java.nio.file.Path;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(
        modid = ThingsMadeEasy.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class TMEResourcePack {

    @SubscribeEvent
    public static void addPack(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) return;

        Path assetsPath = TMEPaths.ROOT.resolve("assets");

        event.addRepositorySource(consumer -> {

            Pack pack = Pack.create(
                    "thingsmadeeasy_external_assets",
                    Component.literal("ThingsMadeEasy Assets"),
                    true,
                    (id) -> new PathPackResources(
                            id,                 // <- must match pack ID
                            false,
                            assetsPath          // <- this should point to "ThingsMadeEasy/assets/"
                    ),
                    new Pack.Info(
                            Component.literal("ThingsMadeEasy Assets"),
                            SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES),
                            FeatureFlagSet.of()
                    ),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    false,
                    PackSource.BUILT_IN
            );


            if (pack != null) {
                consumer.accept(pack);
            }


            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }
}
