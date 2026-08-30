package net.minecraft.world.storage.loot.conditions;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class KilledByPlayer implements LootCondition {
	private final boolean inverse;

	public KilledByPlayer(boolean inverseIn) {
		this.inverse = inverseIn;
	}

	public boolean testCondition(EaglercraftRandom rand, LootContext context) {
		boolean flag = context.getKillerPlayer() != null;
		return flag == !this.inverse;
	}

	public static class Serializer extends LootCondition.Serializer<KilledByPlayer> {
		protected Serializer() {
			super(new ResourceLocation("killed_by_player"), KilledByPlayer.class);
		}

		public void serialize(JSONObject json, KilledByPlayer value) {
			json.put("inverse", Boolean.valueOf(value.inverse));
		}

		public KilledByPlayer deserialize(JSONObject json) {
			return new KilledByPlayer(json.optBoolean("inverse", false));
		}
	}
}
