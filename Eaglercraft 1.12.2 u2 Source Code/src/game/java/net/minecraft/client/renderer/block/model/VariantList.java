package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

public class VariantList {
	private final List<Variant> variantList;

	public VariantList(List<Variant> variantListIn) {
		this.variantList = variantListIn;
	}

	public List<Variant> getVariantList() {
		return this.variantList;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (p_equals_1_ instanceof VariantList) {
			VariantList variantlist = (VariantList) p_equals_1_;
			return this.variantList.equals(variantlist.variantList);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return this.variantList.hashCode();
	}

	public static class Deserializer implements JSONTypeDeserializer<Object, VariantList> {
		public VariantList deserialize(Object p_deserialize_1_) throws JSONException {
			List<Variant> list = Lists.<Variant>newArrayList();

			if (p_deserialize_1_ instanceof JSONArray) {
				JSONArray jsonarray = (JSONArray) p_deserialize_1_;

				if (jsonarray.length() == 0) {
					throw new JSONException("Empty variant array");
				}

				for (Object jsonelement : jsonarray) {
					list.add((Variant) JSONTypeProvider.deserialize(jsonelement, Variant.class));
				}
			} else {
				list.add((Variant) JSONTypeProvider.deserialize(p_deserialize_1_, Variant.class));
			}

			return new VariantList(list);
		}
	}
}
