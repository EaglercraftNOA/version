package net.minecraft.src;

import net.peyton.eagler.minecraft.Tessellator;

public class EntityReddustFX extends EntityFX {
	float field_673_a;

	public EntityReddustFX(World var1, double var2, double var4, double var6) {
		this(var1, var2, var4, var6, 1.0F);
	}

	public EntityReddustFX(World var1, double var2, double var4, double var6, float var8) {
		super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
		this.motionX *= (double)0.1F;
		this.motionY *= (double)0.1F;
		this.motionZ *= (double)0.1F;
		this.particleRed = (float)(Math.random() * (double)0.3F) + 0.7F;
		this.particleBlue = this.particleGreen = (float)(Math.random() * (double)0.1F);
		this.field_665_g *= 12.0F / 16.0F;
		this.field_665_g *= var8;
		this.field_673_a = this.field_665_g;
		this.field_666_f = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
		this.field_666_f = (int)((float)this.field_666_f * var8);
		this.noClip = false;
	}

	public void func_406_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		float var8 = ((float)this.e + var2) / (float)this.field_666_f * 32.0F;
		if(var8 < 0.0F) {
			var8 = 0.0F;
		}

		if(var8 > 1.0F) {
			var8 = 1.0F;
		}

		this.field_665_g = this.field_673_a * var8;
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
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		if(this.posY == this.prevPosY) {
			this.motionX *= 1.1D;
			this.motionZ *= 1.1D;
		}

		this.motionX *= (double)0.96F;
		this.motionY *= (double)0.96F;
		this.motionZ *= (double)0.96F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
