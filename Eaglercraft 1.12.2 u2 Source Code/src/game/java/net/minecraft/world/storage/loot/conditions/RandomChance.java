package net.minecraft.world.storage.loot.conditions;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class RandomChance implements LootCondition {
	private final float chance;

	public RandomChance(float chanceIn) {
		this.chance = chanceIn;
	}

	public boolean testCondition(EaglercraftRandom rand, LootContext context) {
		return rand.nextFloat() < this.chance;
	}

	public static class Serializer extends LootCondition.Serializer<RandomChance> {
		protected Serializer() {
			super(new ResourceLocation("random_chance"), RandomChance.class);
		}

		public void serialize(JSONObject json, RandomChance value) {
			json.put("chance", Float.valueOf(value.chance));
		}

		public RandomChance deserialize(JSONObject json) {
			return new RandomChance(json.getFloat("chance"));
		}
	}
}
