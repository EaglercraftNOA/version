package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenAlert;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.util.Util;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsBridge extends RealmsScreen {
   private GuiScreen previousScreen;

   public void switchToRealms(GuiScreen p_switchToRealms_1_) {
      this.previousScreen = p_switchToRealms_1_;
      this.showMissingRealmsErrorScreen();
   }

   public GuiScreenRealmsProxy getNotificationScreen(GuiScreen p_getNotificationScreen_1_) {
      this.previousScreen = p_getNotificationScreen_1_;
      return null;
   }

   public void init() {
      Minecraft.getInstance().displayGuiScreen(this.previousScreen);
   }

   public static void openUri(String p_openUri_0_) {
      Util.getOSType().openURI(p_openUri_0_);
   }

   public static void setClipboard(String p_setClipboard_0_) {
      Minecraft.getInstance().keyboardListener.setClipboardString(p_setClipboard_0_);
   }

   private void showMissingRealmsErrorScreen() {
      Minecraft.getInstance().displayGuiScreen(new GuiScreenAlert(() -> {
         Minecraft.getInstance().displayGuiScreen(this.previousScreen);
      }, new TextComponentString(""), new TextComponentTranslation("realms.missing.module.error.text")));
   }
}
