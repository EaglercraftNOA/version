package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockMagma extends Block {
   public BlockMagma(Block.Properties properties) {
      super(properties);
   }

   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      if (!entityIn.isImmuneToFire() && entityIn instanceof EntityLivingBase && !EnchantmentHelper.hasFrostWalker((EntityLivingBase)entityIn)) {
         entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
      }

      super.onEntityWalk(worldIn, pos, entityIn);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPackedLightmapCoords(IBlockState state, IWorldReader source, BlockPos pos) {
      return 15728880;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      BlockBubbleColumn.placeBubbleColumn(worldIn, pos.up(), true);
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == EnumFacing.UP && facingState.getBlock() == Blocks.WATER) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, this.tickRate(worldIn));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void randomTick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      BlockPos blockpos = pos.up();
      if (worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);
         if (worldIn instanceof WorldServer) {
            ((WorldServer)worldIn).spawnParticle(Particles.LARGE_SMOKE, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.25D, (double)blockpos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
         }
      }

   }

   public int tickRate(IWorldReaderBase worldIn) {
      return 20;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
   }

   public boolean canEntitySpawn(IBlockState state, Entity entityIn) {
      return entityIn.isImmuneToFire();
   }

   public boolean needsPostProcessing(IBlockState p_201783_1_, IBlockReader worldIn, BlockPos pos) {
      return true;
   }
}
