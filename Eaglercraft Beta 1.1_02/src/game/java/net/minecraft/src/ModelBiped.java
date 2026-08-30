package net.minecraft.src;

public class ModelBiped extends ModelBase {
	public ModelRenderer bipedHead;
	public ModelRenderer field_1285_b;
	public ModelRenderer field_1284_c;
	public ModelRenderer bipedRightArm;
	public ModelRenderer bipedLeftArm;
	public ModelRenderer bipedRightLeg;
	public ModelRenderer bipedLeftLeg;
	public ModelRenderer field_20098_h;
	public ModelRenderer field_20097_i;
	public boolean field_1279_h;
	public boolean field_1278_i;
	public boolean field_1277_j;

	public ModelBiped() {
		this(0.0F);
	}

	public ModelBiped(float var1) {
		this(var1, 0.0F);
	}

	public ModelBiped(float var1, float var2) {
		this.field_1279_h = false;
		this.field_1278_i = false;
		this.field_1277_j = false;
		this.field_20097_i = new ModelRenderer(0, 0);
		this.field_20097_i.addBox(-5.0F, 0.0F, -1.0F, 10, 16, 1, var1);
		this.field_20098_h = new ModelRenderer(24, 0);
		this.field_20098_h.addBox(-3.0F, -6.0F, -1.0F, 6, 6, 1, var1);
		this.bipedHead = new ModelRenderer(0, 0);
		this.bipedHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1);
		this.bipedHead.setPosition(0.0F, 0.0F + var2, 0.0F);
		this.field_1285_b = new ModelRenderer(32, 0);
		this.field_1285_b.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, var1 + 0.5F);
		this.field_1285_b.setPosition(0.0F, 0.0F + var2, 0.0F);
		this.field_1284_c = new ModelRenderer(16, 16);
		this.field_1284_c.addBox(-4.0F, 0.0F, -2.0F, 8, 12, 4, var1);
		this.field_1284_c.setPosition(0.0F, 0.0F + var2, 0.0F);
		this.bipedRightArm = new ModelRenderer(40, 16);
		this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
		this.bipedRightArm.setPosition(-5.0F, 2.0F + var2, 0.0F);
		this.bipedLeftArm = new ModelRenderer(40, 16);
		this.bipedLeftArm.mirror = true;
		this.bipedLeftArm.addBox(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
		this.bipedLeftArm.setPosition(5.0F, 2.0F + var2, 0.0F);
		this.bipedRightLeg = new ModelRenderer(0, 16);
		this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
		this.bipedRightLeg.setPosition(-2.0F, 12.0F + var2, 0.0F);
		this.bipedLeftLeg = new ModelRenderer(0, 16);
		this.bipedLeftLeg.mirror = true;
		this.bipedLeftLeg.addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
		this.bipedLeftLeg.setPosition(2.0F, 12.0F + var2, 0.0F);
	}

	public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.setRotationAngles(var1, var2, var3, var4, var5, var6);
		this.bipedHead.render(var6);
		this.field_1284_c.render(var6);
		this.bipedRightArm.render(var6);
		this.bipedLeftArm.render(var6);
		this.bipedRightLeg.render(var6);
		this.bipedLeftLeg.render(var6);
		this.field_1285_b.render(var6);
	}

	public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.bipedHead.rotateAngleY = var4 / (180.0F / (float)Math.PI);
		this.bipedHead.rotateAngleX = var5 / (180.0F / (float)Math.PI);
		this.field_1285_b.rotateAngleY = this.bipedHead.rotateAngleY;
		this.field_1285_b.rotateAngleX = this.bipedHead.rotateAngleX;
		this.bipedRightArm.rotateAngleX = MathHelper.cos(var1 * 0.6662F + (float)Math.PI) * 2.0F * var2 * 0.5F;
		this.bipedLeftArm.rotateAngleX = MathHelper.cos(var1 * 0.6662F) * 2.0F * var2 * 0.5F;
		this.bipedRightArm.rotateAngleZ = 0.0F;
		this.bipedLeftArm.rotateAngleZ = 0.0F;
		this.bipedRightLeg.rotateAngleX = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
		this.bipedLeftLeg.rotateAngleX = MathHelper.cos(var1 * 0.6662F + (float)Math.PI) * 1.4F * var2;
		this.bipedRightLeg.rotateAngleY = 0.0F;
		this.bipedLeftLeg.rotateAngleY = 0.0F;
		if(this.field_1243_l) {
			this.bipedRightArm.rotateAngleX += (float)Math.PI * -0.2F;
			this.bipedLeftArm.rotateAngleX += (float)Math.PI * -0.2F;
			this.bipedRightLeg.rotateAngleX = (float)Math.PI * -0.4F;
			this.bipedLeftLeg.rotateAngleX = (float)Math.PI * -0.4F;
			this.bipedRightLeg.rotateAngleY = (float)Math.PI * 0.1F;
			this.bipedLeftLeg.rotateAngleY = (float)Math.PI * -0.1F;
		}

		if(this.field_1279_h) {
			this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float)Math.PI * 0.1F;
		}

		if(this.field_1278_i) {
			this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float)Math.PI * 0.1F;
		}

		this.bipedRightArm.rotateAngleY = 0.0F;
		this.bipedLeftArm.rotateAngleY = 0.0F;
		if(this.field_1244_k > -9990.0F) {
			float var7 = this.field_1244_k;
			this.field_1284_c.rotateAngleY = MathHelper.sin(MathHelper.sqrt_float(var7) * (float)Math.PI * 2.0F) * 0.2F;
			this.bipedRightArm.offsetZ = MathHelper.sin(this.field_1284_c.rotateAngleY) * 5.0F;
			this.bipedRightArm.offsetX = -MathHelper.cos(this.field_1284_c.rotateAngleY) * 5.0F;
			this.bipedLeftArm.offsetZ = -MathHelper.sin(this.field_1284_c.rotateAngleY) * 5.0F;
			this.bipedLeftArm.offsetX = MathHelper.cos(this.field_1284_c.rotateAngleY) * 5.0F;
			this.bipedRightArm.rotateAngleY += this.field_1284_c.rotateAngleY;
			this.bipedLeftArm.rotateAngleY += this.field_1284_c.rotateAngleY;
			this.bipedLeftArm.rotateAngleX += this.field_1284_c.rotateAngleY;
			var7 = 1.0F - this.field_1244_k;
			var7 *= var7;
			var7 *= var7;
			var7 = 1.0F - var7;
			float var8 = MathHelper.sin(var7 * (float)Math.PI);
			float var9 = MathHelper.sin(this.field_1244_k * (float)Math.PI) * -(this.bipedHead.rotateAngleX - 0.7F) * (12.0F / 16.0F);
			this.bipedRightArm.rotateAngleX = (float)((double)this.bipedRightArm.rotateAngleX - ((double)var8 * 1.2D + (double)var9));
			this.bipedRightArm.rotateAngleY += this.field_1284_c.rotateAngleY * 2.0F;
			this.bipedRightArm.rotateAngleZ = MathHelper.sin(this.field_1244_k * (float)Math.PI) * -0.4F;
		}

		if(this.field_1277_j) {
			this.field_1284_c.rotateAngleX = 0.5F;
			this.bipedRightLeg.rotateAngleX -= 0.0F;
			this.bipedLeftLeg.rotateAngleX -= 0.0F;
			this.bipedRightArm.rotateAngleX += 0.4F;
			this.bipedLeftArm.rotateAngleX += 0.4F;
			this.bipedRightLeg.offsetZ = 4.0F;
			this.bipedLeftLeg.offsetZ = 4.0F;
			this.bipedRightLeg.offsetY = 9.0F;
			this.bipedLeftLeg.offsetY = 9.0F;
			this.bipedHead.offsetY = 1.0F;
		} else {
			this.field_1284_c.rotateAngleX = 0.0F;
			this.bipedRightLeg.offsetZ = 0.0F;
			this.bipedLeftLeg.offsetZ = 0.0F;
			this.bipedRightLeg.offsetY = 12.0F;
			this.bipedLeftLeg.offsetY = 12.0F;
			this.bipedHead.offsetY = 0.0F;
		}

		this.bipedRightArm.rotateAngleZ += MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F;
		this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(var3 * 0.09F) * 0.05F + 0.05F;
		this.bipedRightArm.rotateAngleX += MathHelper.sin(var3 * 0.067F) * 0.05F;
		this.bipedLeftArm.rotateAngleX -= MathHelper.sin(var3 * 0.067F) * 0.05F;
	}

	public void func_20095_a(float var1) {
		this.field_20098_h.rotateAngleY = this.bipedHead.rotateAngleY;
		this.field_20098_h.rotateAngleX = this.bipedHead.rotateAngleX;
		this.field_20098_h.offsetX = 0.0F;
		this.field_20098_h.offsetY = 0.0F;
		this.field_20098_h.render(var1);
	}

	public void func_20096_b(float var1) {
		this.field_20097_i.render(var1);
	}
}
