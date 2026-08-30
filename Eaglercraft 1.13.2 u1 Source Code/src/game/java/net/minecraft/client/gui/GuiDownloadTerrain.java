package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDownloadTerrain extends GuiScreen {
   public boolean allowCloseWithEscape() {
      return false;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
