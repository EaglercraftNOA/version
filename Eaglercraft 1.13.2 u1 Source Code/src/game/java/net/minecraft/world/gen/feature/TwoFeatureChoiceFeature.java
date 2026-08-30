package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class TwoFeatureChoiceFeature extends Feature<TwoFeatureChoiceConfig> {
   public boolean place(IWorld p_212245_1_, IChunkGenerator<? extends IChunkGenSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, TwoFeatureChoiceConfig p_212245_5_) {
      boolean flag = p_212245_3_.nextBoolean();
      return flag ? this.place(p_212245_5_.trueFeature, p_212245_5_.trueConfig, p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_) : this.place(p_212245_5_.falseFeature, p_212245_5_.falseConfig, p_212245_1_, p_212245_2_, p_212245_3_, p_212245_4_);
   }

   <FC extends IFeatureConfig> boolean place(Feature<FC> p_202360_1_, IFeatureConfig p_202360_2_, IWorld p_202360_3_, IChunkGenerator<? extends IChunkGenSettings> p_202360_4_, Random p_202360_5_, BlockPos p_202360_6_) {
      return p_202360_1_.place(p_202360_3_, p_202360_4_, p_202360_5_, p_202360_6_, (FC)p_202360_2_);
   }
}
