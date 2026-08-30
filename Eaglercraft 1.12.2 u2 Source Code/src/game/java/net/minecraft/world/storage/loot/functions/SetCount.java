package net.minecraft.world.storage.loot.functions;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.peyton.eagler.json.JSONUtils;

public class SetCount extends LootFunction {
	private final RandomValueRange countRange;

	public SetCount(LootCondition[] conditionsIn, RandomValueRange countRangeIn) {
		super(conditionsIn);
		this.countRange = countRangeIn;
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		stack.func_190920_e(this.countRange.generateInt(rand));
		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<SetCount> {
		protected Serializer() {
			super(new ResourceLocation("set_count"), SetCount.class);
		}

		public void serialize(JSONObject object, SetCount functionClazz) {
			object.put("count", (Object) JSONTypeProvider.serialize(functionClazz.countRange));
		}

		public SetCount deserialize(JSONObject object, LootCondition[] conditionsIn) {
			return new SetCount(conditionsIn,
					(RandomValueRange) JSONTypeProvider.deserialize(object.get("count"), RandomValueRange.class));
		}
	}
}
