package net.minecraft.src;

import net.peyton.eagler.minecraft.Tessellator;

public class EntityExplodeFX extends EntityFX {
	public EntityExplodeFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
		super(var1, var2, var4, var6, var8, var10, var12);
		this.motionX = var8 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
		this.motionY = var10 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
		this.motionZ = var12 + (double)((float)(Math.random() * 2.0D - 1.0D) * 0.05F);
		this.particleRed = this.particleBlue = this.particleGreen = this.rand.nextFloat() * 0.3F + 0.7F;
		this.field_665_g = this.rand.nextFloat() * this.rand.nextFloat() * 6.0F + 1.0F;
		this.field_666_f = (int)(16.0D / ((double)this.rand.nextFloat() * 0.8D + 0.2D)) + 2;
	}

	public void func_406_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		super.func_406_a(var1, var2, var3, var4, var5, var6, var7);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.e++ >= this.field_666_f) {
			this.setEntityDead();
		}

		this.field_670_b = 7 - this.e * 8 / this.field_666_f;
		this.motionY += 0.004D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.9F;
		this.motionY *= (double)0.9F;
		this.motionZ *= (double)0.9F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
