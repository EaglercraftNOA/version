package net.minecraft.enchantment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockPumpkin;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemTrident;

public enum EnumEnchantmentType {
   ALL {
      public boolean canEnchantItem(Item itemIn) {
         for(EnumEnchantmentType enumenchantmenttype : EnumEnchantmentType.values()) {
            if (enumenchantmenttype != EnumEnchantmentType.ALL && enumenchantmenttype.canEnchantItem(itemIn)) {
               return true;
            }
         }

         return false;
      }
   },
   ARMOR {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor;
      }
   },
   ARMOR_FEET {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).getEquipmentSlot() == EntityEquipmentSlot.FEET;
      }
   },
   ARMOR_LEGS {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).getEquipmentSlot() == EntityEquipmentSlot.LEGS;
      }
   },
   ARMOR_CHEST {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).getEquipmentSlot() == EntityEquipmentSlot.CHEST;
      }
   },
   ARMOR_HEAD {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemArmor && ((ItemArmor)itemIn).getEquipmentSlot() == EntityEquipmentSlot.HEAD;
      }
   },
   WEAPON {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemSword;
      }
   },
   DIGGER {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemTool;
      }
   },
   FISHING_ROD {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemFishingRod;
      }
   },
   TRIDENT {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemTrident;
      }
   },
   BREAKABLE {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn.isDamageable();
      }
   },
   BOW {
      public boolean canEnchantItem(Item itemIn) {
         return itemIn instanceof ItemBow;
      }
   },
   WEARABLE {
      public boolean canEnchantItem(Item itemIn) {
         Block block = Block.getBlockFromItem(itemIn);
         return itemIn instanceof ItemArmor || itemIn instanceof ItemElytra || block instanceof BlockAbstractSkull || block instanceof BlockPumpkin;
      }
   };

   private EnumEnchantmentType() {
   }

   public abstract boolean canEnchantItem(Item itemIn);
}
