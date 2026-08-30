package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ItemBlockTall extends ItemBlock {
   public ItemBlockTall(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   protected boolean placeBlock(BlockItemUseContext context, IBlockState state) {
      context.getWorld().setBlockState(context.getPos().up(), Blocks.AIR.getDefaultState(), 27);
      return super.placeBlock(context, state);
   }
}
