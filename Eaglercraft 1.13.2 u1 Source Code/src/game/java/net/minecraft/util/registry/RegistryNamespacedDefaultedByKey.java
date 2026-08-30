package net.minecraft.util.registry;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;

public class RegistryNamespacedDefaultedByKey<V> extends RegistryNamespaced<V> {
   private final ResourceLocation defaultValueKey;
   private V defaultValue;

   public RegistryNamespacedDefaultedByKey(ResourceLocation p_i49828_1_) {
      this.defaultValueKey = p_i49828_1_;
   }

   public void register(int id, ResourceLocation key, V value) {
      if (this.defaultValueKey.equals(key)) {
         this.defaultValue = value;
      }

      super.register(id, key, value);
   }

   public int getId(@Nullable V value) {
      int i = super.getId(value);
      return i == -1 ? super.getId(this.defaultValue) : i;
   }

   public ResourceLocation getKey(V value) {
      ResourceLocation resourcelocation = super.getKey(value);
      return resourcelocation == null ? this.defaultValueKey : resourcelocation;
   }

   public V getOrDefault(@Nullable ResourceLocation name) {
      V v = (V)this.get(name);
      return (V)(v == null ? this.defaultValue : v);
   }

   @Nonnull
   public V get(int id) {
      V v = (V)super.get(id);
      return (V)(v == null ? this.defaultValue : v);
   }

   @Nonnull
   public V getRandom(Random random) {
      V v = (V)super.getRandom(random);
      return (V)(v == null ? this.defaultValue : v);
   }

   public ResourceLocation getDefaultKey() {
      return this.defaultValueKey;
   }
}
