package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiConfirmOpenLink extends GuiYesNo {
   private final String openLinkWarning;
   private final String copyLinkButtonText;
   private final String linkText;
   private boolean showSecurityWarning = true;

   public GuiConfirmOpenLink(GuiYesNoCallback parentScreenIn, String linkTextIn, int parentButtonClickedIdIn, boolean trusted) {
      super(parentScreenIn, I18n.format(trusted ? "chat.link.confirmTrusted" : "chat.link.confirm"), linkTextIn, parentButtonClickedIdIn);
      this.confirmButtonText = I18n.format(trusted ? "chat.link.open" : "gui.yes");
      this.cancelButtonText = I18n.format(trusted ? "gui.cancel" : "gui.no");
      this.copyLinkButtonText = I18n.format("chat.copy");
      this.openLinkWarning = I18n.format("chat.link.warning");
      this.linkText = linkTextIn;
   }

   protected void initGui() {
      super.initGui();
      this.buttons.clear();
      this.children.clear();
      this.addButton(new GuiButton(0, this.width / 2 - 50 - 105, this.height / 6 + 96, 100, 20, this.confirmButtonText) {
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmOpenLink.this.parentScreen.confirmResult(true, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
      this.addButton(new GuiButton(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText) {
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmOpenLink.this.copyLinkToClipboard();
            GuiConfirmOpenLink.this.parentScreen.confirmResult(false, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 - 50 + 105, this.height / 6 + 96, 100, 20, this.cancelButtonText) {
         public void onClick(double mouseX, double mouseY) {
            GuiConfirmOpenLink.this.parentScreen.confirmResult(false, GuiConfirmOpenLink.this.parentButtonClickedId);
         }
      });
   }

   public void copyLinkToClipboard() {
      this.mc.keyboardListener.setClipboardString(this.linkText);
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      super.render(mouseX, mouseY, partialTicks);
      if (this.showSecurityWarning) {
         this.drawCenteredString(this.fontRenderer, this.openLinkWarning, this.width / 2, 110, 16764108);
      }

   }

   public void disableSecurityWarning() {
      this.showSecurityWarning = false;
   }
}
