package net.minecraft.world.gen.feature;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenPumpkin extends WorldGenerator {
	public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
		for (int i = 0; i < 64; ++i) {
			BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4),
					rand.nextInt(8) - rand.nextInt(8));

			if (worldIn.isAirBlock(blockpos) && worldIn.getBlockState(blockpos.down()).getBlock() == Blocks.GRASS
					&& Blocks.PUMPKIN.canPlaceBlockAt(worldIn, blockpos)) {
				worldIn.setBlockState(blockpos, Blocks.PUMPKIN.getDefaultState().withProperty(BlockPumpkin.FACING,
						EnumFacing.Plane.HORIZONTAL.random(rand)), 2);
			}
		}

		return true;
	}
}
