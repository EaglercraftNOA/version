package net.minecraft.world.storage.loot.functions;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.peyton.eagler.json.JSONUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class SetMetadata extends LootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final RandomValueRange metaRange;

	public SetMetadata(LootCondition[] conditionsIn, RandomValueRange metaRangeIn) {
		super(conditionsIn);
		this.metaRange = metaRangeIn;
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		if (stack.isItemStackDamageable()) {
			LOGGER.warn("Couldn't set data of loot item {}", (Object) stack);
		} else {
			stack.setItemDamage(this.metaRange.generateInt(rand));
		}

		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<SetMetadata> {
		protected Serializer() {
			super(new ResourceLocation("set_data"), SetMetadata.class);
		}

		public void serialize(JSONObject object, SetMetadata functionClazz) {
			object.put("data", (Object) JSONTypeProvider.serialize(functionClazz.metaRange));
		}

		public SetMetadata deserialize(JSONObject object, LootCondition[] conditionsIn) {
			return new SetMetadata(conditionsIn,
					(RandomValueRange) JSONTypeProvider.deserialize(object.get("data"), RandomValueRange.class));
		}
	}
}
