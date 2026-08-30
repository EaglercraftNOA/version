package net.minecraft.src;

import net.peyton.eagler.minecraft.Tessellator;

public class EntityFX extends Entity {
	protected int field_670_b;
	protected float field_669_c;
	protected float field_668_d;
	protected int e = 0;
	protected int field_666_f = 0;
	protected float field_665_g;
	protected float field_664_h;
	protected float particleRed;
	protected float particleBlue;
	protected float particleGreen;
	public static double field_660_l;
	public static double field_659_m;
	public static double field_658_n;

	public EntityFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		super(var1);
		this.setSize(0.2F, 0.2F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(var2, var4, var6);
		this.particleRed = this.particleBlue = this.particleGreen = 1.0F;
		this.motionX = var8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		this.motionY = var10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		this.motionZ = var12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.4F);
		float var14 = (float)(Math.random() + Math.random() + 1.0D) * 0.15F;
		float var15 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
		this.motionX = this.motionX / (double)var15 * (double)var14 * (double)0.4F;
		this.motionY = this.motionY / (double)var15 * (double)var14 * (double)0.4F + (double)0.1F;
		this.motionZ = this.motionZ / (double)var15 * (double)var14 * (double)0.4F;
		this.field_669_c = this.rand.nextFloat() * 3.0F;
		this.field_668_d = this.rand.nextFloat() * 3.0F;
		this.field_665_g = (this.rand.nextFloat() * 0.5F + 0.5F) * 2.0F;
		this.field_666_f = (int)(4.0F / (this.rand.nextFloat() * 0.9F + 0.1F));
		this.e = 0;
		this.entityWalks = false;
	}

	public EntityFX func_407_b(float var1) {
		this.motionX *= (double)var1;
		this.motionY = (this.motionY - (double)0.1F) * (double)var1 + (double)0.1F;
		this.motionZ *= (double)var1;
		return this;
	}

	public EntityFX func_405_d(float var1) {
		this.setSize(0.2F * var1, 0.2F * var1);
		this.field_665_g *= var1;
		return this;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.e++ >= this.field_666_f) {
			this.setEntityDead();
		}

		this.motionY -= 0.04D * (double)this.field_664_h;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.98F;
		this.motionY *= (double)0.98F;
		this.motionZ *= (double)0.98F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}

	public void func_406_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		float var8 = (float)(this.field_670_b % 16) / 16.0F;
		float var9 = var8 + 0.999F / 16.0F;
		float var10 = (float)(this.field_670_b / 16) / 16.0F;
		float var11 = var10 + 0.999F / 16.0F;
		float var12 = 0.1F * this.field_665_g;
		float var13 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)var2 - field_660_l);
		float var14 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)var2 - field_659_m);
		float var15 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)var2 - field_658_n);
		float var16 = this.getEntityBrightness(var2);
		var1.setColorOpaque_F(this.particleRed * var16, this.particleBlue * var16, this.particleGreen * var16);
		var1.addVertexWithUV((double)(var13 - var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 - var5 * var12 - var7 * var12), (double)var8, (double)var11);
		var1.addVertexWithUV((double)(var13 - var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 - var5 * var12 + var7 * var12), (double)var8, (double)var10);
		var1.addVertexWithUV((double)(var13 + var3 * var12 + var6 * var12), (double)(var14 + var4 * var12), (double)(var15 + var5 * var12 + var7 * var12), (double)var9, (double)var10);
		var1.addVertexWithUV((double)(var13 + var3 * var12 - var6 * var12), (double)(var14 - var4 * var12), (double)(var15 + var5 * var12 - var7 * var12), (double)var9, (double)var11);
	}

	public int func_404_c() {
		return 0;
	}

	public void writeEntityToNBT(NBTTagCompound var1) {
	}

	public void readEntityFromNBT(NBTTagCompound var1) {
	}
}
