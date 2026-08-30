package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.io.InputStream;
import java.io.IOException;
import java.util.List;

import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredTexture extends AbstractTexture {
	private static final Logger LOGGER = LogManager.getLogger();
	public final List<String> layeredTextureNames;

	public LayeredTexture(String... textureNames) {
		this.layeredTextureNames = Lists.newArrayList(textureNames);
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
		this.deleteGlTexture();
		ImageData bufferedimage = null;

		for (int i = 0, l = this.layeredTextureNames.size(); i < l; ++i) {
			String s = this.layeredTextureNames.get(i);

			try {
				if (s != null) {
					InputStream inputstream = resourceManager.getResource(new ResourceLocation(s)).getInputStream();
					ImageData bufferedimage1 = TextureUtil.readBufferedImage(inputstream);

					if (bufferedimage == null) {
						bufferedimage = new ImageData(bufferedimage1.width, bufferedimage1.height, true);
					}

					bufferedimage.drawLayer(bufferedimage1, 0, 0, bufferedimage1.width, bufferedimage1.height, 0, 0,
							bufferedimage1.width, bufferedimage1.height);
				}
			} catch (IOException ioexception) {
				LOGGER.error("Couldn't load layered image", (Throwable) ioexception);
				return;
			}
		}

		regenerateIfNotAllocated();
		TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedimage);
	}
}