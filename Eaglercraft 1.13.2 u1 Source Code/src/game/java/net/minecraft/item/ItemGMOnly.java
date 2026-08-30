package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;

public class ItemGMOnly extends ItemBlock {
   public ItemGMOnly(Block blockIn, Item.Properties builder) {
      super(blockIn, builder);
   }

   @Nullable
   protected IBlockState getStateForPlacement(BlockItemUseContext context) {
      EntityPlayer entityplayer = context.getPlayer();
      return entityplayer != null && !entityplayer.canUseCommandBlock() ? null : super.getStateForPlacement(context);
   }
}
