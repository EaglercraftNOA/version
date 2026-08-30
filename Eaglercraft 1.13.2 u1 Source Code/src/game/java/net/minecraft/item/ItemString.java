package net.minecraft.item;

import net.minecraft.init.Blocks;

public class ItemString extends ItemBlock {
   public ItemString(Item.Properties builder) {
      super(Blocks.TRIPWIRE, builder);
   }

   public String getTranslationKey() {
      return this.getDefaultTranslationKey();
   }
}
