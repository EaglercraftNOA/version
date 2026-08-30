package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSkullWitherWall extends BlockSkullWall {
   protected BlockSkullWitherWall(Block.Properties properties) {
      super(BlockSkull.Types.WITHER_SKELETON, properties);
   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
      Blocks.WITHER_SKELETON_SKULL.onBlockPlacedBy(worldIn, pos, state, placer, stack);
   }
}
