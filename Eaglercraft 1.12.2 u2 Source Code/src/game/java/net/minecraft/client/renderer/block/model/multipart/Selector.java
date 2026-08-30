package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.VariantList;

public class Selector {
	private final ICondition condition;
	private final VariantList variantList;

	public Selector(ICondition conditionIn, VariantList variantListIn) {
		if (conditionIn == null) {
			throw new IllegalArgumentException("Missing condition for selector");
		} else if (variantListIn == null) {
			throw new IllegalArgumentException("Missing variant for selector");
		} else {
			this.condition = conditionIn;
			this.variantList = variantListIn;
		}
	}

	public VariantList getVariantList() {
		return this.variantList;
	}

	public Predicate<IBlockState> getPredicate(BlockStateContainer state) {
		return this.condition.getPredicate(state);
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else {
			if (p_equals_1_ instanceof Selector) {
				Selector selector = (Selector) p_equals_1_;

				if (this.condition.equals(selector.condition)) {
					return this.variantList.equals(selector.variantList);
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.condition.hashCode() + this.variantList.hashCode();
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, Selector> {
		private static final Function<JSONObject, ICondition> FUNCTION_OR_AND = new Function<JSONObject, ICondition>() {
			@Nullable
			public ICondition apply(@Nullable JSONObject p_apply_1_) {
				return p_apply_1_ == null ? null : Selector.Deserializer.getOrAndCondition(p_apply_1_);
			}
		};
		private static final Function<Entry<String, Object>, ICondition> FUNCTION_PROPERTY_VALUE = new Function<Entry<String, Object>, ICondition>() {
			@Nullable
			public ICondition apply(@Nullable Entry<String, Object> p_apply_1_) {
				return p_apply_1_ == null ? null : Selector.Deserializer.makePropertyValue(p_apply_1_);
			}
		};

		public Selector deserialize(JSONObject jsonobject) throws JSONException {
			return new Selector(this.getWhenCondition(jsonobject),
					(VariantList) JSONTypeProvider.deserialize(jsonobject.get("apply"), VariantList.class));
		}

		private ICondition getWhenCondition(JSONObject json) {
			return json.has("when") ? getOrAndCondition(json.getJSONObject("when")) : ICondition.TRUE;
		}

		@VisibleForTesting
		static ICondition getOrAndCondition(JSONObject json) {
			Set<Entry<String, Object>> set = json.entrySet();

			if (set.isEmpty()) {
				throw new JSONException("No elements found in selector");
			} else if (set.size() == 1) {
				if (json.has("OR")) {
					return new ConditionOr(Iterables.transform(json.getJSONArray("OR"), FUNCTION_OR_AND));
				} else {
					return (ICondition) (json.has("AND")
							? new ConditionAnd(Iterables.transform(json.getJSONArray("AND"), FUNCTION_OR_AND))
							: makePropertyValue(set.iterator().next()));
				}
			} else {
				return new ConditionAnd(Iterables.transform(set, FUNCTION_PROPERTY_VALUE));
			}
		}

		private static ConditionPropertyValue makePropertyValue(Entry<String, Object> entry) {
			return new ConditionPropertyValue(entry.getKey(), ((Object) entry.getValue()).toString());
		}
	}
}
