package net.minecraft.advancements.critereon;

import java.util.Map;
import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;

public class EnchantmentPredicate {
	public static final EnchantmentPredicate field_192466_a = new EnchantmentPredicate();
	private final Enchantment field_192467_b;
	private final MinMaxBounds field_192468_c;

	public EnchantmentPredicate() {
		this.field_192467_b = null;
		this.field_192468_c = MinMaxBounds.field_192516_a;
	}

	public EnchantmentPredicate(@Nullable Enchantment p_i47436_1_, MinMaxBounds p_i47436_2_) {
		this.field_192467_b = p_i47436_1_;
		this.field_192468_c = p_i47436_2_;
	}

	public boolean func_192463_a(Map<Enchantment, Integer> p_192463_1_) {
		if (this.field_192467_b != null) {
			if (!p_192463_1_.containsKey(this.field_192467_b)) {
				return false;
			}

			int i = ((Integer) p_192463_1_.get(this.field_192467_b)).intValue();

			if (this.field_192468_c != null && !this.field_192468_c.func_192514_a((float) i)) {
				return false;
			}
		} else if (this.field_192468_c != null) {
			for (Integer integer : p_192463_1_.values()) {
				if (this.field_192468_c.func_192514_a((float) integer.intValue())) {
					return true;
				}
			}

			return false;
		}

		return true;
	}

	public static EnchantmentPredicate func_192464_a(@Nullable Object p_192464_0_) {
		if (p_192464_0_ != null) {
			JSONObject jsonobject = (JSONObject) p_192464_0_;
			Enchantment enchantment = null;

			if (jsonobject.has("enchantment")) {
				ResourceLocation resourcelocation = new ResourceLocation(jsonobject.getString("enchantment"));
				enchantment = Enchantment.REGISTRY.getObject(resourcelocation);

				if (enchantment == null) {
					throw new JSONException("Unknown enchantment '" + resourcelocation + "'");
				}
			}

			MinMaxBounds minmaxbounds = MinMaxBounds.func_192515_a(jsonobject.get("levels"));
			return new EnchantmentPredicate(enchantment, minmaxbounds);
		} else {
			return field_192466_a;
		}
	}

	public static EnchantmentPredicate[] func_192465_b(@Nullable Object p_192465_0_) {
		if (p_192465_0_ != null) {
			JSONArray jsonarray = (JSONArray) p_192465_0_;
			EnchantmentPredicate[] aenchantmentpredicate = new EnchantmentPredicate[jsonarray.length()];

			for (int i = 0; i < aenchantmentpredicate.length; ++i) {
				aenchantmentpredicate[i] = func_192464_a(jsonarray.get(i));
			}

			return aenchantmentpredicate;
		} else {
			return new EnchantmentPredicate[0];
		}
	}
}
