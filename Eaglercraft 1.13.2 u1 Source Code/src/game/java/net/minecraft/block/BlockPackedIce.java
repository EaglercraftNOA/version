package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;

public class BlockPackedIce extends Block {
   public BlockPackedIce(Block.Properties properties) {
      super(properties);
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }
}
