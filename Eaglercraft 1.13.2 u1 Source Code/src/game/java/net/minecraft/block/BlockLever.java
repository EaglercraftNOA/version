package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockLever extends BlockHorizontalFace {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   protected static final VoxelShape LEVER_NORTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 10.0D, 11.0D, 12.0D, 16.0D);
   protected static final VoxelShape LEVER_SOUTH_AABB = Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 11.0D, 12.0D, 6.0D);
   protected static final VoxelShape LEVER_WEST_AABB = Block.makeCuboidShape(10.0D, 4.0D, 5.0D, 16.0D, 12.0D, 11.0D);
   protected static final VoxelShape LEVER_EAST_AABB = Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 6.0D, 12.0D, 11.0D);
   protected static final VoxelShape FLOOR_Z_SHAPE = Block.makeCuboidShape(5.0D, 0.0D, 4.0D, 11.0D, 6.0D, 12.0D);
   protected static final VoxelShape FLOOR_X_SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 5.0D, 12.0D, 6.0D, 11.0D);
   protected static final VoxelShape CEILING_Z_SHAPE = Block.makeCuboidShape(5.0D, 10.0D, 4.0D, 11.0D, 16.0D, 12.0D);
   protected static final VoxelShape CEILING_X_SHAPE = Block.makeCuboidShape(4.0D, 10.0D, 5.0D, 12.0D, 16.0D, 11.0D);

   protected BlockLever(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH).with(POWERED, Boolean.valueOf(false)).with(FACE, AttachFace.WALL));
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      switch((AttachFace)state.get(FACE)) {
      case FLOOR:
         switch(state.get(HORIZONTAL_FACING).getAxis()) {
         case X:
            return FLOOR_X_SHAPE;
         case Z:
         default:
            return FLOOR_Z_SHAPE;
         }
      case WALL:
         switch((EnumFacing)state.get(HORIZONTAL_FACING)) {
         case EAST:
            return LEVER_EAST_AABB;
         case WEST:
            return LEVER_WEST_AABB;
         case SOUTH:
            return LEVER_SOUTH_AABB;
         case NORTH:
         default:
            return LEVER_NORTH_AABB;
         }
      case CEILING:
      default:
         switch(state.get(HORIZONTAL_FACING).getAxis()) {
         case X:
            return CEILING_X_SHAPE;
         case Z:
         default:
            return CEILING_Z_SHAPE;
         }
      }
   }

   public boolean onBlockActivated(IBlockState state, World worldIn, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
      state = state.cycle(POWERED);
      boolean flag = state.get(POWERED);
      if (worldIn.isRemote) {
         if (flag) {
            addParticles(state, worldIn, pos, 1.0F);
         }

         return true;
      } else {
         worldIn.setBlockState(pos, state, 3);
         float f = flag ? 0.6F : 0.5F;
         worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, f);
         this.updateNeighbors(state, worldIn, pos);
         return true;
      }
   }

   private static void addParticles(IBlockState state, IWorld worldIn, BlockPos pos, float alpha) {
      EnumFacing enumfacing = state.get(HORIZONTAL_FACING).getOpposite();
      EnumFacing enumfacing1 = getFacing(state).getOpposite();
      double d0 = (double)pos.getX() + 0.5D + 0.1D * (double)enumfacing.getXOffset() + 0.2D * (double)enumfacing1.getXOffset();
      double d1 = (double)pos.getY() + 0.5D + 0.1D * (double)enumfacing.getYOffset() + 0.2D * (double)enumfacing1.getYOffset();
      double d2 = (double)pos.getZ() + 0.5D + 0.1D * (double)enumfacing.getZOffset() + 0.2D * (double)enumfacing1.getZOffset();
      worldIn.addParticle(new RedstoneParticleData(1.0F, 0.0F, 0.0F, alpha), d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (stateIn.get(POWERED) && rand.nextFloat() < 0.25F) {
         addParticles(stateIn, worldIn, pos, 0.5F);
      }

   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (!isMoving && state.getBlock() != newState.getBlock()) {
         if (state.get(POWERED)) {
            this.updateNeighbors(state, worldIn, pos);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return blockState.get(POWERED) ? 15 : 0;
   }

   public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return blockState.get(POWERED) && getFacing(blockState) == side ? 15 : 0;
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   private void updateNeighbors(IBlockState p_196378_1_, World p_196378_2_, BlockPos p_196378_3_) {
      p_196378_2_.notifyNeighborsOfStateChange(p_196378_3_, this);
      p_196378_2_.notifyNeighborsOfStateChange(p_196378_3_.offset(getFacing(p_196378_1_).getOpposite()), this);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACE, HORIZONTAL_FACING, POWERED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
