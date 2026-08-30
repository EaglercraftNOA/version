package net.minecraft.world.storage.loot.functions;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.peyton.eagler.json.JSONUtils;

public class EnchantWithLevels extends LootFunction {
	private final RandomValueRange randomLevel;
	private final boolean isTreasure;

	public EnchantWithLevels(LootCondition[] conditionsIn, RandomValueRange randomRange, boolean p_i46627_3_) {
		super(conditionsIn);
		this.randomLevel = randomRange;
		this.isTreasure = p_i46627_3_;
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		return EnchantmentHelper.addRandomEnchantment(rand, stack, this.randomLevel.generateInt(rand), this.isTreasure);
	}

	public static class Serializer extends LootFunction.Serializer<EnchantWithLevels> {
		public Serializer() {
			super(new ResourceLocation("enchant_with_levels"), EnchantWithLevels.class);
		}

		public void serialize(JSONObject object, EnchantWithLevels functionClazz) {
			object.put("levels", (Object) JSONTypeProvider.serialize(functionClazz.randomLevel));
			object.put("treasure", Boolean.valueOf(functionClazz.isTreasure));
		}

		public EnchantWithLevels deserialize(JSONObject object, LootCondition[] conditionsIn) {
			RandomValueRange randomvaluerange = (RandomValueRange) JSONTypeProvider.deserialize(object.get("levels"),
					RandomValueRange.class);
			boolean flag = object.optBoolean("treasure", false);
			return new EnchantWithLevels(conditionsIn, randomvaluerange, flag);
		}
	}
}
