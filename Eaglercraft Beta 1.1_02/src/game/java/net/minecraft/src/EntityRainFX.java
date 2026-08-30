package net.minecraft.src;

import net.peyton.eagler.minecraft.Tessellator;

public class EntityRainFX extends EntityFX {
	public EntityRainFX(World var1, double var2, double var4, double var6) {
		super(var1, var2, var4, var6, 0.0D, 0.0D, 0.0D);
		this.motionX *= (double)0.3F;
		this.motionY = (double)((float)Math.random() * 0.2F + 0.1F);
		this.motionZ *= (double)0.3F;
		this.particleRed = 1.0F;
		this.particleBlue = 1.0F;
		this.particleGreen = 1.0F;
		this.field_670_b = 19 + this.rand.nextInt(4);
		this.setSize(0.01F, 0.01F);
		this.field_664_h = 0.06F;
		this.field_666_f = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
	}

	public void func_406_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
		super.func_406_a(var1, var2, var3, var4, var5, var6, var7);
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.motionY -= (double)this.field_664_h;
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		this.motionX *= (double)0.98F;
		this.motionY *= (double)0.98F;
		this.motionZ *= (double)0.98F;
		if(this.field_666_f-- <= 0) {
			this.setEntityDead();
		}

		if(this.onGround) {
			if(Math.random() < 0.5D) {
				this.setEntityDead();
			}

			this.motionX *= (double)0.7F;
			this.motionZ *= (double)0.7F;
		}

		Material var1 = this.worldObj.getBlockMaterial(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
		if(var1.getIsLiquid() || var1.func_878_a()) {
			double var2 = (double)((float)(MathHelper.floor_double(this.posY) + 1) - BlockFluids.setFluidHeight(this.worldObj.getBlockMetadata(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ))));
			if(this.posY < var2) {
				this.setEntityDead();
			}
		}

	}
}
