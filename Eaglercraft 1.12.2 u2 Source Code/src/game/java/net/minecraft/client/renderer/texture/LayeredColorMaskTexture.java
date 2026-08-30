package net.minecraft.client.renderer.texture;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LayeredColorMaskTexture extends AbstractTexture {
	/** Access to the Logger, for all your logging needs. */
	private static final Logger LOG = LogManager.getLogger();

	/** The location of the texture. */
	private final ResourceLocation textureLocation;
	private final List<String> listTextures;
	private final List<EnumDyeColor> listDyeColors;

	public LayeredColorMaskTexture(ResourceLocation textureLocationIn, List<String> p_i46101_2_,
			List<EnumDyeColor> p_i46101_3_) {
		this.textureLocation = textureLocationIn;
		this.listTextures = p_i46101_2_;
		this.listDyeColors = p_i46101_3_;
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
		this.deleteGlTexture();
		ImageData bufferedimage;
		try {
			ImageData bufferedimage1 = TextureUtil
					.readBufferedImage(resourceManager.getResource(this.textureLocation).getInputStream());
			bufferedimage = new ImageData(bufferedimage1.width, bufferedimage1.height, false);
			bufferedimage.drawLayer(bufferedimage1, 0, 0, bufferedimage1.width, bufferedimage1.height, 0, 0,
					bufferedimage1.width, bufferedimage1.height);
			bufferedimage.swapRB();

			for (int j = 0; j < 17 && j < this.listTextures.size() && j < this.listDyeColors.size(); ++j) {
				String s = this.listTextures.get(j);
				int k = ((EnumDyeColor) this.listDyeColors.get(j)).func_193350_e();

				if (s != null) {
					InputStream inputStream = resourceManager.getResource(new ResourceLocation(s)).getInputStream();
					ImageData bufferedimage2 = TextureUtil.readBufferedImage(inputStream);

					if (bufferedimage2.width == bufferedimage.width
							&& bufferedimage2.height == bufferedimage.height /* && bufferedimage2.alpha = false !?!? */) {
						for (int l = 0; l < bufferedimage2.height; ++l) {
							for (int i1 = 0; i1 < bufferedimage2.width; ++i1) {
								int j1 = bufferedimage2.pixels[l * bufferedimage2.width + i1];
								if ((j1 & -16777216) != 0) {
									int k1 = (j1 & 16711680) << 8 & -16777216;
									int l1 = bufferedimage1.pixels[l * bufferedimage1.width + i1];
									int i2 = MathHelper.multiplyColor(l1, k) & 16777215;
									bufferedimage2.pixels[l * bufferedimage2.width + i1] = k1 | i2;
								}
							}
						}

						bufferedimage.drawLayer(bufferedimage2, 0, 0, bufferedimage2.width, bufferedimage2.height, 0, 0,
								bufferedimage2.width, bufferedimage2.height);
						bufferedimage.swapRB();
					}
				}
			}
		} catch (IOException ioexception) {
			LOG.error("Couldn't load layered image", (Throwable) ioexception);
			return;
		}

		TextureUtil.uploadTextureImage(this.getGlTextureId(), bufferedimage);
	}
}