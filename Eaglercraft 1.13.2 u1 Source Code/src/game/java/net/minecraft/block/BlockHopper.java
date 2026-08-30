package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockHopper extends BlockContainer {
   public static final DirectionProperty FACING = BlockStateProperties.FACING_EXCEPT_UP;
   public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
   private static final VoxelShape INPUT_SHAPE = Block.makeCuboidShape(0.0D, 10.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   private static final VoxelShape MIDDLE_SHAPE = Block.makeCuboidShape(4.0D, 4.0D, 4.0D, 12.0D, 10.0D, 12.0D);
   private static final VoxelShape INPUT_MIDDLE_SHAPE = VoxelShapes.or(MIDDLE_SHAPE, INPUT_SHAPE);
   private static final VoxelShape field_196326_A = VoxelShapes.combineAndSimplify(INPUT_MIDDLE_SHAPE, IHopper.INSIDE_BOWL_SHAPE, IBooleanFunction.ONLY_FIRST);
   private static final VoxelShape DOWN_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 4.0D, 10.0D));
   private static final VoxelShape EAST_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(12.0D, 4.0D, 6.0D, 16.0D, 8.0D, 10.0D));
   private static final VoxelShape NORTH_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 4.0D, 0.0D, 10.0D, 8.0D, 4.0D));
   private static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(6.0D, 4.0D, 12.0D, 10.0D, 8.0D, 16.0D));
   private static final VoxelShape WEST_SHAPE = VoxelShapes.or(field_196326_A, Block.makeCuboidShape(0.0D, 4.0D, 6.0D, 4.0D, 8.0D, 10.0D));
   private static final VoxelShape DOWN_RAYTRACE_SHAPE = IHopper.INSIDE_BOWL_SHAPE;
   private static final VoxelShape EAST_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(12.0D, 8.0D, 6.0D, 16.0D, 10.0D, 10.0D));
   private static final VoxelShape NORTH_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 0.0D, 10.0D, 10.0D, 4.0D));
   private static final VoxelShape SOUTH_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(6.0D, 8.0D, 12.0D, 10.0D, 10.0D, 16.0D));
   private static final VoxelShape WEST_RAYTRACE_SHAPE = VoxelShapes.or(IHopper.INSIDE_BOWL_SHAPE, Block.makeCuboidShape(0.0D, 8.0D, 6.0D, 4.0D, 10.0D, 10.0D));

   public BlockHopper(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.DOWN).with(ENABLED, Boolean.valueOf(true)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      switch((EnumFacing)state.get(FACING)) {
      case DOWN:
         return DOWN_SHAPE;
      case NORTH:
         return NORTH_SHAPE;
      case SOUTH:
         return SOUTH_SHAPE;
      case WEST:
         return WEST_SHAPE;
      case EAST:
         return EAST_SHAPE;
      default:
         return field_196326_A;
      }
   }

   public VoxelShape getRaytraceShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      switch((EnumFacing)state.get(FACING)) {
      case DOWN:
         return DOWN_RAYTRACE_SHAPE;
      case NORTH:
         return NORTH_RAYTRACE_SHAPE;
      case SOUTH:
         return SOUTH_RAYTRACE_SHAPE;
      case WEST:
         return WEST_RAYTRACE_SHAPE;
      case EAST:
         return EAST_RAYTRACE_SHAPE;
      default:
         return IHopper.INSIDE_BOWL_SHAPE;
      }
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      EnumFacing enumfacing = context.getFace().getOpposite();
      return this.getDefaultState().with(FACING, enumfacing.getAxis() == EnumFacing.Axis.Y ? EnumFacing.DOWN : enumfacing).with(ENABLED, Boolean.valueOf(true));
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityHopper();
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (stack.hasDisplayName()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityHopper) {
            ((TileEntityHopper)tileentity).setCustomName(stack.getDisplayName());
         }
      }

   }

   public boolean isTopSolid(IBlockState state) {
      return true;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         this.updateState(worldIn, pos, state);
      }
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      } else {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityHopper) {
            player.displayGUIChest((TileEntityHopper)tileentity);
            player.addStat(StatList.INSPECT_HOPPER);
         }

         return true;
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      this.updateState(worldIn, pos, state);
   }

   private void updateState(World worldIn, BlockPos pos, IBlockState state) {
      boolean flag = !worldIn.isBlockPowered(pos);
      if (flag != state.get(ENABLED)) {
         worldIn.setBlockState(pos, state.with(ENABLED, Boolean.valueOf(flag)), 4);
      }

   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         TileEntity tileentity = worldIn.getTileEntity(pos);
         if (tileentity instanceof TileEntityHopper) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityHopper)tileentity);
            worldIn.updateComparatorOutputLevel(pos, this);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING, ENABLED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.UP ? BlockFaceShape.BOWL : BlockFaceShape.UNDEFINED;
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityHopper) {
         ((TileEntityHopper)tileentity).onEntityCollision(entityIn);
      }

   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
