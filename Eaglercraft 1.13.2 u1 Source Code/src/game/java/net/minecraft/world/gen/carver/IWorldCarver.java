package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.IFeatureConfig;

public interface IWorldCarver<C extends IFeatureConfig> {
   boolean shouldCarve(IBlockReader blockReader, Random rand, int x, int z, C config);

   boolean carve(IWorld region, Random random, int chunkX, int chunkZ, int originalX, int originalZ, BitSet mask, C config);
}
