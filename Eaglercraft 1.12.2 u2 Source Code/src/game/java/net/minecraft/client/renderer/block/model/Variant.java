package net.minecraft.client.renderer.block.model;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.minecraft.util.ResourceLocation;

public class Variant {
	private final ResourceLocation modelLocation;
	private final ModelRotation rotation;
	private final boolean uvLock;
	private final int weight;

	public Variant(ResourceLocation modelLocationIn, ModelRotation rotationIn, boolean uvLockIn, int weightIn) {
		this.modelLocation = modelLocationIn;
		this.rotation = rotationIn;
		this.uvLock = uvLockIn;
		this.weight = weightIn;
	}

	public ResourceLocation getModelLocation() {
		return this.modelLocation;
	}

	public ModelRotation getRotation() {
		return this.rotation;
	}

	public boolean isUvLock() {
		return this.uvLock;
	}

	public int getWeight() {
		return this.weight;
	}

	public String toString() {
		return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock
				+ ", weight=" + this.weight + '}';
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof Variant)) {
			return false;
		} else {
			Variant variant = (Variant) p_equals_1_;
			return this.modelLocation.equals(variant.modelLocation) && this.rotation == variant.rotation
					&& this.uvLock == variant.uvLock && this.weight == variant.weight;
		}
	}

	public int hashCode() {
		int i = this.modelLocation.hashCode();
		i = 31 * i + this.rotation.hashCode();
		i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
		i = 31 * i + this.weight;
		return i;
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, Variant> {
		public Variant deserialize(JSONObject jsonobject) throws JSONException {
			String s = this.getStringModel(jsonobject);
			ModelRotation modelrotation = this.parseModelRotation(jsonobject);
			boolean flag = this.parseUvLock(jsonobject);
			int i = this.parseWeight(jsonobject);
			return new Variant(this.getResourceLocationBlock(s), modelrotation, flag, i);
		}

		private ResourceLocation getResourceLocationBlock(String p_188041_1_) {
			ResourceLocation resourcelocation = new ResourceLocation(p_188041_1_);
			resourcelocation = new ResourceLocation(resourcelocation.getResourceDomain(),
					"block/" + resourcelocation.getResourcePath());
			return resourcelocation;
		}

		private boolean parseUvLock(JSONObject json) {
			return json.optBoolean("uvlock", false);
		}

		protected ModelRotation parseModelRotation(JSONObject json) {
			int i = json.optInt("x", 0);
			int j = json.optInt("y", 0);
			ModelRotation modelrotation = ModelRotation.getModelRotation(i, j);

			if (modelrotation == null) {
				throw new JSONException("Invalid BlockModelRotation x: " + i + ", y: " + j);
			} else {
				return modelrotation;
			}
		}

		protected String getStringModel(JSONObject json) {
			return json.getString("model");
		}

		protected int parseWeight(JSONObject json) {
			int i = json.optInt("weight", 1);

			if (i < 1) {
				throw new JSONException("Invalid weight " + i + " found, expected integer >= 1");
			} else {
				return i;
			}
		}
	}
}
