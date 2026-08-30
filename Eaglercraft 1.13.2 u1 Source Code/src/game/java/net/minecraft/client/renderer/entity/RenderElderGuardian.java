package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderElderGuardian extends RenderGuardian {
   private static final ResourceLocation GUARDIAN_ELDER_TEXTURE = new ResourceLocation("textures/entity/guardian_elder.png");

   public RenderElderGuardian(RenderManager renderManagerIn) {
      super(renderManagerIn);
   }

   protected void preRenderCallback(EntityGuardian entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scalef(2.35F, 2.35F, 2.35F);
   }

   protected ResourceLocation getEntityTexture(EntityGuardian entity) {
      return GUARDIAN_ELDER_TEXTURE;
   }
}
