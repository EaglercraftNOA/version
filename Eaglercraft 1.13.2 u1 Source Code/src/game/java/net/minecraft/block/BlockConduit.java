package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockConduit extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty field_212563_a = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

   public BlockConduit(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(field_212563_a, Boolean.valueOf(true)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(field_212563_a);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityConduit();
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public IFluidState getFluidState(IBlockState state) {
      return state.get(field_212563_a) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(field_212563_a)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityBeacon) {
            ((TileEntityBeacon)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return this.getDefaultState().with(field_212563_a, Boolean.valueOf(ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8));
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
      if (state.get(field_212563_a)) {
         worldIn.setBlockState(pos, state.with(field_212563_a, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return !state.get(field_212563_a) && fluidIn == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      if (!state.get(field_212563_a) && fluidStateIn.getFluid() == Fluids.WATER) {
         if (!worldIn.isRemote()) {
            worldIn.setBlockState(pos, state.with(field_212563_a, Boolean.valueOf(true)), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
         }

         return true;
      } else {
         return false;
      }
   }
}
