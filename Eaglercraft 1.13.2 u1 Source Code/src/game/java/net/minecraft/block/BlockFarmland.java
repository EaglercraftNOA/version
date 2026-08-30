package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockFarmland extends Block {
   public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE_0_7;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 15.0D, 16.0D);

   protected BlockFarmland(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(MOISTURE, Integer.valueOf(0)));
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == EnumFacing.UP && !stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.up());
      return !iblockstate.getMaterial().isSolid() || iblockstate.getBlock() instanceof BlockFenceGate;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return !this.getDefaultState().isValidPosition(context.getWorld(), context.getPos()) ? Blocks.DIRT.getDefaultState() : super.getStateForPlacement(context);
   }

   public int getOpacity(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return worldIn.getMaxLightLevel();
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!state.isValidPosition(worldIn, pos)) {
         turnToDirt(state, worldIn, pos);
      } else {
         int i = state.get(MOISTURE);
         if (!hasWater(worldIn, pos) && !worldIn.isRainingAt(pos.up())) {
            if (i > 0) {
               worldIn.setBlockState(pos, state.with(MOISTURE, Integer.valueOf(i - 1)), 2);
            } else if (!hasCrops(worldIn, pos)) {
               turnToDirt(state, worldIn, pos);
            }
         } else if (i < 7) {
            worldIn.setBlockState(pos, state.with(MOISTURE, Integer.valueOf(7)), 2);
         }

      }
   }

   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      if (!worldIn.isRemote && worldIn.rand.nextFloat() < fallDistance - 0.5F && entityIn instanceof EntityLivingBase && (entityIn instanceof EntityPlayer || worldIn.getGameRules().getBoolean("mobGriefing")) && entityIn.width * entityIn.width * entityIn.height > 0.512F) {
         turnToDirt(worldIn.getBlockState(pos), worldIn, pos);
      }

      super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
   }

   public static void turnToDirt(IBlockState state, World worldIn, BlockPos pos) {
      worldIn.setBlockState(pos, nudgeEntitiesWithNewState(state, Blocks.DIRT.getDefaultState(), worldIn, pos));
   }

   private static boolean hasCrops(IBlockReader p_176529_0_, BlockPos worldIn) {
      Block block = p_176529_0_.getBlockState(worldIn.up()).getBlock();
      return block instanceof BlockCrops || block instanceof BlockStem || block instanceof BlockAttachedStem;
   }

   private static boolean hasWater(IWorldReaderBase p_176530_0_, BlockPos worldIn) {
      for(BlockPos.MutableBlockPos blockpos$mutableblockpos : BlockPos.getAllInBoxMutable(worldIn.add(-4, 0, -4), worldIn.add(4, 1, 4))) {
         if (p_176530_0_.getFluidState(blockpos$mutableblockpos).isTagged(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Blocks.DIRT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(MOISTURE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
