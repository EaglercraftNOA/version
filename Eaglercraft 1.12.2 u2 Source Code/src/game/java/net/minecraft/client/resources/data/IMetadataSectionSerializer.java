package net.minecraft.client.resources.data;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;

public interface IMetadataSectionSerializer<T extends IMetadataSection> extends JSONTypeDeserializer<JSONObject, T> {
	/**
	 * The name of this section type as it appears in JSON.
	 */
	String getSectionName();
}
