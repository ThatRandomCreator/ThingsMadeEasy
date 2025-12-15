package git.randomcreator.thingsmadeeasy.items;

import com.google.gson.JsonObject;
import git.randomcreator.thingsmadeeasy.util.JsonUtil;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

public class CustomToolItem extends DiggerItem {

    private final JsonObject config;

    public CustomToolItem(Tier tier, float attackDamage, float attackSpeed, Properties properties, JsonObject config) {
        super(attackDamage, attackSpeed, tier, getEffectiveBlocks(config), properties);
        this.config = config;
    }

    private static TagKey<Block> getEffectiveBlocks(JsonObject config) {
        // Default to pickaxe blocks if not specified
        String blockTag = JsonUtil.getString(config, "effective_blocks", "minecraft:mineable/pickaxe");
        try {
            ResourceLocation tagLocation = new ResourceLocation(blockTag);
            return TagKey.create(Registries.BLOCK, tagLocation);
        } catch (Exception e) {
            return BlockTags.MINEABLE_WITH_PICKAXE;
        }
    }

    @Override
    public float getDestroySpeed(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.block.state.BlockState state) {
        float baseSpeed = super.getDestroySpeed(stack, state);
        float speedMultiplier = JsonUtil.getFloat(config, "speed_multiplier", 1.0f);
        return baseSpeed * speedMultiplier;
    }
}