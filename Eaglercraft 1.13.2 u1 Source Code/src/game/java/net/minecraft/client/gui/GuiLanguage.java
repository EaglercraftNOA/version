package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiLanguage extends GuiScreen {
   protected GuiScreen parentScreen;
   private GuiLanguage.List list;
   private final GameSettings game_settings_3;
   private final LanguageManager languageManager;
   private GuiOptionButton field_211832_i;
   private GuiOptionButton confirmSettingsBtn;

   public GuiLanguage(GuiScreen screen, GameSettings gameSettingsObj, LanguageManager manager) {
      this.parentScreen = screen;
      this.game_settings_3 = gameSettingsObj;
      this.languageManager = manager;
   }

   public IGuiEventListener getFocused() {
      return this.list;
   }

   protected void initGui() {
      this.list = new GuiLanguage.List(this.mc);
      this.children.add(this.list);
      this.field_211832_i = this.addButton(new GuiOptionButton(100, this.width / 2 - 155, this.height - 38, GameSettings.Options.FORCE_UNICODE_FONT, this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT)) {
         public void onClick(double mouseX, double mouseY) {
            GuiLanguage.this.game_settings_3.setOptionValue(this.getOption(), 1);
            this.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
            GuiLanguage.this.func_195181_h();
         }
      });
      this.confirmSettingsBtn = this.addButton(new GuiOptionButton(6, this.width / 2 - 155 + 160, this.height - 38, I18n.format("gui.done")) {
         public void onClick(double mouseX, double mouseY) {
            GuiLanguage.this.mc.displayGuiScreen(GuiLanguage.this.parentScreen);
         }
      });
      super.initGui();
   }

   private void func_195181_h() {
      this.mc.mainWindow.updateSize();
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.list.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, I18n.format("options.language"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRenderer, "(" + I18n.format("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
      super.render(mouseX, mouseY, partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   class List extends GuiSlot {
      private final java.util.List<String> langCodeList = Lists.newArrayList();
      private final Map<String, Language> languageMap = Maps.newHashMap();

      public List(Minecraft mcIn) {
         super(mcIn, GuiLanguage.this.width, GuiLanguage.this.height, 32, GuiLanguage.this.height - 65 + 4, 18);

         for(Language language : GuiLanguage.this.languageManager.getLanguages()) {
            this.languageMap.put(language.getLanguageCode(), language);
            this.langCodeList.add(language.getLanguageCode());
         }

      }

      protected int getSize() {
         return this.langCodeList.size();
      }

      protected boolean mouseClicked(int index, int button, double mouseX, double mouseY) {
         Language language = this.languageMap.get(this.langCodeList.get(index));
         GuiLanguage.this.languageManager.setCurrentLanguage(language);
         GuiLanguage.this.game_settings_3.language = language.getLanguageCode();
         this.mc.refreshResources();
         GuiLanguage.this.fontRenderer.setBidiFlag(GuiLanguage.this.languageManager.isCurrentLanguageBidirectional());
         GuiLanguage.this.confirmSettingsBtn.displayString = I18n.format("gui.done");
         GuiLanguage.this.field_211832_i.displayString = GuiLanguage.this.game_settings_3.getKeyBinding(GameSettings.Options.FORCE_UNICODE_FONT);
         GuiLanguage.this.game_settings_3.saveOptions();
         GuiLanguage.this.func_195181_h();
         return true;
      }

      protected boolean isSelected(int slotIndex) {
         return this.langCodeList.get(slotIndex).equals(GuiLanguage.this.languageManager.getCurrentLanguage().getLanguageCode());
      }

      protected int getContentHeight() {
         return this.getSize() * 18;
      }

      protected void drawBackground() {
         GuiLanguage.this.drawDefaultBackground();
      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         GuiLanguage.this.fontRenderer.setBidiFlag(true);
         this.drawCenteredString(GuiLanguage.this.fontRenderer, this.languageMap.get(this.langCodeList.get(slotIndex)).toString(), this.width / 2, yPos + 1, 16777215);
         GuiLanguage.this.fontRenderer.setBidiFlag(GuiLanguage.this.languageManager.getCurrentLanguage().isBidirectional());
      }
   }
}
