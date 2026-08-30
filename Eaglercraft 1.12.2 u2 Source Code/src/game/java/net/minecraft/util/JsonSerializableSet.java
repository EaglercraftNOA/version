package net.minecraft.util;

import java.util.Set;

import org.json.JSONArray;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable {
	/**
	 * + The set for this ForwardingSet to forward methods to.
	 */
	private final Set<String> underlyingSet = Sets.newHashSet();

	public void fromJson(Object jsonelement) {
		if (jsonelement instanceof JSONArray) {
			JSONArray arr = (JSONArray) jsonelement;
			for (int i = 0; i < arr.length(); ++i) {
				underlyingSet.add(arr.getString(i));
			}
		}

	}

	/**
	 * + Gets the JsonElement that can be serialized.
	 */
	public Object getSerializableElement() {
		JSONArray jsonarray = new JSONArray();

		for (String s : this) {
			jsonarray.put(s);
		}

		return jsonarray;
	}

	protected Set<String> delegate() {
		return this.underlyingSet;
	}
}