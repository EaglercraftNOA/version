package net.minecraft.client.resources.data;

import com.google.common.collect.Lists;
import java.util.List;

import net.lax1dude.eaglercraft.json.JSONTypeSerializer;
import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnimationMetadataSectionSerializer extends BaseMetadataSectionSerializer<AnimationMetadataSection>
		implements JSONTypeSerializer<AnimationMetadataSection, JSONObject> {
	public AnimationMetadataSection deserialize(JSONObject jsonobject) throws JSONException {
		List<AnimationFrame> list = Lists.<AnimationFrame>newArrayList();
		int i = jsonobject.optInt("frametime", 1);

		if (i != 1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long) i, "Invalid default frame time");
		}

		if (jsonobject.has("frames")) {
			try {
				JSONArray jsonarray = jsonobject.getJSONArray("frames");

				for (int j = 0; j < jsonarray.length(); ++j) {
					AnimationFrame animationframe = this.parseAnimationFrame(j, jsonarray.get(j));

					if (animationframe != null) {
						list.add(animationframe);
					}
				}
			} catch (ClassCastException classcastexception) {
				throw new JSONException("Invalid animation->frames: expected array, was " + jsonobject.get("frames"),
						classcastexception);
			}
		}

		int k = jsonobject.optInt("width", -1);
		int l = jsonobject.optInt("height", -1);

		if (k != -1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long) k, "Invalid width");
		}

		if (l != -1) {
			Validate.inclusiveBetween(1L, 2147483647L, (long) l, "Invalid height");
		}

		boolean flag = jsonobject.optBoolean("interpolate", false);
		return new AnimationMetadataSection(list, k, l, i, flag);
	}

	private AnimationFrame parseAnimationFrame(int frame, Object element) {
		if (element instanceof Number) {
			return new AnimationFrame(((Number) element).intValue());
		} else if (element instanceof JSONObject) {
			JSONObject jsonobject = (JSONObject) element;
			int i = jsonobject.optInt("time", -1);

			if (jsonobject.has("time")) {
				Validate.inclusiveBetween(1L, 2147483647L, (long) i, "Invalid frame time");
			}

			int j = jsonobject.getInt("index");
			Validate.inclusiveBetween(0L, 2147483647L, (long) j, "Invalid frame index");
			return new AnimationFrame(j, i);
		} else {
			return null;
		}
	}

	public JSONObject serialize(AnimationMetadataSection p_serialize_1_) {
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("frametime", Integer.valueOf(p_serialize_1_.getFrameTime()));

		if (p_serialize_1_.getFrameWidth() != -1) {
			jsonobject.put("width", Integer.valueOf(p_serialize_1_.getFrameWidth()));
		}

		if (p_serialize_1_.getFrameHeight() != -1) {
			jsonobject.put("height", Integer.valueOf(p_serialize_1_.getFrameHeight()));
		}

		if (p_serialize_1_.getFrameCount() > 0) {
			JSONArray jsonarray = new JSONArray();

			for (int i = 0; i < p_serialize_1_.getFrameCount(); ++i) {
				if (p_serialize_1_.frameHasTime(i)) {
					JSONObject jsonobject1 = new JSONObject();
					jsonobject1.put("index", Integer.valueOf(p_serialize_1_.getFrameIndex(i)));
					jsonobject1.put("time", Integer.valueOf(p_serialize_1_.getFrameTimeSingle(i)));
					jsonarray.put(jsonobject1);
				} else {
					jsonarray.put(Integer.valueOf(p_serialize_1_.getFrameIndex(i)));
				}
			}

			jsonobject.put("frames", jsonarray);
		}

		return jsonobject;
	}

	/**
	 * The name of this section type as it appears in JSON.
	 */
	public String getSectionName() {
		return "animation";
	}
}
