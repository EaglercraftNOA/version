package net.minecraft.world.gen.placement;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class WithChance extends BasePlacement<ChanceConfig> {
   public <C extends IFeatureConfig> boolean generate(IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, ChanceConfig placementConfig, Feature<C> featureIn, C featureConfig) {
      if (random.nextFloat() < 1.0F / (float)placementConfig.chance) {
         featureIn.place(worldIn, chunkGenerator, random, pos, featureConfig);
      }

      return true;
   }
}
