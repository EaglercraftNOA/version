package net.minecraft.entity.projectile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.MobEffects;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityWitherSkull extends EntityFireball {
   private static final DataParameter<Boolean> INVULNERABLE = EntityDataManager.createKey(EntityWitherSkull.class, DataSerializers.BOOLEAN);

   public EntityWitherSkull(World worldIn) {
      super(EntityType.WITHER_SKULL, worldIn, 0.3125F, 0.3125F);
   }

   public EntityWitherSkull(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ) {
      super(EntityType.WITHER_SKULL, shooter, accelX, accelY, accelZ, worldIn, 0.3125F, 0.3125F);
   }

   @OnlyIn(Dist.CLIENT)
   public EntityWitherSkull(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ) {
      super(EntityType.WITHER_SKULL, x, y, z, accelX, accelY, accelZ, worldIn, 0.3125F, 0.3125F);
   }

   protected float getMotionFactor() {
      return this.isSkullInvulnerable() ? 0.73F : super.getMotionFactor();
   }

   public boolean isBurning() {
      return false;
   }

   public float getExplosionResistance(Explosion explosionIn, IBlockReader worldIn, BlockPos pos, IBlockState blockStateIn, IFluidState p_180428_5_, float p_180428_6_) {
      return this.isSkullInvulnerable() && EntityWither.canDestroyBlock(blockStateIn.getBlock()) ? Math.min(0.8F, p_180428_6_) : p_180428_6_;
   }

   protected void onImpact(RayTraceResult result) {
      if (!this.world.isRemote) {
         if (result.entity != null) {
            if (this.shootingEntity != null) {
               if (result.entity.attackEntityFrom(DamageSource.causeMobDamage(this.shootingEntity), 8.0F)) {
                  if (result.entity.isAlive()) {
                     this.applyEnchantments(this.shootingEntity, result.entity);
                  } else {
                     this.shootingEntity.heal(5.0F);
                  }
               }
            } else {
               result.entity.attackEntityFrom(DamageSource.MAGIC, 5.0F);
            }

            if (result.entity instanceof EntityLivingBase) {
               int i = 0;
               if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                  i = 10;
               } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                  i = 40;
               }

               if (i > 0) {
                  ((EntityLivingBase)result.entity).addPotionEffect(new PotionEffect(MobEffects.WITHER, 20 * i, 1));
               }
            }
         }

         this.world.newExplosion(this, this.posX, this.posY, this.posZ, 1.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
         this.remove();
      }

   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      return false;
   }

   protected void registerData() {
      this.dataManager.register(INVULNERABLE, false);
   }

   public boolean isSkullInvulnerable() {
      return this.dataManager.get(INVULNERABLE);
   }

   public void setSkullInvulnerable(boolean invulnerable) {
      this.dataManager.set(INVULNERABLE, invulnerable);
   }

   protected boolean isFireballFiery() {
      return false;
   }
}
