package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.client.renderer.block.model.multipart.Multipart;

public class ModelBlockDefinition {
	private final Map<String, VariantList> mapVariants = Maps.<String, VariantList>newHashMap();
	private Multipart multipart;

	public static ModelBlockDefinition parseFromReader(Reader reader) {
		return (ModelBlockDefinition) JSONTypeProvider.deserialize(reader, ModelBlockDefinition.class);
	}

	public ModelBlockDefinition(Map<String, VariantList> variants, Multipart multipartIn) {
		this.multipart = multipartIn;
		this.mapVariants.putAll(variants);
	}

	public ModelBlockDefinition(List<ModelBlockDefinition> p_i46222_1_) {
		ModelBlockDefinition modelblockdefinition = null;

		for (ModelBlockDefinition modelblockdefinition1 : p_i46222_1_) {
			if (modelblockdefinition1.hasMultipartData()) {
				this.mapVariants.clear();
				modelblockdefinition = modelblockdefinition1;
			}

			this.mapVariants.putAll(modelblockdefinition1.mapVariants);
		}

		if (modelblockdefinition != null) {
			this.multipart = modelblockdefinition.multipart;
		}
	}

	public boolean hasVariant(String p_188000_1_) {
		return this.mapVariants.get(p_188000_1_) != null;
	}

	public VariantList getVariant(String p_188004_1_) {
		VariantList variantlist = this.mapVariants.get(p_188004_1_);

		if (variantlist == null) {
			throw new ModelBlockDefinition.MissingVariantException();
		} else {
			return variantlist;
		}
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else {
			if (p_equals_1_ instanceof ModelBlockDefinition) {
				ModelBlockDefinition modelblockdefinition = (ModelBlockDefinition) p_equals_1_;

				if (this.mapVariants.equals(modelblockdefinition.mapVariants)) {
					return this.hasMultipartData() ? this.multipart.equals(modelblockdefinition.multipart)
							: !modelblockdefinition.hasMultipartData();
				}
			}

			return false;
		}
	}

	public int hashCode() {
		return 31 * this.mapVariants.hashCode() + (this.hasMultipartData() ? this.multipart.hashCode() : 0);
	}

	public Set<VariantList> getMultipartVariants() {
		Set<VariantList> set = Sets.newHashSet(this.mapVariants.values());

		if (this.hasMultipartData()) {
			set.addAll(this.multipart.getVariants());
		}

		return set;
	}

	public boolean hasMultipartData() {
		return this.multipart != null;
	}

	public Multipart getMultipartData() {
		return this.multipart;
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, ModelBlockDefinition> {
		public ModelBlockDefinition deserialize(JSONObject jsonobject) throws JSONException {
			Map<String, VariantList> map = this.parseMapVariants(jsonobject);
			Multipart multipart = this.parseMultipart(jsonobject);

			if (!map.isEmpty() || multipart != null && !multipart.getVariants().isEmpty()) {
				return new ModelBlockDefinition(map, multipart);
			} else {
				throw new JSONException("Neither 'variants' nor 'multipart' found");
			}
		}

		protected Map<String, VariantList> parseMapVariants(JSONObject object) {
			Map<String, VariantList> map = Maps.<String, VariantList>newHashMap();

			if (object.has("variants")) {
				JSONObject jsonobject = object.getJSONObject("variants");

				for (String entry : jsonobject.keySet()) {
					map.put(entry,
							(VariantList) JSONTypeProvider.deserialize(jsonobject.get(entry), VariantList.class));
				}
			}

			return map;
		}

		@Nullable
		protected Multipart parseMultipart(JSONObject object) {
			if (!object.has("multipart")) {
				return null;
			} else {
				JSONArray jsonarray = object.getJSONArray("multipart");
				return (Multipart) JSONTypeProvider.deserialize(jsonarray, Multipart.class);
			}
		}
	}

	public class MissingVariantException extends RuntimeException {
	}
}
