package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.model.ModelZombie;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderGiantZombie extends RenderLiving<EntityGiantZombie> {
   private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
   private final float scale;

   public RenderGiantZombie(RenderManager p_i47206_1_, float scaleIn) {
      super(p_i47206_1_, new ModelZombie(), 0.5F * scaleIn);
      this.scale = scaleIn;
      this.addLayer(new LayerHeldItem(this));
      this.addLayer(new LayerBipedArmor(this) {
         protected void initArmor() {
            this.modelLeggings = new ModelZombie(0.5F, true);
            this.modelArmor = new ModelZombie(1.0F, true);
         }
      });
   }

   protected void preRenderCallback(EntityGiantZombie entitylivingbaseIn, float partialTickTime) {
      GlStateManager.scalef(this.scale, this.scale, this.scale);
   }

   protected ResourceLocation getEntityTexture(EntityGiantZombie entity) {
      return ZOMBIE_TEXTURES;
   }
}
