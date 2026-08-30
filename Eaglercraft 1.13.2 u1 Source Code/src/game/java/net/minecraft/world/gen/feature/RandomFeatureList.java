package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class RandomFeatureList extends Feature<RandomDefaultFeatureListConfig> {
   public boolean place(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, RandomDefaultFeatureListConfig p_212245_5_) {
      for(int i = 0; i < p_212245_5_.features.length; ++i) {
         if (p_212245_3_.nextFloat() < p_212245_5_.chances[i]) {
            return this.place(p_212245_5_.features[i], p_212245_5_.configs[i], p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
         }
      }

      return this.place(p_212245_5_.defaultFeature, p_212245_5_.defaultConfig, p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }

   <FC extends IFeatureConfig> boolean place(Feature<FC> featureIn, IFeatureConfig config, IWorld worldIn, IChunkGenerator<? extends IChunkGenSettings> generator, Random rand, BlockPos pos) {
      return featureIn.place(worldIn, generator, rand, pos, (FC)config);
   }
}
