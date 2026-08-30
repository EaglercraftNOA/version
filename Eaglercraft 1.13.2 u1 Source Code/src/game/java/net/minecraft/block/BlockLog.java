package net.minecraft.block;

import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockLog extends BlockRotatedPillar {
   private final MaterialColor field_196504_b;

   public BlockLog(MaterialColor p_i48367_1_, Block.Properties p_i48367_2_) {
      super(p_i48367_2_);
      this.field_196504_b = p_i48367_1_;
   }

   public MaterialColor getMaterialColor(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(AXIS) == EnumFacing.Axis.Y ? this.field_196504_b : this.materialColor;
   }
}
