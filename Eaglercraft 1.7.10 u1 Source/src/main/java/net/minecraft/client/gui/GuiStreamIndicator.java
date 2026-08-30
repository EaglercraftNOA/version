package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiStreamIndicator {
	private static final ResourceLocation field_152441_a = new ResourceLocation("textures/gui/stream_indicator.png");
	private final Minecraft field_152442_b;
	private float field_152443_c = 1.0F;
	private int field_152444_d = 1;
	private static final String __OBFID = "CL_00001849";

	public GuiStreamIndicator(Minecraft p_i46322_1_) {
		this.field_152442_b = p_i46322_1_;
	}

	public void func_152437_a(int p_152437_1_, int p_152437_2_) {

	}

	private void func_152436_a(int p_152436_1_, int p_152436_2_, int p_152436_3_, int p_152436_4_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.65F + 0.35000002F * this.field_152443_c);
		this.field_152442_b.getTextureManager().bindTexture(field_152441_a);
		float var5 = 150.0F;
		float var6 = 0.0F;
		float var7 = (float) p_152436_3_ * 0.015625F;
		float var8 = 1.0F;
		float var9 = (float) (p_152436_3_ + 16) * 0.015625F;
		Tessellator var10 = Tessellator.instance;
		var10.startDrawingQuads();
		var10.addVertexWithUV((double) (p_152436_1_ - 16 - p_152436_4_), (double) (p_152436_2_ + 16), (double) var5,
				(double) var6, (double) var9);
		var10.addVertexWithUV((double) (p_152436_1_ - p_152436_4_), (double) (p_152436_2_ + 16), (double) var5,
				(double) var8, (double) var9);
		var10.addVertexWithUV((double) (p_152436_1_ - p_152436_4_), (double) (p_152436_2_ + 0), (double) var5,
				(double) var8, (double) var7);
		var10.addVertexWithUV((double) (p_152436_1_ - 16 - p_152436_4_), (double) (p_152436_2_ + 0), (double) var5,
				(double) var6, (double) var7);
		var10.draw();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	public void func_152439_a() {
	}
}
