package net.minecraft.src;

public class ModelChicken extends ModelBase {
	public ModelRenderer field_1289_a;
	public ModelRenderer field_1288_b;
	public ModelRenderer field_1295_c;
	public ModelRenderer field_1294_d;
	public ModelRenderer field_1293_e;
	public ModelRenderer field_1292_f;
	public ModelRenderer field_1291_g;
	public ModelRenderer field_1290_h;

	public ModelChicken() {
		byte var1 = 16;
		this.field_1289_a = new ModelRenderer(0, 0);
		this.field_1289_a.addBox(-2.0F, -6.0F, -2.0F, 4, 6, 3, 0.0F);
		this.field_1289_a.setPosition(0.0F, (float)(-1 + var1), -4.0F);
		this.field_1291_g = new ModelRenderer(14, 0);
		this.field_1291_g.addBox(-2.0F, -4.0F, -4.0F, 4, 2, 2, 0.0F);
		this.field_1291_g.setPosition(0.0F, (float)(-1 + var1), -4.0F);
		this.field_1290_h = new ModelRenderer(14, 4);
		this.field_1290_h.addBox(-1.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
		this.field_1290_h.setPosition(0.0F, (float)(-1 + var1), -4.0F);
		this.field_1288_b = new ModelRenderer(0, 9);
		this.field_1288_b.addBox(-3.0F, -4.0F, -3.0F, 6, 8, 6, 0.0F);
		this.field_1288_b.setPosition(0.0F, (float)(0 + var1), 0.0F);
		this.field_1295_c = new ModelRenderer(26, 0);
		this.field_1295_c.func_921_a(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.field_1295_c.setPosition(-2.0F, (float)(3 + var1), 1.0F);
		this.field_1294_d = new ModelRenderer(26, 0);
		this.field_1294_d.func_921_a(-1.0F, 0.0F, -3.0F, 3, 5, 3);
		this.field_1294_d.setPosition(1.0F, (float)(3 + var1), 1.0F);
		this.field_1293_e = new ModelRenderer(24, 13);
		this.field_1293_e.func_921_a(0.0F, 0.0F, -3.0F, 1, 4, 6);
		this.field_1293_e.setPosition(-4.0F, (float)(-3 + var1), 0.0F);
		this.field_1292_f = new ModelRenderer(24, 13);
		this.field_1292_f.func_921_a(-1.0F, 0.0F, -3.0F, 1, 4, 6);
		this.field_1292_f.setPosition(4.0F, (float)(-3 + var1), 0.0F);
	}

	public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.setRotationAngles(var1, var2, var3, var4, var5, var6);
		this.field_1289_a.render(var6);
		this.field_1291_g.render(var6);
		this.field_1290_h.render(var6);
		this.field_1288_b.render(var6);
		this.field_1295_c.render(var6);
		this.field_1294_d.render(var6);
		this.field_1293_e.render(var6);
		this.field_1292_f.render(var6);
	}

	public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.field_1289_a.rotateAngleX = -(var5 / (180.0F / (float)Math.PI));
		this.field_1289_a.rotateAngleY = var4 / (180.0F / (float)Math.PI);
		this.field_1291_g.rotateAngleX = this.field_1289_a.rotateAngleX;
		this.field_1291_g.rotateAngleY = this.field_1289_a.rotateAngleY;
		this.field_1290_h.rotateAngleX = this.field_1289_a.rotateAngleX;
		this.field_1290_h.rotateAngleY = this.field_1289_a.rotateAngleY;
		this.field_1288_b.rotateAngleX = (float)Math.PI * 0.5F;
		this.field_1295_c.rotateAngleX = MathHelper.cos(var1 * 0.6662F) * 1.4F * var2;
		this.field_1294_d.rotateAngleX = MathHelper.cos(var1 * 0.6662F + (float)Math.PI) * 1.4F * var2;
		this.field_1293_e.rotateAngleZ = var3;
		this.field_1292_f.rotateAngleZ = -var3;
	}
}
