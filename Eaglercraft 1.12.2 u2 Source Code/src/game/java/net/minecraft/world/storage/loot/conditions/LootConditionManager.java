package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.Maps;
import java.util.Map;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;

import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class LootConditionManager {
	private static final Map<ResourceLocation, LootCondition.Serializer<?>> NAME_TO_SERIALIZER_MAP = Maps.<ResourceLocation, LootCondition.Serializer<?>>newHashMap();
	private static final Map<Class<? extends LootCondition>, LootCondition.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.<Class<? extends LootCondition>, LootCondition.Serializer<?>>newHashMap();

	public static <T extends LootCondition> void registerCondition(LootCondition.Serializer<? extends T> condition) {
		ResourceLocation resourcelocation = condition.getLootTableLocation();
		Class<T> oclass = (Class<T>) condition.getConditionClass();

		if (NAME_TO_SERIALIZER_MAP.containsKey(resourcelocation)) {
			throw new IllegalArgumentException("Can't re-register item condition name " + resourcelocation);
		} else if (CLASS_TO_SERIALIZER_MAP.containsKey(oclass)) {
			throw new IllegalArgumentException("Can't re-register item condition class " + oclass.getName());
		} else {
			NAME_TO_SERIALIZER_MAP.put(resourcelocation, condition);
			CLASS_TO_SERIALIZER_MAP.put(oclass, condition);
		}
	}

	public static boolean testAllConditions(@Nullable LootCondition[] conditions, EaglercraftRandom rand,
			LootContext context) {
		if (conditions == null) {
			return true;
		} else {
			for (LootCondition lootcondition : conditions) {
				if (!lootcondition.testCondition(rand, context)) {
					return false;
				}
			}

			return true;
		}
	}

	public static LootCondition.Serializer<?> getSerializerForName(ResourceLocation location) {
		LootCondition.Serializer<?> serializer = (LootCondition.Serializer) NAME_TO_SERIALIZER_MAP.get(location);

		if (serializer == null) {
			throw new IllegalArgumentException("Unknown loot item condition '" + location + "'");
		} else {
			return serializer;
		}
	}

	public static <T extends LootCondition> LootCondition.Serializer<T> getSerializerFor(T conditionClass) {
		LootCondition.Serializer<T> serializer = (LootCondition.Serializer) CLASS_TO_SERIALIZER_MAP
				.get(conditionClass.getClass());

		if (serializer == null) {
			throw new IllegalArgumentException("Unknown loot item condition " + conditionClass);
		} else {
			return serializer;
		}
	}

	static {
		registerCondition(new RandomChance.Serializer());
		registerCondition(new RandomChanceWithLooting.Serializer());
		registerCondition(new EntityHasProperty.Serializer());
		registerCondition(new KilledByPlayer.Serializer());
		registerCondition(new EntityHasScore.Serializer());
	}

	public static class Serializer
			implements JSONTypeDeserializer<JSONObject, LootCondition>, JSONTypeCodec<LootCondition, JSONObject> {
		public LootCondition deserialize(JSONObject jsonobject) throws JSONException {
			ResourceLocation resourcelocation = new ResourceLocation(jsonobject.getString("condition"));
			LootCondition.Serializer<?> serializer;

			try {
				serializer = LootConditionManager.getSerializerForName(resourcelocation);
			} catch (IllegalArgumentException var8) {
				throw new JSONException("Unknown condition '" + resourcelocation + "'");
			}

			return serializer.deserialize(jsonobject);
		}

		public JSONObject serialize(LootCondition p_serialize_1_) {
			LootCondition.Serializer<LootCondition> serializer = LootConditionManager
					.<LootCondition>getSerializerFor(p_serialize_1_);
			JSONObject jsonobject = new JSONObject();
			serializer.serialize(jsonobject, p_serialize_1_);
			jsonobject.put("condition", serializer.getLootTableLocation().toString());
			return jsonobject;
		}
	}
}
