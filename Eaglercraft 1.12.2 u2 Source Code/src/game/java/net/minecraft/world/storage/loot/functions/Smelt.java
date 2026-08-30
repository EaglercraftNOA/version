package net.minecraft.world.storage.loot.functions;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Smelt extends LootFunction {
	private static final Logger LOGGER = LogManager.getLogger();

	public Smelt(LootCondition[] conditionsIn) {
		super(conditionsIn);
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		if (stack.func_190926_b()) {
			return stack;
		} else {
			ItemStack itemstack = FurnaceRecipes.instance().getSmeltingResult(stack);

			if (itemstack.func_190926_b()) {
				LOGGER.warn("Couldn't smelt {} because there is no smelting recipe", (Object) stack);
				return stack;
			} else {
				ItemStack itemstack1 = itemstack.copy();
				itemstack1.func_190920_e(stack.func_190916_E());
				return itemstack1;
			}
		}
	}

	public static class Serializer extends LootFunction.Serializer<Smelt> {
		protected Serializer() {
			super(new ResourceLocation("furnace_smelt"), Smelt.class);
		}

		public void serialize(JSONObject object, Smelt functionClazz) {
		}

		public Smelt deserialize(JSONObject object, LootCondition[] conditionsIn) {
			return new Smelt(conditionsIn);
		}
	}
}
