package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
   protected static final VoxelShape PRESSED_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 0.5D, 15.0D);
   protected static final VoxelShape UNPRESSED_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.0D, 15.0D);
   protected static final AxisAlignedBB PRESSURE_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D);

   protected BlockBasePressurePlate(Block.Properties properties) {
      super(properties);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return this.getRedstoneStrength(state) > 0 ? PRESSED_AABB : UNPRESSED_AABB;
   }

   public int tickRate(IWorldReaderBase worldIn) {
      return 20;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean canSpawnInBlock() {
      return true;
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == EnumFacing.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      return iblockstate.isTopSolid() || iblockstate.getBlock() instanceof BlockFence;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!worldIn.isRemote) {
         int i = this.getRedstoneStrength(state);
         if (i > 0) {
            this.updateState(worldIn, pos, state, i);
         }

      }
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.isRemote) {
         int i = this.getRedstoneStrength(state);
         if (i == 0) {
            this.updateState(worldIn, pos, state, i);
         }

      }
   }

   protected void updateState(World worldIn, BlockPos pos, IBlockState state, int oldRedstoneStrength) {
      int i = this.computeRedstoneStrength(worldIn, pos);
      boolean flag = oldRedstoneStrength > 0;
      boolean flag1 = i > 0;
      if (oldRedstoneStrength != i) {
         state = this.setRedstoneStrength(state, i);
         worldIn.setBlockState(pos, state, 2);
         this.updateNeighbors(worldIn, pos);
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
      }

      if (!flag1 && flag) {
         this.playClickOffSound(worldIn, pos);
      } else if (flag1 && !flag) {
         this.playClickOnSound(worldIn, pos);
      }

      if (flag1) {
         worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
      }

   }

   protected abstract void playClickOnSound(IWorld worldIn, BlockPos pos);

   protected abstract void playClickOffSound(IWorld worldIn, BlockPos pos);

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (!isMoving && state.getBlock() != newState.getBlock()) {
         if (this.getRedstoneStrength(state) > 0) {
            this.updateNeighbors(worldIn, pos);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   protected void updateNeighbors(World worldIn, BlockPos pos) {
      worldIn.notifyNeighborsOfStateChange(pos, this);
      worldIn.notifyNeighborsOfStateChange(pos.down(), this);
   }

   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return this.getRedstoneStrength(blockState);
   }

   public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.UP ? this.getRedstoneStrength(blockState) : 0;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.DESTROY;
   }

   protected abstract int computeRedstoneStrength(World worldIn, BlockPos pos);

   protected abstract int getRedstoneStrength(IBlockState state);

   protected abstract IBlockState setRedstoneStrength(IBlockState state, int strength);

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
