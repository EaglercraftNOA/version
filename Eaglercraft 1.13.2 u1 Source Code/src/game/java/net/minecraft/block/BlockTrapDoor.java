package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.Half;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockTrapDoor extends BlockHorizontal implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape EAST_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape WEST_OPEN_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape SOUTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape NORTH_OPEN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape BOTTOM_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D);
   protected static final VoxelShape TOP_AABB = Block.makeCuboidShape(0.0D, 13.0D, 0.0D, 16.0D, 16.0D, 16.0D);

   protected BlockTrapDoor(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(OPEN, Boolean.valueOf(false)).with(HALF, Half.BOTTOM).with(POWERED, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      if (!state.get(OPEN)) {
         return state.get(HALF) == Half.TOP ? TOP_AABB : BOTTOM_AABB;
      } else {
         switch((EnumFacing)state.get(HORIZONTAL_FACING)) {
         case NORTH:
         default:
            return NORTH_OPEN_AABB;
         case SOUTH:
            return SOUTH_OPEN_AABB;
         case WEST:
            return WEST_OPEN_AABB;
         case EAST:
            return EAST_OPEN_AABB;
         }
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      switch(type) {
      case LAND:
         return state.get(OPEN);
      case WATER:
         return state.get(WATERLOGGED);
      case AIR:
         return state.get(OPEN);
      default:
         return false;
      }
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (this.material == Material.IRON) {
         return false;
      } else {
         state = state.cycle(OPEN);
         worldIn.setBlockState(pos, state, 2);
         if (state.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         }

         this.playSound(player, worldIn, pos, state.get(OPEN));
         return true;
      }
   }

   protected void playSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos, boolean p_185731_4_) {
      if (p_185731_4_) {
         int i = this.material == Material.IRON ? 1037 : 1007;
         worldIn.playEvent(player, i, pos, 0);
      } else {
         int j = this.material == Material.IRON ? 1036 : 1013;
         worldIn.playEvent(player, j, pos, 0);
      }

   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (!worldIn.isRemote) {
         boolean flag = worldIn.isBlockPowered(pos);
         if (flag != state.get(POWERED)) {
            if (state.get(OPEN) != flag) {
               state = state.with(OPEN, Boolean.valueOf(flag));
               this.playSound((EntityPlayer)null, worldIn, pos, flag);
            }

            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)), 2);
            if (state.get(WATERLOGGED)) {
               worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
            }
         }

      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = this.getDefaultState();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      EnumFacing enumfacing = context.getFace();
      if (!context.replacingClickedOnBlock() && enumfacing.getAxis().isHorizontal()) {
         iblockstate = iblockstate.with(HORIZONTAL_FACING, enumfacing).with(HALF, context.getHitY() > 0.5F ? Half.TOP : Half.BOTTOM);
      } else {
         iblockstate = iblockstate.with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite()).with(HALF, enumfacing == EnumFacing.UP ? Half.BOTTOM : Half.TOP);
      }

      if (context.getWorld().isBlockPowered(context.getPos())) {
         iblockstate = iblockstate.with(OPEN, Boolean.valueOf(true)).with(POWERED, Boolean.valueOf(true));
      }

      return iblockstate.with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HORIZONTAL_FACING, OPEN, HALF, POWERED, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return (face == EnumFacing.UP && state.get(HALF) == Half.TOP || face == EnumFacing.DOWN && state.get(HALF) == Half.BOTTOM) && !state.get(OPEN) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
      if (state.get(WATERLOGGED)) {
         worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public IFluidState getFluidState(IBlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      if (!state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
         if (!worldIn.isRemote()) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
         }

         return true;
      } else {
         return false;
      }
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }
}
