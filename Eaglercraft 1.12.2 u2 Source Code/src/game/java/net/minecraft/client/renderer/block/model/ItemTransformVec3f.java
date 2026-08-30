package net.minecraft.client.renderer.block.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.vector.Vector3f;
import net.minecraft.util.math.MathHelper;

public class ItemTransformVec3f {
	public static final ItemTransformVec3f DEFAULT = new ItemTransformVec3f(new Vector3f(), new Vector3f(),
			new Vector3f(1.0F, 1.0F, 1.0F));
	public final Vector3f rotation;
	public final Vector3f translation;
	public final Vector3f scale;

	public ItemTransformVec3f(Vector3f rotation, Vector3f translation, Vector3f scale) {
		this.rotation = rotation != null ? new Vector3f(rotation) : new Vector3f();
		this.translation = translation != null ? new Vector3f(translation) : new Vector3f();
		this.scale = scale != null ? new Vector3f(scale) : new Vector3f(1.0F, 1.0F, 1.0F);
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof ItemTransformVec3f)) {
			return false;
		}

		ItemTransformVec3f other = (ItemTransformVec3f) obj;
		return areVectorsEqual(this.rotation, other.rotation) && areVectorsEqual(this.translation, other.translation)
				&& areVectorsEqual(this.scale, other.scale);
	}

	public int hashCode() {
		int result = 1;
		result = 31 * result + hashVector(rotation);
		result = 31 * result + hashVector(translation);
		result = 31 * result + hashVector(scale);
		return result;
	}

	private static boolean areVectorsEqual(Vector3f v1, Vector3f v2) {
		if (v1 == v2)
			return true;
		if (v1 == null || v2 == null)
			return false;
		return Float.compare(v1.x, v2.x) == 0 && Float.compare(v1.y, v2.y) == 0 && Float.compare(v1.z, v2.z) == 0;
	}

	private static int hashVector(Vector3f v) {
		if (v == null)
			return 0;
		int result = Float.floatToIntBits(v.x);
		result = 31 * result + Float.floatToIntBits(v.y);
		result = 31 * result + Float.floatToIntBits(v.z);
		return result;
	}

	public static class Deserializer implements JSONTypeDeserializer<JSONObject, ItemTransformVec3f> {
		private static final Vector3f ROTATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
		private static final Vector3f TRANSLATION_DEFAULT = new Vector3f(0.0F, 0.0F, 0.0F);
		private static final Vector3f SCALE_DEFAULT = new Vector3f(1.0F, 1.0F, 1.0F);

		public ItemTransformVec3f deserialize(JSONObject jsonobject) throws JSONException {
			Vector3f vector3f = this.parseVector3f(jsonobject, "rotation", ROTATION_DEFAULT);
			Vector3f vector3f1 = this.parseVector3f(jsonobject, "translation", TRANSLATION_DEFAULT);
			vector3f1.scale(0.0625F);
			vector3f1.x = MathHelper.clamp(vector3f1.x, -5.0F, 5.0F);
			vector3f1.y = MathHelper.clamp(vector3f1.y, -5.0F, 5.0F);
			vector3f1.z = MathHelper.clamp(vector3f1.z, -5.0F, 5.0F);
			Vector3f vector3f2 = this.parseVector3f(jsonobject, "scale", SCALE_DEFAULT);
			vector3f2.x = MathHelper.clamp(vector3f2.x, -4.0F, 4.0F);
			vector3f2.y = MathHelper.clamp(vector3f2.y, -4.0F, 4.0F);
			vector3f2.z = MathHelper.clamp(vector3f2.z, -4.0F, 4.0F);
			return new ItemTransformVec3f(vector3f, vector3f1, vector3f2);
		}

		private Vector3f parseVector3f(JSONObject jsonObject, String key, Vector3f defaultValue) {
			if (!jsonObject.has(key)) {
				return new Vector3f(defaultValue);
			}

			JSONArray jsonarray = jsonObject.getJSONArray(key);

			if (jsonarray.length() != 3) {
				throw new JSONException("Expected 3 " + key + " values, found: " + jsonarray.length());
			}

			float[] afloat = new float[3];
			for (int i = 0; i < 3; ++i) {
				afloat[i] = jsonarray.getFloat(i);
			}

			return new Vector3f(afloat[0], afloat[1], afloat[2]);
		}
	}
}