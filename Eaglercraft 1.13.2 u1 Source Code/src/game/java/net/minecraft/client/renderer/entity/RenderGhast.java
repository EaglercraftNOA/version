package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelGhast;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGhast extends RenderLiving<EntityGhast> {
   private static final ResourceLocation GHAST_TEXTURES = new ResourceLocation("textures/entity/ghast/ghast.png");
   private static final ResourceLocation GHAST_SHOOTING_TEXTURES = new ResourceLocation("textures/entity/ghast/ghast_shooting.png");

   public RenderGhast(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelGhast(), 0.5F);
   }

   protected ResourceLocation getEntityTexture(EntityGhast entity) {
      return entity.isAttacking() ? GHAST_SHOOTING_TEXTURES : GHAST_TEXTURES;
   }

   protected void preRenderCallback(EntityGhast entitylivingbaseIn, float partialTickTime) {
      float f = 1.0F;
      float f1 = 4.5F;
      float f2 = 4.5F;
      GlStateManager.scalef(4.5F, 4.5F, 4.5F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
