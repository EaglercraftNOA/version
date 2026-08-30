package net.minecraft.client.renderer.block.model;

import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;

public class BlockFaceUV {
	public float[] uvs;
	public final int rotation;

	public BlockFaceUV(@Nullable float[] uvsIn, int rotationIn) {
		this.uvs = uvsIn;
		this.rotation = rotationIn;
	}

	public float getVertexU(int p_178348_1_) {
		if (this.uvs == null) {
			throw new NullPointerException("uvs");
		} else {
			int i = this.getVertexRotated(p_178348_1_);
			return i != 0 && i != 1 ? this.uvs[2] : this.uvs[0];
		}
	}

	public float getVertexV(int p_178346_1_) {
		if (this.uvs == null) {
			throw new NullPointerException("uvs");
		} else {
			int i = this.getVertexRotated(p_178346_1_);
			return i != 0 && i != 3 ? this.uvs[3] : this.uvs[1];
		}
	}

	private int getVertexRotated(int p_178347_1_) {
		return (p_178347_1_ + this.rotation / 90) % 4;
	}

	public int getVertexRotatedRev(int p_178345_1_) {
		return (p_178345_1_ + (4 - this.rotation / 90)) % 4;
	}

	public void setUvs(float[] uvsIn) {
		if (this.uvs == null) {
			this.uvs = uvsIn;
		}
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, BlockFaceUV> {
		public BlockFaceUV deserialize(JSONObject jsonobject) throws JSONException {
			float[] afloat = this.parseUV(jsonobject);
			int i = this.parseRotation(jsonobject);
			return new BlockFaceUV(afloat, i);
		}

		protected int parseRotation(JSONObject object) {
			int i = object.optInt("rotation", 0);

			if (i >= 0 && i % 90 == 0 && i / 90 <= 3) {
				return i;
			} else {
				throw new JSONException("Invalid rotation " + i + " found, only 0/90/180/270 allowed");
			}
		}

		@Nullable
		private float[] parseUV(JSONObject object) {
			if (!object.has("uv")) {
				return null;
			} else {
				JSONArray jsonarray = object.getJSONArray("uv");

				if (jsonarray.length() != 4) {
					throw new JSONException("Expected 4 uv values, found: " + jsonarray.length());
				} else {
					float[] afloat = new float[4];

					for (int i = 0; i < afloat.length; ++i) {
						afloat[i] = jsonarray.getFloat(i);
					}

					return afloat;
				}
			}
		}
	}
}
