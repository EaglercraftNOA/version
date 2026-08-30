package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderWitherSkeleton extends RenderSkeleton {
   private static final ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");

   public RenderWitherSkeleton(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected ResourceLocation getEntityTexture(AbstractSkeleton entity) {
      return WITHER_SKELETON_TEXTURES;
   }

   protected void preRenderCallback(AbstractSkeleton entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scalef(1.2F, 1.2F, 1.2F);
   }
}
