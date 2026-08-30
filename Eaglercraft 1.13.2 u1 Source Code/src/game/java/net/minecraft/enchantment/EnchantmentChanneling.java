package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentChanneling extends Enchantment {
   public EnchantmentChanneling(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.TRIDENT, slots);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 25;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench);
   }
}
