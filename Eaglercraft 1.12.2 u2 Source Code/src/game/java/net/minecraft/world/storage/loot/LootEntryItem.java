package net.minecraft.world.storage.loot;

import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.peyton.eagler.json.JSONUtils;

public class LootEntryItem extends LootEntry {
	protected final Item item;
	protected final LootFunction[] functions;

	public LootEntryItem(Item itemIn, int weightIn, int qualityIn, LootFunction[] functionsIn,
			LootCondition[] conditionsIn) {
		super(weightIn, qualityIn, conditionsIn);
		this.item = itemIn;
		this.functions = functionsIn;
	}

	public void addLoot(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context) {
		ItemStack itemstack = new ItemStack(this.item);

		for (LootFunction lootfunction : this.functions) {
			if (LootConditionManager.testAllConditions(lootfunction.getConditions(), rand, context)) {
				itemstack = lootfunction.apply(itemstack, rand, context);
			}
		}

		if (!itemstack.func_190926_b()) {
			if (itemstack.func_190916_E() < this.item.getItemStackLimit()) {
				stacks.add(itemstack);
			} else {
				int i = itemstack.func_190916_E();

				while (i > 0) {
					ItemStack itemstack1 = itemstack.copy();
					itemstack1.func_190920_e(Math.min(itemstack.getMaxStackSize(), i));
					i -= itemstack1.func_190916_E();
					stacks.add(itemstack1);
				}
			}
		}
	}

	protected void serialize(JSONObject json) {
		if (this.functions != null && this.functions.length > 0) {
			JSONArray functions = new JSONArray();
			for (LootFunction function : this.functions) {
				functions.put((Object) JSONTypeProvider.serialize(function));
			}
			json.put("functions", functions);
		}

		ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(this.item);

		if (resourcelocation == null) {
			throw new IllegalArgumentException("Can't serialize unknown item " + this.item);
		} else {
			json.put("name", resourcelocation.toString());
		}
	}

	public static LootEntryItem deserialize(JSONObject object, int weightIn, int qualityIn,
			LootCondition[] conditionsIn) {
		Item item = JSONUtils.getItem(object, "name");
		LootFunction[] alootfunction;

		if (object.has("functions")) {
			JSONArray functions = object.getJSONArray("functions");
			alootfunction = new LootFunction[functions.length()];
			for (int i = 0; i < alootfunction.length; i++) {
				alootfunction[i] = JSONTypeProvider.deserialize(functions.get(i), LootFunction.class);
			}
		} else {
			alootfunction = new LootFunction[0];
		}

		return new LootEntryItem(item, weightIn, qualityIn, alootfunction, conditionsIn);
	}
}
