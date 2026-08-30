package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.renderer.block.model.VariantList;

public class Multipart {
	private final List<Selector> selectors;
	private BlockStateContainer stateContainer;

	public Multipart(List<Selector> selectorsIn) {
		this.selectors = selectorsIn;
	}

	public List<Selector> getSelectors() {
		return this.selectors;
	}

	public Set<VariantList> getVariants() {
		Set<VariantList> set = Sets.<VariantList>newHashSet();

		for (Selector selector : this.selectors) {
			set.add(selector.getVariantList());
		}

		return set;
	}

	public void setStateContainer(BlockStateContainer stateContainerIn) {
		this.stateContainer = stateContainerIn;
	}

	public BlockStateContainer getStateContainer() {
		return this.stateContainer;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else {
			if (p_equals_1_ instanceof Multipart) {
				Multipart multipart = (Multipart) p_equals_1_;

				if (this.selectors.equals(multipart.selectors)) {
					if (this.stateContainer == null) {
						return multipart.stateContainer == null;
					}

					return this.stateContainer.equals(multipart.stateContainer);
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.selectors.hashCode() + (this.stateContainer == null ? 0 : this.stateContainer.hashCode());
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONArray, Multipart> {
		public Multipart deserialize(JSONArray p_deserialize_1_) throws JSONException {
			return new Multipart(this.getSelectors(p_deserialize_1_));
		}

		private List<Selector> getSelectors(JSONArray elements) {
			List<Selector> list = Lists.<Selector>newArrayList();

			for (Object jsonelement : elements) {
				list.add((Selector) JSONTypeProvider.deserialize(jsonelement, Selector.class));
			}

			return list;
		}
	}
}
