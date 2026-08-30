package net.minecraft.entity.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMinecartTNT extends EntityMinecart {
   private int minecartTNTFuse = -1;

   public EntityMinecartTNT(World worldIn) {
      super(EntityType.TNT_MINECART, worldIn);
   }

   public EntityMinecartTNT(World worldIn, double x, double y, double z) {
      super(EntityType.TNT_MINECART, worldIn, x, y, z);
   }

   public EntityMinecart.Type getMinecartType() {
      return EntityMinecart.Type.TNT;
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.TNT.getDefaultState();
   }

   public void tick() {
      super.tick();
      if (this.minecartTNTFuse > 0) {
         --this.minecartTNTFuse;
         this.world.addParticle(Particles.SMOKE, this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
      } else if (this.minecartTNTFuse == 0) {
         this.explodeCart(this.motionX * this.motionX + this.motionZ * this.motionZ);
      }

      if (this.collidedHorizontally) {
         double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;
         if (d0 >= (double)0.01F) {
            this.explodeCart(d0);
         }
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      Entity entity = source.getImmediateSource();
      if (entity instanceof EntityArrow) {
         EntityArrow entityarrow = (EntityArrow)entity;
         if (entityarrow.isBurning()) {
            this.explodeCart(entityarrow.motionX * entityarrow.motionX + entityarrow.motionY * entityarrow.motionY + entityarrow.motionZ * entityarrow.motionZ);
         }
      }

      return super.attackEntityFrom(source, amount);
   }

   public void killMinecart(DamageSource source) {
      double d0 = this.motionX * this.motionX + this.motionZ * this.motionZ;
      if (!source.isFireDamage() && !source.isExplosion() && !(d0 >= (double)0.01F)) {
         super.killMinecart(source);
         if (!source.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.entityDropItem(Blocks.TNT);
         }

      } else {
         if (this.minecartTNTFuse < 0) {
            this.ignite();
            this.minecartTNTFuse = this.rand.nextInt(20) + this.rand.nextInt(20);
         }

      }
   }

   protected void explodeCart(double p_94103_1_) {
      if (!this.world.isRemote) {
         double d0 = Math.sqrt(p_94103_1_);
         if (d0 > 5.0D) {
            d0 = 5.0D;
         }

         this.world.createExplosion(this, this.posX, this.posY, this.posZ, (float)(4.0D + this.rand.nextDouble() * 1.5D * d0), true);
         this.remove();
      }

   }

   public void fall(float distance, float damageMultiplier) {
      if (distance >= 3.0F) {
         float f = distance / 10.0F;
         this.explodeCart((double)(f * f));
      }

      super.fall(distance, damageMultiplier);
   }

   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
      if (receivingPower && this.minecartTNTFuse < 0) {
         this.ignite();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 10) {
         this.ignite();
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public void ignite() {
      this.minecartTNTFuse = 80;
      if (!this.world.isRemote) {
         this.world.setEntityState(this, (byte)10);
         if (!this.isSilent()) {
            this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public int getFuseTicks() {
      return this.minecartTNTFuse;
   }

   public boolean isIgnited() {
      return this.minecartTNTFuse > -1;
   }

   public float getExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, IBlockState blockStateIn, IFluidState p_180428_5_, float p_180428_6_) {
      return !this.isIgnited() || !blockStateIn.isIn(BlockTags.RAILS) && !worldIn.getBlockState(pos.up()).isIn(BlockTags.RAILS) ? super.getExplosionResistance(explosionIn, worldIn, pos, blockStateIn, p_180428_5_, p_180428_6_) : 0.0F;
   }

   public boolean canExplosionDestroyBlock(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, IBlockState blockStateIn, float p_174816_5_) {
      return !this.isIgnited() || !blockStateIn.isIn(BlockTags.RAILS) && !worldIn.getBlockState(pos.up()).isIn(BlockTags.RAILS) ? super.canExplosionDestroyBlock(explosionIn, worldIn, pos, blockStateIn, p_174816_5_) : false;
   }

   protected void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      if (compound.contains("TNTFuse", 99)) {
         this.minecartTNTFuse = compound.getInt("TNTFuse");
      }

   }

   protected void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putInt("TNTFuse", this.minecartTNTFuse);
   }
}
