package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSkullWallPlayer extends BlockSkullWall {
   protected BlockSkullWallPlayer(Block.Properties properties) {
      super(BlockSkull.Types.PLAYER, properties);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
      Blocks.PLAYER_HEAD.onBlockPlacedBy(worldIn, pos, state, placer, stack);
   }
}
