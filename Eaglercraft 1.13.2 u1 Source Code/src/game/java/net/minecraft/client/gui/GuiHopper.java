package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiHopper extends GuiContainer {
   private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation("textures/gui/container/hopper.png");
   private final IInventory playerInventory;
   private final IInventory hopperInventory;

   public GuiHopper(InventoryPlayer playerInv, IInventory hopperInv) {
      super(new ContainerHopper(playerInv, hopperInv, Minecraft.getInstance().player));
      this.playerInventory = playerInv;
      this.hopperInventory = hopperInv;
      this.allowUserInput = false;
      this.ySize = 133;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.render(mouseX, mouseY, partialTicks);
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRenderer.drawString(this.hopperInventory.getDisplayName().getFormattedText(), 8.0F, 6.0F, 4210752);
      this.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
   }
}
