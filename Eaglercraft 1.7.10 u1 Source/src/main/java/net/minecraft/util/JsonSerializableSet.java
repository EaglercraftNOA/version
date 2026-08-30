package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONException;

public class JsonSerializableSet extends ForwardingSet<String> implements IJsonSerializable {
	/** The set for this ForwardingSet to forward methods to. */
	private final Set<String> underlyingSet = Sets.newHashSet();
	private static final String __OBFID = "CL_00001482";

	@Override
	public void func_152753_a(Object json) throws JSONException {
		if (json instanceof JSONArray) {
			JSONArray arr = (JSONArray) json;
			for (int i = 0; i < arr.length(); ++i) {
				this.add(arr.getString(i));
			}
		}
	}

	/**
	 * Gets the JsonElement that can be serialized.
	 */
	@Override
	public Object getSerializableElement() throws JSONException {
		JSONArray jsonarray = new JSONArray();
		for (String s : this) {
			jsonarray.put(s);
		}
		return jsonarray;
	}

	@Override
	protected Set<String> delegate() {
		return this.underlyingSet;
	}
}
