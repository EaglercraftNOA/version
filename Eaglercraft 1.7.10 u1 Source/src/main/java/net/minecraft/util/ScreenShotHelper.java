package net.minecraft.util;

import net.lax1dude.eaglercraft.internal.PlatformApplication;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.internal.PlatformRuntime;

public class ScreenShotHelper {

	/**
	 * Saves a screenshot in the game directory with a time-stamped filename.
	 * Returns an ITextComponent indicating the success/failure of the saving.
	 */
	public static IChatComponent saveScreenshot() {
		return new ChatComponentText("Saved Screenshot As: " + PlatformApplication.saveScreenshot());
	}

	/**
	 * Captures the current contents of the given framebuffer as a ImageData.
	 */
	public static ImageData createScreenshot(int width, int height) {
		int bpp = 4;		ByteBuffer buffer = PlatformRuntime.allocateByteBuffer(width * height * bpp);
		org.lwjgl.opengl.GL11.glReadPixels(0, 0, width, height, org.lwjgl.opengl.GL11.GL_RGBA,
				org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE, buffer);

		int[] pixels = new int[width * height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int i = (x + (width * (height - y - 1))) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				int a = buffer.get(i + 3) & 0xFF;
				pixels[x + y * width] = ((a << 24) | (r << 16) | (g << 8) | b);
			}
		}
		return new ImageData(width, height, pixels, true);
	}
}