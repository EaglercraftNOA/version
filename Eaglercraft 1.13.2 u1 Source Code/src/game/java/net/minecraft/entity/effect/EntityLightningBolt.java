package net.minecraft.entity.effect;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityLightningBolt extends EntityWeatherEffect {
   private int lightningState;
   public long boltVertex;
   private int boltLivingTime;
   private final boolean effectOnly;
   @Nullable
   private EntityPlayerMP caster;

   public EntityLightningBolt(World worldIn, double x, double y, double z, boolean effectOnlyIn) {
      super(EntityType.LIGHTNING_BOLT, worldIn);
      this.setLocationAndAngles(x, y, z, 0.0F, 0.0F);
      this.lightningState = 2;
      this.boltVertex = this.rand.nextLong();
      this.boltLivingTime = this.rand.nextInt(3) + 1;
      this.effectOnly = effectOnlyIn;
      EnumDifficulty enumdifficulty = worldIn.getDifficulty();
      if (enumdifficulty == EnumDifficulty.NORMAL || enumdifficulty == EnumDifficulty.HARD) {
         this.igniteBlocks(4);
      }

   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.WEATHER;
   }

   public void setCaster(@Nullable EntityPlayerMP p_204809_1_) {
      this.caster = p_204809_1_;
   }

   public void tick() {
      super.tick();
      if (this.lightningState == 2) {
         this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
         this.world.playSound((EntityPlayer)null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
      }

      --this.lightningState;
      if (this.lightningState < 0) {
         if (this.boltLivingTime == 0) {
            this.remove();
         } else if (this.lightningState < -this.rand.nextInt(10)) {
            --this.boltLivingTime;
            this.lightningState = 1;
            this.boltVertex = this.rand.nextLong();
            this.igniteBlocks(0);
         }
      }

      if (this.lightningState >= 0) {
         if (this.world.isRemote) {
            this.world.setLastLightningBolt(2);
         } else if (!this.effectOnly) {
            double d0 = 3.0D;
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, new AxisAlignedBB(this.posX - 3.0D, this.posY - 3.0D, this.posZ - 3.0D, this.posX + 3.0D, this.posY + 6.0D + 3.0D, this.posZ + 3.0D));

            for(int i = 0; i < list.size(); ++i) {
               Entity entity = list.get(i);
               entity.onStruckByLightning(this);
            }

            if (this.caster != null) {
               CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.caster, list);
            }
         }
      }

   }

   private void igniteBlocks(int extraIgnitions) {
      if (!this.effectOnly && !this.world.isRemote && this.world.getGameRules().getBoolean("doFireTick")) {
         IBlockState iblockstate = Blocks.FIRE.getDefaultState();
         BlockPos blockpos = new BlockPos(this);
         if (this.world.isAreaLoaded(blockpos, 10) && this.world.getBlockState(blockpos).isAir() && iblockstate.isValidPosition(this.world, blockpos)) {
            this.world.setBlockState(blockpos, iblockstate);
         }

         for(int i = 0; i < extraIgnitions; ++i) {
            BlockPos blockpos1 = blockpos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);
            if (this.world.getBlockState(blockpos1).isAir() && iblockstate.isValidPosition(this.world, blockpos1)) {
               this.world.setBlockState(blockpos1, iblockstate);
            }
         }

      }
   }

   protected void registerData() {
   }

   protected void readAdditional(NBTTagCompound compound) {
   }

   protected void writeAdditional(NBTTagCompound compound) {
   }
}
