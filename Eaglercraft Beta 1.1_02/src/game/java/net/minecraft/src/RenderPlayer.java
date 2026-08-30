package net.minecraft.src;

import org.lwjgl.opengl.GL11;

import net.peyton.eagler.minecraft.FontRenderer;
import net.peyton.eagler.minecraft.Tessellator;
import net.peyton.eagler.minecraft.TextureLocation;

public class RenderPlayer extends RenderLiving {
	private ModelBiped field_209_f = (ModelBiped)this.e;
	private ModelBiped field_208_g = new ModelBiped(1.0F);
	private ModelBiped field_207_h = new ModelBiped(0.5F);
	private static final String[] armorFilenamePrefix = new String[]{"cloth", "chain", "iron", "diamond", "gold"};

	public RenderPlayer() {
		super(new ModelBiped(0.0F), 0.5F);
	}

	protected boolean a(EntityPlayer var1, int var2) {
		ItemStack var3 = var1.inventory.armorItemInSlot(3 - var2);
		if(var3 != null) {
			Item var4 = var3.getItem();
			if(var4 instanceof ItemArmor) {
				ItemArmor var5 = (ItemArmor)var4;
				this.loadTexture("/armor/" + armorFilenamePrefix[var5.renderIndex] + "_" + (var2 == 2 ? 2 : 1) + ".png");
				ModelBiped var6 = var2 == 2 ? this.field_207_h : this.field_208_g;
				var6.bipedHead.field_1403_h = var2 == 0;
				var6.field_1285_b.field_1403_h = var2 == 0;
				var6.field_1284_c.field_1403_h = var2 == 1 || var2 == 2;
				var6.bipedRightArm.field_1403_h = var2 == 1;
				var6.bipedLeftArm.field_1403_h = var2 == 1;
				var6.bipedRightLeg.field_1403_h = var2 == 2 || var2 == 3;
				var6.bipedLeftLeg.field_1403_h = var2 == 2 || var2 == 3;
				this.setRenderPassModel(var6);
				return true;
			}
		}

		return false;
	}

	public void a(EntityPlayer var1, double var2, double var4, double var6, float var8, float var9) {
		ItemStack var10 = var1.inventory.getCurrentItem();
		this.field_208_g.field_1278_i = this.field_207_h.field_1278_i = this.field_209_f.field_1278_i = var10 != null;
		this.field_208_g.field_1277_j = this.field_207_h.field_1277_j = this.field_209_f.field_1277_j = var1.isSneaking();
		double var11 = var4 - (double)var1.yOffset;
		if(var1.field_12240_bw) {
			var11 -= 0.125D;
		}

		super.a(var1, var2, var11, var6, var8, var9);
		this.field_208_g.field_1277_j = this.field_207_h.field_1277_j = this.field_209_f.field_1277_j = false;
		this.field_208_g.field_1278_i = this.field_207_h.field_1278_i = this.field_209_f.field_1278_i = false;
		float var13 = 1.6F;
		float var14 = (float)(1.0D / 60.0D) * var13;
		float var15 = var1.getDistanceToEntity(this.renderManager.field_1226_h);
		float var16 = var1.isSneaking() ? 32.0F : 64.0F;
		if(var15 < var16) {
			var14 = (float)((double)var14 * (Math.sqrt((double)var15) / 2.0D));
			FontRenderer var17 = this.getFontRendererFromRenderManager();
			GL11.glPushMatrix();
			GL11.glTranslatef((float)var2 + 0.0F, (float)var4 + 2.3F, (float)var6);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.field_1225_i, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.renderManager.field_1224_j, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-var14, -var14, var14);
			String var18 = var1.field_771_i;
			GL11.glDisable(GL11.GL_LIGHTING);
			Tessellator var19;
			if(!var1.isSneaking()) {
				GL11.glDepthMask(false);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				var19 = Tessellator.instance;
				byte var20 = 0;
				if(var1.field_771_i.equals("deadmau5")) {
					var20 = -10;
				}

				GL11.glDisable(GL11.GL_TEXTURE_2D);
				var19.startDrawingQuads();
				int var21 = var17.getStringWidth(var18) / 2;
				var19.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
				var19.addVertex((double)(-var21 - 1), (double)(-1 + var20), 0.0D);
				var19.addVertex((double)(-var21 - 1), (double)(8 + var20), 0.0D);
				var19.addVertex((double)(var21 + 1), (double)(8 + var20), 0.0D);
				var19.addVertex((double)(var21 + 1), (double)(-1 + var20), 0.0D);
				var19.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				var17.drawString(var18, -var17.getStringWidth(var18) / 2, var20, 553648127);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(true);
				var17.drawString(var18, -var17.getStringWidth(var18) / 2, var20, -1);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glPopMatrix();
			} else {
				GL11.glTranslatef(0.0F, 0.25F / var14, 0.0F);
				GL11.glDepthMask(false);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				var19 = Tessellator.instance;
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				var19.startDrawingQuads();
				int var22 = var17.getStringWidth(var18) / 2;
				var19.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
				var19.addVertex((double)(-var22 - 1), -1.0D, 0.0D);
				var19.addVertex((double)(-var22 - 1), 8.0D, 0.0D);
				var19.addVertex((double)(var22 + 1), 8.0D, 0.0D);
				var19.addVertex((double)(var22 + 1), -1.0D, 0.0D);
				var19.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				GL11.glDepthMask(true);
				var17.drawString(var18, -var17.getStringWidth(var18) / 2, 0, 553648127);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glPopMatrix();
			}
		}

	}

	protected void a(EntityPlayer var1, float var2) {
		ItemStack var3 = var1.inventory.armorItemInSlot(3);
		if(var3 != null && var3.getItem().shiftedIndex < 256) {
			GL11.glPushMatrix();
			this.field_209_f.bipedHead.func_926_b(1.0F / 16.0F);
			if(RenderBlocks.func_1219_a(Block.blocksList[var3.itemID].getRenderType())) {
				float var4 = 10.0F / 16.0F;
				GL11.glTranslatef(0.0F, -0.25F, 0.0F);
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var4, -var4, var4);
			}

			this.renderManager.field_4236_f.renderItem(var3);
			GL11.glPopMatrix();
		}

		float var5;
		if(var1.field_771_i.equals("deadmau5") && this.func_140_a((TextureLocation)null)) {
			for(int var19 = 0; var19 < 2; ++var19) {
				var5 = var1.prevRotationYaw + (var1.rotationYaw - var1.prevRotationYaw) * var2 - (var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var2);
				float var6 = var1.prevRotationPitch + (var1.rotationPitch - var1.prevRotationPitch) * var2;
				GL11.glPushMatrix();
				GL11.glRotatef(var5, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);
				GL11.glTranslatef(6.0F / 16.0F * (float)(var19 * 2 - 1), 0.0F, 0.0F);
				GL11.glTranslatef(0.0F, -(6.0F / 16.0F), 0.0F);
				GL11.glRotatef(-var6, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(-var5, 0.0F, 1.0F, 0.0F);
				float var7 = 4.0F / 3.0F;
				GL11.glScalef(var7, var7, var7);
				this.field_209_f.func_20095_a(1.0F / 16.0F);
				GL11.glPopMatrix();
			}
		}

		if(this.func_140_a((TextureLocation)null)) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.0F, 0.0F, 2.0F / 16.0F);
			double var20 = var1.field_20066_r + (var1.field_20063_u - var1.field_20066_r) * (double)var2 - (var1.prevPosX + (var1.posX - var1.prevPosX) * (double)var2);
			double var22 = var1.field_20065_s + (var1.field_20062_v - var1.field_20065_s) * (double)var2 - (var1.prevPosY + (var1.posY - var1.prevPosY) * (double)var2);
			double var8 = var1.field_20064_t + (var1.field_20061_w - var1.field_20064_t) * (double)var2 - (var1.prevPosZ + (var1.posZ - var1.prevPosZ) * (double)var2);
			float var10 = var1.prevRenderYawOffset + (var1.renderYawOffset - var1.prevRenderYawOffset) * var2;
			double var11 = (double)MathHelper.sin(var10 * (float)Math.PI / 180.0F);
			double var13 = (double)(-MathHelper.cos(var10 * (float)Math.PI / 180.0F));
			float var15 = (float)var22 * 10.0F;
			if(var15 < -6.0F) {
				var15 = -6.0F;
			}

			if(var15 > 32.0F) {
				var15 = 32.0F;
			}

			float var16 = (float)(var20 * var11 + var8 * var13) * 100.0F;
			float var17 = (float)(var20 * var13 - var8 * var11) * 100.0F;
			if(var16 < 0.0F) {
				var16 = 0.0F;
			}

			float var18 = var1.field_775_e + (var1.field_774_f - var1.field_775_e) * var2;
			var15 += MathHelper.sin((var1.prevDistanceWalkedModified + (var1.distanceWalkedModified - var1.prevDistanceWalkedModified) * var2) * 6.0F) * 32.0F * var18;
			GL11.glRotatef(6.0F + var16 / 2.0F + var15, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(var17 / 2.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(-var17 / 2.0F, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			this.field_209_f.func_20096_b(1.0F / 16.0F);
			GL11.glPopMatrix();
		}

		ItemStack var21 = var1.inventory.getCurrentItem();
		if(var21 != null) {
			GL11.glPushMatrix();
			this.field_209_f.bipedRightArm.func_926_b(1.0F / 16.0F);
			GL11.glTranslatef(-(1.0F / 16.0F), 7.0F / 16.0F, 1.0F / 16.0F);
			if(var1.fishEntity != null) {
				var21 = new ItemStack(Item.stick.shiftedIndex);
			}

			if(var21.itemID < 256 && RenderBlocks.func_1219_a(Block.blocksList[var21.itemID].getRenderType())) {
				var5 = 0.5F;
				GL11.glTranslatef(0.0F, 3.0F / 16.0F, -(5.0F / 16.0F));
				var5 *= 12.0F / 16.0F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(var5, -var5, var5);
			} else if(Item.itemsList[var21.itemID].isFull3D()) {
				var5 = 10.0F / 16.0F;
				if(Item.itemsList[var21.itemID].shouldRotateAroundWhenRendering()) {
					GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(0.0F, -(2.0F / 16.0F), 0.0F);
				}

				GL11.glTranslatef(0.0F, 3.0F / 16.0F, 0.0F);
				GL11.glScalef(var5, -var5, var5);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				var5 = 6.0F / 16.0F;
				GL11.glTranslatef(0.25F, 3.0F / 16.0F, -(3.0F / 16.0F));
				GL11.glScalef(var5, var5, var5);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			this.renderManager.field_4236_f.renderItem(var21);
			GL11.glPopMatrix();
		}

	}

	protected void b(EntityPlayer var1, float var2) {
		float var3 = 15.0F / 16.0F;
		GL11.glScalef(var3, var3, var3);
	}

	public void drawFirstPersonHand() {
		this.field_209_f.field_1244_k = 0.0F;
		this.field_209_f.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F / 16.0F);
		this.field_209_f.bipedRightArm.render(1.0F / 16.0F);
	}

	protected void preRenderCallback(EntityLiving var1, float var2) {
		this.b((EntityPlayer)var1, var2);
	}

	protected boolean shouldRenderPass(EntityLiving var1, int var2) {
		return this.a((EntityPlayer)var1, var2);
	}

	protected void renderEquippedItems(EntityLiving var1, float var2) {
		this.a((EntityPlayer)var1, var2);
	}

	public void a(EntityLiving var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityPlayer)var1, var2, var4, var6, var8, var9);
	}

	public void doRender(Entity var1, double var2, double var4, double var6, float var8, float var9) {
		this.a((EntityPlayer)var1, var2, var4, var6, var8, var9);
	}
}
