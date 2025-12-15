package git.randomcreator.thingsmadeeasy.items;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public class CustomArmorMaterial implements ArmorMaterial {

    private final String name;
    private final int defense;
    private final float toughness;
    private final float knockbackResistance;

    public CustomArmorMaterial(String name, int defense, float toughness, float knockbackResistance) {
        this.name = name;
        this.defense = defense;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) {
            case BOOTS -> 13 * 15;
            case LEGGINGS -> 15 * 15;
            case CHESTPLATE -> 16 * 15;
            case HELMET -> 11 * 15;
        };
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return defense;
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    @Override
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_GENERIC;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}