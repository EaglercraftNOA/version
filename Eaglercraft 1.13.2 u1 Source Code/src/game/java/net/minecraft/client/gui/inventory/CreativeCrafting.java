package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreativeCrafting implements IContainerListener {
   private final Minecraft mc;

   public CreativeCrafting(Minecraft mc) {
      this.mc = mc;
   }

   public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
   }

   public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
      this.mc.playerController.sendSlotPacket(stack, slotInd);
   }

   public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
   }

   public void sendAllWindowProperties(Container containerIn, IInventory inventory) {
   }
}
