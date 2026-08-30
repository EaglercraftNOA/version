package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.Maps;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.peyton.eagler.json.JSONUtils;

public class LootFunctionManager {
	private static final Map<ResourceLocation, LootFunction.Serializer<?>> NAME_TO_SERIALIZER_MAP = Maps.<ResourceLocation, LootFunction.Serializer<?>>newHashMap();
	private static final Map<Class<? extends LootFunction>, LootFunction.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.<Class<? extends LootFunction>, LootFunction.Serializer<?>>newHashMap();

	public static <T extends LootFunction> void registerFunction(LootFunction.Serializer<? extends T> p_186582_0_) {
		ResourceLocation resourcelocation = p_186582_0_.getFunctionName();
		Class<T> oclass = (Class<T>) p_186582_0_.getFunctionClass();

		if (NAME_TO_SERIALIZER_MAP.containsKey(resourcelocation)) {
			throw new IllegalArgumentException("Can't re-register item function name " + resourcelocation);
		} else if (CLASS_TO_SERIALIZER_MAP.containsKey(oclass)) {
			throw new IllegalArgumentException("Can't re-register item function class " + oclass.getName());
		} else {
			NAME_TO_SERIALIZER_MAP.put(resourcelocation, p_186582_0_);
			CLASS_TO_SERIALIZER_MAP.put(oclass, p_186582_0_);
		}
	}

	public static LootFunction.Serializer<?> getSerializerForName(ResourceLocation location) {
		LootFunction.Serializer<?> serializer = (LootFunction.Serializer) NAME_TO_SERIALIZER_MAP.get(location);

		if (serializer == null) {
			throw new IllegalArgumentException("Unknown loot item function '" + location + "'");
		} else {
			return serializer;
		}
	}

	public static <T extends LootFunction> LootFunction.Serializer<T> getSerializerFor(T functionClass) {
		LootFunction.Serializer<T> serializer = (LootFunction.Serializer) CLASS_TO_SERIALIZER_MAP
				.get(functionClass.getClass());

		if (serializer == null) {
			throw new IllegalArgumentException("Unknown loot item function " + functionClass);
		} else {
			return serializer;
		}
	}

	static {
		registerFunction(new SetCount.Serializer());
		registerFunction(new SetMetadata.Serializer());
		registerFunction(new EnchantWithLevels.Serializer());
		registerFunction(new EnchantRandomly.Serializer());
		registerFunction(new SetNBT.Serializer());
		registerFunction(new Smelt.Serializer());
		registerFunction(new LootingEnchantBonus.Serializer());
		registerFunction(new SetDamage.Serializer());
		registerFunction(new SetAttributes.Serializer());
	}

	public static class Serializer
			implements JSONTypeDeserializer<JSONObject, LootFunction>, JSONTypeCodec<LootFunction, JSONObject> {
		public LootFunction deserialize(JSONObject jsonobject) throws JSONException {
			ResourceLocation resourcelocation = new ResourceLocation(jsonobject.getString("function"));
			LootFunction.Serializer<?> serializer;

			try {
				serializer = LootFunctionManager.getSerializerForName(resourcelocation);
			} catch (IllegalArgumentException var8) {
				throw new JSONException("Unknown function '" + resourcelocation + "'");
			}

			LootCondition[] conditions;
			if (jsonobject.has("conditions")) {
				JSONArray jsonarray = jsonobject.getJSONArray("conditions");
				conditions = new LootCondition[jsonarray.length()];
				for (int i = 0; i < conditions.length; i++) {
					conditions[i] = JSONTypeProvider.deserialize(jsonarray.get(i), LootCondition.class);
				}
			} else {
				conditions = new LootCondition[0];
			}

			return serializer.deserialize(jsonobject, conditions);
		}

		public JSONObject serialize(LootFunction p_serialize_1_) {
			LootFunction.Serializer<LootFunction> serializer = LootFunctionManager
					.<LootFunction>getSerializerFor(p_serialize_1_);
			JSONObject jsonobject = new JSONObject();
			serializer.serialize(jsonobject, p_serialize_1_);
			jsonobject.put("function", serializer.getFunctionName().toString());

			if (p_serialize_1_.getConditions() != null && p_serialize_1_.getConditions().length > 0) {
				JSONArray jsonarray = new JSONArray();
				for (LootCondition condition : p_serialize_1_.getConditions()) {
					jsonarray.put((Object) JSONTypeProvider.serialize(condition));
				}
				jsonobject.put("conditions", jsonarray);
			}

			return jsonobject;
		}
	}
}
