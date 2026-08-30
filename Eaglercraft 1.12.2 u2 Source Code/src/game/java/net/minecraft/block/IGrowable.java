package net.minecraft.block;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGrowable {
	/**
	 * Whether this IGrowable can grow
	 */
	boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient);

	boolean canUseBonemeal(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state);

	void grow(World worldIn, EaglercraftRandom rand, BlockPos pos, IBlockState state);
}
