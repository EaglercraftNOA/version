package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;

public class EnchantRandomly extends LootFunction {
	private static final Logger LOGGER = LogManager.getLogger();
	private final List<Enchantment> enchantments;

	public EnchantRandomly(LootCondition[] conditionsIn, @Nullable List<Enchantment> enchantmentsIn) {
		super(conditionsIn);
		this.enchantments = enchantmentsIn == null ? Collections.emptyList() : enchantmentsIn;
	}

	public ItemStack apply(ItemStack stack, EaglercraftRandom rand, LootContext context) {
		Enchantment enchantment;

		if (this.enchantments.isEmpty()) {
			List<Enchantment> list = Lists.<Enchantment>newArrayList();

			for (Enchantment enchantment1 : Enchantment.REGISTRY) {
				if (stack.getItem() == Items.BOOK || enchantment1.canApply(stack)) {
					list.add(enchantment1);
				}
			}

			if (list.isEmpty()) {
				LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object) stack);
				return stack;
			}

			enchantment = list.get(rand.nextInt(list.size()));
		} else {
			enchantment = this.enchantments.get(rand.nextInt(this.enchantments.size()));
		}

		int i = MathHelper.getInt(rand, enchantment.getMinLevel(), enchantment.getMaxLevel());

		if (stack.getItem() == Items.BOOK) {
			stack = new ItemStack(Items.ENCHANTED_BOOK);
			ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, i));
		} else {
			stack.addEnchantment(enchantment, i);
		}

		return stack;
	}

	public static class Serializer extends LootFunction.Serializer<EnchantRandomly> {
		public Serializer() {
			super(new ResourceLocation("enchant_randomly"), EnchantRandomly.class);
		}

		public void serialize(JSONObject object, EnchantRandomly functionClazz) {
			if (!functionClazz.enchantments.isEmpty()) {
				JSONArray jsonarray = new JSONArray();

				for (Enchantment enchantment : functionClazz.enchantments) {
					ResourceLocation resourcelocation = Enchantment.REGISTRY.getNameForObject(enchantment);

					if (resourcelocation == null) {
						throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
					}

					jsonarray.put(new String(resourcelocation.toString()));
				}

				object.put("enchantments", jsonarray);
			}
		}

		public EnchantRandomly deserialize(JSONObject object, LootCondition[] conditionsIn) {
			List<Enchantment> list = Lists.<Enchantment>newArrayList();

			if (object.has("enchantments")) {
				for (Object jsonelement : object.getJSONArray("enchantments")) {
					String s = (String) jsonelement;
					Enchantment enchantment = Enchantment.REGISTRY.getObject(new ResourceLocation(s));

					if (enchantment == null) {
						throw new JSONException("Unknown enchantment '" + s + "'");
					}

					list.add(enchantment);
				}
			}

			return new EnchantRandomly(conditionsIn, list);
		}
	}
}
