package net.minecraft.client.gui.fonts;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TexturedGlyph {
   private final ResourceLocation textureLocation;
   private final float u0;
   private final float u1;
   private final float v0;
   private final float v1;
   private final float minX;
   private final float maxX;
   private final float minY;
   private final float maxY;

   public TexturedGlyph(ResourceLocation loc, float minU, float maxU, float minV, float maxV, float xStart, float xEnd, float yStart, float yEnd) {
      this.textureLocation = loc;
      this.u0 = minU;
      this.u1 = maxU;
      this.v0 = minV;
      this.v1 = maxV;
      this.minX = xStart;
      this.maxX = xEnd;
      this.minY = yStart;
      this.maxY = yEnd;
   }

   public void render(TextureManager textureManagerIn, boolean isItalic, float x, float y, BufferBuilder buffer, float red, float green, float blue, float alpha) {
      int i = 3;
      float f = x + this.minX;
      float f1 = x + this.maxX;
      float f2 = this.minY - 3.0F;
      float f3 = this.maxY - 3.0F;
      float f4 = y + f2;
      float f5 = y + f3;
      float f6 = isItalic ? 1.0F - 0.25F * f2 : 0.0F;
      float f7 = isItalic ? 1.0F - 0.25F * f3 : 0.0F;
      buffer.pos((double)(f + f6), (double)f4, 0.0D).tex((double)this.u0, (double)this.v0).color(red, green, blue, alpha).endVertex();
      buffer.pos((double)(f + f7), (double)f5, 0.0D).tex((double)this.u0, (double)this.v1).color(red, green, blue, alpha).endVertex();
      buffer.pos((double)(f1 + f7), (double)f5, 0.0D).tex((double)this.u1, (double)this.v1).color(red, green, blue, alpha).endVertex();
      buffer.pos((double)(f1 + f6), (double)f4, 0.0D).tex((double)this.u1, (double)this.v0).color(red, green, blue, alpha).endVertex();
   }

   @Nullable
   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }
}
