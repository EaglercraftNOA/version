package net.minecraft.util;

import org.json.JSONObject;
import org.json.JSONException;

public interface IJsonSerializable {
	void func_152753_a(Object json) throws JSONException;

	/**
	 * Gets the JSONObject that can be serialized.
	 */
	Object getSerializableElement() throws JSONException;
}
