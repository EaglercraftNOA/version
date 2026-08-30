package net.minecraft.client.resources.data;

import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import org.json.JSONObject;

public interface IMetadataSectionSerializer<T extends IMetadataSection> extends JSONTypeDeserializer<JSONObject, T> {
	/**
	 * The name of this section type as it appears in JSON.
	 */
	String getSectionName();
}
