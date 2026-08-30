package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockHorizontal {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   protected BlockRedstoneDiode(Block.Properties builder) {
      super(builder);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isTopSolid();
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!this.isLocked(worldIn, pos, state)) {
         boolean flag = state.get(POWERED);
         boolean flag1 = this.shouldBePowered(worldIn, pos, state);
         if (flag && !flag1) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 2);
         } else if (!flag) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 2);
            if (!flag1) {
               worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.getDelay(state), TickPriority.HIGH);
            }
         }

      }
   }

   public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return blockState.getWeakPower(blockAccess, pos, side);
   }

   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      if (!blockState.get(POWERED)) {
         return 0;
      } else {
         return blockState.get(HORIZONTAL_FACING) == side ? this.getActiveSignal(blockAccess, pos, blockState) : 0;
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (state.isValidPosition(worldIn, pos)) {
         this.updateState(worldIn, pos, state);
      } else {
         state.dropBlockAsItem(worldIn, pos, 0);
         worldIn.removeBlock(pos);

         for(EnumFacing enumfacing : EnumFacing._VALUES) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
         }

      }
   }

   protected void updateState(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.isLocked(worldIn, pos, state)) {
         boolean flag = state.get(POWERED);
         boolean flag1 = this.shouldBePowered(worldIn, pos, state);
         if (flag != flag1 && !worldIn.getPendingBlockTicks().isTickPending(pos, this)) {
            TickPriority tickpriority = TickPriority.HIGH;
            if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
               tickpriority = TickPriority.EXTREMELY_HIGH;
            } else if (flag) {
               tickpriority = TickPriority.VERY_HIGH;
            }

            worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.getDelay(state), tickpriority);
         }

      }
   }

   public boolean isLocked(IWorldReaderBase worldIn, BlockPos pos, IBlockState state) {
      return false;
   }

   protected boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
      return this.calculateInputStrength(worldIn, pos, state) > 0;
   }

   protected int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumfacing = state.get(HORIZONTAL_FACING);
      BlockPos blockpos = pos.offset(enumfacing);
      int i = worldIn.getRedstonePower(blockpos, enumfacing);
      if (i >= 15) {
         return i;
      } else {
         IBlockState iblockstate = worldIn.getBlockState(blockpos);
         return Math.max(i, iblockstate.getBlock() == Blocks.REDSTONE_WIRE ? iblockstate.get(BlockRedstoneWire.POWER) : 0);
      }
   }

   protected int getPowerOnSides(IWorldReaderBase worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumfacing = state.get(HORIZONTAL_FACING);
      EnumFacing enumfacing1 = enumfacing.rotateY();
      EnumFacing enumfacing2 = enumfacing.rotateYCCW();
      return Math.max(this.getPowerOnSide(worldIn, pos.offset(enumfacing1), enumfacing1), this.getPowerOnSide(worldIn, pos.offset(enumfacing2), enumfacing2));
   }

   protected int getPowerOnSide(IWorldReaderBase worldIn, BlockPos pos, EnumFacing side) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      Block block = iblockstate.getBlock();
      if (this.isAlternateInput(iblockstate)) {
         if (block == Blocks.REDSTONE_BLOCK) {
            return 15;
         } else {
            return block == Blocks.REDSTONE_WIRE ? iblockstate.get(BlockRedstoneWire.POWER) : worldIn.getStrongPower(pos, side);
         }
      } else {
         return 0;
      }
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing().getOpposite());
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (this.shouldBePowered(worldIn, pos, state)) {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, 1);
      }

   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      this.notifyNeighbors(worldIn, pos, state);
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (!isMoving && state.getBlock() != newState.getBlock()) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         this.onReplaced(worldIn, pos);
         this.notifyNeighbors(worldIn, pos, state);
      }
   }

   protected void onReplaced(World worldIn, BlockPos pos) {
   }

   protected void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumfacing = state.get(HORIZONTAL_FACING);
      BlockPos blockpos = pos.offset(enumfacing.getOpposite());
      worldIn.neighborChanged(blockpos, this, pos);
      worldIn.notifyNeighborsOfStateExcept(blockpos, this, enumfacing);
   }

   protected boolean isAlternateInput(IBlockState state) {
      return state.canProvidePower();
   }

   protected int getActiveSignal(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return 15;
   }

   public static boolean isDiode(IBlockState state) {
      return state.getBlock() instanceof BlockRedstoneDiode;
   }

   public boolean isFacingTowardsRepeater(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      EnumFacing enumfacing = state.get(HORIZONTAL_FACING).getOpposite();
      IBlockState iblockstate = worldIn.getBlockState(pos.offset(enumfacing));
      return isDiode(iblockstate) && iblockstate.get(HORIZONTAL_FACING) != enumfacing;
   }

   protected abstract int getDelay(IBlockState p_196346_1_);

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isSolid(IBlockState state) {
      return true;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }
}
