package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.init.Particles;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEnderChest extends BlockContainer implements IBucketPickupHandler, ILiquidContainer {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

   protected BlockEnderChest(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasCustomBreakingProgress(IBlockState state) {
      return true;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Blocks.OBSIDIAN;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 8;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      InventoryEnderChest inventoryenderchest = player.getInventoryEnderChest();
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (inventoryenderchest != null && tileentity instanceof TileEntityEnderChest) {
         if (worldIn.getBlockState(pos.up()).isNormalCube()) {
            return true;
         } else if (worldIn.isRemote) {
            return true;
         } else {
            inventoryenderchest.setChestTileEntity((TileEntityEnderChest)tileentity);
            player.displayGUIChest(inventoryenderchest);
            player.addStat(StatList.OPEN_ENDERCHEST);
            return true;
         }
      } else {
         return true;
      }
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityEnderChest();
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      for(int i = 0; i < 3; ++i) {
         int j = rand.nextInt(2) * 2 - 1;
         int k = rand.nextInt(2) * 2 - 1;
         double d0 = (double)pos.getX() + 0.5D + 0.25D * (double)j;
         double d1 = (double)((float)pos.getY() + rand.nextFloat());
         double d2 = (double)pos.getZ() + 0.5D + 0.25D * (double)k;
         double d3 = (double)(rand.nextFloat() * (float)j);
         double d4 = ((double)rand.nextFloat() - 0.5D) * 0.125D;
         double d5 = (double)(rand.nextFloat() * (float)k);
         worldIn.addParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
      }

   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
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
            worldIn.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
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

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
