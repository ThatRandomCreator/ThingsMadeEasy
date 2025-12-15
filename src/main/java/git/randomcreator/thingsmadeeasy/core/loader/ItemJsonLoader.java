package git.randomcreator.thingsmadeeasy.core.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import git.randomcreator.thingsmadeeasy.core.TMEPaths;
import git.randomcreator.thingsmadeeasy.core.registry.TMEItems;
import git.randomcreator.thingsmadeeasy.items.*;
import git.randomcreator.thingsmadeeasy.util.JsonUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

            ItemModelGenerator.generate(id);

            LangGenerator.generate(id, name);

            Item.Properties properties = new Item.Properties();

            properties.stacksTo(stackSize);

            if (json.has("durability")) {
                int durability = json.get("durability").getAsInt();
                properties.durability(durability);
            }

            if (JsonUtil.getBoolean(json, "fire_resistant", false)) {
                properties.fireResistant();
            }

            // Rarity
            if (json.has("rarity")) {
                String rarityStr = json.get("rarity").getAsString().toUpperCase();
                try {
                    Rarity rarity = Rarity.valueOf(rarityStr);
                    properties.rarity(rarity);
                } catch (IllegalArgumentException e) {
                    System.err.println("[ThingsMadeEasy] Invalid rarity: " + rarityStr);
                }
            }

            // Food properties
            if (json.has("food")) {
                FoodProperties food = parseFoodProperties(json.getAsJsonObject("food"));
                properties.food(food);
            }

            // Register the appropriate item type
            if (json.has("tool")) {
                registerToolItem(id, json.getAsJsonObject("tool"), properties);
            } else if (json.has("armor")) {
                registerArmorItem(id, json.getAsJsonObject("armor"), properties);
            } else if (json.has("right_click")) {
                registerInteractiveItem(id, json.getAsJsonObject("right_click"), properties);
            } else {
                TMEItems.register(id, () -> new Item(properties));
            }

            if (json.has("tags")) {
                TagGenerator.generate(id, json.getAsJsonArray("tags"));
            }

        } catch (Exception e) {
            System.err.println("[ThingsMadeEasy] Failed to load item: " + path);
            e.printStackTrace();
        }
    }

    private static void registerToolItem(String id, JsonObject toolJson, Item.Properties properties) {
        String toolType = toolJson.get("type").getAsString().toLowerCase();
        String tierName = JsonUtil.getString(toolJson, "tier", "iron");

        Tier tier = parseTier(tierName);
        float attackDamage = JsonUtil.getFloat(toolJson, "attack_damage", 2.0f);
        float attackSpeed = JsonUtil.getFloat(toolJson, "attack_speed", -2.4f);

        switch (toolType) {
            case "pickaxe":
                TMEItems.register(id, () -> new PickaxeItem(tier, (int) attackDamage, attackSpeed, properties));
                break;
            case "axe":
                TMEItems.register(id, () -> new AxeItem(tier, attackDamage, attackSpeed, properties));
                break;
            case "shovel":
                TMEItems.register(id, () -> new ShovelItem(tier, attackDamage, attackSpeed, properties));
                break;
            case "hoe":
                TMEItems.register(id, () -> new HoeItem(tier, (int) attackDamage, attackSpeed, properties));
                break;
            case "sword":
                TMEItems.register(id, () -> new SwordItem(tier, (int) attackDamage, attackSpeed, properties));
                break;
            default:
                TMEItems.register(id, () -> new CustomToolItem(tier, attackDamage, attackSpeed, properties, toolJson));
                break;
        }
    }

    private static void registerArmorItem(String id, JsonObject armorJson, Item.Properties properties) {
        String slotName = armorJson.get("slot").getAsString().toLowerCase();
        int defense = JsonUtil.getInt(armorJson, "defense", 2);
        float toughness = JsonUtil.getFloat(armorJson, "toughness", 0.0f);
        float knockbackResist = JsonUtil.getFloat(armorJson, "knockback_resistance", 0.0f);

        ArmorItem.Type armorType = switch (slotName) {
            case "helmet", "head" -> ArmorItem.Type.HELMET;
            case "chestplate", "chest" -> ArmorItem.Type.CHESTPLATE;
            case "leggings", "legs" -> ArmorItem.Type.LEGGINGS;
            case "boots", "feet" -> ArmorItem.Type.BOOTS;
            default -> ArmorItem.Type.HELMET;
        };

        ArmorMaterial material = new CustomArmorMaterial(id, defense, toughness, knockbackResist);

        TMEItems.register(id, () -> new ArmorItem(material, armorType, properties));
    }

    private static void registerInteractiveItem(String id, JsonObject rightClickJson, Item.Properties properties) {
        TMEItems.register(id, () -> new InteractiveItem(properties, rightClickJson));
    }

    private static Tier parseTier(String tierName) {
        return switch (tierName.toLowerCase()) {
            case "wood", "wooden" -> Tiers.WOOD;
            case "stone" -> Tiers.STONE;
            case "iron" -> Tiers.IRON;
            case "gold", "golden" -> Tiers.GOLD;
            case "diamond" -> Tiers.DIAMOND;
            case "netherite" -> Tiers.NETHERITE;
            default -> Tiers.IRON;
        };
    }

    private static FoodProperties parseFoodProperties(JsonObject foodJson) {
        FoodProperties.Builder builder = new FoodProperties.Builder();

        builder.nutrition(JsonUtil.getInt(foodJson, "nutrition", 1));
        builder.saturationMod(JsonUtil.getFloat(foodJson, "saturation", 0.1f));

        if (JsonUtil.getBoolean(foodJson, "meat", false)) {
            builder.meat();
        }

        if (JsonUtil.getBoolean(foodJson, "always_edible", false)) {
            builder.alwaysEat();
        }

        if (JsonUtil.getBoolean(foodJson, "fast", false)) {
            builder.fast();
        }

        // Parse effects
        if (foodJson.has("effects")) {
            JsonArray effectsArray = foodJson.getAsJsonArray("effects");
            for (JsonElement effectElement : effectsArray) {
                JsonObject effectObj = effectElement.getAsJsonObject();
                MobEffectInstance effect = parseEffect(effectObj);
                float probability = JsonUtil.getFloat(effectObj, "probability", 1.0f);
                if (effect != null) {
                    builder.effect(() -> effect, probability);
                }
            }
        }

        return builder.build();
    }

    private static MobEffectInstance parseEffect(JsonObject effectObj) {
        try {
            String effectId = effectObj.get("effect").getAsString();
            ResourceLocation effectLocation = new ResourceLocation(effectId);
            var effect = BuiltInRegistries.MOB_EFFECT.get(effectLocation);

            if (effect != null) {
                int duration = JsonUtil.getInt(effectObj, "duration", 100);
                int amplifier = JsonUtil.getInt(effectObj, "amplifier", 0);
                return new MobEffectInstance(effect, duration, amplifier);
            }
        } catch (Exception e) {
            System.err.println("[ThingsMadeEasy] Failed to parse effect: " + e.getMessage());
        }
        return null;
    }
}