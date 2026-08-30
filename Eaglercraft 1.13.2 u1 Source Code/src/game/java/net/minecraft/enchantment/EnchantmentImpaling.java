package net.minecraft.enchantment;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;

public class EnchantmentImpaling extends Enchantment {
   public EnchantmentImpaling(Enchantment.Rarity rarityIn, EntityEquipmentSlot... slots) {
      super(rarityIn, EnumEnchantmentType.TRIDENT, slots);
   }

   public int getMinEnchantability(int enchantmentLevel) {
      return 1 + (enchantmentLevel - 1) * 8;
   }

   public int getMaxEnchantability(int enchantmentLevel) {
      return this.getMinEnchantability(enchantmentLevel) + 20;
   }

   public int getMaxLevel() {
      return 5;
   }

   public float calcDamageByCreature(int level, CreatureAttribute creatureType) {
      return creatureType == CreatureAttribute.WATER ? (float)level * 2.5F : 0.0F;
   }
}
