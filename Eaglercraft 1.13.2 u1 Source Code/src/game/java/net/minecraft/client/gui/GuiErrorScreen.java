package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiErrorScreen extends GuiScreen {
   private final String title;
   private final String message;

   public GuiErrorScreen(String titleIn, String messageIn) {
      this.title = titleIn;
      this.message = messageIn;
   }

   protected void initGui() {
      super.initGui();
      this.addButton(new GuiButton(0, this.width / 2 - 100, 140, I18n.format("gui.cancel")) {
         public void onClick(double mouseX, double mouseY) {
            GuiErrorScreen.this.mc.displayGuiScreen((GuiScreen)null);
         }
      });
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawGradientRect(0, 0, this.width, this.height, -12574688, -11530224);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 90, 16777215);
      this.drawCenteredString(this.fontRenderer, this.message, this.width / 2, 110, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }

   public boolean allowCloseWithEscape() {
      return false;
   }
}
