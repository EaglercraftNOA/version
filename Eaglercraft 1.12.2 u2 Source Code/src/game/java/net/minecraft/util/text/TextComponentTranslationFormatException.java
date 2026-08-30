package net.minecraft.util.text;

import net.lax1dude.eaglercraft.HString;

public class TextComponentTranslationFormatException extends IllegalArgumentException {
	public TextComponentTranslationFormatException(TextComponentTranslation component, String message) {
		super(HString.format("Error parsing: %s: %s", new Object[] { component, message }));
	}

	public TextComponentTranslationFormatException(TextComponentTranslation component, int index) {
		super(HString.format("Invalid index %d requested for %s", new Object[] { Integer.valueOf(index), component }));
	}

	public TextComponentTranslationFormatException(TextComponentTranslation component, Throwable cause) {
		super(HString.format("Error while parsing: %s", new Object[] { component }), cause);
	}
}
