package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelGuardian extends ModelBase {
   private static final float[] spineXRotations = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
   private static final float[] spineYRotations = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
   private static final float[] spineZRotations = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
   private static final float[] spineXPositions = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
   private static final float[] spineYPositions = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
   private static final float[] spineZPositions = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
   private final ModelRenderer guardianBody;
   private final ModelRenderer guardianEye;
   private final ModelRenderer[] guardianSpines;
   private final ModelRenderer[] guardianTail;

   public ModelGuardian() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.guardianSpines = new ModelRenderer[12];
      this.guardianBody = new ModelRenderer(this);
      this.guardianBody.setTextureOffset(0, 0).addBox(-6.0F, 10.0F, -8.0F, 12, 12, 16);
      this.guardianBody.setTextureOffset(0, 28).addBox(-8.0F, 10.0F, -6.0F, 2, 12, 12);
      this.guardianBody.setTextureOffset(0, 28).addBox(6.0F, 10.0F, -6.0F, 2, 12, 12, true);
      this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 8.0F, -6.0F, 12, 2, 12);
      this.guardianBody.setTextureOffset(16, 40).addBox(-6.0F, 22.0F, -6.0F, 12, 2, 12);

      for(int i = 0; i < this.guardianSpines.length; ++i) {
         this.guardianSpines[i] = new ModelRenderer(this, 0, 0);
         this.guardianSpines[i].addBox(-1.0F, -4.5F, -1.0F, 2, 9, 2);
         this.guardianBody.addChild(this.guardianSpines[i]);
      }

      this.guardianEye = new ModelRenderer(this, 8, 0);
      this.guardianEye.addBox(-1.0F, 15.0F, 0.0F, 2, 2, 1);
      this.guardianBody.addChild(this.guardianEye);
      this.guardianTail = new ModelRenderer[3];
      this.guardianTail[0] = new ModelRenderer(this, 40, 0);
      this.guardianTail[0].addBox(-2.0F, 14.0F, 7.0F, 4, 4, 8);
      this.guardianTail[1] = new ModelRenderer(this, 0, 54);
      this.guardianTail[1].addBox(0.0F, 14.0F, 0.0F, 3, 3, 7);
      this.guardianTail[2] = new ModelRenderer(this);
      this.guardianTail[2].setTextureOffset(41, 32).addBox(0.0F, 14.0F, 0.0F, 2, 2, 6);
      this.guardianTail[2].setTextureOffset(25, 19).addBox(1.0F, 10.5F, 3.0F, 1, 9, 9);
      this.guardianBody.addChild(this.guardianTail[0]);
      this.guardianTail[0].addChild(this.guardianTail[1]);
      this.guardianTail[1].addChild(this.guardianTail[2]);
   }

   public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
      this.guardianBody.render(scale);
   }

   public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn) {
      EntityGuardian entityguardian = (EntityGuardian)entityIn;
      float f = ageInTicks - (float)entityguardian.ticksExisted;
      this.guardianBody.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.guardianBody.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      float f1 = (1.0F - entityguardian.getSpikesAnimation(f)) * 0.55F;

      for(int i = 0; i < 12; ++i) {
         this.guardianSpines[i].rotateAngleX = (float)Math.PI * spineXRotations[i];
         this.guardianSpines[i].rotateAngleY = (float)Math.PI * spineYRotations[i];
         this.guardianSpines[i].rotateAngleZ = (float)Math.PI * spineZRotations[i];
         this.guardianSpines[i].rotationPointX = spineXPositions[i] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + (float)i) * 0.01F - f1);
         this.guardianSpines[i].rotationPointY = 16.0F + spineYPositions[i] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + (float)i) * 0.01F - f1);
         this.guardianSpines[i].rotationPointZ = spineZPositions[i] * (1.0F + MathHelper.cos(ageInTicks * 1.5F + (float)i) * 0.01F - f1);
      }

      this.guardianEye.rotationPointZ = -8.25F;
      Entity entity = Minecraft.getInstance().getRenderViewEntity();
      if (entityguardian.hasTargetedEntity()) {
         entity = entityguardian.getTargetedEntity();
      }

      if (entity != null) {
         double d2 = entity.prevPosX;
         double d3 = entity.prevPosY + (double)entity.getEyeHeight();
         double d4 = entity.prevPosZ;
         double d5 = entityIn.prevPosX;
         double d6 = entityIn.prevPosY + (double)entityIn.getEyeHeight();
         double d7 = entityIn.prevPosZ;
         double d0 = d3 - d6;
         if (d0 > 0.0D) {
            this.guardianEye.rotationPointY = 0.0F;
         } else {
            this.guardianEye.rotationPointY = 1.0F;
         }

         float f3 = entityIn.prevRotationPitch * ((float)Math.PI / 180F);
         float f4 = -entityIn.prevRotationYaw * ((float)Math.PI / 180F);
         double d8 = (double)(MathHelper.sin(f4) * MathHelper.cos(f3));
         double d9 = (double)(MathHelper.cos(f4) * MathHelper.cos(f3));
         double d10 = d5 - d2;
         double d11 = d7 - d4;
         double d12 = (double)MathHelper.sqrt(d10 * d10 + d11 * d11);
         if (d12 >= 1.0E-4D) {
            d10 /= d12;
            d11 /= d12;
         } else {
            d10 = 0.0D;
            d11 = 0.0D;
         }
         double d1 = d8 * d11 - d9 * d10;
         this.guardianEye.rotationPointX = MathHelper.sqrt((float)Math.abs(d1)) * 2.0F * (float)Math.signum(d1);
      }

      this.guardianEye.showModel = true;
      float f2 = entityguardian.getTailAnimation(f);
      this.guardianTail[0].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.05F;
      this.guardianTail[1].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.1F;
      this.guardianTail[1].rotationPointX = -1.5F;
      this.guardianTail[1].rotationPointY = 0.5F;
      this.guardianTail[1].rotationPointZ = 14.0F;
      this.guardianTail[2].rotateAngleY = MathHelper.sin(f2) * (float)Math.PI * 0.15F;
      this.guardianTail[2].rotationPointX = 0.5F;
      this.guardianTail[2].rotationPointY = 0.5F;
      this.guardianTail[2].rotationPointZ = 6.0F;
   }
}
