package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class ItemGroup {
   public static final ItemGroup[] GROUPS = new ItemGroup[12];
   public static final ItemGroup BUILDING_BLOCKS = (new ItemGroup(0, "buildingBlocks") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.BRICKS);
      }
   }).func_199783_b("building_blocks");
   public static final ItemGroup DECORATIONS = new ItemGroup(1, "decorations") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.PEONY);
      }
   };
   public static final ItemGroup REDSTONE = new ItemGroup(2, "redstone") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.REDSTONE);
      }
   };
   public static final ItemGroup TRANSPORTATION = new ItemGroup(3, "transportation") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.POWERED_RAIL);
      }
   };
   public static final ItemGroup MISC = new ItemGroup(6, "misc") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.LAVA_BUCKET);
      }
   };
   public static final ItemGroup SEARCH = (new ItemGroup(5, "search") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.COMPASS);
      }
   }).setBackgroundImageName("item_search.png");
   public static final ItemGroup FOOD = new ItemGroup(7, "food") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.APPLE);
      }
   };
   public static final ItemGroup TOOLS = (new ItemGroup(8, "tools") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.IRON_AXE);
      }
   }).setRelevantEnchantmentTypes(new EnumEnchantmentType[]{EnumEnchantmentType.ALL, EnumEnchantmentType.DIGGER, EnumEnchantmentType.FISHING_ROD, EnumEnchantmentType.BREAKABLE});
   public static final ItemGroup COMBAT = (new ItemGroup(9, "combat") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Items.GOLDEN_SWORD);
      }
   }).setRelevantEnchantmentTypes(new EnumEnchantmentType[]{EnumEnchantmentType.ALL, EnumEnchantmentType.ARMOR, EnumEnchantmentType.ARMOR_FEET, EnumEnchantmentType.ARMOR_HEAD, EnumEnchantmentType.ARMOR_LEGS, EnumEnchantmentType.ARMOR_CHEST, EnumEnchantmentType.BOW, EnumEnchantmentType.WEAPON, EnumEnchantmentType.WEARABLE, EnumEnchantmentType.BREAKABLE, EnumEnchantmentType.TRIDENT});
   public static final ItemGroup BREWING = new ItemGroup(10, "brewing") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return PotionUtils.addPotionToItemStack(new ItemStack(Items.POTION), PotionTypes.WATER);
      }
   };
   public static final ItemGroup MATERIALS = MISC;
   public static final ItemGroup HOTBAR = new ItemGroup(4, "hotbar") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.BOOKSHELF);
      }

      @OnlyIn(Dist.CLIENT)
      public void fill(NonNullList<ItemStack> items) {
         throw new RuntimeException("Implement exception client-side.");
      }

      @OnlyIn(Dist.CLIENT)
      public boolean isAlignedRight() {
         return true;
      }
   };
   public static final ItemGroup INVENTORY = (new ItemGroup(11, "inventory") {
      @OnlyIn(Dist.CLIENT)
      public ItemStack createIcon() {
         return new ItemStack(Blocks.CHEST);
      }
   }).setBackgroundImageName("inventory.png").setNoScrollbar().setNoTitle();
   private final int index;
   private final String tabLabel;
   private String field_199784_q;
   private String backgroundTexture = "items.png";
   private boolean hasScrollbar = true;
   private boolean drawTitle = true;
   private EnumEnchantmentType[] enchantmentTypes = new EnumEnchantmentType[0];
   private ItemStack icon;

   public ItemGroup(int index, String label) {
      this.index = index;
      this.tabLabel = label;
      this.icon = ItemStack.EMPTY;
      GROUPS[index] = this;
   }

   @OnlyIn(Dist.CLIENT)
   public int getIndex() {
      return this.index;
   }

   @OnlyIn(Dist.CLIENT)
   public String getTabLabel() {
      return this.tabLabel;
   }

   public String func_200300_c() {
      return this.field_199784_q == null ? this.tabLabel : this.field_199784_q;
   }

   @OnlyIn(Dist.CLIENT)
   public String getTranslationKey() {
      return "itemGroup." + this.getTabLabel();
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getIcon() {
      if (this.icon.isEmpty()) {
         this.icon = this.createIcon();
      }

      return this.icon;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract ItemStack createIcon();

   @OnlyIn(Dist.CLIENT)
   public String getBackgroundImageName() {
      return this.backgroundTexture;
   }

   public ItemGroup setBackgroundImageName(String texture) {
      this.backgroundTexture = texture;
      return this;
   }

   public ItemGroup func_199783_b(String p_199783_1_) {
      this.field_199784_q = p_199783_1_;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean drawInForegroundOfTab() {
      return this.drawTitle;
   }

   public ItemGroup setNoTitle() {
      this.drawTitle = false;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasScrollbar() {
      return this.hasScrollbar;
   }

   public ItemGroup setNoScrollbar() {
      this.hasScrollbar = false;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public int getColumn() {
      return this.index % 6;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isOnTopRow() {
      return this.index < 6;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isAlignedRight() {
      return this.getColumn() == 5;
   }

   public EnumEnchantmentType[] getRelevantEnchantmentTypes() {
      return this.enchantmentTypes;
   }

   public ItemGroup setRelevantEnchantmentTypes(EnumEnchantmentType... types) {
      this.enchantmentTypes = types;
      return this;
   }

   public boolean hasRelevantEnchantmentType(@Nullable EnumEnchantmentType enchantmentType) {
      if (enchantmentType != null) {
         for(EnumEnchantmentType enumenchantmenttype : this.enchantmentTypes) {
            if (enumenchantmenttype == enchantmentType) {
               return true;
            }
         }
      }

      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public void fill(NonNullList<ItemStack> items) {
      for(Item item : IRegistry.ITEM) {
         item.fillItemGroup(this, items);
      }

   }
}
