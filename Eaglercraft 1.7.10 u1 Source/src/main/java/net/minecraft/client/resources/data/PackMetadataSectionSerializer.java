package net.minecraft.client.resources.data;

import net.minecraft.util.IChatComponent;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.lax1dude.eaglercraft.json.JSONTypeSerializer;

import java.awt.*;

public class PackMetadataSectionSerializer extends BaseMetadataSectionSerializer<PackMetadataSection>
		implements JSONTypeSerializer<PackMetadataSection, JSONObject> {
	public PackMetadataSection deserialize(JSONObject jsonobject) throws JSONException {
		Object desc = jsonobject.get("description");
		IChatComponent itextcomponent;
		if (desc instanceof String) {
			itextcomponent = new net.minecraft.util.ChatComponentText((String) desc);
		} else {
			itextcomponent = (IChatComponent) JSONTypeProvider.deserialize(desc, IChatComponent.class);
		}

		if (itextcomponent == null) {
			throw new JSONException("Invalid/missing description!");
		}

		int packFormat;
		if (jsonobject.has("pack_format")) {
			Object pf = jsonobject.get("pack_format");
			if (pf instanceof Number) {
				packFormat = ((Number) pf).intValue();
			} else if (pf instanceof String) {
				try {
					packFormat = Integer.parseInt((String) pf);
				} catch (NumberFormatException e) {
					packFormat = 1;				}
			} else {
				packFormat = 1;
			}
		} else {
			packFormat = 1;
		}

		return new PackMetadataSection(itextcomponent, packFormat);
	}

	public JSONObject serialize(PackMetadataSection p_serialize_1_) {
		JSONObject jsonobject = new JSONObject();
		jsonobject.put("pack_format", Integer.valueOf(p_serialize_1_.getPackFormat()));
		return jsonobject;
	}

	/**
	 * The name of this section type as it appears in JSON.
	 */
	public String getSectionName() {
		return "pack";
	}
}
