package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiButtonRealmsProxy extends GuiButton {
   private final RealmsButton realmsButton;

   public GuiButtonRealmsProxy(RealmsButton realmsButtonIn, int buttonId, int x, int y, String text) {
      super(buttonId, x, y, text);
      this.realmsButton = realmsButtonIn;
   }

   public GuiButtonRealmsProxy(RealmsButton realmsButtonIn, int buttonId, int x, int y, String text, int widthIn, int heightIn) {
      super(buttonId, x, y, widthIn, heightIn, text);
      this.realmsButton = realmsButtonIn;
   }

   public int id() {
      return this.id;
   }

   public boolean active() {
      return this.enabled;
   }

   public void active(boolean p_207706_1_) {
      this.enabled = p_207706_1_;
   }

   public void msg(String p_207705_1_) {
      super.displayString = p_207705_1_;
   }

   public int getWidth() {
      return super.getWidth();
   }

   public int y() {
      return this.y;
   }

   public void onClick(double mouseX, double mouseY) {
      this.realmsButton.onClick(mouseX, mouseY);
   }

   public void onRelease(double mouseX, double mouseY) {
      this.realmsButton.onRelease(mouseX, mouseY);
   }

   public void renderBg(Minecraft mc, int mouseX, int mouseY) {
      this.realmsButton.renderBg(mouseX, mouseY);
   }

   public RealmsButton getRealmsButton() {
      return this.realmsButton;
   }

   public int getHoverState(boolean mouseOver) {
      return this.realmsButton.getYImage(mouseOver);
   }

   public int getYImage(boolean p_154312_1_) {
      return super.getHoverState(p_154312_1_);
   }

   public int getHeight() {
      return this.height;
   }
}
