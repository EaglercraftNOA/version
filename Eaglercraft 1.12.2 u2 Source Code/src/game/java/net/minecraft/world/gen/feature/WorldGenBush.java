package net.minecraft.world.gen.feature;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.minecraft.block.BlockBush;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenBush extends WorldGenerator {
	private final BlockBush block;

	public WorldGenBush(BlockBush blockIn) {
		this.block = blockIn;
	}

	public boolean generate(World worldIn, EaglercraftRandom rand, BlockPos position) {
		for (int i = 0; i < 64; ++i) {
			BlockPos blockpos = position.add(rand.nextInt(8) - rand.nextInt(8), rand.nextInt(4) - rand.nextInt(4),
					rand.nextInt(8) - rand.nextInt(8));

			if (worldIn.isAirBlock(blockpos) && (!worldIn.provider.getHasNoSky() || blockpos.getY() < 255)
					&& this.block.canBlockStay(worldIn, blockpos, this.block.getDefaultState())) {
				worldIn.setBlockState(blockpos, this.block.getDefaultState(), 2);
			}
		}

		return true;
	}
}
