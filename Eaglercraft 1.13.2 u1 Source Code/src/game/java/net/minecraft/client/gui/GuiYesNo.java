package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiYesNo extends GuiScreen {
   protected GuiYesNoCallback parentScreen;
   protected String messageLine1;
   private final String messageLine2;
   private final List<String> listLines = Lists.newArrayList();
   protected String confirmButtonText;
   protected String cancelButtonText;
   protected int parentButtonClickedId;
   private int ticksUntilEnable;

   public GuiYesNo(GuiYesNoCallback parentScreenIn, String messageLine1In, String messageLine2In, int parentButtonClickedIdIn) {
      this.parentScreen = parentScreenIn;
      this.messageLine1 = messageLine1In;
      this.messageLine2 = messageLine2In;
      this.parentButtonClickedId = parentButtonClickedIdIn;
      this.confirmButtonText = I18n.format("gui.yes");
      this.cancelButtonText = I18n.format("gui.no");
   }

   public GuiYesNo(GuiYesNoCallback parentScreenIn, String messageLine1In, String messageLine2In, String confirmButtonTextIn, String cancelButtonTextIn, int parentButtonClickedIdIn) {
      this.parentScreen = parentScreenIn;
      this.messageLine1 = messageLine1In;
      this.messageLine2 = messageLine2In;
      this.confirmButtonText = confirmButtonTextIn;
      this.cancelButtonText = cancelButtonTextIn;
      this.parentButtonClickedId = parentButtonClickedIdIn;
   }

   protected void initGui() {
      super.initGui();
      this.addButton(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText) {
         public void onClick(double mouseX, double mouseY) {
            GuiYesNo.this.parentScreen.confirmResult(true, GuiYesNo.this.parentButtonClickedId);
         }
      });
      this.addButton(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText) {
         public void onClick(double mouseX, double mouseY) {
            GuiYesNo.this.parentScreen.confirmResult(false, GuiYesNo.this.parentButtonClickedId);
         }
      });
      this.listLines.clear();
      this.listLines.addAll(this.fontRenderer.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.messageLine1, this.width / 2, 70, 16777215);
      int i = 90;

      for(String s : this.listLines) {
         this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
         i += this.fontRenderer.FONT_HEIGHT;
      }

      super.render(mouseX, mouseY, partialTicks);
   }

   public void setButtonDelay(int ticksUntilEnableIn) {
      this.ticksUntilEnable = ticksUntilEnableIn;

      for(GuiButton guibutton : this.buttons) {
         guibutton.enabled = false;
      }

   }

   public void tick() {
      super.tick();
      if (--this.ticksUntilEnable == 0) {
         for(GuiButton guibutton : this.buttons) {
            guibutton.enabled = true;
         }
      }

   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.parentScreen.confirmResult(false, this.parentButtonClickedId);
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }
}
