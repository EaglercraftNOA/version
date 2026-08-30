package net.minecraft.world.storage.loot;

import java.util.Collection;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryEmpty extends LootEntry {
	public LootEntryEmpty(int weightIn, int qualityIn, LootCondition[] conditionsIn) {
		super(weightIn, qualityIn, conditionsIn);
	}

	public void addLoot(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context) {
	}

	protected void serialize(JSONObject json) {
	}

	public static LootEntryEmpty deserialize(JSONObject object, int weightIn, int qualityIn,
			LootCondition[] conditionsIn) {
		return new LootEntryEmpty(weightIn, qualityIn, conditionsIn);
	}
}
