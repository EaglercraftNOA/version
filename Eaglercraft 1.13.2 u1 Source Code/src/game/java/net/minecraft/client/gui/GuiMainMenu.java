package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Runnables;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglercraftVersion;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.profile.GuiScreenEditProfile;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderSkybox;
import net.minecraft.client.renderer.RenderSkyboxCube;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenDemoIntegratedServerStartup;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenDemoPlayWorldSelection;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerStartup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL;

@OnlyIn(Dist.CLIENT)
public class GuiMainMenu extends GuiScreen {
   private static final Random RANDOM = new Random();
   private final float minceraftRoll;
   private String splashText;
   private GuiButton buttonOptions;
   private GuiButton buttonResetDemo;
   private final Object threadLock = new Object();
   public static final String MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here" + TextFormatting.RESET + " for more information.";
   private int openGLWarning2Width;
   private int openGLWarning1Width;
   private int openGLWarningX1;
   private int openGLWarningY1;
   private int openGLWarningX2;
   private int openGLWarningY2;
   private String openGLWarning1;
   private String openGLWarning2 = MORE_INFO_TEXT;
   private String openGLWarningLink;
   private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
   private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation("textures/gui/title/minecraft.png");
   private static final ResourceLocation MINECRAFT_TITLE_EDITION = new ResourceLocation("textures/gui/title/edition.png");
   private boolean hasCheckedForRealmsNotification;
   private GuiScreen realmsNotification;
   private int widthCopyright;
   private int widthCopyrightRest;
   private final RenderSkybox panorama = new RenderSkybox(new RenderSkyboxCube(new ResourceLocation("textures/gui/title/background/panorama")));

   public GuiMainMenu() {
      this.splashText = "missingno";
      IResource iresource = null;

      try {
         List<String> list = Lists.newArrayList();
         iresource = Minecraft.getInstance().getResourceManager().getResource(SPLASH_TEXTS);
         BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));

         String s;
         while((s = bufferedreader.readLine()) != null) {
            s = s.trim();
            if (!s.isEmpty()) {
               list.add(s);
            }
         }

         if (!list.isEmpty()) {
            while(true) {
               this.splashText = list.get(RANDOM.nextInt(list.size()));
               if (this.splashText.hashCode() != 125780783) {
                  break;
               }
            }
         }
      } catch (IOException var8) {
         ;
      } finally {
         IOUtils.closeQuietly((Closeable)iresource);
      }

      this.minceraftRoll = RANDOM.nextFloat();
      this.openGLWarning1 = "";
      if (!GL.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
         this.openGLWarning1 = I18n.format("title.oldgl1");
         this.openGLWarning2 = I18n.format("title.oldgl2");
         this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }

   }

   private boolean areRealmsNotificationsEnabled() {
      //audrey <3
      return false;
   }

   public void tick() {
      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.tick();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   protected void initGui() {
      this.widthCopyright = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
      this.widthCopyrightRest = this.width - this.widthCopyright - 2;
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
         this.splashText = "Merry X-mas!";
      } else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
         this.splashText = "Happy new year!";
      } else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
         this.splashText = "OOoooOOOoooo! Spooky!";
      }

      int i = 24;
      int j = this.height / 4 + 48;
      if (this.mc.isDemo()) {
         this.addDemoButtons(j, 24);
      } else {
         this.addSingleplayerMultiplayerButtons(j, 24);
      }

      this.buttonOptions = this.addButton(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")) {
         public void onClick(double mouseX, double mouseY) {
            GuiMainMenu.this.mc.displayGuiScreen(new GuiOptions(GuiMainMenu.this, GuiMainMenu.this.mc.gameSettings));
         }
      });
      //audrey <3
      this.addButton(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, this.eaglerFormat("menu.editProfile", "Edit Profile")) {
         public void onClick(double mouseX, double mouseY) {
            //audrey <3
            GuiMainMenu.this.mc.displayGuiScreen(new GuiScreenEditProfile(GuiMainMenu.this));
         }
      });
      this.addButton(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12) {
         public void onClick(double mouseX, double mouseY) {
            GuiMainMenu.this.mc.displayGuiScreen(new GuiLanguage(GuiMainMenu.this, GuiMainMenu.this.mc.gameSettings, GuiMainMenu.this.mc.getLanguageManager()));
         }
      });
      synchronized(this.threadLock) {
         this.openGLWarning1Width = this.fontRenderer.getStringWidth(this.openGLWarning1);
         this.openGLWarning2Width = this.fontRenderer.getStringWidth(this.openGLWarning2);
         int k = Math.max(this.openGLWarning1Width, this.openGLWarning2Width);
         this.openGLWarningX1 = (this.width - k) / 2;
         this.openGLWarningY1 = j - 24;
         this.openGLWarningX2 = this.openGLWarningX1 + k;
         this.openGLWarningY2 = this.openGLWarningY1 + 24;
      }

      //audrey <3
      this.mc.setConnectedToRealms(false);

   }

   private void addSingleplayerMultiplayerButtons(int yIn, int rowHeightIn) {
      this.addButton(new GuiButton(1, this.width / 2 - 100, yIn, I18n.format("menu.singleplayer")) {
         public void onClick(double mouseX, double mouseY) {
            // audrey <3
            GuiMainMenu.this.mc.displayGuiScreen(SingleplayerServerController.isReady() ? new GuiWorldSelection(GuiMainMenu.this) : new GuiScreenIntegratedServerStartup(GuiMainMenu.this));
         }
      });
      this.addButton(new GuiButton(2, this.width / 2 - 100, yIn + rowHeightIn * 1, I18n.format("menu.multiplayer")) {
         public void onClick(double mouseX, double mouseY) {
            GuiMainMenu.this.mc.displayGuiScreen(new GuiMultiplayer(GuiMainMenu.this));
         }
      });
   }

   //audrey <3
   private String eaglerFormat(String key, String fallback) {
      return I18n.hasKey(key) ? I18n.format(key) : fallback;
   }

   //audrey <3
   private void drawCreditsLink(int mouseX, int mouseY) {
      String s = "CREDITS.txt";
      int i = this.fontRenderer.getStringWidth(s) * 3 / 4;
      if (mouseX >= this.width - i - 4 && mouseX <= this.width && mouseY >= 0 && mouseY <= 9) {
         Mouse.showCursor(EnumCursorType.HAND);
         drawRect(this.width - i - 4, 0, this.width, 10, 0x55000099);
      } else {
         drawRect(this.width - i - 4, 0, this.width, 10, 0x55200000);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)(this.width - i - 2), 2.0F, 0.0F);
      GlStateManager.scalef(0.75F, 0.75F, 0.75F);
      this.drawString(this.fontRenderer, s, 0, 0, 16777215);
      GlStateManager.popMatrix();
   }

   //audrey <3
   private boolean mouseClickedCredits(double mouseX, double mouseY, int button) {
      if (button == 0) {
         String s = "CREDITS.txt";
         int i = this.fontRenderer.getStringWidth(s) * 3 / 4;
         if (mouseX >= (double)(this.width - i - 4) && mouseX <= (double)this.width && mouseY >= 0.0D && mouseY <= 10.0D) {
            String s1 = EagRuntime.getResourceString("/assets/eagler/CREDITS.txt");
            if (s1 != null) {
               EagRuntime.openCreditsPopup(s1);
            }

            this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
         }
      }

      return false;
   }

   private void addDemoButtons(int yIn, int rowHeightIn) {
      this.addButton(new GuiButton(11, this.width / 2 - 100, yIn, I18n.format("menu.playdemo")) {
         public void onClick(double mouseX, double mouseY) {
            // audrey <3
            if (SingleplayerServerController.isReady()) {
               GuiMainMenu.this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
            } else {
               GuiMainMenu.this.mc.displayGuiScreen(new GuiScreenDemoIntegratedServerStartup(new GuiScreenDemoPlayWorldSelection(GuiMainMenu.this)));
            }
         }
      });
      this.buttonResetDemo = this.addButton(new GuiButton(12, this.width / 2 - 100, yIn + rowHeightIn * 1, I18n.format("menu.resetdemo")) {
         public void onClick(double mouseX, double mouseY) {
            ISaveFormat isaveformat1 = GuiMainMenu.this.mc.getSaveLoader();
            WorldInfo worldinfo1 = isaveformat1.getWorldInfo("Demo_World");
            if (worldinfo1 != null) {
               GuiMainMenu.this.mc.displayGuiScreen(new GuiYesNo(GuiMainMenu.this, I18n.format("selectWorld.deleteQuestion"), I18n.format("selectWorld.deleteWarning", worldinfo1.getWorldName()), I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), 12));
            }

         }
      });
      ISaveFormat isaveformat = this.mc.getSaveLoader();
      WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
      if (worldinfo == null) {
         this.buttonResetDemo.enabled = false;
      }

   }

   private void switchToRealms() {
      RealmsBridge realmsbridge = new RealmsBridge();
      realmsbridge.switchToRealms(this);
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      if (p_confirmResult_1_ && p_confirmResult_2_ == 12) {
         ISaveFormat isaveformat = this.mc.getSaveLoader();
         isaveformat.flushCache();
         isaveformat.deleteWorldDirectory("Demo_World");
         this.mc.displayGuiScreen(this);
      } else if (p_confirmResult_2_ == 12) {
         this.mc.displayGuiScreen(this);
      } else if (p_confirmResult_2_ == 13) {
         if (p_confirmResult_1_) {
            Util.getOSType().openURI(this.openGLWarningLink);
         }

         this.mc.displayGuiScreen(this);
      }

   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.panorama.render(partialTicks);
      int i = 274;
      int j = this.width / 2 - 137;
      int k = 30;
      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/title/background/panorama_overlay.png"));
      drawScaledCustomSizeModalRect(0, 0, 0.0F, 0.0F, 16, 128, this.width, this.height, 16.0F, 128.0F);
      this.mc.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if ((double)this.minceraftRoll < 1.0E-4D) {
         this.drawTexturedModalRect(j + 0, 30, 0, 0, 99, 44);
         this.drawTexturedModalRect(j + 99, 30, 129, 0, 27, 44);
         this.drawTexturedModalRect(j + 99 + 26, 30, 126, 0, 3, 44);
         this.drawTexturedModalRect(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
         this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
      } else {
         this.drawTexturedModalRect(j + 0, 30, 0, 0, 155, 44);
         this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
      }

      this.mc.getTextureManager().bindTexture(MINECRAFT_TITLE_EDITION);
      drawModalRectWithCustomSizedTexture(j + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
      GlStateManager.rotatef(-20.0F, 0.0F, 0.0F, 1.0F);
      float f = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.milliTime() % 1000L) / 1000.0F * ((float)Math.PI * 2F)) * 0.1F);
      f = f * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
      GlStateManager.scalef(f, f, f);
      this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, -256);
      GlStateManager.popMatrix();

      //audrey <3
      this.drawString(this.fontRenderer, EaglercraftVersion.mainMenuStringA, 2, this.height - (10 + this.fontRenderer.FONT_HEIGHT), -1);
      this.drawString(this.fontRenderer, EaglercraftVersion.mainMenuStringB, 2, this.height - 10, -1);
      this.drawString(this.fontRenderer, EaglercraftVersion.mainMenuStringD, this.widthCopyrightRest, this.height - 10, -1);
      if (mouseX > this.widthCopyrightRest && mouseX < this.widthCopyrightRest + this.widthCopyright && mouseY > this.height - 10 && mouseY < this.height) {
         drawRect(this.widthCopyrightRest, this.height - 1, this.widthCopyrightRest + this.widthCopyright, this.height, -1);
      }

      if (this.openGLWarning1 != null && !this.openGLWarning1.isEmpty()) {
         drawRect(this.openGLWarningX1 - 2, this.openGLWarningY1 - 2, this.openGLWarningX2 + 2, this.openGLWarningY2 - 1, 1428160512);
         this.drawString(this.fontRenderer, this.openGLWarning1, this.openGLWarningX1, this.openGLWarningY1, -1);
         this.drawString(this.fontRenderer, this.openGLWarning2, (this.width - this.openGLWarning2Width) / 2, this.openGLWarningY1 + 12, -1);
      }

      super.render(mouseX, mouseY, partialTicks);
      //audrey <3
      this.drawCreditsLink(mouseX, mouseY);
      if (this.areRealmsNotificationsEnabled()) {
         this.realmsNotification.render(mouseX, mouseY, partialTicks);
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      //audrey <3
      if (this.mouseClickedCredits(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      }

      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         synchronized(this.threadLock) {
            if (!this.openGLWarning1.isEmpty() && !StringUtils.isNullOrEmpty(this.openGLWarningLink) && p_mouseClicked_1_ >= (double)this.openGLWarningX1 && p_mouseClicked_1_ <= (double)this.openGLWarningX2 && p_mouseClicked_3_ >= (double)this.openGLWarningY1 && p_mouseClicked_3_ <= (double)this.openGLWarningY2) {
               GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
               guiconfirmopenlink.disableSecurityWarning();
               this.mc.displayGuiScreen(guiconfirmopenlink);
               return true;
            }
         }

         if (this.areRealmsNotificationsEnabled() && this.realmsNotification.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
         } else {
            if (p_mouseClicked_1_ > (double)this.widthCopyrightRest && p_mouseClicked_1_ < (double)(this.widthCopyrightRest + this.widthCopyright) && p_mouseClicked_3_ > (double)(this.height - 10) && p_mouseClicked_3_ < (double)this.height) {
               this.mc.displayGuiScreen(new GuiWinGame(false, Runnables.doNothing()));
            }

            return false;
         }
      }
   }

   public void onGuiClosed() {
      if (this.realmsNotification != null) {
         this.realmsNotification.onGuiClosed();
      }

   }
}
