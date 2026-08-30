package net.minecraft.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public interface IWorldWriter {
   boolean setBlockState(BlockPos pos, IBlockState newState, int flags);

   boolean spawnEntity(Entity entityIn);

   boolean removeBlock(BlockPos pos);

   void setLightFor(EnumLightType type, BlockPos pos, int lightValue);

   boolean destroyBlock(BlockPos pos, boolean dropBlock);
}
