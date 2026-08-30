package net.minecraft.src;

import net.peyton.eagler.minecraft.Tessellator;

public class EntityLavaFX extends EntityFX {
	private float field_674_a;

	public EntityLavaFX(World var1, double var2, double var4, double var6) {
		super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
		this.motionX *= (double)0.8F;
		this.motionY *= (double)0.8F;
		this.motionZ *= (double)0.8F;
		this.motionY = (double)(this.rand.nextFloat() * 0.4F + 0.05F);
		this.particleRed = this.particleBlue = this.particleGreen = 1.0F;
		this.field_665_g *= this.rand.nextFloat() * 2.0F + 0.2F;
		this.field_674_a = this.field_665_g;
		this.field_666_f = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
		this.noClip = false;
		this.field_670_b = 49;
	}

	public float getEntityBrightness(float var1) {
		return 1.0F;
	}

	public void func_406_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		float var8 = ((float)this.e + var2) / (float)this.field_666_f;
		this.field_665_g = this.field_674_a * (1.0F - var8 * var8);
		super.func_406_a(var1, var2, var3, var4, var5, var6, var7);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		if(this.e++ >= this.field_666_f) {
			this.setEntityDead();
		}

		float var1 = (float)this.e / (float)this.field_666_f;
		if(this.rand.nextFloat() > var1) {
			this.worldObj.spawnParticle("smoke", this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ);
		}

		this.motionY -= 0.03D;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.999F;
		this.motionY *= (double)0.999F;
		this.motionZ *= (double)0.999F;
		if(this.onGround) {
			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

	}
}
