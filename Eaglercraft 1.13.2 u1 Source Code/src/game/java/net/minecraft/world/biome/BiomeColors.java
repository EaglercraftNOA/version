package net.minecraft.world.biome;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BiomeColors {
   private static final BiomeColors.ColorResolver GRASS_COLOR = Biome::getGrassColor;
   private static final BiomeColors.ColorResolver FOLIAGE_COLOR = Biome::getFoliageColor;
   private static final BiomeColors.ColorResolver WATER_COLOR = (p_210280_0_, p_210280_1_) -> {
      return p_210280_0_.getWaterColor();
   };
   private static final BiomeColors.ColorResolver WATER_FOG_COLOR = (p_210279_0_, p_210279_1_) -> {
      return p_210279_0_.getWaterFogColor();
   };
   private static final BlockPos.MutableBlockPos colorPos = new BlockPos.MutableBlockPos();

   private static int getColor(IWorldReaderBase worldIn, BlockPos pos, BiomeColors.ColorResolver resolver) {
      int l = Minecraft.getInstance().gameSettings.biomeBlendRadius;
      if (l == 0) {
         return resolver.getColor(worldIn.getBiome(pos), pos);
      }

      int i = 0;
      int j = 0;
      int k = 0;
      int i1 = (l * 2 + 1) * (l * 2 + 1);
      int j1 = pos.getX();
      int k1 = pos.getY();
      int l1 = pos.getZ();
      BlockPos.MutableBlockPos blockpos$mutableblockpos = colorPos;

      for(int i2 = j1 - l; i2 <= j1 + l; ++i2) {
         for(int j2 = l1 - l; j2 <= l1 + l; ++j2) {
            blockpos$mutableblockpos.setPos(i2, k1, j2);
            int k2 = resolver.getColor(worldIn.getBiome(blockpos$mutableblockpos), blockpos$mutableblockpos);
            i += (k2 & 16711680) >> 16;
            j += (k2 & '\uff00') >> 8;
            k += k2 & 255;
         }
      }

      return (i / i1 & 255) << 16 | (j / i1 & 255) << 8 | k / i1 & 255;
   }

   public static int getGrassColor(IWorldReaderBase worldIn, BlockPos pos) {
      return getColor(worldIn, pos, GRASS_COLOR);
   }

   public static int getFoliageColor(IWorldReaderBase worldIn, BlockPos pos) {
      return getColor(worldIn, pos, FOLIAGE_COLOR);
   }

   public static int getWaterColor(IWorldReaderBase worldIn, BlockPos pos) {
      return getColor(worldIn, pos, WATER_COLOR);
   }

   @OnlyIn(Dist.CLIENT)
   interface ColorResolver {
      int getColor(Biome p_getColor_1_, BlockPos p_getColor_2_);
   }
}
