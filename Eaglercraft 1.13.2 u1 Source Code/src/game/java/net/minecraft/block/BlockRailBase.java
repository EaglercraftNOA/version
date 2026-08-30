package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.state.IProperty;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   protected static final VoxelShape FLAT_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   protected static final VoxelShape ASCENDING_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final boolean disableCorners;

   public static boolean isRail(World p_208488_0_, BlockPos p_208488_1_) {
      return isRail(p_208488_0_.getBlockState(p_208488_1_));
   }

   public static boolean isRail(IBlockState p_208487_0_) {
      return p_208487_0_.isIn(BlockTags.RAILS);
   }

   protected BlockRailBase(boolean p_i48444_1_, Block.Properties p_i48444_2_) {
      super(p_i48444_2_);
      this.disableCorners = p_i48444_1_;
   }

   public boolean areCornersDisabled() {
      return this.disableCorners;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      RailShape railshape = state.getBlock() == this ? state.get(this.getShapeProperty()) : null;
      return railshape != null && railshape.isAscending() ? ASCENDING_AABB : FLAT_AABB;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isTopSolid();
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         if (!worldIn.isRemote) {
            state = this.getUpdatedState(worldIn, pos, state, true);
            if (this.disableCorners) {
               state.neighborChanged(worldIn, pos, this, pos);
            }
         }

      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         RailShape railshape = state.get(this.getShapeProperty());
         boolean flag = false;
         if (!worldIn.getBlockState(pos.down()).isTopSolid()) {
            flag = true;
         }

         if (railshape == RailShape.ASCENDING_EAST && !worldIn.getBlockState(pos.east()).isTopSolid()) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_WEST && !worldIn.getBlockState(pos.west()).isTopSolid()) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_NORTH && !worldIn.getBlockState(pos.north()).isTopSolid()) {
            flag = true;
         } else if (railshape == RailShape.ASCENDING_SOUTH && !worldIn.getBlockState(pos.south()).isTopSolid()) {
            flag = true;
         }

         if (flag && !worldIn.isAirBlock(pos)) {
            state.dropBlockAsItemWithChance(worldIn, pos, 1.0F, 0);
            worldIn.removeBlock(pos);
         } else {
            this.updateState(state, worldIn, pos, blockIn);
         }

      }
   }

   protected void updateState(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
   }

   protected IBlockState getUpdatedState(World p_208489_1_, BlockPos p_208489_2_, IBlockState p_208489_3_, boolean placing) {
      return p_208489_1_.isRemote ? p_208489_3_ : (new BlockRailState(p_208489_1_, p_208489_2_, p_208489_3_)).update(p_208489_1_.isBlockPowered(p_208489_2_), placing).getNewState();
   }

   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.NORMAL;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (!isMoving) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         if (state.get(this.getShapeProperty()).isAscending()) {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
         }

         if (this.disableCorners) {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         }

      }
   }

   public abstract IProperty<RailShape> getShapeProperty();
}
