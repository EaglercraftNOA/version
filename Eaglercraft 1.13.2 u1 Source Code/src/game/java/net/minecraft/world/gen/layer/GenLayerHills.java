package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset1Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum GenLayerHills implements IAreaTransformer2, IDimOffset1Transformer {
   INSTANCE;

   private static final Logger LOGGER = LogManager.getLogger();
   private static final int BIRCH_FOREST = IRegistry.BIOME.getId(Biomes.BIRCH_FOREST);
   private static final int BIRCH_FOREST_HILLS = IRegistry.BIOME.getId(Biomes.BIRCH_FOREST_HILLS);
   private static final int DESERT = IRegistry.BIOME.getId(Biomes.DESERT);
   private static final int DESERT_HILLS = IRegistry.BIOME.getId(Biomes.DESERT_HILLS);
   private static final int MOUNTAINS = IRegistry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int WOODED_MOUNTAINS = IRegistry.BIOME.getId(Biomes.WOODED_MOUNTAINS);
   private static final int FOREST = IRegistry.BIOME.getId(Biomes.FOREST);
   private static final int WOODED_HILLS = IRegistry.BIOME.getId(Biomes.WOODED_HILLS);
   private static final int SNOWY_TUNDRA = IRegistry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int SNOWY_MOUNTAINS = IRegistry.BIOME.getId(Biomes.SNOWY_MOUNTAINS);
   private static final int JUNGLE = IRegistry.BIOME.getId(Biomes.JUNGLE);
   private static final int JUNGLE_HILLS = IRegistry.BIOME.getId(Biomes.JUNGLE_HILLS);
   private static final int BADLANDS = IRegistry.BIOME.getId(Biomes.BADLANDS);
   private static final int WOODED_BADLANDS_PLATEAU = IRegistry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int PLAINS = IRegistry.BIOME.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = IRegistry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int GIANT_TREE_TAIGA_HILLS = IRegistry.BIOME.getId(Biomes.GIANT_TREE_TAIGA_HILLS);
   private static final int DARK_FOREST = IRegistry.BIOME.getId(Biomes.DARK_FOREST);
   private static final int SAVANNA = IRegistry.BIOME.getId(Biomes.SAVANNA);
   private static final int SAVANA_PLATEAU = IRegistry.BIOME.getId(Biomes.SAVANNA_PLATEAU);
   private static final int TAIGA = IRegistry.BIOME.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = IRegistry.BIOME.getId(Biomes.SNOWY_TAIGA);
   private static final int SNOWY_TAIGA_HILLS = IRegistry.BIOME.getId(Biomes.SNOWY_TAIGA_HILLS);
   private static final int TAIGA_HILLS = IRegistry.BIOME.getId(Biomes.TAIGA_HILLS);

   public int apply(IContext context, AreaDimension dimensionIn, IArea area1, IArea area2, int x, int z) {
      int i = area1.getValue(x + 1, z + 1);
      int j = area2.getValue(x + 1, z + 1);
      if (i > 255) {
         LOGGER.debug("old! {}", (int)i);
      }

      int k = (j - 2) % 29;
      if (!LayerUtil.isShallowOcean(i) && j >= 2 && k == 1) {
         Biome biome = IRegistry.BIOME.get(i);
         if (biome == null || !biome.isMutation()) {
            Biome biome2 = Biome.getMutationForBiome(biome);
            return biome2 == null ? i : IRegistry.BIOME.getId(biome2);
         }
      }

      if (context.random(3) == 0 || k == 0) {
         int l = i;
         if (i == DESERT) {
            l = DESERT_HILLS;
         } else if (i == FOREST) {
            l = WOODED_HILLS;
         } else if (i == BIRCH_FOREST) {
            l = BIRCH_FOREST_HILLS;
         } else if (i == DARK_FOREST) {
            l = PLAINS;
         } else if (i == TAIGA) {
            l = TAIGA_HILLS;
         } else if (i == GIANT_TREE_TAIGA) {
            l = GIANT_TREE_TAIGA_HILLS;
         } else if (i == SNOWY_TAIGA) {
            l = SNOWY_TAIGA_HILLS;
         } else if (i == PLAINS) {
            l = context.random(3) == 0 ? WOODED_HILLS : FOREST;
         } else if (i == SNOWY_TUNDRA) {
            l = SNOWY_MOUNTAINS;
         } else if (i == JUNGLE) {
            l = JUNGLE_HILLS;
         } else if (i == LayerUtil.OCEAN) {
            l = LayerUtil.DEEP_OCEAN;
         } else if (i == LayerUtil.LUKEWARM_OCEAN) {
            l = LayerUtil.DEEP_LUKEWARM_OCEAN;
         } else if (i == LayerUtil.COLD_OCEAN) {
            l = LayerUtil.DEEP_COLD_OCEAN;
         } else if (i == LayerUtil.FROZEN_OCEAN) {
            l = LayerUtil.DEEP_FROZEN_OCEAN;
         } else if (i == MOUNTAINS) {
            l = WOODED_MOUNTAINS;
         } else if (i == SAVANNA) {
            l = SAVANA_PLATEAU;
         } else if (LayerUtil.areBiomesSimilar(i, WOODED_BADLANDS_PLATEAU)) {
            l = BADLANDS;
         } else if ((i == LayerUtil.DEEP_OCEAN || i == LayerUtil.DEEP_LUKEWARM_OCEAN || i == LayerUtil.DEEP_COLD_OCEAN || i == LayerUtil.DEEP_FROZEN_OCEAN) && context.random(3) == 0) {
            l = context.random(2) == 0 ? PLAINS : FOREST;
         }

         if (k == 0 && l != i) {
            Biome biome1 = Biome.getMutationForBiome(IRegistry.BIOME.get(l));
            l = biome1 == null ? i : IRegistry.BIOME.getId(biome1);
         }

         if (l != i) {
            int i1 = 0;
            if (LayerUtil.areBiomesSimilar(area1.getValue(x + 1, z + 0), i)) {
               ++i1;
            }

            if (LayerUtil.areBiomesSimilar(area1.getValue(x + 2, z + 1), i)) {
               ++i1;
            }

            if (LayerUtil.areBiomesSimilar(area1.getValue(x + 0, z + 1), i)) {
               ++i1;
            }

            if (LayerUtil.areBiomesSimilar(area1.getValue(x + 1, z + 2), i)) {
               ++i1;
            }

            if (i1 >= 3) {
               return l;
            }
         }
      }

      return i;
   }
}
