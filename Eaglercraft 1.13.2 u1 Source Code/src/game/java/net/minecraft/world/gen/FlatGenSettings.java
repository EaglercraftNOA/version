package net.minecraft.world.gen;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.LakesConfig;
import net.minecraft.world.gen.feature.structure.DesertPyramidConfig;
import net.minecraft.world.gen.feature.structure.EndCityConfig;
import net.minecraft.world.gen.feature.structure.FortressConfig;
import net.minecraft.world.gen.feature.structure.IglooConfig;
import net.minecraft.world.gen.feature.structure.JunglePyramidConfig;
import net.minecraft.world.gen.feature.structure.MineshaftConfig;
import net.minecraft.world.gen.feature.structure.MineshaftStructure;
import net.minecraft.world.gen.feature.structure.OceanMonumentConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinConfig;
import net.minecraft.world.gen.feature.structure.OceanRuinStructure;
import net.minecraft.world.gen.feature.structure.ShipwreckConfig;
import net.minecraft.world.gen.feature.structure.StrongholdConfig;
import net.minecraft.world.gen.feature.structure.SwampHutConfig;
import net.minecraft.world.gen.feature.structure.VillageConfig;
import net.minecraft.world.gen.feature.structure.VillagePieces;
import net.minecraft.world.gen.feature.structure.WoodlandMansionConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.LakeChanceConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlatGenSettings extends ChunkGenSettings {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final CompositeFeature<MineshaftConfig, NoPlacementConfig> MINESHAFT = Biome.createCompositeFeature(Feature.MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<VillageConfig, NoPlacementConfig> VILLAGE = Biome.createCompositeFeature(Feature.VILLAGE, new VillageConfig(0, VillagePieces.Type.OAK), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<StrongholdConfig, NoPlacementConfig> STRONGHOLD = Biome.createCompositeFeature(Feature.STRONGHOLD, new StrongholdConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<SwampHutConfig, NoPlacementConfig> SWAMP_HUT = Biome.createCompositeFeature(Feature.SWAMP_HUT, new SwampHutConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<DesertPyramidConfig, NoPlacementConfig> DESERT_PYRAMID = Biome.createCompositeFeature(Feature.DESERT_PYRAMID, new DesertPyramidConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<JunglePyramidConfig, NoPlacementConfig> JUNGLE_PYRAMID = Biome.createCompositeFeature(Feature.JUNGLE_PYRAMID, new JunglePyramidConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<IglooConfig, NoPlacementConfig> IGLOO = Biome.createCompositeFeature(Feature.IGLOO, new IglooConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<ShipwreckConfig, NoPlacementConfig> SHIPWRECK = Biome.createCompositeFeature(Feature.SHIPWRECK, new ShipwreckConfig(false), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<OceanMonumentConfig, NoPlacementConfig> OCEAN_MONUMENT = Biome.createCompositeFeature(Feature.OCEAN_MONUMENT, new OceanMonumentConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<LakesConfig, LakeChanceConfig> LAKE_WATER = Biome.createCompositeFeature(Feature.LAKES, new LakesConfig(Blocks.WATER), Biome.LAKE_WATER, new LakeChanceConfig(4));
   private static final CompositeFeature<LakesConfig, LakeChanceConfig> LAKE_LAVA = Biome.createCompositeFeature(Feature.LAKES, new LakesConfig(Blocks.LAVA), Biome.LAVA_LAKE, new LakeChanceConfig(80));
   private static final CompositeFeature<EndCityConfig, NoPlacementConfig> END_CITY = Biome.createCompositeFeature(Feature.END_CITY, new EndCityConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<WoodlandMansionConfig, NoPlacementConfig> WOODLAND_MANSION = Biome.createCompositeFeature(Feature.WOODLAND_MANSION, new WoodlandMansionConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<FortressConfig, NoPlacementConfig> FORTRESS = Biome.createCompositeFeature(Feature.FORTRESS, new FortressConfig(), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   private static final CompositeFeature<OceanRuinConfig, NoPlacementConfig> OCEAN_RUIN = Biome.createCompositeFeature(Feature.OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.1F), Biome.PASSTHROUGH, IPlacementConfig.NO_PLACEMENT_CONFIG);
   public static final Map<CompositeFeature<?, ?>, GenerationStage.Decoration> FEATURE_STAGES = Util.make(Maps.newHashMap(), (p_209406_0_) -> {
      p_209406_0_.put(MINESHAFT, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(VILLAGE, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(STRONGHOLD, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(SWAMP_HUT, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(DESERT_PYRAMID, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(JUNGLE_PYRAMID, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(IGLOO, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(SHIPWRECK, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(OCEAN_RUIN, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(LAKE_WATER, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
      p_209406_0_.put(LAKE_LAVA, GenerationStage.Decoration.LOCAL_MODIFICATIONS);
      p_209406_0_.put(END_CITY, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(WOODLAND_MANSION, GenerationStage.Decoration.SURFACE_STRUCTURES);
      p_209406_0_.put(FORTRESS, GenerationStage.Decoration.UNDERGROUND_STRUCTURES);
      p_209406_0_.put(OCEAN_MONUMENT, GenerationStage.Decoration.SURFACE_STRUCTURES);
   });
   public static final Map<String, CompositeFeature<?, ?>[]> STRUCTURES = Util.make(Maps.newHashMap(), (p_209404_0_) -> {
      p_209404_0_.put("mineshaft", new CompositeFeature[]{MINESHAFT});
      p_209404_0_.put("village", new CompositeFeature[]{VILLAGE});
      p_209404_0_.put("stronghold", new CompositeFeature[]{STRONGHOLD});
      p_209404_0_.put("biome_1", new CompositeFeature[]{SWAMP_HUT, DESERT_PYRAMID, JUNGLE_PYRAMID, IGLOO, OCEAN_RUIN, SHIPWRECK});
      p_209404_0_.put("oceanmonument", new CompositeFeature[]{OCEAN_MONUMENT});
      p_209404_0_.put("lake", new CompositeFeature[]{LAKE_WATER});
      p_209404_0_.put("lava_lake", new CompositeFeature[]{LAKE_LAVA});
      p_209404_0_.put("endcity", new CompositeFeature[]{END_CITY});
      p_209404_0_.put("mansion", new CompositeFeature[]{WOODLAND_MANSION});
      p_209404_0_.put("fortress", new CompositeFeature[]{FORTRESS});
   });
   public static final Map<CompositeFeature<?, ?>, IFeatureConfig> FEATURE_CONFIGS = Util.make(Maps.newHashMap(), (p_209405_0_) -> {
      p_209405_0_.put(MINESHAFT, new MineshaftConfig(0.004D, MineshaftStructure.Type.NORMAL));
      p_209405_0_.put(VILLAGE, new VillageConfig(0, VillagePieces.Type.OAK));
      p_209405_0_.put(STRONGHOLD, new StrongholdConfig());
      p_209405_0_.put(SWAMP_HUT, new SwampHutConfig());
      p_209405_0_.put(DESERT_PYRAMID, new DesertPyramidConfig());
      p_209405_0_.put(JUNGLE_PYRAMID, new JunglePyramidConfig());
      p_209405_0_.put(IGLOO, new IglooConfig());
      p_209405_0_.put(OCEAN_RUIN, new OceanRuinConfig(OceanRuinStructure.Type.COLD, 0.3F, 0.9F));
      p_209405_0_.put(SHIPWRECK, new ShipwreckConfig(false));
      p_209405_0_.put(OCEAN_MONUMENT, new OceanMonumentConfig());
      p_209405_0_.put(END_CITY, new EndCityConfig());
      p_209405_0_.put(WOODLAND_MANSION, new WoodlandMansionConfig());
      p_209405_0_.put(FORTRESS, new FortressConfig());
   });
   private final List<FlatLayerInfo> flatLayers = Lists.newArrayList();
   private final Map<String, Map<String, String>> worldFeatures = Maps.newHashMap();
   private Biome biomeToUse;
   private final IBlockState[] states = new IBlockState[256];
   private boolean allAir;
   private int field_202246_E;

   @Nullable
   public static Block getBlock(String p_212683_0_) {
      try {
         ResourceLocation resourcelocation = new ResourceLocation(p_212683_0_);
         if (IRegistry.BLOCK.containsKey(resourcelocation)) {
            return IRegistry.BLOCK.getOrDefault(resourcelocation);
         }
      } catch (IllegalArgumentException illegalargumentexception) {
         LOGGER.warn("Invalid blockstate: {}", p_212683_0_, illegalargumentexception);
      }

      return null;
   }

   public Biome getBiome() {
      return this.biomeToUse;
   }

   public void setBiome(Biome biome) {
      this.biomeToUse = biome;
   }

   public Map<String, Map<String, String>> getWorldFeatures() {
      return this.worldFeatures;
   }

   public List<FlatLayerInfo> getFlatLayers() {
      return this.flatLayers;
   }

   public void updateLayers() {
      int i = 0;

      for(FlatLayerInfo flatlayerinfo : this.flatLayers) {
         flatlayerinfo.setMinY(i);
         i += flatlayerinfo.getLayerCount();
      }

      this.field_202246_E = 0;
      this.allAir = true;
      i = 0;

      for(FlatLayerInfo flatlayerinfo1 : this.flatLayers) {
         for(int j = flatlayerinfo1.getMinY(); j < flatlayerinfo1.getMinY() + flatlayerinfo1.getLayerCount(); ++j) {
            IBlockState iblockstate = flatlayerinfo1.getLayerMaterial();
            if (iblockstate.getBlock() != Blocks.AIR) {
               this.allAir = false;
               this.states[j] = iblockstate;
            }
         }

         if (flatlayerinfo1.getLayerMaterial().getBlock() == Blocks.AIR) {
            i += flatlayerinfo1.getLayerCount();
         } else {
            this.field_202246_E += flatlayerinfo1.getLayerCount() + i;
            i = 0;
         }
      }

   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < this.flatLayers.size(); ++i) {
         if (i > 0) {
            stringbuilder.append(",");
         }

         stringbuilder.append(this.flatLayers.get(i));
      }

      stringbuilder.append(";");
      stringbuilder.append((Object)IRegistry.BIOME.getKey(this.biomeToUse));
      stringbuilder.append(";");
      if (!this.worldFeatures.isEmpty()) {
         int k = 0;

         for(Entry<String, Map<String, String>> entry : this.worldFeatures.entrySet()) {
            if (k++ > 0) {
               stringbuilder.append(",");
            }

            stringbuilder.append(entry.getKey().toLowerCase(Locale.ROOT));
            Map<String, String> map = entry.getValue();
            if (!map.isEmpty()) {
               stringbuilder.append("(");
               int j = 0;

               for(Entry<String, String> entry1 : map.entrySet()) {
                  if (j++ > 0) {
                     stringbuilder.append(" ");
                  }

                  stringbuilder.append(entry1.getKey());
                  stringbuilder.append("=");
                  stringbuilder.append(entry1.getValue());
               }

               stringbuilder.append(")");
            }
         }
      }

      return stringbuilder.toString();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   private static FlatLayerInfo deserializeLayer(String p_197526_0_, int p_197526_1_) {
      String[] astring = p_197526_0_.split("\\*", 2);
      int i;
      if (astring.length == 2) {
         try {
            i = MathHelper.clamp(Integer.parseInt(astring[0]), 0, 256 - p_197526_1_);
         } catch (NumberFormatException numberformatexception) {
            LOGGER.error("Error while parsing flat world string => {}", (Object)numberformatexception.getMessage());
            return null;
         }
      } else {
         i = 1;
      }

      Block block;
      try {
         block = getBlock(astring[astring.length - 1]);
      } catch (Exception exception) {
         LOGGER.error("Error while parsing flat world string => {}", (Object)exception.getMessage());
         return null;
      }

      if (block == null) {
         LOGGER.error("Error while parsing flat world string => Unknown block, {}", (Object)astring[astring.length - 1]);
         return null;
      } else {
         FlatLayerInfo flatlayerinfo = new FlatLayerInfo(i, block);
         flatlayerinfo.setMinY(p_197526_1_);
         return flatlayerinfo;
      }
   }

   @OnlyIn(Dist.CLIENT)
   private static List<FlatLayerInfo> deserializeLayers(String p_197527_0_) {
      List<FlatLayerInfo> list = Lists.newArrayList();
      String[] astring = p_197527_0_.split(",");
      int i = 0;

      for(String s : astring) {
         FlatLayerInfo flatlayerinfo = deserializeLayer(s, i);
         if (flatlayerinfo == null) {
            return Collections.emptyList();
         }

         list.add(flatlayerinfo);
         i += flatlayerinfo.getLayerCount();
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public <T> Dynamic<T> func_210834_a(DynamicOps<T> p_210834_1_) {
      T t = p_210834_1_.createList(this.flatLayers.stream().map((p_210837_1_) -> {
         return p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("height"), p_210834_1_.createInt(p_210837_1_.getLayerCount()), p_210834_1_.createString("block"), p_210834_1_.createString(IRegistry.BLOCK.getKey(p_210837_1_.getLayerMaterial().getBlock()).toString())));
      }));
      T t1 = p_210834_1_.createMap(this.worldFeatures.entrySet().stream().map((p_210833_1_) -> {
         return Pair.of(p_210834_1_.createString(p_210833_1_.getKey().toLowerCase(Locale.ROOT)), p_210834_1_.createMap(p_210833_1_.getValue().entrySet().stream().map((p_210836_1_) -> {
            return Pair.of(p_210834_1_.createString(p_210836_1_.getKey()), p_210834_1_.createString(p_210836_1_.getValue()));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond))));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return new Dynamic<>(p_210834_1_, p_210834_1_.createMap(ImmutableMap.of(p_210834_1_.createString("layers"), t, p_210834_1_.createString("biome"), p_210834_1_.createString(IRegistry.BIOME.getKey(this.biomeToUse).toString()), p_210834_1_.createString("structures"), t1)));
   }

   public static FlatGenSettings createFlatGenerator(Dynamic<?> settings) {
      FlatGenSettings flatgensettings = ChunkGeneratorType.FLAT.createSettings();
      List<Pair<Integer, Block>> list = settings.get("layers").flatMap(Dynamic::getStream).orElse(Stream.empty()).map((p_210838_0_) -> {
         return Pair.of(p_210838_0_.getInt("height", 1), getBlock(p_210838_0_.getString("block")));
      }).collect(Collectors.toList());
      if (list.stream().anyMatch((p_211743_0_) -> {
         return p_211743_0_.getSecond() == null;
      })) {
         return getDefaultFlatGenerator();
      } else {
         List<FlatLayerInfo> list1 = list.stream().map((p_211740_0_) -> {
            return new FlatLayerInfo(p_211740_0_.getFirst(), p_211740_0_.getSecond());
         }).collect(Collectors.toList());
         if (list1.isEmpty()) {
            return getDefaultFlatGenerator();
         } else {
            flatgensettings.getFlatLayers().addAll(list1);
            flatgensettings.updateLayers();
            flatgensettings.setBiome(IRegistry.BIOME.get(new ResourceLocation(settings.getString("biome"))));
            settings.get("structures").flatMap((p_210976_0_) -> {
               return p_210976_0_.getMapValues().result();
            }).ifPresent((p_211738_1_) -> {
               p_211738_1_.keySet().forEach((p_211739_1_) -> {
                  p_211739_1_.getStringValue().map((p_211742_1_) -> {
                     return flatgensettings.getWorldFeatures().put(p_211742_1_, Maps.newHashMap());
                  });
               });
            });
            return flatgensettings;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static FlatGenSettings createFlatGeneratorFromString(String flatGeneratorSettings) {
      Iterator<String> iterator = Splitter.on(';').split(flatGeneratorSettings).iterator();
      if (!iterator.hasNext()) {
         return getDefaultFlatGenerator();
      } else {
         FlatGenSettings flatgensettings = ChunkGeneratorType.FLAT.createSettings();
         List<FlatLayerInfo> list = deserializeLayers(iterator.next());
         if (list.isEmpty()) {
            return getDefaultFlatGenerator();
         } else {
            flatgensettings.getFlatLayers().addAll(list);
            flatgensettings.updateLayers();
            Biome biome = iterator.hasNext() ? IRegistry.BIOME.get(new ResourceLocation(iterator.next())) : null;
            flatgensettings.setBiome(biome == null ? Biomes.PLAINS : biome);
            if (iterator.hasNext()) {
               String[] astring = iterator.next().toLowerCase(Locale.ROOT).split(",");

               for(String s : astring) {
                  String[] astring1 = s.split("\\(", 2);
                  if (!astring1[0].isEmpty()) {
                     flatgensettings.addStructure(astring1[0]);
                     if (astring1.length > 1 && astring1[1].endsWith(")") && astring1[1].length() > 1) {
                        String[] astring2 = astring1[1].substring(0, astring1[1].length() - 1).split(" ");

                        for(String s1 : astring2) {
                           String[] astring3 = s1.split("=", 2);
                           if (astring3.length == 2) {
                              flatgensettings.setStructureOption(astring1[0], astring3[0], astring3[1]);
                           }
                        }
                     }
                  }
               }
            } else {
               flatgensettings.getWorldFeatures().put("village", Maps.newHashMap());
            }

            return flatgensettings;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void addStructure(String structureIn) {
      Map<String, String> map = Maps.newHashMap();
      this.worldFeatures.put(structureIn, map);
   }

   @OnlyIn(Dist.CLIENT)
   private void setStructureOption(String structureIn, String key, String value) {
      this.worldFeatures.get(structureIn).put(key, value);
      if ("village".equals(structureIn) && "distance".equals(key)) {
         this.villageDistance = MathHelper.getInt(value, this.villageDistance, 9);
      }

      if ("biome_1".equals(structureIn) && "distance".equals(key)) {
         this.biomeFeatureDistance = MathHelper.getInt(value, this.biomeFeatureDistance, 9);
      }

      if ("stronghold".equals(structureIn)) {
         if ("distance".equals(key)) {
            this.strongholdDistance = MathHelper.getInt(value, this.strongholdDistance, 1);
         } else if ("count".equals(key)) {
            this.strongholdCount = MathHelper.getInt(value, this.strongholdCount, 1);
         } else if ("spread".equals(key)) {
            this.strongholdSpread = MathHelper.getInt(value, this.strongholdSpread, 1);
         }
      }

      if ("oceanmonument".equals(structureIn)) {
         if ("separation".equals(key)) {
            this.oceanMonumentSeparation = MathHelper.getInt(value, this.oceanMonumentSeparation, 1);
         } else if ("spacing".equals(key)) {
            this.oceanMonumentSpacing = MathHelper.getInt(value, this.oceanMonumentSpacing, 1);
         }
      }

      if ("endcity".equals(structureIn) && "distance".equals(key)) {
         this.endCityDistance = MathHelper.getInt(value, this.endCityDistance, 1);
      }

      if ("mansion".equals(structureIn) && "distance".equals(key)) {
         this.mansionDistance = MathHelper.getInt(value, this.mansionDistance, 1);
      }

   }

   public static FlatGenSettings getDefaultFlatGenerator() {
      FlatGenSettings flatgensettings = ChunkGeneratorType.FLAT.createSettings();
      flatgensettings.setBiome(Biomes.PLAINS);
      flatgensettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.BEDROCK));
      flatgensettings.getFlatLayers().add(new FlatLayerInfo(2, Blocks.DIRT));
      flatgensettings.getFlatLayers().add(new FlatLayerInfo(1, Blocks.GRASS_BLOCK));
      flatgensettings.updateLayers();
      flatgensettings.getWorldFeatures().put("village", Maps.newHashMap());
      return flatgensettings;
   }

   public boolean isAllAir() {
      return this.allAir;
   }

   public IBlockState[] getStates() {
      return this.states;
   }
}
