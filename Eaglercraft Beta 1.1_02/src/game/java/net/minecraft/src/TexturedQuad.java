package net.minecraft.src;

import net.peyton.eagler.minecraft.Tessellator;

public class TexturedQuad {
	public PositionTexureVertex[] field_1195_a;
	public int field_1194_b;
	private boolean field_1196_c;

	public TexturedQuad(PositionTexureVertex[] var1) {
		this.field_1194_b = 0;
		this.field_1196_c = false;
		this.field_1195_a = var1;
		this.field_1194_b = var1.length;
	}

	public TexturedQuad(PositionTexureVertex[] var1, int var2, int var3, int var4, int var5) {
		this(var1);
		float var6 = 0.0015625F;
		float var7 = 0.003125F;
		var1[0] = var1[0].setTexturePosition((float)var4 / 64.0F - var6, (float)var3 / 32.0F + var7);
		var1[1] = var1[1].setTexturePosition((float)var2 / 64.0F + var6, (float)var3 / 32.0F + var7);
		var1[2] = var1[2].setTexturePosition((float)var2 / 64.0F + var6, (float)var5 / 32.0F - var7);
		var1[3] = var1[3].setTexturePosition((float)var4 / 64.0F - var6, (float)var5 / 32.0F - var7);
	}

	public void func_809_a() {
		PositionTexureVertex[] var1 = new PositionTexureVertex[this.field_1195_a.length];

		for(int var2 = 0; var2 < this.field_1195_a.length; ++var2) {
			var1[var2] = this.field_1195_a[this.field_1195_a.length - var2 - 1];
		}

		this.field_1195_a = var1;
	}

	public void render(Tessellator var1, float var2) {
		Vec3D var3 = this.field_1195_a[1].vector3D.subtract(this.field_1195_a[0].vector3D);
		Vec3D var4 = this.field_1195_a[1].vector3D.subtract(this.field_1195_a[2].vector3D);
		Vec3D var5 = var4.crossProduct(var3).normalize();
		if(this.field_1196_c) {
			var1.setNormal(-((float)var5.xCoord), -((float)var5.yCoord), -((float)var5.zCoord));
		} else {
			var1.setNormal((float)var5.xCoord, (float)var5.yCoord, (float)var5.zCoord);
		}

		for(int var6 = 0; var6 < 4; ++var6) {
			PositionTexureVertex var7 = this.field_1195_a[var6];
			var1.addVertexWithUV((double)((float)var7.vector3D.xCoord * var2), (double)((float)var7.vector3D.yCoord * var2), (double)((float)var7.vector3D.zCoord * var2), (double)var7.texturePositionX, (double)var7.texturePositionY);
		}
	}
}
