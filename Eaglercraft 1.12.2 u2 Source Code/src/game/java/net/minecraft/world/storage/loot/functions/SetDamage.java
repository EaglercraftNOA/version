package net.minecraft.world.storage.loot.functions;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.peyton.eagler.json.JSONUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class SetDamage extends LootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RandomValueRange damageRange;

	public SetDamage(LootCondition[] conditionsIn, RandomValueRange damageRangeIn) {
		super(conditionsIn);
		this.damageRange = damageRangeIn;
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		if (stack.isItemStackDamageable()) {
			float f = 1.0F - this.damageRange.generateFloat(rand);
			stack.setItemDamage(MathHelper.floor(f * (float) stack.getMaxDamage()));
		} else {
			LOGGER.warn("Couldn't set damage of loot item {}", (Object) stack);
		}

		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<SetDamage> {
		protected Serializer() {
			super(new ResourceLocation("set_damage"), SetDamage.class);
		}

		public void serialize(JSONObject object, SetDamage functionClazz) {
			object.put("damage", (Object) JSONTypeProvider.serialize(functionClazz.damageRange));
		}

		public SetDamage deserialize(JSONObject object, LootCondition[] conditionsIn) {
			return new SetDamage(conditionsIn,
					(RandomValueRange) JSONTypeProvider.deserialize(object.get("damage"), RandomValueRange.class));
		}
	}
}
