package net.minecraft.util;

import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.item.Item;

public class JsonUtils {

	public static Item getItem(Object json, String memberName) {
		if (json instanceof String) {
			String s = (String) json;
			Item item = Item.getItemById(Integer.parseInt(s));
			if (item == null) {
				throw new JSONException("Expected " + memberName + " to be an item, was unknown string '" + s + "'");
			} else {
				return item;
			}
		} else {
			throw new JSONException(
					"Expected " + memberName + " to be an item, was " + json + "(" + json.getClass() + ")");
		}
	}

	public static Item getItem(JSONObject json, String memberName) {
		if (json.has(memberName)) {
			return getItem(json.get(memberName), memberName);
		} else {
			throw new JSONException("Missing " + memberName + ", expected to find an item");
		}
	}

}
