package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderGhast extends RenderLiving {
	public RenderGhast() {
		super(new ModelGhast(), 0.5F);
	}

	protected void a(EntityGhast var1, float var2) {
		float var4 = ((float)var1.field_4125_e + (float)(var1.field_4124_f - var1.field_4125_e) * var2) / 20.0F;
		if(var4 < 0.0F) {
			var4 = 0.0F;
		}

		var4 = 1.0F / (var4 * var4 * var4 * var4 * var4 * 2.0F + 1.0F);
		float var5 = (8.0F + var4) / 2.0F;
		float var6 = (8.0F + 1.0F / var4) / 2.0F;
		GL11.glScalef(var6, var5, var6);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	protected void preRenderCallback(EntityLiving var1, float var2) {
		this.a((EntityGhast)var1, var2);
	}
}
