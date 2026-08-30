package net.minecraft.client.renderer.entity.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureOffset {
   public final int textureOffsetX;
   public final int textureOffsetY;

   public TextureOffset(int textureOffsetXIn, int textureOffsetYIn) {
      this.textureOffsetX = textureOffsetXIn;
      this.textureOffsetY = textureOffsetYIn;
   }
}
