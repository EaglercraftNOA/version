package net.minecraft.src;

import net.peyton.eagler.minecraft.Tessellator;

public class EntitySlimeFX extends EntityFX {
	public EntitySlimeFX(World var1, double var2, double var4, double var6, Item var8) {
		super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
		this.field_670_b = var8.getIconIndex((ItemStack)null);
		this.particleRed = this.particleBlue = this.particleGreen = 1.0F;
		this.field_664_h = Block.blockSnow.field_357_bm;
		this.field_665_g /= 2.0F;
	}

	public int func_404_c() {
		return 2;
	}

	public void func_406_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		float var8 = ((float)(this.field_670_b % 16) + this.field_669_c / 4.0F) / 16.0F;
		float var9 = var8 + 0.999F / 64.0F;
		float var10 = ((float)(this.field_670_b / 16) + this.field_668_d / 4.0F) / 16.0F;
		float var11 = var10 + 0.999F / 64.0F;
		float var12 = 0.1F * this.field_665_g;
		float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)var2 - field_660_l);
		float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)var2 - field_659_m);
		float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var2 - field_658_n);
		float var16 = this.getEntityBrightness(var2);
		var1.setColorOpaque_F(var16 * this.particleRed, var16 * this.particleBlue, var16 * this.particleGreen);
		var1.addVertexWithUV((double)(var13 - var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 - var5 * var12 - var7 * var12), (double)var8, (double)var11);
		var1.addVertexWithUV((double)(var13 - var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 - var5 * var12 + var7 * var12), (double)var8, (double)var10);
		var1.addVertexWithUV((double)(var13 + var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 + var5 * var12 + var7 * var12), (double)var9, (double)var10);
		var1.addVertexWithUV((double)(var13 + var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 + var5 * var12 - var7 * var12), (double)var9, (double)var11);
	}
}
