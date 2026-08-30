package net.minecraft.util;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class MessageDeserializer {
	public Object fromJson(String json) throws JSONException {

		json = json.trim();
		if (json.startsWith("{")) {
			return new JSONObject(json);
		} else if (json.startsWith("[")) {
			return new JSONArray(json);
		} else {
			throw new JSONException("Invalid JSON input");
		}
	}

	public Object fromJson(Object json) throws JSONException {

		if (json instanceof JSONObject || json instanceof JSONArray) {
			return json;
		} else if (json instanceof String) {
			return fromJson((String) json);
		} else {
			throw new JSONException("Unsupported input type for deserialization");
		}
	}
}
