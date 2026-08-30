package net.minecraft.village;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MerchantRecipe {
   private ItemStack itemToBuy = ItemStack.EMPTY;
   private ItemStack secondItemToBuy = ItemStack.EMPTY;
   private ItemStack itemToSell = ItemStack.EMPTY;
   private int toolUses;
   private int maxTradeUses;
   private boolean rewardsExp;

   public MerchantRecipe(NBTTagCompound tagCompound) {
      this.readFromTags(tagCompound);
   }

   public MerchantRecipe(ItemStack buy1, ItemStack buy2, ItemStack sell) {
      this(buy1, buy2, sell, 0, 7);
   }

   public MerchantRecipe(ItemStack buy1, ItemStack buy2, ItemStack sell, int toolUsesIn, int maxTradeUsesIn) {
      this.itemToBuy = buy1;
      this.secondItemToBuy = buy2;
      this.itemToSell = sell;
      this.toolUses = toolUsesIn;
      this.maxTradeUses = maxTradeUsesIn;
      this.rewardsExp = true;
   }

   public MerchantRecipe(ItemStack buy1, ItemStack sell) {
      this(buy1, ItemStack.EMPTY, sell);
   }

   public MerchantRecipe(ItemStack buy1, Item sellItem) {
      this(buy1, new ItemStack(sellItem));
   }

   public ItemStack getItemToBuy() {
      return this.itemToBuy;
   }

   public ItemStack getSecondItemToBuy() {
      return this.secondItemToBuy;
   }

   public boolean hasSecondItemToBuy() {
      return !this.secondItemToBuy.isEmpty();
   }

   public ItemStack getItemToSell() {
      return this.itemToSell;
   }

   public int getToolUses() {
      return this.toolUses;
   }

   public int getMaxTradeUses() {
      return this.maxTradeUses;
   }

   public void incrementToolUses() {
      ++this.toolUses;
   }

   public void increaseMaxTradeUses(int increment) {
      this.maxTradeUses += increment;
   }

   public boolean isRecipeDisabled() {
      return this.toolUses >= this.maxTradeUses;
   }

   @OnlyIn(Dist.CLIENT)
   public void compensateToolUses() {
      this.toolUses = this.maxTradeUses;
   }

   public boolean getRewardsExp() {
      return this.rewardsExp;
   }

   public void readFromTags(NBTTagCompound tagCompound) {
      NBTTagCompound nbttagcompound = tagCompound.getCompound("buy");
      this.itemToBuy = ItemStack.read(nbttagcompound);
      NBTTagCompound nbttagcompound1 = tagCompound.getCompound("sell");
      this.itemToSell = ItemStack.read(nbttagcompound1);
      if (tagCompound.contains("buyB", 10)) {
         this.secondItemToBuy = ItemStack.read(tagCompound.getCompound("buyB"));
      }

      if (tagCompound.contains("uses", 99)) {
         this.toolUses = tagCompound.getInt("uses");
      }

      if (tagCompound.contains("maxUses", 99)) {
         this.maxTradeUses = tagCompound.getInt("maxUses");
      } else {
         this.maxTradeUses = 7;
      }

      if (tagCompound.contains("rewardExp", 1)) {
         this.rewardsExp = tagCompound.getBoolean("rewardExp");
      } else {
         this.rewardsExp = true;
      }

   }

   public NBTTagCompound writeToTags() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.put("buy", this.itemToBuy.write(new NBTTagCompound()));
      nbttagcompound.put("sell", this.itemToSell.write(new NBTTagCompound()));
      if (!this.secondItemToBuy.isEmpty()) {
         nbttagcompound.put("buyB", this.secondItemToBuy.write(new NBTTagCompound()));
      }

      nbttagcompound.putInt("uses", this.toolUses);
      nbttagcompound.putInt("maxUses", this.maxTradeUses);
      nbttagcompound.putBoolean("rewardExp", this.rewardsExp);
      return nbttagcompound;
   }
}
