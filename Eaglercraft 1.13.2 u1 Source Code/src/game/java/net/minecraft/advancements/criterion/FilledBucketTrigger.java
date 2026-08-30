package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FilledBucketTrigger implements ICriterionTrigger<FilledBucketTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("filled_bucket");
   private final Map<PlayerAdvancements, FilledBucketTrigger.Listeners> listeners = Maps.newHashMap();

   public ResourceLocation getId() {
      return ID;
   }

   public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<FilledBucketTrigger.Instance> listener) {
      FilledBucketTrigger.Listeners filledbuckettrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (filledbuckettrigger$listeners == null) {
         filledbuckettrigger$listeners = new FilledBucketTrigger.Listeners(playerAdvancementsIn);
         this.listeners.put(playerAdvancementsIn, filledbuckettrigger$listeners);
      }

      filledbuckettrigger$listeners.add(listener);
   }

   public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<FilledBucketTrigger.Instance> listener) {
      FilledBucketTrigger.Listeners filledbuckettrigger$listeners = this.listeners.get(playerAdvancementsIn);
      if (filledbuckettrigger$listeners != null) {
         filledbuckettrigger$listeners.remove(listener);
         if (filledbuckettrigger$listeners.isEmpty()) {
            this.listeners.remove(playerAdvancementsIn);
         }
      }

   }

   public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.listeners.remove(playerAdvancementsIn);
   }

   public FilledBucketTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new FilledBucketTrigger.Instance(itempredicate);
   }

   public void trigger(EntityPlayerMP p_204817_1_, ItemStack p_204817_2_) {
      FilledBucketTrigger.Listeners filledbuckettrigger$listeners = this.listeners.get(p_204817_1_.getAdvancements());
      if (filledbuckettrigger$listeners != null) {
         filledbuckettrigger$listeners.trigger(p_204817_2_);
      }

   }

   public static class Instance extends AbstractCriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate item) {
         super(FilledBucketTrigger.ID);
         this.item = item;
      }

      public static FilledBucketTrigger.Instance forItem(ItemPredicate p_204827_0_) {
         return new FilledBucketTrigger.Instance(p_204827_0_);
      }

      public boolean test(ItemStack p_204826_1_) {
         return this.item.test(p_204826_1_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }

   static class Listeners {
      private final PlayerAdvancements field_204856_a;
      private final Set<ICriterionTrigger.Listener<FilledBucketTrigger.Instance>> listeners = Sets.newHashSet();

      public Listeners(PlayerAdvancements p_i48919_1_) {
         this.field_204856_a = p_i48919_1_;
      }

      public boolean isEmpty() {
         return this.listeners.isEmpty();
      }

      public void add(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> p_204852_1_) {
         this.listeners.add(p_204852_1_);
      }

      public void remove(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> p_204855_1_) {
         this.listeners.remove(p_204855_1_);
      }

      public void trigger(ItemStack p_204854_1_) {
         List<ICriterionTrigger.Listener<FilledBucketTrigger.Instance>> list = null;

         for(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> listener : this.listeners) {
            if (listener.getCriterionInstance().test(p_204854_1_)) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<FilledBucketTrigger.Instance> listener1 : list) {
               listener1.grantCriterion(this.field_204856_a);
            }
         }

      }
   }
}
