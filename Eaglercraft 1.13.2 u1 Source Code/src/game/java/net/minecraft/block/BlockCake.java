package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCake extends Block {
   public static final IntegerProperty BITES = BlockStateProperties.BITES_0_6;
   protected static final VoxelShape[] field_196402_b = new VoxelShape[]{Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(3.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(5.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(7.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(9.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(11.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D), Block.makeCuboidShape(13.0D, 0.0D, 1.0D, 15.0D, 8.0D, 15.0D)};

   protected BlockCake(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(BITES, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return field_196402_b[state.get(BITES)];
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote) {
         return this.eatCake(worldIn, pos, state, player);
      } else {
         ItemStack itemstack = player.getHeldItem(hand);
         return this.eatCake(worldIn, pos, state, player) || itemstack.isEmpty();
      }
   }

   private boolean eatCake(IWorld worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (!player.canEat(false)) {
         return false;
      } else {
         player.addStat(StatList.EAT_CAKE_SLICE);
         player.getFoodStats().addStats(2, 0.1F);
         int i = state.get(BITES);
         if (i < 6) {
            worldIn.setBlockState(pos, state.with(BITES, Integer.valueOf(i + 1)), 3);
         } else {
            worldIn.removeBlock(pos);
         }

         return true;
      }
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == EnumFacing.DOWN && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(BITES);
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return (7 - blockState.get(BITES)) * 2;
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
