package net.minecraft.util;

import net.lax1dude.eaglercraft.internal.PlatformApplication;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ScreenShotHelper {

	/**
	 * Saves a screenshot in the game directory with a time-stamped filename.
	 * Returns an ITextComponent indicating the success/failure of the saving.
	 */
	public static ITextComponent saveScreenshot() {
		return new TextComponentString("Saved Screenshot As: " + PlatformApplication.saveScreenshot());
	}
}
