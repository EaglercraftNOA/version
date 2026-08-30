package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockDirtSnowy extends Block {
   public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

   protected BlockDirtSnowy(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(SNOWY, Boolean.valueOf(false)));
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing != EnumFacing.UP) {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         Block block = facingState.getBlock();
         return stateIn.with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      Block block = context.getWorld().getBlockState(context.getPos().up()).getBlock();
      return this.getDefaultState().with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(SNOWY);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Blocks.DIRT;
   }
}
