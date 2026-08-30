package net.lax1dude.eaglercraft.beta;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Item;
import net.minecraft.src.TextureFX;

public class TextureNewClockFX extends TextureFX {

	private final int[] clockSpriteSheet;
	private final int clockSpriteSheetLength;
	private float field_94239_h;
	private float field_94240_i;

	public TextureNewClockFX() {
		super(Item.pocketSundial.getIconIndex(null));
		field_1128_f = 1;
		this.clockSpriteSheet = ImageData.loadImageFile(EagRuntime.getRequiredResourceBytes("/sprite_sheet/clock.png")).swapRB().pixels;
		this.clockSpriteSheetLength = clockSpriteSheet.length / 256;
	}

	public void onTick() {
		Minecraft var1 = Minecraft.getMinecraft();
		double var2 = 0.0D;

		if (var1.theWorld != null && var1.thePlayer != null) {
			float var4 = var1.theWorld.getCelestialAngle(1.0F);
			var2 = (double) var4;

			if (var1.theWorld.worldProvider.field_4220_c) {
				var2 = Math.random();
			}
		}

		double var7;

		for (var7 = var2 - this.field_94239_h; var7 < -0.5D; ++var7) {
			;
		}

		while (var7 >= 0.5D) {
			--var7;
		}

		if (var7 < -1.0D) {
			var7 = -1.0D;
		}

		if (var7 > 1.0D) {
			var7 = 1.0D;
		}

		this.field_94240_i += var7 * 0.1D;
		this.field_94240_i *= 0.8D;
		this.field_94239_h += this.field_94240_i;
		int var6;
		
		for (var6 = (int) ((this.field_94239_h + 1.0D) * (double) clockSpriteSheetLength) % clockSpriteSheetLength; var6 < 0; var6 = (var6 + clockSpriteSheetLength) % clockSpriteSheetLength) {
			;
		}
		
		int offset = var6 * 256;
		for(int i = 0; i < 256; ++i) {
			this.field_1127_a[i * 4] = (byte)((clockSpriteSheet[offset + i] >> 16) & 0xFF);
			this.field_1127_a[i * 4 + 1] = (byte)((clockSpriteSheet[offset + i] >> 8) & 0xFF);
			this.field_1127_a[i * 4 + 2] = (byte)(clockSpriteSheet[offset + i] & 0xFF);
			this.field_1127_a[i * 4 + 3] = (byte)((clockSpriteSheet[offset + i] >> 24) & 0xFF);
		}
	}
	
}