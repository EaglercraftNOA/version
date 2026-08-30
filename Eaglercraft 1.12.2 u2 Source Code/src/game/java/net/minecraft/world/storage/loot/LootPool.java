package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.peyton.eagler.json.JSONUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LootPool {
	private final LootEntry[] lootEntries;
	private final LootCondition[] poolConditions;
	private final RandomValueRange rolls;
	private final RandomValueRange bonusRolls;

	public LootPool(LootEntry[] lootEntriesIn, LootCondition[] poolConditionsIn, RandomValueRange rollsIn,
			RandomValueRange bonusRollsIn) {
		this.lootEntries = lootEntriesIn;
		this.poolConditions = poolConditionsIn;
		this.rolls = rollsIn;
		this.bonusRolls = bonusRollsIn;
	}

	/**
	 * generates the contents for a single roll. The first for loop calculates the
	 * sum of all the lootentries and the second for loop adds a random item with
	 * items with higher weights being more probable.
	 */
	protected void createLootRoll(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context) {
		List<LootEntry> list = Lists.<LootEntry>newArrayList();
		int i = 0;

		for (LootEntry lootentry : this.lootEntries) {
			if (LootConditionManager.testAllConditions(lootentry.conditions, rand, context)) {
				int j = lootentry.getEffectiveWeight(context.getLuck());

				if (j > 0) {
					list.add(lootentry);
					i += j;
				}
			}
		}

		if (i != 0 && !list.isEmpty()) {
			int k = rand.nextInt(i);

			for (LootEntry lootentry1 : list) {
				k -= lootentry1.getEffectiveWeight(context.getLuck());

				if (k < 0) {
					lootentry1.addLoot(stacks, rand, context);
					return;
				}
			}
		}
	}

	/**
	 * generates loot and puts it in an inventory
	 */
	public void generateLoot(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context) {
		if (LootConditionManager.testAllConditions(this.poolConditions, rand, context)) {
			int i = this.rolls.generateInt(rand)
					+ MathHelper.floor(this.bonusRolls.generateFloat(rand) * context.getLuck());

			for (int j = 0; j < i; ++j) {
				this.createLootRoll(stacks, rand, context);
			}
		}
	}

	public static class Serializer
			implements JSONTypeDeserializer<JSONObject, LootPool>, JSONTypeCodec<LootPool, JSONObject> {
		public LootPool deserialize(JSONObject jsonobject) throws JSONException {
			LootEntry[] alootentry;
			if (jsonobject.has("entries")) {
				JSONArray jsonarray = jsonobject.getJSONArray("entries");
				alootentry = new LootEntry[jsonarray.length()];
				for (int i = 0; i < alootentry.length; i++) {
					alootentry[i] = (LootEntry) JSONTypeProvider.deserialize(jsonarray.get(i), LootEntry.class);
				}
			} else {
				alootentry = new LootEntry[0];
			}
			LootCondition[] alootcondition;
			if (jsonobject.has("conditions")) {
				JSONArray jsonarray = jsonobject.getJSONArray("conditions");
				alootcondition = new LootCondition[jsonarray.length()];
				for (int i = 0; i < alootcondition.length; i++) {
					alootcondition[i] = (LootCondition) JSONTypeProvider.deserialize(jsonarray.get(i),
							LootCondition.class);
				}
			} else {
				alootcondition = new LootCondition[0];
			}
			RandomValueRange randomvaluerange = (RandomValueRange) JSONTypeProvider.deserialize(jsonobject.get("rolls"),
					RandomValueRange.class);
			RandomValueRange randomvaluerange1 = null;
			try {
				randomvaluerange1 = (RandomValueRange) JSONTypeProvider.deserialize(jsonobject.get("bonus_rolls"),
						RandomValueRange.class);
			} catch (Throwable t) {
				randomvaluerange1 = new RandomValueRange(0.0F, 0.0F);
			}
			if (randomvaluerange1 == null) {
				randomvaluerange1 = new RandomValueRange(0.0F, 0.0F);
			}
			return new LootPool(alootentry, alootcondition, randomvaluerange, randomvaluerange1);
		}

		public JSONObject serialize(LootPool p_serialize_1_) {
			JSONObject jsonobject = new JSONObject();
			JSONArray entries = new JSONArray();
			for (LootEntry entry : p_serialize_1_.lootEntries) {
				entries.put((Object) JSONTypeProvider.serialize(entry));
			}
			jsonobject.put("entries", entries);
			jsonobject.put("rolls", (Object) JSONTypeProvider.serialize(p_serialize_1_.rolls));

			if (p_serialize_1_.bonusRolls.getMin() != 0.0F && p_serialize_1_.bonusRolls.getMax() != 0.0F) {
				jsonobject.put("bonus_rolls", (Object) JSONTypeProvider.serialize(p_serialize_1_.bonusRolls));
			}

			if (!ArrayUtils.isEmpty((Object[]) p_serialize_1_.poolConditions)) {
				JSONArray conditions = new JSONArray();
				for (LootCondition condition : p_serialize_1_.poolConditions) {
					conditions.put((Object) JSONTypeProvider.serialize(condition));
				}
				jsonobject.put("conditions", conditions);
			}

			return jsonobject;
		}
	}
}
