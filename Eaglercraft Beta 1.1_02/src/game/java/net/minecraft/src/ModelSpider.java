package net.minecraft.src;

public class ModelSpider extends ModelBase {
	public ModelRenderer field_1255_a;
	public ModelRenderer field_1254_b;
	public ModelRenderer field_1253_c;
	public ModelRenderer field_1252_d;
	public ModelRenderer field_1251_e;
	public ModelRenderer field_1250_f;
	public ModelRenderer field_1249_g;
	public ModelRenderer field_1248_h;
	public ModelRenderer field_1247_i;
	public ModelRenderer field_1246_j;
	public ModelRenderer field_1245_m;

	public ModelSpider() {
		float var1 = 0.0F;
		byte var2 = 15;
		this.field_1255_a = new ModelRenderer(32, 4);
		this.field_1255_a.addBox(-4.0F, -4.0F, -8.0F, 8, 8, 8, var1);
		this.field_1255_a.setPosition(0.0F, (float)(0 + var2), -3.0F);
		this.field_1254_b = new ModelRenderer(0, 0);
		this.field_1254_b.addBox(-3.0F, -3.0F, -3.0F, 6, 6, 6, var1);
		this.field_1254_b.setPosition(0.0F, (float)var2, 0.0F);
		this.field_1253_c = new ModelRenderer(0, 12);
		this.field_1253_c.addBox(-5.0F, -4.0F, -6.0F, 10, 8, 12, var1);
		this.field_1253_c.setPosition(0.0F, (float)(0 + var2), 9.0F);
		this.field_1252_d = new ModelRenderer(18, 0);
		this.field_1252_d.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
		this.field_1252_d.setPosition(-4.0F, (float)(0 + var2), 2.0F);
		this.field_1251_e = new ModelRenderer(18, 0);
		this.field_1251_e.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
		this.field_1251_e.setPosition(4.0F, (float)(0 + var2), 2.0F);
		this.field_1250_f = new ModelRenderer(18, 0);
		this.field_1250_f.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
		this.field_1250_f.setPosition(-4.0F, (float)(0 + var2), 1.0F);
		this.field_1249_g = new ModelRenderer(18, 0);
		this.field_1249_g.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
		this.field_1249_g.setPosition(4.0F, (float)(0 + var2), 1.0F);
		this.field_1248_h = new ModelRenderer(18, 0);
		this.field_1248_h.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
		this.field_1248_h.setPosition(-4.0F, (float)(0 + var2), 0.0F);
		this.field_1247_i = new ModelRenderer(18, 0);
		this.field_1247_i.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
		this.field_1247_i.setPosition(4.0F, (float)(0 + var2), 0.0F);
		this.field_1246_j = new ModelRenderer(18, 0);
		this.field_1246_j.addBox(-15.0F, -1.0F, -1.0F, 16, 2, 2, var1);
		this.field_1246_j.setPosition(-4.0F, (float)(0 + var2), -1.0F);
		this.field_1245_m = new ModelRenderer(18, 0);
		this.field_1245_m.addBox(-1.0F, -1.0F, -1.0F, 16, 2, 2, var1);
		this.field_1245_m.setPosition(4.0F, (float)(0 + var2), -1.0F);
	}

	public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.setRotationAngles(var1, var2, var3, var4, var5, var6);
		this.field_1255_a.render(var6);
		this.field_1254_b.render(var6);
		this.field_1253_c.render(var6);
		this.field_1252_d.render(var6);
		this.field_1251_e.render(var6);
		this.field_1250_f.render(var6);
		this.field_1249_g.render(var6);
		this.field_1248_h.render(var6);
		this.field_1247_i.render(var6);
		this.field_1246_j.render(var6);
		this.field_1245_m.render(var6);
	}

	public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6) {
		this.field_1255_a.rotateAngleY = var4 / (180.0F / (float)Math.PI);
		this.field_1255_a.rotateAngleX = var5 / (180.0F / (float)Math.PI);
		float var7 = (float)Math.PI * 0.25F;
		this.field_1252_d.rotateAngleZ = -var7;
		this.field_1251_e.rotateAngleZ = var7;
		this.field_1250_f.rotateAngleZ = -var7 * 0.74F;
		this.field_1249_g.rotateAngleZ = var7 * 0.74F;
		this.field_1248_h.rotateAngleZ = -var7 * 0.74F;
		this.field_1247_i.rotateAngleZ = var7 * 0.74F;
		this.field_1246_j.rotateAngleZ = -var7;
		this.field_1245_m.rotateAngleZ = var7;
		float var8 = -0.0F;
		float var9 = (float)Math.PI * 0.125F;
		this.field_1252_d.rotateAngleY = var9 * 2.0F + var8;
		this.field_1251_e.rotateAngleY = -var9 * 2.0F - var8;
		this.field_1250_f.rotateAngleY = var9 * 1.0F + var8;
		this.field_1249_g.rotateAngleY = -var9 * 1.0F - var8;
		this.field_1248_h.rotateAngleY = -var9 * 1.0F + var8;
		this.field_1247_i.rotateAngleY = var9 * 1.0F - var8;
		this.field_1246_j.rotateAngleY = -var9 * 2.0F + var8;
		this.field_1245_m.rotateAngleY = var9 * 2.0F - var8;
		float var10 = -(MathHelper.cos(var1 * 0.6662F * 2.0F + 0.0F) * 0.4F) * var2;
		float var11 = -(MathHelper.cos(var1 * 0.6662F * 2.0F + (float)Math.PI) * 0.4F) * var2;
		float var12 = -(MathHelper.cos(var1 * 0.6662F * 2.0F + (float)Math.PI * 0.5F) * 0.4F) * var2;
		float var13 = -(MathHelper.cos(var1 * 0.6662F * 2.0F + (float)Math.PI * 3.0F / 2.0F) * 0.4F) * var2;
		float var14 = Math.abs(MathHelper.sin(var1 * 0.6662F + 0.0F) * 0.4F) * var2;
		float var15 = Math.abs(MathHelper.sin(var1 * 0.6662F + (float)Math.PI) * 0.4F) * var2;
		float var16 = Math.abs(MathHelper.sin(var1 * 0.6662F + (float)Math.PI * 0.5F) * 0.4F) * var2;
		float var17 = Math.abs(MathHelper.sin(var1 * 0.6662F + (float)Math.PI * 3.0F / 2.0F) * 0.4F) * var2;
		this.field_1252_d.rotateAngleY += var10;
		this.field_1251_e.rotateAngleY += -var10;
		this.field_1250_f.rotateAngleY += var11;
		this.field_1249_g.rotateAngleY += -var11;
		this.field_1248_h.rotateAngleY += var12;
		this.field_1247_i.rotateAngleY += -var12;
		this.field_1246_j.rotateAngleY += var13;
		this.field_1245_m.rotateAngleY += -var13;
		this.field_1252_d.rotateAngleZ += var14;
		this.field_1251_e.rotateAngleZ += -var14;
		this.field_1250_f.rotateAngleZ += var15;
		this.field_1249_g.rotateAngleZ += -var15;
		this.field_1248_h.rotateAngleZ += var16;
		this.field_1247_i.rotateAngleZ += -var16;
		this.field_1246_j.rotateAngleZ += var17;
		this.field_1245_m.rotateAngleZ += -var17;
	}
}
