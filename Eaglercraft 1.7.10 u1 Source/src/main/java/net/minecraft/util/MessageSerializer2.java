package net.minecraft.util;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class MessageSerializer2 {
	public String toJson(Object obj) throws JSONException {
		if (obj instanceof JSONObject || obj instanceof JSONArray) {
			return obj.toString();
		} else {

			return new JSONObject(obj).toString();
		}
	}
}
