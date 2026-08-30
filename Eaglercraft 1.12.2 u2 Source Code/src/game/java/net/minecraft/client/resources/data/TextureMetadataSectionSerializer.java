package net.minecraft.client.resources.data;

import org.json.JSONException;
import org.json.JSONObject;

public class TextureMetadataSectionSerializer extends BaseMetadataSectionSerializer<TextureMetadataSection> {
	public TextureMetadataSection deserialize(JSONObject jsonobject) throws JSONException {
		boolean flag = jsonobject.optBoolean("blur", false);
		boolean flag1 = jsonobject.optBoolean("clamp", false);
		return new TextureMetadataSection(flag, flag1);
	}

	/**
	 * The name of this section type as it appears in JSON.
	 */
	public String getSectionName() {
		return "texture";
	}
}
