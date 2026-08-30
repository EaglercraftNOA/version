package net.minecraft.enchantment;

import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentRiptide extends Enchantment {
   public EnchantmentRiptide(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.TRIDENT, slots);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 10 + enchantmentLevel * 7;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return 50;
   }

   public int getMaxLevel() {
      return 3;
   }

   public boolean canApplyTogether(Enchantment ench) {
      return super.canApplyTogether(ench) && ench != Enchantments.LOYALTY && ench != Enchantments.CHANNELING;
   }
}
