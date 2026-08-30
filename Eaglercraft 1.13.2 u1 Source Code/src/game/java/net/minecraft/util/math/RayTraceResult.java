package net.minecraft.util.math;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;

public class RayTraceResult {
   private BlockPos blockPos;
   public RayTraceResult.Type type;
   public EnumFacing sideHit;
   public Vec3d hitVec;
   public Entity entity;

   public RayTraceResult(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
      this(RayTraceResult.Type.BLOCK, hitVecIn, sideHitIn, blockPosIn);
   }

   public RayTraceResult(Entity entityIn) {
      this(entityIn, new Vec3d(entityIn.posX, entityIn.posY, entityIn.posZ));
   }

   public RayTraceResult(RayTraceResult.Type typeIn, Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
      this.type = typeIn;
      this.blockPos = blockPosIn;
      this.sideHit = sideHitIn;
      this.hitVec = new Vec3d(hitVecIn.x, hitVecIn.y, hitVecIn.z);
   }

   public RayTraceResult(Entity entityHitIn, Vec3d hitVecIn) {
      this.type = RayTraceResult.Type.ENTITY;
      this.entity = entityHitIn;
      this.hitVec = hitVecIn;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public String toString() {
      return "HitResult{type=" + this.type + ", blockpos=" + this.blockPos + ", f=" + this.sideHit + ", pos=" + this.hitVec + ", entity=" + this.entity + '}';
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}
