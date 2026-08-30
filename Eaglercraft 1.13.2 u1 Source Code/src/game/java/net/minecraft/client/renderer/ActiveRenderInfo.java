package net.minecraft.client.renderer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ActiveRenderInfo {
   private static final float[] MODELVIEW = new float[16];
   private static final Matrix4f MATRIX = new Matrix4f();
   private static final Vector4f VECTOR = new Vector4f();
   private static final BlockPos.MutableBlockPos BLOCK_POS = new BlockPos.MutableBlockPos();
   private static double positionX;
   private static double positionY;
   private static double positionZ;
   private static float rotationX;
   private static float rotationXZ;
   private static float rotationZ;
   private static float rotationYZ;
   private static float rotationXY;

   public static void updateRenderInfo(EntityPlayer entityPlayerIn, boolean invert, float farPlane) {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.getFloat(2982, MODELVIEW);
      Matrix4f matrix4f = MATRIX;

      for(int j = 0; j < 16; ++j) {
         matrix4f.set(j & 3, j >> 2, MODELVIEW[j]);
      }

      matrix4f.func_195887_c();
      float f = 0.05F;
      float f1 = farPlane * MathHelper.SQRT_2;
      Vector4f vector4f = VECTOR;
      vector4f.set(0.0F, 0.0F, -2.0F * f1 * 0.05F / (f1 + 0.05F), 1.0F);
      vector4f.mul(matrix4f);
      positionX = (double)vector4f.getX();
      positionY = (double)vector4f.getY();
      positionZ = (double)vector4f.getZ();
      float f2 = entityPlayerIn.rotationPitch;
      float f3 = entityPlayerIn.rotationYaw;
      int i = invert ? -1 : 1;
      rotationX = MathHelper.cos(f3 * ((float)Math.PI / 180F)) * (float)i;
      rotationZ = MathHelper.sin(f3 * ((float)Math.PI / 180F)) * (float)i;
      rotationYZ = -rotationZ * MathHelper.sin(f2 * ((float)Math.PI / 180F)) * (float)i;
      rotationXY = rotationX * MathHelper.sin(f2 * ((float)Math.PI / 180F)) * (float)i;
      rotationXZ = MathHelper.cos(f2 * ((float)Math.PI / 180F));
   }

   public static Vec3d projectViewFromEntity(Entity entityIn, double p_178806_1_) {
      double d0 = entityIn.prevPosX + (entityIn.posX - entityIn.prevPosX) * p_178806_1_;
      double d1 = entityIn.prevPosY + (entityIn.posY - entityIn.prevPosY) * p_178806_1_;
      double d2 = entityIn.prevPosZ + (entityIn.posZ - entityIn.prevPosZ) * p_178806_1_;
      double d3 = d0 + positionX;
      double d4 = d1 + positionY;
      double d5 = d2 + positionZ;
      return new Vec3d(d3, d4, d5);
   }

   public static double getViewPositionX() {
      return positionX;
   }

   public static double getViewPositionY() {
      return positionY;
   }

   public static double getViewPositionZ() {
      return positionZ;
   }

   public static IBlockState getBlockStateAtEntityViewpoint(IBlockReader worldIn, Entity entityIn, float p_186703_2_) {
      double d0 = entityIn.prevPosX + (entityIn.posX - entityIn.prevPosX) * (double)p_186703_2_ + positionX;
      double d1 = entityIn.prevPosY + (entityIn.posY - entityIn.prevPosY) * (double)p_186703_2_ + positionY;
      double d2 = entityIn.prevPosZ + (entityIn.posZ - entityIn.prevPosZ) * (double)p_186703_2_ + positionZ;
      BlockPos.MutableBlockPos blockpos = BLOCK_POS.setPos(d0, d1, d2);
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      IFluidState ifluidstate = worldIn.getFluidState(blockpos);
      if (!ifluidstate.isEmpty()) {
         float f = (float)blockpos.getY() + ifluidstate.getHeight() + 0.11111111F;
         if (d1 >= (double)f) {
            iblockstate = worldIn.getBlockState(blockpos.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ()));
         }
      }

      return iblockstate;
   }

   public static IFluidState getFluidStateAtEntityViewpoint(IBlockReader p_206243_0_, Entity p_206243_1_, float p_206243_2_) {
      double d0 = p_206243_1_.prevPosX + (p_206243_1_.posX - p_206243_1_.prevPosX) * (double)p_206243_2_ + positionX;
      double d1 = p_206243_1_.prevPosY + (p_206243_1_.posY - p_206243_1_.prevPosY) * (double)p_206243_2_ + positionY;
      double d2 = p_206243_1_.prevPosZ + (p_206243_1_.posZ - p_206243_1_.prevPosZ) * (double)p_206243_2_ + positionZ;
      BlockPos.MutableBlockPos blockpos = BLOCK_POS.setPos(d0, d1, d2);
      IFluidState ifluidstate = p_206243_0_.getFluidState(blockpos);
      if (!ifluidstate.isEmpty()) {
         float f = (float)blockpos.getY() + ifluidstate.getHeight() + 0.11111111F;
         if (d1 >= (double)f) {
            ifluidstate = p_206243_0_.getFluidState(blockpos.setPos(blockpos.getX(), blockpos.getY() + 1, blockpos.getZ()));
         }
      }

      return ifluidstate;
   }

   public static float getRotationX() {
      return rotationX;
   }

   public static float getRotationXZ() {
      return rotationXZ;
   }

   public static float getRotationZ() {
      return rotationZ;
   }

   public static float getRotationYZ() {
      return rotationYZ;
   }

   public static float getRotationXY() {
      return rotationXY;
   }
}
