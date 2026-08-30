package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class BlockConcretePowder extends BlockFalling {
   private final IBlockState solidifiedState;

   public BlockConcretePowder(Block p_i48423_1_, Block.Properties properties) {
      super(properties);
      this.solidifiedState = p_i48423_1_.getDefaultState();
   }

   public void onEndFalling(World worldIn, BlockPos pos, IBlockState fallingState, IBlockState hitState) {
      if (causesSolidify(hitState)) {
         worldIn.setBlockState(pos, this.solidifiedState, 3);
      }

   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      return !causesSolidify(iblockreader.getBlockState(blockpos)) && !isTouchingLiquid(iblockreader, blockpos) ? super.getStateForPlacement(context) : this.solidifiedState;
   }

   private static boolean isTouchingLiquid(IBlockReader p_196441_0_, BlockPos p_196441_1_) {
      boolean flag = false;
      BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(p_196441_1_);

      for(EnumFacing enumfacing : EnumFacing._VALUES) {
         IBlockState iblockstate = p_196441_0_.getBlockState(blockpos$mutableblockpos);
         if (enumfacing != EnumFacing.DOWN || causesSolidify(iblockstate)) {
            blockpos$mutableblockpos.setPos(p_196441_1_).move(enumfacing);
            iblockstate = p_196441_0_.getBlockState(blockpos$mutableblockpos);
            if (causesSolidify(iblockstate) && !Block.doesSideFillSquare(iblockstate.getCollisionShape(p_196441_0_, p_196441_1_), enumfacing.getOpposite())) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private static boolean causesSolidify(IBlockState p_212566_0_) {
      return p_212566_0_.getFluidState().isTagged(FluidTags.WATER);
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return isTouchingLiquid(worldIn, currentPos) ? this.solidifiedState : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }
}
