package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;

public abstract class BlockHorizontal extends Block {
   public static final DirectionProperty HORIZONTAL_FACING = BlockStateProperties.HORIZONTAL_FACING;

   protected BlockHorizontal(Block.Properties builder) {
      super(builder);
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
   }
}
