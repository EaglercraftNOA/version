package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockGrassPath extends Block {
   protected static final VoxelShape SHAPE = BlockFarmland.SHAPE;

   protected BlockGrassPath(Block.Properties builder) {
      super(builder);
   }

   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return worldIn.getMaxLightLevel();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return !this.getDefaultState().isValidPosition(context.getWorld(), context.getPos()) ? Block.nudgeEntitiesWithNewState(this.getDefaultState(), Blocks.DIRT.getDefaultState(), context.getWorld(), context.getPos()) : super.getStateForPlacement(context);
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == EnumFacing.UP && !stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      BlockFarmland.turnToDirt(state, worldIn, pos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.up());
      return !iblockstate.getMaterial().isSolid() || iblockstate.getBlock() instanceof BlockFenceGate;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Blocks.DIRT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
