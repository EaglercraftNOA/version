package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockCoralPlant extends BlockCoralPlantBase {
   private final Block deadBlock;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 15.0D, 14.0D);

   protected BlockCoralPlant(Block p_i49809_1_, Block.Properties p_i49809_2_) {
      super(p_i49809_2_);
      this.deadBlock = p_i49809_1_;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      this.updateIfDry(state, worldIn, pos);
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!isInWater(state, worldIn, pos)) {
         worldIn.setBlockState(pos, this.deadBlock.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)), 2);
      }

   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == EnumFacing.DOWN && !stateIn.isValidPosition(worldIn, currentPos)) {
         return Blocks.AIR.getDefaultState();
      } else {
         this.updateIfDry(stateIn, worldIn, currentPos);
         if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         }

         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }
}
