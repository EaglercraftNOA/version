package net.minecraft.client.gui;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
   private String title = "";
   private String stage = "";
   private int progress;
   private boolean doneWorking;

   public boolean allowCloseWithEscape() {
      return false;
   }

   public void displaySavingString(ITextComponent component) {
      this.resetProgressAndMessage(component);
   }

   public void resetProgressAndMessage(ITextComponent component) {
      this.title = component.getFormattedText();
      this.displayLoadingString(new TextComponentTranslation("progress.working"));
   }

   public void displayLoadingString(ITextComponent component) {
      this.stage = component.getFormattedText();
      this.setLoadingProgress(0);
   }

   public void eaglerShow(String title, String stage) {
      this.title = title == null ? "" : title;
      this.stage = stage == null ? "" : stage;
      this.progress = 0;
      this.doneWorking = false;
      Minecraft.getInstance().displayGuiScreen(this);
   }

   public void displayLoadingString(String str) {
      this.displayLoadingString(new TextComponentString(str));
   }

   public void setLoadingProgress(int progress) {
      this.progress = progress;
   }

   public void setDoneWorking() {
      this.doneWorking = true;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      if (this.doneWorking) {
         if (!this.mc.isConnectedToRealms()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }

      } else {
         this.drawDefaultBackground();
         this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 70, 16777215);
         if (!Objects.equals(this.stage, "") && this.progress != 0) {
            this.drawCenteredString(this.fontRenderer, this.stage + " " + this.progress + "%", this.width / 2, 90, 16777215);
         }

         super.render(mouseX, mouseY, partialTicks);
      }
   }
}
