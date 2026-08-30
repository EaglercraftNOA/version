package net.peyton.eagler.json;

import javax.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.item.Item;

public class JSONUtils {
//	 public static <T> T deserializeClass(@Nullable Object json, String memberName, Class <? extends T > adapter) {
//		 if(adapter.isArray()) {
//			 throw new RuntimeException("Cannot deserialize array type!");
//		 }
//		 
//		 if (json != null) {
//			 return (T)JSONTypeProvider.deserialize(json, adapter);
//		 } else {
//			 throw new JSONException("Missing " + memberName);
//		 }
//	 }
//	
//	 public static <T> T deserializeClass(JSONObject json, String memberName, Class <? extends T > adapter) {
//		 if(adapter.isArray()) {
//			 throw new RuntimeException("Cannot deserialize array type!");
//		 }
//		 
//		 if (json.has(memberName)) {
//			 return (T)deserializeClass(json.get(memberName), memberName, adapter);
//		 } else {
//			 throw new JSONException("Missing " + memberName);
//		 }
//	 }
//
//    public static <T> T deserializeClass(JSONObject json, String memberName, T fallback, Class <? extends T > adapter) {
//    	if(adapter.isArray()) {
//    		throw new RuntimeException("Cannot deserialize array type!");
//    	}
//    	
//        return (T)(json.has(memberName) ? deserializeClass(json.get(memberName), memberName, adapter) : fallback);
//    }

	public static Item getItem(Object json, String memberName) {
		if (json instanceof String) {
			String s = (String) json;
			Item item = Item.getByNameOrId(s);

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
