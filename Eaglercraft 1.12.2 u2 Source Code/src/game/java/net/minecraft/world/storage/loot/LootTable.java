package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LootTable {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final LootTable EMPTY_LOOT_TABLE = new LootTable(new LootPool[0]);
	private final LootPool[] pools;

	public LootTable(LootPool[] poolsIn) {
		this.pools = poolsIn;
	}

	public List<ItemStack> generateLootForPools(EaglercraftRandom rand, LootContext context) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();

		if (context.addLootTable(this)) {
			for (LootPool lootpool : this.pools) {
				lootpool.generateLoot(list, rand, context);
			}

			context.removeLootTable(this);
		} else {
			LOGGER.warn("Detected infinite loop in loot tables");
		}

		return list;
	}

	public void fillInventory(IInventory inventory, EaglercraftRandom rand, LootContext context) {
		List<ItemStack> list = this.generateLootForPools(rand, context);
		List<Integer> list1 = this.getEmptySlotsRandomized(inventory, rand);
		this.shuffleItems(list, list1.size(), rand);

		for (ItemStack itemstack : list) {
			if (list1.isEmpty()) {
				LOGGER.warn("Tried to over-fill a container");
				return;
			}

			if (itemstack.func_190926_b()) {
				inventory.setInventorySlotContents(((Integer) list1.remove(list1.size() - 1)).intValue(),
						ItemStack.field_190927_a);
			} else {
				inventory.setInventorySlotContents(((Integer) list1.remove(list1.size() - 1)).intValue(), itemstack);
			}
		}
	}

	/**
	 * shuffles items by changing their order and splitting stacks
	 */
	private void shuffleItems(List<ItemStack> stacks, int p_186463_2_, EaglercraftRandom rand) {
		List<ItemStack> list = Lists.<ItemStack>newArrayList();
		Iterator<ItemStack> iterator = stacks.iterator();

		while (iterator.hasNext()) {
			ItemStack itemstack = iterator.next();

			if (itemstack.func_190926_b()) {
				iterator.remove();
			} else if (itemstack.func_190916_E() > 1) {
				list.add(itemstack);
				iterator.remove();
			}
		}

		p_186463_2_ = p_186463_2_ - stacks.size();

		while (p_186463_2_ > 0 && !list.isEmpty()) {
			ItemStack itemstack2 = list.remove(MathHelper.getInt(rand, 0, list.size() - 1));
			int i = MathHelper.getInt(rand, 1, itemstack2.func_190916_E() / 2);
			ItemStack itemstack1 = itemstack2.splitStack(i);

			if (itemstack2.func_190916_E() > 1 && rand.nextBoolean()) {
				list.add(itemstack2);
			} else {
				stacks.add(itemstack2);
			}

			if (itemstack1.func_190916_E() > 1 && rand.nextBoolean()) {
				list.add(itemstack1);
			} else {
				stacks.add(itemstack1);
			}
		}

		stacks.addAll(list);
		Collections.shuffle(stacks, rand);
	}

	private List<Integer> getEmptySlotsRandomized(IInventory inventory, EaglercraftRandom rand) {
		List<Integer> list = Lists.<Integer>newArrayList();

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			if (inventory.getStackInSlot(i).func_190926_b()) {
				list.add(Integer.valueOf(i));
			}
		}

		Collections.shuffle(list, rand);
		return list;
	}

	public static class Serializer
			implements JSONTypeDeserializer<JSONObject, LootTable>, JSONTypeCodec<LootTable, JSONObject> {
		public LootTable deserialize(JSONObject jsonobject) throws JSONException {
			LootPool[] alootpool;
			if (jsonobject.has("pools")) {
				JSONArray jsonarray = jsonobject.getJSONArray("pools");
				alootpool = new LootPool[jsonarray.length()];
				for (int i = 0; i < alootpool.length; i++) {
					alootpool[i] = (LootPool) JSONTypeProvider.deserialize(jsonarray.get(i), LootPool.class);
				}
			} else {
				alootpool = new LootPool[0];
			}
			return new LootTable(alootpool);
		}

		public JSONObject serialize(LootTable p_serialize_1_) {
			JSONObject jsonobject = new JSONObject();
			JSONArray pools = new JSONArray();
			for (LootPool pool : p_serialize_1_.pools) {
				pools.put((Object) JSONTypeProvider.serialize(pool));
			}
			jsonobject.put("pools", pools);
			return jsonobject;
		}
	}
}
