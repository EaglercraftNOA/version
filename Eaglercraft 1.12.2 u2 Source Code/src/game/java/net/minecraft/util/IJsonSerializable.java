package net.minecraft.util;

public interface IJsonSerializable {
	void fromJson(Object json);

	/**
	 * Gets the JsonElement that can be serialized.
	 */
	Object getSerializableElement();
}
