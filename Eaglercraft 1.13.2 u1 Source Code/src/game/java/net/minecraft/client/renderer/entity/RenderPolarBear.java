package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelPolarBear;
import net.minecraft.entity.monster.EntityPolarBear;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderPolarBear extends RenderLiving<EntityPolarBear> {
   private static final ResourceLocation POLAR_BEAR_TEXTURE = new ResourceLocation("textures/entity/bear/polarbear.png");

   public RenderPolarBear(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelPolarBear(), 0.7F);
   }

   protected ResourceLocation getEntityTexture(EntityPolarBear entity) {
      return POLAR_BEAR_TEXTURE;
   }

   public void doRender(EntityPolarBear entity, double x, double y, double z, float entityYaw, float partialTicks) {
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected void preRenderCallback(EntityPolarBear entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scalef(1.2F, 1.2F, 1.2F);
      super.preRenderCallback(entitylivingbaseIn, partialTickTime);
   }
}
