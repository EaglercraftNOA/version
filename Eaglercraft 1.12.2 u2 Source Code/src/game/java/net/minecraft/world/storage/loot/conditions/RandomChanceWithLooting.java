package net.minecraft.world.storage.loot.conditions;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class RandomChanceWithLooting implements LootCondition {
	private final float chance;
	private final float lootingMultiplier;

	public RandomChanceWithLooting(float chanceIn, float lootingMultiplierIn) {
		this.chance = chanceIn;
		this.lootingMultiplier = lootingMultiplierIn;
	}

	public boolean testCondition(EaglercraftRandom rand, LootContext context) {
		int i = 0;

		if (context.getKiller() instanceof EntityLivingBase) {
			i = EnchantmentHelper.getLootingModifier((EntityLivingBase) context.getKiller());
		}

		return rand.nextFloat() < this.chance + (float) i * this.lootingMultiplier;
	}

	public static class Serializer extends LootCondition.Serializer<RandomChanceWithLooting> {
		protected Serializer() {
			super(new ResourceLocation("random_chance_with_looting"), RandomChanceWithLooting.class);
		}

		public void serialize(JSONObject json, RandomChanceWithLooting value) {
			json.put("chance", Float.valueOf(value.chance));
			json.put("looting_multiplier", Float.valueOf(value.lootingMultiplier));
		}

		public RandomChanceWithLooting deserialize(JSONObject json) {
			return new RandomChanceWithLooting(json.getFloat("chance"), json.getFloat("looting_multiplier"));
		}
	}
}
