package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItemWitch;
import net.minecraft.client.renderer.entity.model.ModelWitch;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderWitch extends RenderLiving<EntityWitch> {
   private static final ResourceLocation WITCH_TEXTURES = new ResourceLocation("textures/entity/witch.png");

   public RenderWitch(RenderManager renderManagerIn) {
      super(renderManagerIn, new ModelWitch(0.0F), 0.5F);
      this.addLayer(new LayerHeldItemWitch(this));
   }

   public ModelWitch getMainModel() {
      return (ModelWitch)super.getMainModel();
   }

   public void doRender(EntityWitch entity, double x, double y, double z, float entityYaw, float partialTicks) {
      ((ModelWitch)this.mainModel).func_205074_a(!entity.getHeldItemMainhand().isEmpty());
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   protected ResourceLocation getEntityTexture(EntityWitch entity) {
      return WITCH_TEXTURES;
   }

   protected void preRenderCallback(EntityWitch entitylivingbaseIn, float partialTickTime) {
      float f = 0.9375F;
      GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
   }
}
