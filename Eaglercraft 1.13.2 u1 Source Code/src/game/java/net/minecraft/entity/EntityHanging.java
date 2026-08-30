package net.minecraft.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class EntityHanging extends Entity {
   protected static final Predicate<Entity> IS_HANGING_ENTITY = (p_210144_0_) -> {
      return p_210144_0_ instanceof EntityHanging;
   };
   private int tickCounter1;
   protected BlockPos hangingPosition;
   @Nullable
   public EnumFacing facingDirection;

   protected EntityHanging(EntityType<?> type, World p_i48561_2_) {
      super(type, p_i48561_2_);
      this.setSize(0.5F, 0.5F);
   }

   protected EntityHanging(EntityType<?> type, World p_i48562_2_, BlockPos p_i48562_3_) {
      this(type, p_i48562_2_);
      this.hangingPosition = p_i48562_3_;
   }

   protected void registerData() {
   }

   protected void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
      Validate.notNull(facingDirectionIn);
      Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
      this.facingDirection = facingDirectionIn;
      this.rotationYaw = (float)(this.facingDirection.getHorizontalIndex() * 90);
      this.prevRotationYaw = this.rotationYaw;
      this.updateBoundingBox();
   }

   protected void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d0 = (double)this.hangingPosition.getX() + 0.5D;
         double d1 = (double)this.hangingPosition.getY() + 0.5D;
         double d2 = (double)this.hangingPosition.getZ() + 0.5D;
         double d3 = 0.46875D;
         double d4 = this.offs(this.getWidthPixels());
         double d5 = this.offs(this.getHeightPixels());
         d0 = d0 - (double)this.facingDirection.getXOffset() * 0.46875D;
         d2 = d2 - (double)this.facingDirection.getZOffset() * 0.46875D;
         d1 = d1 + d5;
         EnumFacing enumfacing = this.facingDirection.rotateYCCW();
         d0 = d0 + d4 * (double)enumfacing.getXOffset();
         d2 = d2 + d4 * (double)enumfacing.getZOffset();
         this.posX = d0;
         this.posY = d1;
         this.posZ = d2;
         double d6 = (double)this.getWidthPixels();
         double d7 = (double)this.getHeightPixels();
         double d8 = (double)this.getWidthPixels();
         if (this.facingDirection.getAxis() == EnumFacing.Axis.Z) {
            d8 = 1.0D;
         } else {
            d6 = 1.0D;
         }

         d6 = d6 / 32.0D;
         d7 = d7 / 32.0D;
         d8 = d8 / 32.0D;
         this.setBoundingBox(new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8));
      }
   }

   private double offs(int p_190202_1_) {
      return p_190202_1_ % 32 == 0 ? 0.5D : 0.0D;
   }

   public void tick() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.tickCounter1++ == 100 && !this.world.isRemote) {
         this.tickCounter1 = 0;
         if (!this.removed && !this.onValidSurface()) {
            this.remove();
            this.onBroken((Entity)null);
         }
      }

   }

   public boolean onValidSurface() {
      if (!this.world.isCollisionBoxesEmpty(this, this.getBoundingBox())) {
         return false;
      } else {
         int i = Math.max(1, this.getWidthPixels() / 16);
         int j = Math.max(1, this.getHeightPixels() / 16);
         BlockPos blockpos = this.hangingPosition.offset(this.facingDirection.getOpposite());
         EnumFacing enumfacing = this.facingDirection.rotateYCCW();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = 0; k < i; ++k) {
            for(int l = 0; l < j; ++l) {
               int i1 = (i - 1) / -2;
               int j1 = (j - 1) / -2;
               blockpos$mutableblockpos.setPos(blockpos).move(enumfacing, k + i1).move(EnumFacing.UP, l + j1);
               IBlockState iblockstate = this.world.getBlockState(blockpos$mutableblockpos);
               if (!iblockstate.getMaterial().isSolid() && !BlockRedstoneDiode.isDiode(iblockstate)) {
                  return false;
               }
            }
         }

         return this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), IS_HANGING_ENTITY).isEmpty();
      }
   }

   public boolean canBeCollidedWith() {
      return true;
   }

   public boolean hitByEntity(Entity entityIn) {
      return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F) : false;
   }

   public EnumFacing getHorizontalFacing() {
      return this.facingDirection;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         if (!this.removed && !this.world.isRemote) {
            this.remove();
            this.markVelocityChanged();
            this.onBroken(source.getTrueSource());
         }

         return true;
      }
   }

   public void move(MoverType type, double x, double y, double z) {
      if (!this.world.isRemote && !this.removed && x * x + y * y + z * z > 0.0D) {
         this.remove();
         this.onBroken((Entity)null);
      }

   }

   public void addVelocity(double x, double y, double z) {
      if (!this.world.isRemote && !this.removed && x * x + y * y + z * z > 0.0D) {
         this.remove();
         this.onBroken((Entity)null);
      }

   }

   public void writeAdditional(NBTTagCompound compound) {
      compound.putByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
      BlockPos blockpos = this.getHangingPosition();
      compound.putInt("TileX", blockpos.getX());
      compound.putInt("TileY", blockpos.getY());
      compound.putInt("TileZ", blockpos.getZ());
   }

   public void readAdditional(NBTTagCompound compound) {
      this.hangingPosition = new BlockPos(compound.getInt("TileX"), compound.getInt("TileY"), compound.getInt("TileZ"));
      this.updateFacingWithBoundingBox(EnumFacing.byHorizontalIndex(compound.getByte("Facing")));
   }

   public abstract int getWidthPixels();

   public abstract int getHeightPixels();

   public abstract void onBroken(@Nullable Entity brokenEntity);

   public abstract void playPlaceSound();

   public EntityItem entityDropItem(ItemStack stack, float offsetY) {
      EntityItem entityitem = new EntityItem(this.world, this.posX + (double)((float)this.facingDirection.getXOffset() * 0.15F), this.posY + (double)offsetY, this.posZ + (double)((float)this.facingDirection.getZOffset() * 0.15F), stack);
      entityitem.setDefaultPickupDelay();
      this.world.spawnEntity(entityitem);
      return entityitem;
   }

   protected boolean shouldSetPosAfterLoading() {
      return false;
   }

   public void setPosition(double x, double y, double z) {
      this.hangingPosition = new BlockPos(x, y, z);
      this.updateBoundingBox();
      this.isAirBorne = true;
   }

   public BlockPos getHangingPosition() {
      return this.hangingPosition;
   }

   public float getRotatedYaw(Rotation transformRotation) {
      if (this.facingDirection != null && this.facingDirection.getAxis() != EnumFacing.Axis.Y) {
         switch(transformRotation) {
         case CLOCKWISE_180:
            this.facingDirection = this.facingDirection.getOpposite();
            break;
         case COUNTERCLOCKWISE_90:
            this.facingDirection = this.facingDirection.rotateYCCW();
            break;
         case CLOCKWISE_90:
            this.facingDirection = this.facingDirection.rotateY();
         }
      }

      float f = MathHelper.wrapDegrees(this.rotationYaw);
      switch(transformRotation) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 90.0F;
      case CLOCKWISE_90:
         return f + 270.0F;
      default:
         return f;
      }
   }

   public float getMirroredYaw(Mirror transformMirror) {
      return this.getRotatedYaw(transformMirror.toRotation(this.facingDirection));
   }

   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
   }
}
