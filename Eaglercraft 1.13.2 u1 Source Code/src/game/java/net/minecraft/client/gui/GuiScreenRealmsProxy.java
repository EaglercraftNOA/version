package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiScreenRealmsProxy extends GuiScreen {
   private final RealmsScreen proxy;
   private static final Logger field_212333_f = LogManager.getLogger();

   public GuiScreenRealmsProxy(RealmsScreen proxyIn) {
      this.proxy = proxyIn;
   }

   public RealmsScreen getProxy() {
      return this.proxy;
   }

   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      this.proxy.init(mc, width, height);
      super.setWorldAndResolution(mc, width, height);
   }

   protected void initGui() {
      this.proxy.init();
      super.initGui();
   }

   public void drawCenteredString(String text, int x, int y, int color) {
      super.drawCenteredString(this.fontRenderer, text, x, y, color);
   }

   public void drawString(String p_207734_1_, int p_207734_2_, int p_207734_3_, int p_207734_4_, boolean p_207734_5_) {
      if (p_207734_5_) {
         super.drawString(this.fontRenderer, p_207734_1_, p_207734_2_, p_207734_3_, p_207734_4_);
      } else {
         this.fontRenderer.drawString(p_207734_1_, (float)p_207734_2_, (float)p_207734_3_, p_207734_4_);
      }

   }

   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      this.proxy.blit(x, y, textureX, textureY, width, height);
      super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
   }

   public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      super.drawGradientRect(left, top, right, bottom, startColor, endColor);
   }

   public void drawDefaultBackground() {
      super.drawDefaultBackground();
   }

   public boolean doesGuiPauseGame() {
      return super.doesGuiPauseGame();
   }

   public void drawWorldBackground(int tint) {
      super.drawWorldBackground(tint);
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.proxy.render(mouseX, mouseY, partialTicks);
   }

   public void renderToolTip(ItemStack stack, int x, int y) {
      super.renderToolTip(stack, x, y);
   }

   public void drawHoveringText(String text, int x, int y) {
      super.drawHoveringText(text, x, y);
   }

   public void drawHoveringText(List<String> textLines, int x, int y) {
      super.drawHoveringText(textLines, x, y);
   }

   public void tick() {
      this.proxy.tick();
      super.tick();
   }

   public int getFontHeight() {
      return this.fontRenderer.FONT_HEIGHT;
   }

   public int fontWidth(String p_207731_1_) {
      return this.fontRenderer.getStringWidth(p_207731_1_);
   }

   public void fontDrawShadow(String p_207728_1_, int p_207728_2_, int p_207728_3_, int p_207728_4_) {
      this.fontRenderer.drawStringWithShadow(p_207728_1_, (float)p_207728_2_, (float)p_207728_3_, p_207728_4_);
   }

   public List<String> fontSplit(String text, int wrapWidth) {
      return this.fontRenderer.listFormattedStringToWidth(text, wrapWidth);
   }

   public void childrenClear() {
      this.children.clear();
   }

   public void addWidget(RealmsGuiEventListener p_207730_1_) {
      if (this.func_212332_c(p_207730_1_) || !this.children.add(p_207730_1_.getProxy())) {
         field_212333_f.error("Tried to add the same widget multiple times: " + p_207730_1_);
      }

   }

   public void removeWidget(RealmsGuiEventListener p_207733_1_) {
      if (!this.func_212332_c(p_207733_1_) || !this.children.remove(p_207733_1_.getProxy())) {
         field_212333_f.error("Tried to add the same widget multiple times: " + p_207733_1_);
      }

   }

   public boolean func_212332_c(RealmsGuiEventListener p_212332_1_) {
      return this.children.contains(p_212332_1_.getProxy());
   }

   public void buttonsAdd(RealmsButton button) {
      this.addButton(button.getProxy());
   }

   public List<RealmsButton> buttons() {
      List<RealmsButton> list = Lists.newArrayListWithExpectedSize(this.buttons.size());

      for(GuiButton guibutton : this.buttons) {
         list.add(((GuiButtonRealmsProxy)guibutton).getRealmsButton());
      }

      return list;
   }

   public void buttonsClear() {
      HashSet<IGuiEventListener> hashset = new HashSet<>(this.buttons);
      this.children.removeIf(hashset::contains);
      this.buttons.clear();
   }

   public void removeButton(RealmsButton p_207732_1_) {
      this.children.remove(p_207732_1_.getProxy());
      this.buttons.remove(p_207732_1_.getProxy());
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      return this.proxy.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_) ? true : super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      return this.proxy.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      return this.proxy.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_) ? true : super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return this.proxy.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.proxy.charTyped(p_charTyped_1_, p_charTyped_2_) ? true : super.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      this.proxy.confirmResult(p_confirmResult_1_, p_confirmResult_2_);
   }

   public void onGuiClosed() {
      this.proxy.removed();
      super.onGuiClosed();
   }

   public int draw(String p_209208_1_, int p_209208_2_, int p_209208_3_, int p_209208_4_, boolean p_209208_5_) {
      return p_209208_5_ ? this.fontRenderer.drawStringWithShadow(p_209208_1_, (float)p_209208_2_, (float)p_209208_3_, p_209208_4_) : this.fontRenderer.drawString(p_209208_1_, (float)p_209208_2_, (float)p_209208_3_, p_209208_4_);
   }
}
