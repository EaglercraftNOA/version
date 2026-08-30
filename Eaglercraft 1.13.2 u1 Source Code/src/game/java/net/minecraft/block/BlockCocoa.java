package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCocoa extends BlockHorizontal implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_2;
   protected static final VoxelShape[] COCOA_EAST_AABB = new VoxelShape[]{Block.makeCuboidShape(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.makeCuboidShape(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.makeCuboidShape(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] COCOA_WEST_AABB = new VoxelShape[]{Block.makeCuboidShape(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.makeCuboidShape(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.makeCuboidShape(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] COCOA_NORTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.makeCuboidShape(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.makeCuboidShape(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
   protected static final VoxelShape[] COCOA_SOUTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.makeCuboidShape(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.makeCuboidShape(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};

   public BlockCocoa(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(AGE, Integer.valueOf(0)));
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (worldIn.rand.nextInt(5) == 0) {
         int i = state.get(AGE);
         if (i < 2) {
            worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
         }
      }

   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.offset(state.get(HORIZONTAL_FACING))).getBlock();
      return block.isIn(BlockTags.JUNGLE_LOGS);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      int i = state.get(AGE);
      switch((EnumFacing)state.get(HORIZONTAL_FACING)) {
      case SOUTH:
         return COCOA_SOUTH_AABB[i];
      case NORTH:
      default:
         return COCOA_NORTH_AABB[i];
      case WEST:
         return COCOA_WEST_AABB[i];
      case EAST:
         return COCOA_EAST_AABB[i];
      }
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = this.getDefaultState();
      IWorldReaderBase iworldreaderbase = context.getWorld();
      BlockPos blockpos = context.getPos();

      for(EnumFacing enumfacing : context.getNearestLookingDirections()) {
         if (enumfacing.getAxis().isHorizontal()) {
            iblockstate = iblockstate.with(HORIZONTAL_FACING, enumfacing);
            if (iblockstate.isValidPosition(iworldreaderbase, blockpos)) {
               return iblockstate;
            }
         }
      }

      return null;
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == stateIn.get(HORIZONTAL_FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      int i = state.get(AGE);
      int j = 1;
      if (i >= 2) {
         j = 3;
      }

      for(int k = 0; k < j; ++k) {
         spawnAsEntity(worldIn, pos, new ItemStack(Items.COCOA_BEANS));
      }

   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return new ItemStack(Items.COCOA_BEANS);
   }

   public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return state.get(AGE) < 2;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(state.get(AGE) + 1)), 2);
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HORIZONTAL_FACING, AGE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
