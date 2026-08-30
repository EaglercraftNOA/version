package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentLoyalty extends Enchantment {
   public EnchantmentLoyalty(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.TRIDENT, slots);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 5 + enchantmentLevel * 7;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench);
   }
}
