package net.minecraft.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.peyton.eagler.minecraft.ResourcePack;

import org.lwjgl.opengl.GL11;

public class TexturePackCustom extends TexturePackBase {
	private int texturePackName = -1;
	private ImageData texturePackThumbnail;
	private ResourcePack rp;

	public TexturePackCustom(ResourcePack var1) {
		this.texturePackFileName = var1.getName();
		this.rp = var1;
	}

	private String truncateString(String var1) {
		if (var1 != null && var1.length() > 34) {
			var1 = var1.substring(0, 34);
		}

		return var1;
	}

	public void func_6485_a(Minecraft var1) throws IOException {
		InputStream var3 = null;

		try {
			try {
				var3 = rp.getResourceAsStream("pack.txt");
				BufferedReader var4 = new BufferedReader(new InputStreamReader(var3));
				this.firstDescriptionLine = this.truncateString(var4.readLine());
				this.secondDescriptionLine = this.truncateString(var4.readLine());
				var4.close();
				var3.close();
			} catch (Exception var20) {
			}

			try {
				var3 = rp.getResourceAsStream("pack.png");
				this.texturePackThumbnail = ImageData.loadImageFile(var3).swapRB();
				var3.close();
			} catch (Exception var19) {
			}
		} catch (Exception var21) {
			var21.printStackTrace();
		} finally {
			try {
				var3.close();
			} catch (Exception var18) {
			}
		}

	}

	public void func_6484_b(Minecraft var1) {
		if (this.texturePackThumbnail != null) {
			var1.renderEngine.deleteTexture(this.texturePackName);
		}

		this.closeTexturePackFile();
	}

	public void func_6483_c(Minecraft var1) {
		if (this.texturePackThumbnail != null && this.texturePackName < 0) {
			this.texturePackName = var1.renderEngine.allocateAndSetupTexture(this.texturePackThumbnail);
		}

		if (this.texturePackThumbnail != null) {
			var1.renderEngine.bindTexture(this.texturePackName);
		} else {
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, var1.renderEngine.getTexture("/gui/unknown_pack.png"));
		}

	}

	public void func_6482_a() {
	}

	public void closeTexturePackFile() {
	}

	public InputStream func_6481_a(String var1) {
		return rp.getResourceAsStream(var1);
	}
}