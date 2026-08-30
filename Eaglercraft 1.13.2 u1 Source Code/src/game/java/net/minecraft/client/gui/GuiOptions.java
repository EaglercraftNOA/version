package net.minecraft.client.gui;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.cookie.GuiScreenRevokeSessionToken;
import net.lax1dude.eaglercraft.v1_8.cookie.ServerCookieDataStore;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenGenericErrorMessage;
import net.lax1dude.eaglercraft.v1_8.profile.GuiScreenImportExportProfile;
import net.minecraft.client.GameSettings;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumDifficulty;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiOptions extends GuiScreen {
   private static final GameSettings.Options[] SCREEN_OPTIONS = new GameSettings.Options[]{GameSettings.Options.FOV};
   private final GuiScreen lastScreen;
   private final GameSettings settings;
   private GuiButton difficultyButton;
   private GuiLockIconButton lockButton;
   protected String title = "Options";

   public GuiOptions(GuiScreen p_i1046_1_, GameSettings p_i1046_2_) {
      this.lastScreen = p_i1046_1_;
      this.settings = p_i1046_2_;
   }

   protected void initGui() {
      this.title = I18n.format("options.title");
      int i = 0;

      for(GameSettings.Options gamesettings$options : SCREEN_OPTIONS) {
         if (gamesettings$options.isFloat()) {
            this.addButton(new GuiOptionSlider(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options));
         } else {
            GuiOptionButton guioptionbutton = new GuiOptionButton(gamesettings$options.getOrdinal(), this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), gamesettings$options, this.settings.getKeyBinding(gamesettings$options)) {
               public void onClick(double mouseX, double mouseY) {
                  GuiOptions.this.settings.setOptionValue(this.getOption(), 1);
                  this.displayString = GuiOptions.this.settings.getKeyBinding(GameSettings.Options.byOrdinal(this.id));
               }
            };
            this.addButton(guioptionbutton);
         }

         ++i;
      }

      if (this.mc.world != null) {
         EnumDifficulty enumdifficulty = this.mc.world.getDifficulty();
         this.difficultyButton = new GuiButton(108, this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.getDifficultyText(enumdifficulty)) {
            public void onClick(double mouseX, double mouseY) {
               GuiOptions.this.mc.world.getWorldInfo().setDifficulty(EnumDifficulty.byId(GuiOptions.this.mc.world.getDifficulty().getId() + 1));
               // audrey <3
               SingleplayerServerController.setDifficulty(GuiOptions.this.mc.world.getWorldInfo().getDifficulty().getId());
               GuiOptions.this.difficultyButton.displayString = GuiOptions.this.getDifficultyText(GuiOptions.this.mc.world.getDifficulty());
            }
         };
         this.addButton(this.difficultyButton);
         if (this.mc.isSingleplayer() && !this.mc.world.getWorldInfo().isHardcore()) {
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
            this.lockButton = new GuiLockIconButton(109, this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y) {
               public void onClick(double mouseX, double mouseY) {
                  GuiOptions.this.mc.displayGuiScreen(new GuiYesNo(GuiOptions.this, (new TextComponentTranslation("difficulty.lock.title")).getFormattedText(), (new TextComponentTranslation("difficulty.lock.question", new TextComponentTranslation(GuiOptions.this.mc.world.getWorldInfo().getDifficulty().getTranslationKey()))).getFormattedText(), 109));
               }
            };
            this.addButton(this.lockButton);
            this.lockButton.setLocked(this.mc.world.getWorldInfo().isDifficultyLocked());
            this.lockButton.enabled = !this.lockButton.isLocked();
            this.difficultyButton.enabled = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.enabled = false;
         }
      }

      this.addButton(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.skinCustomisation")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiCustomizeSkin(GuiOptions.this));
         }
      });
      this.addButton(new GuiButton(106, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.format("options.sounds")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiScreenOptionsSounds(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.video")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiVideoSettings(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.controls")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiControls(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(102, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.language")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiLanguage(GuiOptions.this, GuiOptions.this.settings, GuiOptions.this.mc.getLanguageManager()));
         }
      });
      this.addButton(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.chat.title")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new ScreenChatOptions(GuiOptions.this, GuiOptions.this.settings));
         }
      });
      this.addButton(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.resourcepack")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(new GuiScreenResourcePacks(GuiOptions.this));
         }
      });
      //audrey <3
      GuiButton guibutton = this.addButton(new GuiButton(104, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, this.eaglerFormat("options.debugConsoleButton", "Open Debug Console")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            //audrey <3
            EagRuntime.showDebugConsole();
         }
      });
      //audrey <3
      guibutton.enabled = EagRuntime.getPlatformType() != EnumPlatformType.DESKTOP;
      this.addButton(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")) {
         public void onClick(double mouseX, double mouseY) {
            GuiOptions.this.mc.gameSettings.saveOptions();
            GuiOptions.this.mc.displayGuiScreen(GuiOptions.this.lastScreen);
         }
      });
   }

   public String getDifficultyText(EnumDifficulty p_175355_1_) {
      return (new TextComponentTranslation("options.difficulty")).appendText(": ").appendSibling(p_175355_1_.getDisplayName()).getFormattedText();
   }

   //audrey <3
   private String eaglerFormat(String key, String fallback) {
      return I18n.hasKey(key) ? I18n.format(key) : fallback;
   }

   //audrey <3
   private String importExportText() {
      return this.eaglerFormat("editProfile.importExport", "Import/Export Profile");
   }

   //audrey <3
   private String revokeSessionText() {
      return this.eaglerFormat("revokeSessionToken.button", "Revoke Session Token");
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      this.mc.displayGuiScreen(this);
      if (p_confirmResult_2_ == 109 && p_confirmResult_1_ && this.mc.world != null) {
         this.mc.world.getWorldInfo().setDifficultyLocked(true);
         // audrey <3
         SingleplayerServerController.setDifficulty(-1);
         this.lockButton.setLocked(true);
         this.lockButton.enabled = false;
         this.difficultyButton.enabled = false;
      }

   }

   public void close() {
      this.mc.gameSettings.saveOptions();
      super.close();
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 15, 16777215);
      //audrey <3
      this.drawImportExportLink(mouseX, mouseY);
      //audrey <3
      this.drawRevokeSessionLink(mouseX, mouseY);
      super.render(mouseX, mouseY, partialTicks);
   }

   //audrey <3
   private void drawImportExportLink(int mouseX, int mouseY) {
      if (this.mc.world == null && !EagRuntime.getConfiguration().isDemo()) {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         String s = this.importExportText();
         int i = this.mc.fontRenderer.getStringWidth(s);
         boolean flag = mouseX > 1 && mouseY > 1 && mouseX < i * 3 / 4 + 7 && mouseY < 12;
         if (flag) {
            Mouse.showCursor(EnumCursorType.HAND);
         }

         drawString(this.mc.fontRenderer, TextFormatting.UNDERLINE + s, 5, 5, flag ? 0xFFEEEE22 : 0xFFCCCCCC);
         GlStateManager.popMatrix();
      }
   }

   //audrey <3
   private void drawRevokeSessionLink(int mouseX, int mouseY) {
      if (EagRuntime.getConfiguration().isEnableServerCookies() && this.mc.player == null) {
         GlStateManager.pushMatrix();
         GlStateManager.scalef(0.75F, 0.75F, 0.75F);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         String s = this.revokeSessionText();
         int i = this.mc.fontRenderer.getStringWidth(s);
         boolean flag = mouseX > this.width - 5 - (i + 5) * 3 / 4 && mouseY > 1 && mouseX < this.width - 2 && mouseY < 12;
         if (flag) {
            Mouse.showCursor(EnumCursorType.HAND);
         }

         drawString(this.mc.fontRenderer, TextFormatting.UNDERLINE + s, (this.width - 1) * 4 / 3 - i - 5, 5, flag ? 0xFFEEEE22 : 0xFFCCCCCC);
         GlStateManager.popMatrix();
      }
   }

   //audrey <3
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      if (button == 0) {
         String s = this.importExportText();
         int i = this.mc.fontRenderer.getStringWidth(s);
         if (this.mc.world == null && !EagRuntime.getConfiguration().isDemo() && mouseX > 1.0D && mouseY > 1.0D && mouseX < (double)(i * 3 / 4 + 7) && mouseY < 12.0D) {
            this.mc.displayGuiScreen(new GuiScreenImportExportProfile(this));
            return true;
         }

         s = this.revokeSessionText();
         i = this.mc.fontRenderer.getStringWidth(s);
         if (EagRuntime.getConfiguration().isEnableServerCookies() && this.mc.player == null && mouseX > (double)(this.width - 5 - (i + 5) * 3 / 4) && mouseY > 1.0D && mouseX < (double)(this.width - 2) && mouseY < 12.0D) {
            ServerCookieDataStore.flush();
            this.mc.displayGuiScreen(ServerCookieDataStore.numRevokable() == 0 ? new GuiScreenGenericErrorMessage("errorNoSessions.title", "errorNoSessions.desc", this) : new GuiScreenRevokeSessionToken(this));
            return true;
         }
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }
}
