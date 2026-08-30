package net.minecraft.util.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryNamespaced<V> implements IRegistry<V> {
   protected static final Logger LOGGER0 = LogManager.getLogger();
   protected final IntIdentityHashBiMap<V> underlyingIntegerMap = new IntIdentityHashBiMap<>(256);
   protected final BiMap<ResourceLocation, V> registryObjects = HashBiMap.create();
   protected Object[] values;
   private int nextFreeId;

   public void register(int id, ResourceLocation key, V value) {
      this.underlyingIntegerMap.put(value, id);
      Validate.notNull(key);
      Validate.notNull(value);
      this.values = null;
      if (this.registryObjects.containsKey(key)) {
         LOGGER0.debug("Adding duplicate key '{}' to registry", (Object)key);
      }

      this.registryObjects.put(key, value);
      if (this.nextFreeId <= id) {
         this.nextFreeId = id + 1;
      }

   }

   public void put(ResourceLocation key, V value) {
      this.register(this.nextFreeId, key, value);
   }

   @Nullable
   public ResourceLocation getKey(V value) {
      return this.registryObjects.inverse().get(value);
   }

   public V getOrDefault(@Nullable ResourceLocation name) {
      throw new UnsupportedOperationException("No default value");
   }

   public ResourceLocation getDefaultKey() {
      throw new UnsupportedOperationException("No default key");
   }

   public int getId(@Nullable V value) {
      return this.underlyingIntegerMap.getId(value);
   }

   @Nullable
   public V get(int id) {
      return this.underlyingIntegerMap.get(id);
   }

   public Iterator<V> iterator() {
      return this.underlyingIntegerMap.iterator();
   }

   @Nullable
   public V get(@Nullable ResourceLocation name) {
      return this.registryObjects.get(name);
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.registryObjects.keySet());
   }

   public boolean isEmpty() {
      return this.registryObjects.isEmpty();
   }

   @Nullable
   public V getRandom(Random random) {
      if (this.values == null) {
         Collection<?> collection = this.registryObjects.values();
         if (collection.isEmpty()) {
            return (V)null;
         }

         this.values = collection.toArray(new Object[collection.size()]);
      }

      return (V)this.values[random.nextInt(this.values.length)];
   }

   public boolean containsKey(ResourceLocation name) {
      return this.registryObjects.containsKey(name);
   }
}
