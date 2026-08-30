package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiDisconnected extends GuiScreen {
   private final String reason;
   private final ITextComponent message;
   private List<String> multilineMessage;
   private final GuiScreen parentScreen;
   private int textHeight;

   public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, ITextComponent chatComp) {
      this.parentScreen = screen;
      this.reason = I18n.format(reasonLocalizationKey);
      this.message = chatComp;
   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   protected void initGui() {
      this.multilineMessage = this.fontRenderer.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      this.textHeight = this.multilineMessage.size() * this.fontRenderer.FONT_HEIGHT;
      this.addButton(new GuiButton(0, this.width / 2 - 100, Math.min(this.height / 2 + this.textHeight / 2 + this.fontRenderer.FONT_HEIGHT, this.height - 30), I18n.format("gui.toMenu")) {
         public void onClick(double mouseX, double mouseY) {
            GuiDisconnected.this.mc.displayGuiScreen(GuiDisconnected.this.parentScreen);
         }
      });
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.reason, this.width / 2, this.height / 2 - this.textHeight / 2 - this.fontRenderer.FONT_HEIGHT * 2, 11184810);
      int i = this.height / 2 - this.textHeight / 2;
      if (this.multilineMessage != null) {
         for(String s : this.multilineMessage) {
            this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
            i += this.fontRenderer.FONT_HEIGHT;
         }
      }

      super.render(mouseX, mouseY, partialTicks);
   }
}
