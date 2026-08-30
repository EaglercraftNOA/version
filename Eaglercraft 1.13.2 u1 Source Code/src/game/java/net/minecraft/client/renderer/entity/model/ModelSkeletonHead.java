package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelSkeletonHead extends ModelBase {
   protected ModelRenderer skeletonHead;

   public ModelSkeletonHead() {
      this(0, 35, 64, 64);
   }

   public ModelSkeletonHead(int p_i1155_1_, int p_i1155_2_, int p_i1155_3_, int p_i1155_4_) {
      this.textureWidth = p_i1155_3_;
      this.textureHeight = p_i1155_4_;
      this.skeletonHead = new ModelRenderer(this, p_i1155_1_, p_i1155_2_);
      this.skeletonHead.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.0F);
      this.skeletonHead.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      this.skeletonHead.render(scale);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      super.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor, entityIn);
      this.skeletonHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.skeletonHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
   }
}
