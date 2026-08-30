package net.minecraft.client.resources.data;

import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.lax1dude.eaglercraft.json.JSONTypeSerializer;
import net.minecraft.util.text.ITextComponent;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer<PackMetadataSection>
		implements JSONTypeSerializer<PackMetadataSection, JSONObject> {
	public PackMetadataSection deserialize(JSONObject jsonobject) throws JSONException {
		ITextComponent itextcomponent = (ITextComponent) JSONTypeProvider.deserialize(jsonobject.get("description"),
				ITextComponent.class);

		if (itextcomponent == null) {
			throw new JSONException("Invalid/missing description!");
		} else {
			int i = jsonobject.getInt("pack_format");
			return new PackMetadataSection(itextcomponent, i);
		}
	}

	public JSONObject serialize(PackMetadataSection p_serialize_1_) {
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("pack_format", Integer.valueOf(p_serialize_1_.getPackFormat()));
		jsonobject.put("description", (JSONObject) JSONTypeProvider.serialize(p_serialize_1_.getPackDescription()));
		return jsonobject;
	}

	/**
	 * The name of this section type as it appears in JSON.
	 */
	public String getSectionName() {
		return "pack";
	}
}
