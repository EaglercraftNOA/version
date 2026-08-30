package net.lax1dude.eaglercraft.beta;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.src.Item;
import net.minecraft.src.TextureFX;
import net.minecraft.src.World;

public class TextureNewCompassFX extends TextureFX {
	
	private Minecraft mc;
	private final int[] compassSpriteSheet;
	private final int compassSpriteSheetLength;
	private float angleDelta = 0.0f;
	private float currentAngle = 0.0f;
	
	public TextureNewCompassFX() {
		super(Item.compass.getIconIndex(null));
		mc = Minecraft.getMinecraft();
		field_1128_f = 1;
		this.compassSpriteSheet = ImageData.loadImageFile(EagRuntime.getRequiredResourceBytes("/sprite_sheet/compass.png")).swapRB().pixels;
		this.compassSpriteSheetLength = compassSpriteSheet.length / 256;
	}
	
	public void func_783_a() {
		Minecraft var1 = Minecraft.getMinecraft();

		if (var1.theWorld != null && var1.thePlayer != null) {
			this.updateCompass(var1.theWorld, var1.thePlayer.posX, var1.thePlayer.posZ, (double) var1.thePlayer.rotationYaw, false, false);
		} else {
			this.updateCompass((World) null, 0.0D, 0.0D, 0.0D, true, false);
		}
	}
	
	public void updateCompass(World par1World, double par2, double par4, double par6, boolean par8, boolean par9) {
		double var10 = 0.0D;

		if (par1World != null && !par8) {
			double var13 = (double)par1World.spawnX - this.mc.thePlayer.posX;
			double var15 = (double)par1World.spawnZ - this.mc.thePlayer.posZ;
			par6 %= 360.0D;
			var10 = -((par6 - 90.0D) * Math.PI / 180.0D - Math.atan2(var15, var13));

			if (this.mc.theWorld.worldProvider.field_4220_c) {
				var10 = Math.random() * Math.PI * 2.0D;
			}
		}

		if (par9) {
			this.currentAngle = (float) var10;
		} else {
			double var17;

			for (var17 = var10 - this.currentAngle; var17 < -Math.PI; var17 += (Math.PI * 2D)) {
				;
			}

			while (var17 >= Math.PI) {
				var17 -= (Math.PI * 2D);
			}

			if (var17 < -1.0D) {
				var17 = -1.0D;
			}

			if (var17 > 1.0D) {
				var17 = 1.0D;
			}

			this.angleDelta += var17 * 0.1D;
			this.angleDelta *= 0.8D;
			this.currentAngle += this.angleDelta;
		}

		int var18;

		for (var18 = (int) ((this.currentAngle / (Math.PI * 2D) + 1.0D) * (double) compassSpriteSheetLength) % compassSpriteSheetLength; var18 < 0; var18 = (var18 + compassSpriteSheetLength) % compassSpriteSheetLength) {
			;
		}
		
		int offset = var18 * 256;
		for(int i = 0; i < 256; ++i) {
			this.field_1127_a[i * 4] = (byte)((compassSpriteSheet[offset + i] >> 16) & 0xFF);
			this.field_1127_a[i * 4 + 1] = (byte)((compassSpriteSheet[offset + i] >> 8) & 0xFF);
			this.field_1127_a[i * 4 + 2] = (byte)((compassSpriteSheet[offset + i]) & 0xFF);
			this.field_1127_a[i * 4 + 3] = (byte)((compassSpriteSheet[offset + i] >> 24) & 0xFF);
		}
	}
}