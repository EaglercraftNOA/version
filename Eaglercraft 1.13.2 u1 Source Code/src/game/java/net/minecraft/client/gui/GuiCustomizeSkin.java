package net.minecraft.client.gui;

import net.minecraft.client.GameSettings;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCustomizeSkin extends GuiScreen {
   private final GuiScreen parentScreen;
   private String title;
   // audrey <3
   private GuiButton enableFNAWSkinsButton;

   public GuiCustomizeSkin(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   protected void initGui() {
      int i = 0;
      this.title = I18n.format("options.skinCustomisation.title");

      for(EnumPlayerModelParts enumplayermodelparts : EnumPlayerModelParts.values()) {
         this.addButton(new GuiCustomizeSkin.ButtonPart(enumplayermodelparts.getPartId(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20, enumplayermodelparts));
         ++i;
      }

      this.addButton(new GuiOptionButton(199, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), GameSettings.Options.MAIN_HAND, this.mc.gameSettings.getKeyBinding(GameSettings.Options.MAIN_HAND)) {
         public void onClick(double mouseX, double mouseY) {
            GuiCustomizeSkin.this.mc.gameSettings.setOptionValue(GameSettings.Options.MAIN_HAND, 1);
            this.displayString = GuiCustomizeSkin.this.mc.gameSettings.getKeyBinding(GameSettings.Options.MAIN_HAND);
            GuiCustomizeSkin.this.mc.gameSettings.sendSettingsToServer();
         }
      });
      ++i;
      if (i % 2 == 1) {
         ++i;
      }

      // audrey <3
      this.enableFNAWSkinsButton = this.addButton(new GuiButton(201, this.width / 2 - 100, this.height / 6 + 10 + 24 * (i >> 1), this.getFNAWSkinsText()) {
         public void onClick(double mouseX, double mouseY) {
            GuiCustomizeSkin.this.mc.gameSettings.enableFNAWSkins = !GuiCustomizeSkin.this.mc.gameSettings.enableFNAWSkins;
            GuiCustomizeSkin.this.mc.getRenderManager().setEnableFNAWSkins(GuiCustomizeSkin.this.mc.getEnableFNAWSkins());
            GuiCustomizeSkin.this.enableFNAWSkinsButton.displayString = GuiCustomizeSkin.this.getFNAWSkinsText();
         }
      });

      this.addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 40 + 24 * (i >> 1), I18n.format("gui.done")) {
         public void onClick(double mouseX, double mouseY) {
            GuiCustomizeSkin.this.mc.gameSettings.saveOptions();
            GuiCustomizeSkin.this.mc.displayGuiScreen(GuiCustomizeSkin.this.parentScreen);
         }
      });
   }

   public void close() {
      this.mc.gameSettings.saveOptions();
      super.close();
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }

   private String getMessage(EnumPlayerModelParts playerModelParts) {
      String s;
      if (this.mc.gameSettings.getModelParts().contains(playerModelParts)) {
         s = I18n.format("options.on");
      } else {
         s = I18n.format("options.off");
      }

      return playerModelParts.getName().getFormattedText() + ": " + s;
   }

   // audrey <3
   private String getFNAWSkinsText() {
      return I18n.format("options.skinCustomisation.enableFNAWSkins") + ": " + I18n.format(this.mc.gameSettings.enableFNAWSkins ? "options.on" : "options.off");
   }

   @OnlyIn(Dist.CLIENT)
   class ButtonPart extends GuiButton {
      private final EnumPlayerModelParts playerModelParts;

      private ButtonPart(int p_i45514_2_, int p_i45514_3_, int p_i45514_4_, int p_i45514_5_, int p_i45514_6_, EnumPlayerModelParts playerModelParts) {
         super(p_i45514_2_, p_i45514_3_, p_i45514_4_, p_i45514_5_, p_i45514_6_, GuiCustomizeSkin.this.getMessage(playerModelParts));
         this.playerModelParts = playerModelParts;
      }

      public void onClick(double mouseX, double mouseY) {
         GuiCustomizeSkin.this.mc.gameSettings.switchModelPartEnabled(this.playerModelParts);
         this.displayString = GuiCustomizeSkin.this.getMessage(this.playerModelParts);
      }
   }
}
