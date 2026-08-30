package net.minecraft.world.storage.loot;

import java.util.Collection;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.LootCondition;

public class LootEntryTable extends LootEntry {
	protected final ResourceLocation table;

	public LootEntryTable(ResourceLocation tableIn, int weightIn, int qualityIn, LootCondition[] conditionsIn) {
		super(weightIn, qualityIn, conditionsIn);
		this.table = tableIn;
	}

	public void addLoot(Collection<ItemStack> stacks, EaglercraftRandom rand, LootContext context) {
		LootTable loottable = context.getLootTableManager().getLootTableFromLocation(this.table);
		Collection<ItemStack> collection = loottable.generateLootForPools(rand, context);
		stacks.addAll(collection);
	}

	protected void serialize(JSONObject json) {
		json.put("name", this.table.toString());
	}

	public static LootEntryTable deserialize(JSONObject object, int weightIn, int qualityIn,
			LootCondition[] conditionsIn) {
		ResourceLocation resourcelocation = new ResourceLocation(object.getString("name"));
		return new LootEntryTable(resourcelocation, weightIn, qualityIn, conditionsIn);
	}
}
