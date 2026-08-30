package net.minecraft.world.gen.feature;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenFire extends WorldGenerator {
	public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
		for (int i = 0; i < 64; ++i) {
			BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4),
					rand.nextInt(8) - rand.nextInt(8));

			if (worldIn.isAirBlock(blockpos)
					&& worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.NETHERRACK) {
				worldIn.setBlockState(blockpos, Blocks.FIRE.getDefaultState(), 2);
			}
		}

		return true;
	}
}
