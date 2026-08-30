package net.minecraft.util;

import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;

import java.util.Locale;
import org.apache.commons.lang3.Validate;
import org.json.JSONException;
import org.json.JSONObject;

public class ResourceLocation implements Comparable<ResourceLocation> {
	protected final String resourceDomain;
	protected final String resourcePath;
	
	public Object cachedPointer = null;
	public int cachedPointerType = 0;

	public static final int CACHED_POINTER_NONE = 0;
	public static final int CACHED_POINTER_TEXTURE = 1;
	public static final int CACHED_POINTER_EAGLER_MESH = 2;

	protected ResourceLocation(int unused, String... resourceName) {
		this.resourceDomain = org.apache.commons.lang3.StringUtils.isEmpty(resourceName[0]) ? "minecraft"
				: resourceName[0].toLowerCase();
		this.resourcePath = resourceName[1].toLowerCase();
		Validate.notNull(this.resourcePath);
	}

	public ResourceLocation(String resourceName) {
		this(0, splitObjectName(resourceName));
	}

	public ResourceLocation(String resourceDomainIn, String resourcePathIn) {
		this(0, resourceDomainIn, resourcePathIn);
	}

	/**
	 * Splits an object name (such as minecraft:apple) into the domain and path
	 * parts and returns these as an array of length 2. If no colon is present in
	 * the passed value the returned array will contain {null, toSplit}.
	 */
	protected static String[] splitObjectName(String toSplit) {
		String[] astring = new String[] { "minecraft", toSplit };
		int i = toSplit.indexOf(58);

		if (i >= 0) {
			astring[1] = toSplit.substring(i + 1, toSplit.length());

			if (i > 1) {
				astring[0] = toSplit.substring(0, i);
			}
		}

		return astring;
	}

	public String getResourcePath() {
		return this.resourcePath;
	}

	public String getResourceDomain() {
		return this.resourceDomain;
	}

	public String toString() {
		return this.resourceDomain + ':' + this.resourcePath;
	}

	public boolean equals(Object p_equals_1_) {
		if (this == p_equals_1_) {
			return true;
		} else if (!(p_equals_1_ instanceof ResourceLocation)) {
			return false;
		} else {
			ResourceLocation resourcelocation = (ResourceLocation) p_equals_1_;
			return this.resourceDomain.equals(resourcelocation.resourceDomain)
					&& this.resourcePath.equals(resourcelocation.resourcePath);
		}
	}

	public int hashCode() {
		return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
	}

	public int compareTo(ResourceLocation p_compareTo_1_) {
		int i = this.resourceDomain.compareTo(p_compareTo_1_.resourceDomain);

		if (i == 0) {
			i = this.resourcePath.compareTo(p_compareTo_1_.resourcePath);
		}

		return i;
	}

	public static class Serializer
			implements JSONTypeDeserializer<JSONObject, ResourceLocation>, JSONTypeCodec<ResourceLocation, JSONObject> {
		public ResourceLocation deserialize(JSONObject p_deserialize_1_) throws JSONException {
			return new ResourceLocation(p_deserialize_1_.getString("location"));
		}

		public JSONObject serialize(ResourceLocation p_serialize_1_) {
			JSONObject jsonobject = new JSONObject();
			jsonobject.put("location", p_serialize_1_.toString());
			return jsonobject;
		}
	}
}
