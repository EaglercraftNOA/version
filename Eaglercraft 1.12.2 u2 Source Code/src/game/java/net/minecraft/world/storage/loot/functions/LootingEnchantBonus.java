package net.minecraft.world.storage.loot.functions;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.peyton.eagler.json.JSONUtils;

public class LootingEnchantBonus extends LootFunction {
	private final RandomValueRange count;
	private final int limit;

	public LootingEnchantBonus(LootCondition[] p_i47145_1_, RandomValueRange p_i47145_2_, int p_i47145_3_) {
		super(p_i47145_1_);
		this.count = p_i47145_2_;
		this.limit = p_i47145_3_;
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		Entity entity = context.getKiller();

		if (entity instanceof EntityLivingBase) {
			int i = EnchantmentHelper.getLootingModifier((EntityLivingBase) entity);

			if (i == 0) {
				return stack;
			}

			float f = (float) i * this.count.generateFloat(rand);
			stack.func_190917_f(Math.round(f));

			if (this.limit != 0 && stack.func_190916_E() > this.limit) {
				stack.func_190920_e(this.limit);
			}
		}

		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<LootingEnchantBonus> {
		protected Serializer() {
			super(new ResourceLocation("looting_enchant"), LootingEnchantBonus.class);
		}

		public void serialize(JSONObject object, LootingEnchantBonus functionClazz) {
			object.put("count", (Object) JSONTypeProvider.serialize(functionClazz.count));

			if (functionClazz.limit > 0) {
				object.put("limit", Integer.valueOf(functionClazz.limit));
			}
		}

		public LootingEnchantBonus deserialize(JSONObject object, LootCondition[] conditionsIn) {
			int i = object.optInt("limit", 0);
			return new LootingEnchantBonus(conditionsIn,
					(RandomValueRange) JSONTypeProvider.deserialize(object.get("count"), RandomValueRange.class), i);
		}
	}
}
