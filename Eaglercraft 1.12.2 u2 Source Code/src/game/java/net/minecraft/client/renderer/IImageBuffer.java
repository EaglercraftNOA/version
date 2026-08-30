package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.opengl.ImageData;

public interface IImageBuffer {
	ImageData parseUserSkin(ImageData image);

	void skinAvailable();
}
