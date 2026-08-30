package net.minecraft.item;

public class ItemTiered extends Item {
   private final IItemTier tier;

   public ItemTiered(IItemTier tierIn, Item.Properties builder) {
      super(builder.defaultMaxDamage(tierIn.getMaxUses()));
      this.tier = tierIn;
   }

   public IItemTier getTier() {
      return this.tier;
   }

   public int getItemEnchantability() {
      return this.tier.getEnchantability();
   }

   public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
      return this.tier.getRepairMaterial().test(repair) || super.getIsRepairable(toRepair, repair);
   }
}
