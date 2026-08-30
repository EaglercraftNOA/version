package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenLANConnect;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenLANInfo;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenLANNotSupported;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenCreateWorldSelection;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerBusy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiWorldSelection extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   protected GuiScreen prevScreen;
   protected String title = "Select world";
   private String worldVersTooltip;
   private GuiButton deleteButton;
   private GuiButton selectButton;
   private GuiButton renameButton;
   private GuiButton copyButton;
   protected GuiTextField field_212352_g;
   private GuiListWorldSelection selectionList;
   // audrey <3
   private boolean hasRequestedWorlds;
   private boolean waitingForWorlds;

   public GuiWorldSelection(GuiScreen screenIn) {
      this.prevScreen = screenIn;
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      return this.selectionList.mouseScrolled(p_mouseScrolled_1_);
   }

   public void tick() {
      this.field_212352_g.tick();
      if (!this.hasRequestedWorlds && SingleplayerServerController.isReady()) {
         this.hasRequestedWorlds = true;
         this.waitingForWorlds = true;
         SingleplayerServerController.instance.flushCache();
         this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(this, "singleplayer.busy.listingworlds", "singleplayer.failed.listingworlds", SingleplayerServerController::isReady, (t, u) -> {
            GuiScreenIntegratedServerBusy tt = (GuiScreenIntegratedServerBusy)t;
            this.mc.displayGuiScreen(GuiScreenIntegratedServerBusy.createException(this.prevScreen, tt.failMessage, u));
         }));
      } else if (this.waitingForWorlds && SingleplayerServerController.isReady()) {
         this.waitingForWorlds = false;
         this.selectionList.func_212330_a(() -> {
            return this.field_212352_g.getText();
         }, true);
      }
      // audrey <3
   }

   protected void initGui() {
      this.mc.keyboardListener.enableRepeatEvents(true);
      this.title = I18n.format("selectWorld.title");
      this.field_212352_g = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 22, 200, 20, this.field_212352_g) {
         public void setFocused(boolean isFocusedIn) {
            super.setFocused(true);
         }
      };
      this.field_212352_g.setTextAcceptHandler((p_212350_1_, p_212350_2_) -> {
         this.selectionList.func_212330_a(() -> {
            return p_212350_2_;
         }, false);
      });
      this.selectionList = new GuiListWorldSelection(this, this.mc, this.width, this.height, 48, this.height - 64, 36, () -> {
         return this.field_212352_g.getText();
      }, this.selectionList);
      this.selectButton = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")) {
         public void onClick(double mouseX, double mouseY) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.joinWorld();
            }

         }
      });
      this.addButton(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")) {
         public void onClick(double mouseX, double mouseY) {
            // audrey <3
            GuiWorldSelection.this.mc.displayGuiScreen(new GuiScreenCreateWorldSelection(GuiWorldSelection.this));
         }
      });
      // audrey <3
      this.renameButton = this.addButton(new GuiButton(4, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename")) {
         public void onClick(double mouseX, double mouseY) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.editWorld();
            }

         }
      });
      this.deleteButton = this.addButton(new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")) {
         public void onClick(double mouseX, double mouseY) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.deleteWorld();
            }

         }
      });
      // audrey <3
      this.copyButton = this.addButton(new GuiButton(5, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.backup")) {
         public void onClick(double mouseX, double mouseY) {
            GuiListWorldSelectionEntry guilistworldselectionentry = GuiWorldSelection.this.selectionList.getSelectedWorld();
            if (guilistworldselectionentry != null) {
               guilistworldselectionentry.recreateWorld();
            }

         }
      });
      this.addButton(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")) {
         public void onClick(double mouseX, double mouseY) {
            GuiWorldSelection.this.mc.displayGuiScreen(GuiWorldSelection.this.prevScreen);
         }
      });
      this.selectButton.enabled = false;
      this.deleteButton.enabled = false;
      this.renameButton.enabled = false;
      this.copyButton.enabled = false;
      this.children.add(this.field_212352_g);
      this.children.add(this.selectionList);
      this.field_212352_g.setFocused(true);
      this.field_212352_g.setCanLoseFocus(false);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ? true : this.field_212352_g.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      return this.field_212352_g.charTyped(p_charTyped_1_, p_charTyped_2_);
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.worldVersTooltip = null;
      this.selectionList.drawScreen(mouseX, mouseY, partialTicks);
      this.field_212352_g.drawTextField(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.title, this.width / 2, 8, 16777215);
      //audrey <3
      this.drawSharedWorldLink(mouseX, mouseY);
      super.render(mouseX, mouseY, partialTicks);
      if (this.worldVersTooltip != null) {
         this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.worldVersTooltip)), mouseX, mouseY);
      }

   }

   //audrey <3
   private String eaglerFormat(String key, String fallback) {
      return I18n.hasKey(key) ? I18n.format(key) : fallback;
   }

   //audrey <3
   private void drawSharedWorldLink(int mouseX, int mouseY) {
      GlStateManager.pushMatrix();
      GlStateManager.scalef(0.75F, 0.75F, 0.75F);
      String s = this.eaglerFormat("directConnect.lanWorld", "Join Shared World");
      int i = this.fontRenderer.getStringWidth(s);
      boolean flag = mouseX > 1 && mouseY > 1 && mouseX < i * 3 / 4 + 7 && mouseY < 12;
      if (flag) {
         Mouse.showCursor(EnumCursorType.HAND);
      }

      this.drawString(this.fontRenderer, net.minecraft.util.text.TextFormatting.UNDERLINE + s, 5, 5, flag ? 0xFFEEEE22 : 0xFFCCCCCC);
      GlStateManager.popMatrix();
   }

   //audrey <3
   private boolean mouseClickedSharedWorld(double mouseX, double mouseY, int button) {
      if (button == 0) {
         String s = this.eaglerFormat("directConnect.lanWorld", "Join Shared World");
         int i = this.fontRenderer.getStringWidth(s);
         if (mouseX > 2.0D && mouseY > 2.0D && mouseX < (double)(i * 3 / 4 + 5) && mouseY < 12.0D) {
            if (LANServerController.supported()) {
               this.mc.displayGuiScreen(GuiScreenLANInfo.showLANInfoScreen(new GuiScreenLANConnect(this)));
            } else {
               this.mc.displayGuiScreen(new GuiScreenLANNotSupported(this));
            }

            this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            return true;
         }
      }

      return false;
   }

   //audrey <3
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return this.mouseClickedSharedWorld(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
   }

   public void setVersionTooltip(String p_184861_1_) {
      this.worldVersTooltip = p_184861_1_;
   }

   public void selectWorld(@Nullable GuiListWorldSelectionEntry entry) {
      boolean flag = entry != null;
      this.selectButton.enabled = flag;
      this.deleteButton.enabled = flag;
      this.renameButton.enabled = flag;
      this.copyButton.enabled = flag;
   }

   // audrey <3
   public void reloadWorlds() {
      this.hasRequestedWorlds = false;
      this.waitingForWorlds = false;
   }

   public void onGuiClosed() {
      if (this.selectionList != null) {
         this.selectionList.getChildren().forEach(GuiListWorldSelectionEntry::close);
      }

   }
}
