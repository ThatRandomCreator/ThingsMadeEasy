package git.randomcreator.thingsmadeeasy.items;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import git.randomcreator.thingsmadeeasy.util.JsonUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

public class InteractiveItem extends Item {

    private final JsonObject config;
    private static final Map<Player, Long> COOLDOWNS = new HashMap<>();

    public InteractiveItem(Properties properties, JsonObject config) {
        super(properties);
        this.config = config;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        int cooldown = JsonUtil.getInt(config, "cooldown", 0);
        if (cooldown > 0) {
            long currentTime = level.getGameTime();
            Long lastUse = COOLDOWNS.get(player);
            if (lastUse != null && currentTime - lastUse < cooldown) {
                return InteractionResultHolder.fail(stack);
            }
            COOLDOWNS.put(player, currentTime);
        }

        String actionType = JsonUtil.getString(config, "type", "none");

        switch (actionType.toLowerCase()) {
            /*case "teleport":
                handleTeleport(level, player, stack);
                break;*/
            case "heal":
                handleHeal(level, player, stack);
                break;
            case "damage":
                handleDamage(level, player, stack);
                break;
            case "explode":
                handleExplode(level, player, stack);
                break;
            case "lightning":
                handleLightning(level, player, stack);
                break;
        }

        if (config.has("effects")) {
            applyEffects(player, config.getAsJsonArray("effects"));
        }

        if (config.has("sound")) {
            playSound(level, player, config.get("sound").getAsString());
        }

        if (config.has("particles") && level instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel, player, config.get("particles").getAsString());
        }

        int durabilityConsume = JsonUtil.getInt(config, "consume_durability", 0);
        if (durabilityConsume > 0 && stack.isDamageableItem()) {
            stack.hurtAndBreak(durabilityConsume, player, (p) -> p.broadcastBreakEvent(hand));
        }

        return InteractionResultHolder.success(stack);
    }

    /*private void handleTeleport(Level level, Player player, ItemStack stack) {
        if (!level.isClientSide) {
            Vec3 lookVec = player.getLookAngle();
            double distance = JsonUtil.getDouble(config, "distance", 10.0);

            Vec3 targetPos = player.position().add(
                    lookVec.x * distance,
                    lookVec.y * distance,
                    lookVec.z * distance
            );

            player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        }
    }*/

    private void handleHeal(Level level, Player player, ItemStack stack) {
        float amount = JsonUtil.getFloat(config, "amount", 4.0f);
        player.heal(amount);
    }

    private void handleDamage(Level level, Player player, ItemStack stack) {
        float amount = JsonUtil.getFloat(config, "amount", 2.0f);
        player.hurt(level.damageSources().magic(), amount);
    }

    private void handleExplode(Level level, Player player, ItemStack stack) {
        if (!level.isClientSide) {
            float power = JsonUtil.getFloat(config, "power", 2.0f);
            boolean destroyBlocks = JsonUtil.getBoolean(config, "destroy_blocks", false);
            level.explode(null, player.getX(), player.getY(), player.getZ(),
                    power, destroyBlocks, Level.ExplosionInteraction.NONE);
        }
    }

    private void handleLightning(Level level, Player player, ItemStack stack) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            Vec3 lookVec = player.getLookAngle();
            double distance = JsonUtil.getDouble(config, "distance", 10.0);

            Vec3 targetPos = player.position().add(
                    lookVec.x * distance,
                    0,
                    lookVec.z * distance
            );

            var lightning = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(level);
            if (lightning != null) {
                lightning.moveTo(targetPos.x, targetPos.y, targetPos.z);
                level.addFreshEntity(lightning);
            }
        }
    }

    private void applyEffects(Player player, JsonArray effectsArray) {
        for (JsonElement element : effectsArray) {
            JsonObject effectObj = element.getAsJsonObject();
            try {
                String effectId = effectObj.get("effect").getAsString();
                ResourceLocation effectLocation = new ResourceLocation(effectId);
                var effect = BuiltInRegistries.MOB_EFFECT.get(effectLocation);

                if (effect != null) {
                    int duration = JsonUtil.getInt(effectObj, "duration", 100);
                    int amplifier = JsonUtil.getInt(effectObj, "amplifier", 0);
                    player.addEffect(new MobEffectInstance(effect, duration, amplifier));
                }
            } catch (Exception e) {
                System.err.println("[ThingsMadeEasy] Failed to apply effect: " + e.getMessage());
            }
        }
    }

    private void playSound(Level level, Player player, String soundId) {
        try {
            ResourceLocation soundLocation = new ResourceLocation(soundId);
            var sound = BuiltInRegistries.SOUND_EVENT.get(soundLocation);
            if (sound != null) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        sound, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
        } catch (Exception e) {
            System.err.println("[ThingsMadeEasy] Failed to play sound: " + e.getMessage());
        }
    }

    private void spawnParticles(ServerLevel level, Player player, String particleId) {
        try {
            ResourceLocation particleLocation = new ResourceLocation(particleId);
            var particleType = BuiltInRegistries.PARTICLE_TYPE.get(particleLocation);
            if (particleType != null && particleType instanceof net.minecraft.core.particles.ParticleOptions particleOptions) {
                level.sendParticles(particleOptions,
                        player.getX(), player.getY() + 1, player.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1);
            }
        } catch (Exception e) {
            System.err.println("[ThingsMadeEasy] Failed to spawn particles: " + e.getMessage());
        }
    }
}