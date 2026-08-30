package net.minecraft.world.gen.layer;

import net.minecraft.init.Biomes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.WorldType;
import net.minecraft.world.gen.IContext;
import net.minecraft.world.gen.OverworldGenSettings;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public class GenLayerBiome implements IC0Transformer {
   private static final int BIRCH_FOREST = IRegistry.BIOME.getId(Biomes.BIRCH_FOREST);
   private static final int DESERT = IRegistry.BIOME.getId(Biomes.DESERT);
   private static final int MOUNTAINS = IRegistry.BIOME.getId(Biomes.MOUNTAINS);
   private static final int FOREST = IRegistry.BIOME.getId(Biomes.FOREST);
   private static final int SNOWY_TUNDRA = IRegistry.BIOME.getId(Biomes.SNOWY_TUNDRA);
   private static final int JUNGLE = IRegistry.BIOME.getId(Biomes.JUNGLE);
   private static final int BADLANDS_PLATEAU = IRegistry.BIOME.getId(Biomes.BADLANDS_PLATEAU);
   private static final int WOODED_BADLANDS_PLATEAU = IRegistry.BIOME.getId(Biomes.WOODED_BADLANDS_PLATEAU);
   private static final int MUSHROOM_FIELDS = IRegistry.BIOME.getId(Biomes.MUSHROOM_FIELDS);
   private static final int PLAINS = IRegistry.BIOME.getId(Biomes.PLAINS);
   private static final int GIANT_TREE_TAIGA = IRegistry.BIOME.getId(Biomes.GIANT_TREE_TAIGA);
   private static final int DARK_FOREST = IRegistry.BIOME.getId(Biomes.DARK_FOREST);
   private static final int SAVANNA = IRegistry.BIOME.getId(Biomes.SAVANNA);
   private static final int SWAMP = IRegistry.BIOME.getId(Biomes.SWAMP);
   private static final int TAIGA = IRegistry.BIOME.getId(Biomes.TAIGA);
   private static final int SNOWY_TAIGA = IRegistry.BIOME.getId(Biomes.SNOWY_TAIGA);
   private static final int[] field_202743_q = new int[]{DESERT, FOREST, MOUNTAINS, SWAMP, PLAINS, TAIGA};
   private static final int[] field_202744_r = new int[]{DESERT, DESERT, DESERT, SAVANNA, SAVANNA, PLAINS};
   private static final int[] field_202745_s = new int[]{FOREST, DARK_FOREST, MOUNTAINS, PLAINS, BIRCH_FOREST, SWAMP};
   private static final int[] field_202746_t = new int[]{FOREST, MOUNTAINS, TAIGA, PLAINS};
   private static final int[] field_202747_u = new int[]{SNOWY_TUNDRA, SNOWY_TUNDRA, SNOWY_TUNDRA, SNOWY_TAIGA};
   private final OverworldGenSettings settings;
   private int[] warmBiomes = field_202744_r;

   public GenLayerBiome(WorldType p_i48641_1_, OverworldGenSettings p_i48641_2_) {
      if (p_i48641_1_ == WorldType.DEFAULT_1_1) {
         this.warmBiomes = field_202743_q;
         this.settings = null;
      } else {
         this.settings = p_i48641_2_;
      }

   }

   public int apply(IContext context, int value) {
      if (this.settings != null && this.settings.func_202199_l() >= 0) {
         return this.settings.func_202199_l();
      } else {
         int i = (value & 3840) >> 8;
         value = value & -3841;
         if (!LayerUtil.isOcean(value) && value != MUSHROOM_FIELDS) {
            switch(value) {
            case 1:
               if (i > 0) {
                  return context.random(3) == 0 ? BADLANDS_PLATEAU : WOODED_BADLANDS_PLATEAU;
               }

               return this.warmBiomes[context.random(this.warmBiomes.length)];
            case 2:
               if (i > 0) {
                  return JUNGLE;
               }

               return field_202745_s[context.random(field_202745_s.length)];
            case 3:
               if (i > 0) {
                  return GIANT_TREE_TAIGA;
               }

               return field_202746_t[context.random(field_202746_t.length)];
            case 4:
               return field_202747_u[context.random(field_202747_u.length)];
            default:
               return MUSHROOM_FIELDS;
            }
         } else {
            return value;
         }
      }
   }
}
