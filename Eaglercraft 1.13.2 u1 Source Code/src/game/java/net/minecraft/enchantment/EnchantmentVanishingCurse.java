package net.minecraft.enchantment;

import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentVanishingCurse extends Enchantment {
   public EnchantmentVanishingCurse(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.ALL, slots);
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

   public boolean isTreasureEnchantment() {
      return true;
   }

   public boolean isCurse() {
      return true;
   }
}
