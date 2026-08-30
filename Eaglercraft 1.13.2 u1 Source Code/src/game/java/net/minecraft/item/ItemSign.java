package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemSign extends ItemWallOrFloor {
   public ItemSign(Item.Properties builder) {
      super(Blocks.SIGN, Blocks.WALL_SIGN, builder);
   }

   protected boolean onBlockPlaced(BlockPos pos, World worldIn, @Nullable EntityPlayer player, ItemStack stack, IBlockState state) {
      boolean flag = super.onBlockPlaced(pos, worldIn, player, stack, state);
      if (!worldIn.isRemote && !flag && player != null) {
         player.openSignEditor((TileEntitySign)worldIn.getTileEntity(pos));
      }

      return flag;
   }
}
