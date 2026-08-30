package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerDolphinCarriedItem;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderDolphin extends RenderLiving<EntityDolphin> {
   private static final ResourceLocation DOLPHIN_LOCATION = new ResourceLocation("textures/entity/dolphin.png");

   public RenderDolphin(RenderManager renderManagerIn) {
      super(renderManagerIn, new DolphinModel(), 0.7F);
      this.addLayer(new LayerDolphinCarriedItem(this));
   }

   protected ResourceLocation getEntityTexture(EntityDolphin entity) {
      return DOLPHIN_LOCATION;
   }

   protected void preRenderCallback(EntityDolphin entitylivingbaseIn, float partialTickTime) {
      float f = 1.0F;
      GlStateManager.scalef(1.0F, 1.0F, 1.0F);
   }

   protected void applyRotations(EntityDolphin entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      super.applyRotations(entityLiving, ageInTicks, rotationYaw, partialTicks);
   }
}
