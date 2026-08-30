package net.minecraft.src;

public class ModelBoat extends ModelBase {
	public ModelRenderer[] field_1287_a = new ModelRenderer[5];

	public ModelBoat() {
		this.field_1287_a[0] = new ModelRenderer(0, 8);
		this.field_1287_a[1] = new ModelRenderer(0, 0);
		this.field_1287_a[2] = new ModelRenderer(0, 0);
		this.field_1287_a[3] = new ModelRenderer(0, 0);
		this.field_1287_a[4] = new ModelRenderer(0, 0);
		byte var1 = 24;
		byte var2 = 6;
		byte var3 = 20;
		byte var4 = 4;
		this.field_1287_a[0].addBox((float)(-var1 / 2), (float)(-var3 / 2 + 2), -3.0F, var1, var3 - 4, 4, 0.0F);
		this.field_1287_a[0].setPosition(0.0F, (float)(0 + var4), 0.0F);
		this.field_1287_a[1].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
		this.field_1287_a[1].setPosition((float)(-var1 / 2 + 1), (float)(0 + var4), 0.0F);
		this.field_1287_a[2].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
		this.field_1287_a[2].setPosition((float)(var1 / 2 - 1), (float)(0 + var4), 0.0F);
		this.field_1287_a[3].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
		this.field_1287_a[3].setPosition(0.0F, (float)(0 + var4), (float)(-var3 / 2 + 1));
		this.field_1287_a[4].addBox((float)(-var1 / 2 + 2), (float)(-var2 - 1), -1.0F, var1 - 4, var2, 2, 0.0F);
		this.field_1287_a[4].setPosition(0.0F, (float)(0 + var4), (float)(var3 / 2 - 1));
		this.field_1287_a[0].rotateAngleX = (float)Math.PI * 0.5F;
		this.field_1287_a[1].rotateAngleY = (float)Math.PI * 3.0F / 2.0F;
		this.field_1287_a[2].rotateAngleY = (float)Math.PI * 0.5F;
		this.field_1287_a[3].rotateAngleY = (float)Math.PI;
	}

	public void render(float var1, float var2, float var3, float var4, float var5, float var6) {
		for(int var7 = 0; var7 < 5; ++var7) {
			this.field_1287_a[var7].render(var6);
		}

	}

	public void setRotationAngles(float var1, float var2, float var3, float var4, float var5, float var6) {
	}
}
