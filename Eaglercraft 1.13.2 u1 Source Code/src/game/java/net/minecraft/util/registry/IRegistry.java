package net.minecraft.util.registry;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.PaintingType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.stats.StatType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.IObjectIntIterable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGeneratorType;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface IRegistry<T> extends IObjectIntIterable<T> {
   Logger LOGGER = LogManager.getLogger();
   IRegistry<IRegistry<?>> REGISTRY = new RegistryNamespaced<>();
   IRegistry<Block> BLOCK = register("block", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("air")));
   IRegistry<Fluid> FLUID = register("fluid", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("empty")));
   IRegistry<PaintingType> MOTIVE = register("motive", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("kebab")));
   IRegistry<PotionType> POTION = register("potion", new RegistryNamespacedDefaultedByKey<>(new ResourceLocation("empty")));
   IRegistry<DimensionType> DIMENSION_TYPE = register("dimension_type", new RegistryNamespaced<>());
   IRegistry<ResourceLocation> CUSTOM_STAT = register("custom_stat", new RegistryNamespaced<>());
   IRegistry<Biome> BIOME = register("biome", new RegistryNamespaced<>());
   IRegistry<BiomeProviderType<?, ?>> BIOME_SOURCE_TYPE = register("biome_source_type", new RegistryNamespaced<>());
   IRegistry<TileEntityType<?>> BLOCK_ENTITY_TYPE = register("block_entity_type", new RegistryNamespaced<>());
   IRegistry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = register("chunk_generator_type", new RegistryNamespaced<>());
   IRegistry<Enchantment> ENCHANTMENT = register("enchantment", new RegistryNamespaced<>());
   IRegistry<EntityType<?>> ENTITY_TYPE = register("entity_type", new RegistryNamespaced<>());
   IRegistry<Item> ITEM = register("item", new RegistryNamespaced<>());
   IRegistry<Potion> MOB_EFFECT = register("mob_effect", new RegistryNamespaced<>());
   IRegistry<ParticleType<? extends IParticleData>> PARTICLE_TYPE = register("particle_type", new RegistryNamespaced<>());
   IRegistry<SoundEvent> SOUND_EVENT = register("sound_event", new RegistryNamespaced<>());
   IRegistry<StatType<?>> STATS = register("stats", new RegistryNamespaced<>());

   static <T> IRegistry<T> register(String id, IRegistry<T> registry) {
      REGISTRY.put(new ResourceLocation(id), registry);
      return registry;
   }

   static void boot() {
      REGISTRY.forEach((p_212606_0_) -> {
         if (p_212606_0_.isEmpty()) {
            LOGGER.error("Registry '{}' was empty after loading", (Object)REGISTRY.getKey(p_212606_0_));
            if (SharedConstants.developmentMode) {
               throw new IllegalStateException("Registry: '" + REGISTRY.getKey(p_212606_0_) + "' is empty, not allowed, fix me!");
            }
         }

         if (p_212606_0_ instanceof RegistryNamespacedDefaultedByKey) {
            ResourceLocation resourcelocation = p_212606_0_.getDefaultKey();
            Validate.notNull(p_212606_0_.get(resourcelocation), "Missing default of DefaultedMappedRegistry: " + resourcelocation);
         }

      });
   }

   @Nullable
   ResourceLocation getKey(T value);

   T getOrDefault(@Nullable ResourceLocation name);

   ResourceLocation getDefaultKey();

   int getId(@Nullable T value);

   @Nullable
   T get(int id);

   Iterator<T> iterator();

   @Nullable
   T get(@Nullable ResourceLocation name);

   void register(int id, ResourceLocation key, T value);

   void put(ResourceLocation key, T value);

   Set<ResourceLocation> keySet();

   boolean isEmpty();

   @Nullable
   T getRandom(Random random);

   default Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   boolean containsKey(ResourceLocation name);
}
