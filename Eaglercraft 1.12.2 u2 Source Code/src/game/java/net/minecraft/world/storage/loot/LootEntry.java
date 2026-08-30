package net.minecraft.world.storage.loot;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public abstract class LootEntry {
	protected final int weight;
	protected final int quality;
	protected final LootCondition[] conditions;

	protected LootEntry(int weightIn, int qualityIn, LootCondition[] conditionsIn) {
		this.weight = weightIn;
		this.quality = qualityIn;
		this.conditions = conditionsIn;
	}

	/**
	 * Gets the effective weight based on the loot entry's weight and quality
	 * multiplied by looter's luck.
	 */
	public int getEffectiveWeight(float luck) {
		return Math.max(MathHelper.floor((float) this.weight + (float) this.quality * luck), 0);
	}

	public abstract void addLoot(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context);

	protected abstract void serialize(JSONObject json);

	public static class Serializer
			implements JSONTypeDeserializer<JSONObject, LootEntry>, JSONTypeCodec<LootEntry, JSONObject> {
		public LootEntry deserialize(JSONObject jsonobject) throws JSONException {
			String s = jsonobject.getString("type");
			int i = jsonobject.optInt("weight", 1);
			int j = jsonobject.optInt("quality", 0);
			LootCondition[] alootcondition;

			if (jsonobject.has("conditions")) {
				JSONArray conditions = jsonobject.getJSONArray("conditions");
				alootcondition = new LootCondition[conditions.length()];
				for (int i1 = 0; i1 < alootcondition.length; i1++) {
					alootcondition[i1] = JSONTypeProvider.deserialize(conditions.get(i1), LootCondition.class);
				}
			} else {
				alootcondition = new LootCondition[0];
			}

			if ("item".equals(s)) {
				return LootEntryItem.deserialize(jsonobject, i, j, alootcondition);
			} else if ("loot_table".equals(s)) {
				return LootEntryTable.deserialize(jsonobject, i, j, alootcondition);
			} else if ("empty".equals(s)) {
				return LootEntryEmpty.deserialize(jsonobject, i, j, alootcondition);
			} else {
				throw new JSONException("Unknown loot entry type '" + s + "'");
			}
		}

		public JSONObject serialize(LootEntry p_serialize_1_) {
			JSONObject jsonobject = new JSONObject();
			jsonobject.put("weight", Integer.valueOf(p_serialize_1_.weight));
			jsonobject.put("quality", Integer.valueOf(p_serialize_1_.quality));

			if (p_serialize_1_.conditions.length > 0) {
				JSONArray conditions = new JSONArray();
				for (LootCondition condition : p_serialize_1_.conditions) {
					conditions.put((Object) JSONTypeProvider.serialize(condition));
				}
				jsonobject.put("conditions", conditions);
			}

			if (p_serialize_1_ instanceof LootEntryItem) {
				jsonobject.put("type", "item");
			} else if (p_serialize_1_ instanceof LootEntryTable) {
				jsonobject.put("type", "loot_table");
			} else {
				if (!(p_serialize_1_ instanceof LootEntryEmpty)) {
					throw new IllegalArgumentException("Don't know how to serialize " + p_serialize_1_);
				}

				jsonobject.put("type", "empty");
			}

			p_serialize_1_.serialize(jsonobject);
			return jsonobject;
		}
	}
}
