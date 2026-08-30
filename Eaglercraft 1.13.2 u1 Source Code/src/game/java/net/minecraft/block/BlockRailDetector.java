package net.minecraft.block;

import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartCommandBlock;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {
   public static final EnumProperty<RailShape> SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public BlockRailDetector(Block.Properties properties) {
      super(true, properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)).with(SHAPE, RailShape.NORTH_SOUTH));
   }

   public int tickRate(IWorldReaderBase worldIn) {
      return 20;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.isRemote) {
         if (!state.get(POWERED)) {
            this.updatePoweredState(worldIn, pos, state);
         }
      }
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!worldIn.isRemote && state.get(POWERED)) {
         this.updatePoweredState(worldIn, pos, state);
      }
   }

   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return blockState.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      if (!blockState.get(POWERED)) {
         return 0;
      } else {
         return side == EnumFacing.UP ? 15 : 0;
      }
   }

   private void updatePoweredState(World worldIn, BlockPos pos, IBlockState state) {
      boolean flag = state.get(POWERED);
      boolean flag1 = false;
      List<EntityMinecart> list = this.findMinecarts(worldIn, pos, EntityMinecart.class, (Predicate<Entity>)null);
      if (!list.isEmpty()) {
         flag1 = true;
      }

      if (flag1 && !flag) {
         worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 3);
         this.updateConnectedRails(worldIn, pos, state, true);
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
      }

      if (!flag1 && flag) {
         worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 3);
         this.updateConnectedRails(worldIn, pos, state, false);
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
      }

      if (flag1) {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
      }

      worldIn.updateComparatorOutputLevel(pos, this);
   }

   protected void updateConnectedRails(World worldIn, BlockPos pos, IBlockState state, boolean powered) {
      BlockRailState blockrailstate = new BlockRailState(worldIn, pos, state);

      for(BlockPos blockpos : blockrailstate.getConnectedRails()) {
         IBlockState iblockstate = worldIn.getBlockState(blockpos);
         iblockstate.neighborChanged(worldIn, blockpos, iblockstate.getBlock(), pos);
      }

   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         super.onBlockAdded(state, worldIn, pos, oldState);
         this.updatePoweredState(worldIn, pos, state);
      }
   }

   public IProperty<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      if (blockState.get(POWERED)) {
         List<EntityMinecartCommandBlock> list = this.findMinecarts(worldIn, pos, EntityMinecartCommandBlock.class, (Predicate<Entity>)null);
         if (!list.isEmpty()) {
            return list.get(0).getCommandBlockLogic().getSuccessCount();
         }

         List<EntityMinecart> list1 = this.findMinecarts(worldIn, pos, EntityMinecart.class, EntitySelectors.HAS_INVENTORY);
         if (!list1.isEmpty()) {
            return Container.calcRedstoneFromInventory((IInventory)list1.get(0));
         }
      }

      return 0;
   }

   protected <T extends EntityMinecart> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> cartType, @Nullable Predicate<Entity> filter) {
      return worldIn.getEntitiesWithinAABB(cartType, this.getDectectionBox(pos), filter);
   }

   private AxisAlignedBB getDectectionBox(BlockPos pos) {
      float f = 0.2F;
      return new AxisAlignedBB((double)((float)pos.getX() + 0.2F), (double)pos.getY(), (double)((float)pos.getZ() + 0.2F), (double)((float)(pos.getX() + 1) - 0.2F), (double)((float)(pos.getY() + 1) - 0.2F), (double)((float)(pos.getZ() + 1) - 0.2F));
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         switch((RailShape)state.get(SHAPE)) {
         case ASCENDING_EAST:
            return state.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return state.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
            return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return state.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         }
      case COUNTERCLOCKWISE_90:
         switch((RailShape)state.get(SHAPE)) {
         case ASCENDING_EAST:
            return state.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_WEST:
            return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_NORTH:
            return state.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_SOUTH:
            return state.with(SHAPE, RailShape.ASCENDING_EAST);
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_SOUTH:
            return state.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return state.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      case CLOCKWISE_90:
         switch((RailShape)state.get(SHAPE)) {
         case ASCENDING_EAST:
            return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_WEST:
            return state.with(SHAPE, RailShape.ASCENDING_NORTH);
         case ASCENDING_NORTH:
            return state.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_SOUTH:
            return state.with(SHAPE, RailShape.ASCENDING_WEST);
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_SOUTH:
            return state.with(SHAPE, RailShape.EAST_WEST);
         case EAST_WEST:
            return state.with(SHAPE, RailShape.NORTH_SOUTH);
         }
      default:
         return state;
      }
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      RailShape railshape = state.get(SHAPE);
      switch(mirrorIn) {
      case LEFT_RIGHT:
         switch(railshape) {
         case ASCENDING_NORTH:
            return state.with(SHAPE, RailShape.ASCENDING_SOUTH);
         case ASCENDING_SOUTH:
            return state.with(SHAPE, RailShape.ASCENDING_NORTH);
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         default:
            return super.mirror(state, mirrorIn);
         }
      case FRONT_BACK:
         switch(railshape) {
         case ASCENDING_EAST:
            return state.with(SHAPE, RailShape.ASCENDING_WEST);
         case ASCENDING_WEST:
            return state.with(SHAPE, RailShape.ASCENDING_EAST);
         case ASCENDING_NORTH:
         case ASCENDING_SOUTH:
         default:
            break;
         case SOUTH_EAST:
            return state.with(SHAPE, RailShape.SOUTH_WEST);
         case SOUTH_WEST:
            return state.with(SHAPE, RailShape.SOUTH_EAST);
         case NORTH_WEST:
            return state.with(SHAPE, RailShape.NORTH_EAST);
         case NORTH_EAST:
            return state.with(SHAPE, RailShape.NORTH_WEST);
         }
      }

      return super.mirror(state, mirrorIn);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(SHAPE, POWERED);
   }
}
