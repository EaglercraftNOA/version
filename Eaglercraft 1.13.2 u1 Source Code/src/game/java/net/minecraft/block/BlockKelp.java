package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockKelp extends Block implements ILiquidContainer {
   final BlockKelpTop field_209904_a;

   protected BlockKelp(BlockKelpTop p_i49501_1_, Block.Properties p_i49501_2_) {
      super(p_i49501_2_);
      this.field_209904_a = p_i49501_1_;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public IFluidState getFluidState(IBlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (facing == EnumFacing.UP) {
            Block block = facingState.getBlock();
            if (block != this && block != this.field_209904_a) {
               return this.field_209904_a.randomAge(worldIn);
            }
         }

         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      return block != Blocks.MAGMA_BLOCK && (block == this || Block.doesSideFillSquare(iblockstate.getCollisionShape(worldIn, blockpos), EnumFacing.UP));
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Blocks.KELP;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(Blocks.KELP);
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return false;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      return false;
   }
}
