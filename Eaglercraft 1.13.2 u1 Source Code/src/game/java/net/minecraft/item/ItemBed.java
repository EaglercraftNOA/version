package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

public class ItemBed extends ItemBlock {
   public ItemBed(Block blockIn, Item.Properties properties) {
      super(blockIn, properties);
   }

   protected boolean placeBlock(BlockItemUseContext context, IBlockState state) {
      return context.getWorld().setBlockState(context.getPos(), state, 26);
   }
}
