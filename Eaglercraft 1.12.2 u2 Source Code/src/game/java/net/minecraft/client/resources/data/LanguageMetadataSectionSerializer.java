package net.minecraft.client.resources.data;

import com.google.common.collect.Sets;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import net.minecraft.client.resources.Language;

public class LanguageMetadataSectionSerializer extends BaseMetadataSectionSerializer<LanguageMetadataSection> {
	public LanguageMetadataSection deserialize(JSONObject jsonobject) throws JSONException {
		Set<Language> set = Sets.<Language>newHashSet();

		for (String entry : jsonobject.keySet()) {
			String s = entry;

			if (s.length() > 16) {
				throw new JSONException("Invalid language->'" + s + "': language code must not be more than " + 16
						+ " characters long");
			}

			JSONObject jsonobject1 = jsonobject.getJSONObject(entry);
			String s1 = jsonobject1.getString("region");
			String s2 = jsonobject1.getString("name");
			boolean flag = jsonobject1.optBoolean("bidirectional", false);

			if (s1.isEmpty()) {
				throw new JSONException("Invalid language->'" + s + "'->region: empty value");
			}

			if (s2.isEmpty()) {
				throw new JSONException("Invalid language->'" + s + "'->name: empty value");
			}

			if (!set.add(new Language(s, s1, s2, flag))) {
				throw new JSONException("Duplicate language->'" + s + "' defined");
			}
		}

		return new LanguageMetadataSection(set);
	}

	/**
	 * The name of this section type as it appears in JSON.
	 */
	public String getSectionName() {
		return "language";
	}
}
