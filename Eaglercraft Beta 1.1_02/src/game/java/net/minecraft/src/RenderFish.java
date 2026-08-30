package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.peyton.eagler.minecraft.Tessellator;

public class RenderFish extends Render {
	public void a(EntityFish var1, double var2, double var4, double var6, float var8, float var9) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var2, (float)var4, (float)var6);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F, 0.5F, 0.5F);
		byte var10 = 1;
		byte var11 = 2;
		this.loadTexture("/particles.png");
		Tessellator var12 = Tessellator.instance;
		float var13 = (float)(var10 * 8 + 0) / 128.0F;
		float var14 = (float)(var10 * 8 + 8) / 128.0F;
		float var15 = (float)(var11 * 8 + 0) / 128.0F;
		float var16 = (float)(var11 * 8 + 8) / 128.0F;
		float var17 = 1.0F;
		float var18 = 0.5F;
		float var19 = 0.5F;
		GL11.glRotatef(180.0F - this.renderManager.field_1225_i, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.field_1224_j, 1.0F, 0.0F, 0.0F);
		var12.startDrawingQuads();
		var12.setNormal(0.0F, 1.0F, 0.0F);
		var12.addVertexWithUV((double)(0.0F - var18), (double)(0.0F - var19), 0.0D, (double)var13, (double)var16);
		var12.addVertexWithUV((double)(var17 - var18), (double)(0.0F - var19), 0.0D, (double)var14, (double)var16);
		var12.addVertexWithUV((double)(var17 - var18), (double)(1.0F - var19), 0.0D, (double)var14, (double)var15);
		var12.addVertexWithUV((double)(0.0F - var18), (double)(1.0F - var19), 0.0D, (double)var13, (double)var15);
		var12.draw();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		if(var1.field_4097_b != null) {
			float var20 = (var1.field_4097_b.prevRotationYaw + (var1.field_4097_b.rotationYaw - var1.field_4097_b.prevRotationYaw) * var9) * (float)Math.PI / 180.0F;
			float var21 = (var1.field_4097_b.prevRotationPitch + (var1.field_4097_b.rotationPitch - var1.field_4097_b.prevRotationPitch) * var9) * (float)Math.PI / 180.0F;
			double var22 = (double)MathHelper.sin(var20);
			double var24 = (double)MathHelper.cos(var20);
			double var26 = (double)MathHelper.sin(var21);
			double var28 = (double)MathHelper.cos(var21);
			double var30 = var1.field_4097_b.prevPosX + (var1.field_4097_b.posX - var1.field_4097_b.prevPosX) * (double)var9 - var24 * 0.7D - var22 * 0.5D * var28;
			double var32 = var1.field_4097_b.prevPosY + (var1.field_4097_b.posY - var1.field_4097_b.prevPosY) * (double)var9 - var26 * 0.5D;
			double var34 = var1.field_4097_b.prevPosZ + (var1.field_4097_b.posZ - var1.field_4097_b.prevPosZ) * (double)var9 - var22 * 0.7D + var24 * 0.5D * var28;
			if(this.renderManager.options.thirdPersonView) {
				var20 = (var1.field_4097_b.prevRenderYawOffset + (var1.field_4097_b.renderYawOffset - var1.field_4097_b.prevRenderYawOffset) * var9) * (float)Math.PI / 180.0F;
				var22 = (double)MathHelper.sin(var20);
				var24 = (double)MathHelper.cos(var20);
				var30 = var1.field_4097_b.prevPosX + (var1.field_4097_b.posX - var1.field_4097_b.prevPosX) * (double)var9 - var24 * 0.35D - var22 * 0.85D;
				var32 = var1.field_4097_b.prevPosY + (var1.field_4097_b.posY - var1.field_4097_b.prevPosY) * (double)var9 - 0.45D;
				var34 = var1.field_4097_b.prevPosZ + (var1.field_4097_b.posZ - var1.field_4097_b.prevPosZ) * (double)var9 - var22 * 0.35D + var24 * 0.85D;
			}

			double var36 = var1.prevPosX + (var1.posX - var1.prevPosX) * (double)var9;
			double var38 = var1.prevPosY + (var1.posY - var1.prevPosY) * (double)var9 + 0.25D;
			double var40 = var1.prevPosZ + (var1.posZ - var1.prevPosZ) * (double)var9;
			double var42 = (double)((float)(var30 - var36));
			double var44 = (double)((float)(var32 - var38));
			double var46 = (double)((float)(var34 - var40));
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			var12.startDrawing(3);
			var12.setColorOpaque_I(0);
			byte var48 = 16;

			for(int var49 = 0; var49 <= var48; ++var49) {
				float var50 = (float)var49 / (float)var48;
				var12.addVertex(var2 + var42 * (double)var50, var4 + var44 * (double)(var50 * var50 + var50) * 0.5D + 0.25D, var6 + var46 * (double)var50);
			}

			var12.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityFish)var1, var2, var4, var6, var8, var9);
	}
}
