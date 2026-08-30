package net.minecraft.client.renderer.model;

import java.util.Arrays;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedQuadRetextured extends BakedQuad {
   private final TextureAtlasSprite texture;

   public BakedQuadRetextured(BakedQuad quad, TextureAtlasSprite textureIn) {
      super(Arrays.copyOf(quad.getVertexData(), quad.getVertexData().length), quad.tintIndex, FaceBakery.getFacingFromVertexData(quad.getVertexData()), quad.getSprite());
      this.texture = textureIn;
      this.remapQuad();
   }

   private void remapQuad() {
      for(int i = 0; i < 4; ++i) {
         int j = 7 * i;
         this.vertexData[j + 4] = Float.floatToRawIntBits(this.texture.getInterpolatedU((double)this.sprite.getUnInterpolatedU(Float.intBitsToFloat(this.vertexData[j + 4]))));
         this.vertexData[j + 4 + 1] = Float.floatToRawIntBits(this.texture.getInterpolatedV((double)this.sprite.getUnInterpolatedV(Float.intBitsToFloat(this.vertexData[j + 4 + 1]))));
      }

   }
}
