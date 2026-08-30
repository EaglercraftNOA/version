package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;

import org.apache.commons.lang3.Validate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SoundListSerializer implements JSONTypeDeserializer<JSONObject, SoundList> {
	public SoundList deserialize(JSONObject jsonobject) throws JSONException {
		boolean flag = jsonobject.optBoolean("replace", false);
		String s = jsonobject.optString("subtitle", (String) null);
		List<Sound> list = this.deserializeSounds(jsonobject);
		return new SoundList(list, flag, s);
	}

	private List<Sound> deserializeSounds(JSONObject object) {
		List<Sound> list = Lists.<Sound>newArrayList();

		if (object.has("sounds")) {
			JSONArray jsonarray = object.getJSONArray("sounds");

			for (int i = 0; i < jsonarray.length(); ++i) {
				Object jsonelement = jsonarray.get(i);

				// TODO: come back to this later, it might be broken
				if (jsonelement instanceof String) {
					list.add(new Sound((String) jsonelement, 1.0F, 1.0F, 1, Sound.Type.FILE, false));
				} else {
					if (jsonelement instanceof JSONObject) {
						list.add(this.deserializeSound(((JSONObject) jsonelement)));
					}
				}
			}
		}

		return list;
	}

	private Sound deserializeSound(JSONObject object) {
		String s = object.getString("name");
		Sound.Type sound$type = this.deserializeType(object, Sound.Type.FILE);
		float f = object.optFloat("volume", 1.0F);
		Validate.isTrue(f > 0.0F, "Invalid volume");
		float f1 = object.optFloat("pitch", 1.0F);
		Validate.isTrue(f1 > 0.0F, "Invalid pitch");
		int i = object.optInt("weight", 1);
		Validate.isTrue(i > 0, "Invalid weight");
		boolean flag = object.optBoolean("stream", false);
		return new Sound(s, f, f1, i, sound$type, flag);
	}

	private Sound.Type deserializeType(JSONObject object, Sound.Type defaultValue) {
		Sound.Type sound$type = defaultValue;

		if (object.has("type")) {
			sound$type = Sound.Type.getByName(object.getString("type"));
			Validate.notNull(sound$type, "Invalid type");
		}

		return sound$type;
	}
}
