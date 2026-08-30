package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemFishFood extends ItemFood {
   private final boolean cooked;
   private final ItemFishFood.FishType type;

   public ItemFishFood(ItemFishFood.FishType fishTypeIn, boolean isCooked, Item.Properties builder) {
      super(0, 0.0F, false, builder);
      this.type = fishTypeIn;
      this.cooked = isCooked;
   }

   public int getHealAmount(ItemStack stack) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
      return this.cooked && itemfishfood$fishtype.canCook() ? itemfishfood$fishtype.getCookedHealAmount() : itemfishfood$fishtype.getUncookedHealAmount();
   }

   public float getSaturationModifier(ItemStack stack) {
      return this.cooked && this.type.canCook() ? this.type.getCookedSaturationModifier() : this.type.getUncookedSaturationModifier();
   }

   protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
      ItemFishFood.FishType itemfishfood$fishtype = ItemFishFood.FishType.byItemStack(stack);
      if (itemfishfood$fishtype == ItemFishFood.FishType.PUFFERFISH) {
         player.addPotionEffect(new PotionEffect(MobEffects.POISON, 1200, 3));
         player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 300, 2));
         player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 300, 1));
      }

      super.onFoodEaten(stack, worldIn, player);
   }

   public static enum FishType {
      COD(2, 0.1F, 5, 0.6F),
      SALMON(2, 0.1F, 6, 0.8F),
      TROPICAL_FISH(1, 0.1F),
      PUFFERFISH(1, 0.1F);

      private final int uncookedHealAmount;
      private final float uncookedSaturationModifier;
      private final int cookedHealAmount;
      private final float cookedSaturationModifier;
      private final boolean cookable;

      private FishType(int p_i49622_3_, float p_i49622_4_, int p_i49622_5_, float p_i49622_6_) {
         this.uncookedHealAmount = p_i49622_3_;
         this.uncookedSaturationModifier = p_i49622_4_;
         this.cookedHealAmount = p_i49622_5_;
         this.cookedSaturationModifier = p_i49622_6_;
         this.cookable = p_i49622_5_ != 0;
      }

      private FishType(int p_i49623_3_, float p_i49623_4_) {
         this(p_i49623_3_, p_i49623_4_, 0, 0.0F);
      }

      public int getUncookedHealAmount() {
         return this.uncookedHealAmount;
      }

      public float getUncookedSaturationModifier() {
         return this.uncookedSaturationModifier;
      }

      public int getCookedHealAmount() {
         return this.cookedHealAmount;
      }

      public float getCookedSaturationModifier() {
         return this.cookedSaturationModifier;
      }

      public boolean canCook() {
         return this.cookable;
      }

      public static ItemFishFood.FishType byItemStack(ItemStack stack) {
         Item item = stack.getItem();
         return item instanceof ItemFishFood ? ((ItemFishFood)item).type : COD;
      }
   }
}
