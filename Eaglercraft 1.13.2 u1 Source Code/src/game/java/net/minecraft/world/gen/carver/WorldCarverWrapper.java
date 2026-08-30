package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;

public class WorldCarverWrapper<C extends IFeatureConfig> implements IWorldCarver<NoFeatureConfig> {
   private final IWorldCarver<C> carver;
   private final C config;

   public WorldCarverWrapper(IWorldCarver<C> carverIn, C configIn) {
      this.carver = carverIn;
      this.config = configIn;
   }

   public boolean shouldCarve(IBlockReader blockReader, Random rand, int x, int z, NoFeatureConfig config) {
      return this.carver.shouldCarve(blockReader, rand, x, z, this.config);
   }

   public boolean carve(IWorld region, Random random, int chunkX, int chunkZ, int originalX, int originalZ, BitSet mask, NoFeatureConfig config) {
      return this.carver.carve(region, random, chunkX, chunkZ, originalX, originalZ, mask, this.config);
   }
}
