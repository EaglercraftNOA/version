package net.minecraft.world.storage.loot.conditions;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public interface LootCondition {
	boolean testCondition(EaglercraftRandom rand, LootContext context);

	public abstract static class Serializer<T extends LootCondition> {
		private final ResourceLocation lootTableLocation;
		private final Class<T> conditionClass;

		protected Serializer(ResourceLocation location, Class<T> clazz) {
			this.lootTableLocation = location;
			this.conditionClass = clazz;
		}

		public ResourceLocation getLootTableLocation() {
			return this.lootTableLocation;
		}

		public Class<T> getConditionClass() {
			return this.conditionClass;
		}

		public abstract void serialize(JSONObject json, T value);

		public abstract T deserialize(JSONObject json);
	}
}
