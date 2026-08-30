package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.state.IBlockState;

public class BlockWithContextConfig implements IFeatureConfig {
   final IBlockState state;
   final List<IBlockState> placeOn;
   final List<IBlockState> placeIn;
   final List<IBlockState> placeUnder;

   public BlockWithContextConfig(IBlockState state, IBlockState[] placeOn, IBlockState[] placeIn, IBlockState[] placeUnder) {
      this.state = state;
      this.placeOn = Lists.newArrayList(placeOn);
      this.placeIn = Lists.newArrayList(placeIn);
      this.placeUnder = Lists.newArrayList(placeUnder);
   }
}
