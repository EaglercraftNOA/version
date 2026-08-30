package net.minecraft.client.gui.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.HotbarSnapshot;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiContainerCreative extends InventoryEffectRenderer {
   private static final ResourceLocation CREATIVE_INVENTORY_TABS = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final InventoryBasic TMP_INVENTORY = new InventoryBasic(new TextComponentString("tmp"), 45);
   private static int selectedTabIndex = ItemGroup.BUILDING_BLOCKS.getIndex();
   private float currentScroll;
   private boolean isScrolling;
   private GuiTextField searchField;
   private List<Slot> originalSlots;
   private Slot destroyItemSlot;
   private CreativeCrafting listener;
   private boolean field_195377_F;
   private boolean field_199506_G;

   public GuiContainerCreative(EntityPlayer player) {
      super(new GuiContainerCreative.ContainerCreative(player));
      player.openContainer = this.inventorySlots;
      this.allowUserInput = true;
      this.ySize = 136;
      this.xSize = 195;
   }

   public void tick() {
      if (!this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.player));
      }

   }

   protected void handleMouseClick(@Nullable Slot slotIn, int slotId, int mouseButton, ClickType type) {
      if (this.hasTmpInventory(slotIn)) {
         this.searchField.setCursorPositionEnd();
         this.searchField.setSelectionPos(0);
      }

      boolean flag = type == ClickType.QUICK_MOVE;
      type = slotId == -999 && type == ClickType.PICKUP ? ClickType.THROW : type;
      if (slotIn == null && selectedTabIndex != ItemGroup.INVENTORY.getIndex() && type != ClickType.QUICK_CRAFT) {
         InventoryPlayer inventoryplayer1 = this.mc.player.inventory;
         if (!inventoryplayer1.getItemStack().isEmpty() && this.field_199506_G) {
            if (mouseButton == 0) {
               this.mc.player.dropItem(inventoryplayer1.getItemStack(), true);
               this.mc.playerController.sendPacketDropItem(inventoryplayer1.getItemStack());
               inventoryplayer1.setItemStack(ItemStack.EMPTY);
            }

            if (mouseButton == 1) {
               ItemStack itemstack6 = inventoryplayer1.getItemStack().split(1);
               this.mc.player.dropItem(itemstack6, true);
               this.mc.playerController.sendPacketDropItem(itemstack6);
            }
         }
      } else {
         if (slotIn != null && !slotIn.canTakeStack(this.mc.player)) {
            return;
         }

         if (slotIn == this.destroyItemSlot && flag) {
            for(int j = 0; j < this.mc.player.inventoryContainer.getInventory().size(); ++j) {
               this.mc.playerController.sendSlotPacket(ItemStack.EMPTY, j);
            }
         } else if (selectedTabIndex == ItemGroup.INVENTORY.getIndex()) {
            if (slotIn == this.destroyItemSlot) {
               this.mc.player.inventory.setItemStack(ItemStack.EMPTY);
            } else if (type == ClickType.THROW && slotIn != null && slotIn.getHasStack()) {
               ItemStack itemstack = slotIn.decrStackSize(mouseButton == 0 ? 1 : slotIn.getStack().getMaxStackSize());
               ItemStack itemstack1 = slotIn.getStack();
               this.mc.player.dropItem(itemstack, true);
               this.mc.playerController.sendPacketDropItem(itemstack);
               this.mc.playerController.sendSlotPacket(itemstack1, ((GuiContainerCreative.CreativeSlot)slotIn).slot.slotNumber);
            } else if (type == ClickType.THROW && !this.mc.player.inventory.getItemStack().isEmpty()) {
               this.mc.player.dropItem(this.mc.player.inventory.getItemStack(), true);
               this.mc.playerController.sendPacketDropItem(this.mc.player.inventory.getItemStack());
               this.mc.player.inventory.setItemStack(ItemStack.EMPTY);
            } else {
               this.mc.player.inventoryContainer.slotClick(slotIn == null ? slotId : ((GuiContainerCreative.CreativeSlot)slotIn).slot.slotNumber, mouseButton, type, this.mc.player);
               this.mc.player.inventoryContainer.detectAndSendChanges();
            }
         } else if (type != ClickType.QUICK_CRAFT && slotIn.inventory == TMP_INVENTORY) {
            InventoryPlayer inventoryplayer = this.mc.player.inventory;
            ItemStack itemstack5 = inventoryplayer.getItemStack();
            ItemStack itemstack7 = slotIn.getStack();
            if (type == ClickType.SWAP) {
               if (!itemstack7.isEmpty() && mouseButton >= 0 && mouseButton < 9) {
                  ItemStack itemstack10 = itemstack7.copy();
                  itemstack10.setCount(itemstack10.getMaxStackSize());
                  this.mc.player.inventory.setInventorySlotContents(mouseButton, itemstack10);
                  this.mc.player.inventoryContainer.detectAndSendChanges();
               }

               return;
            }

            if (type == ClickType.CLONE) {
               if (inventoryplayer.getItemStack().isEmpty() && slotIn.getHasStack()) {
                  ItemStack itemstack9 = slotIn.getStack().copy();
                  itemstack9.setCount(itemstack9.getMaxStackSize());
                  inventoryplayer.setItemStack(itemstack9);
               }

               return;
            }

            if (type == ClickType.THROW) {
               if (!itemstack7.isEmpty()) {
                  ItemStack itemstack8 = itemstack7.copy();
                  itemstack8.setCount(mouseButton == 0 ? 1 : itemstack8.getMaxStackSize());
                  this.mc.player.dropItem(itemstack8, true);
                  this.mc.playerController.sendPacketDropItem(itemstack8);
               }

               return;
            }

            if (!itemstack5.isEmpty() && !itemstack7.isEmpty() && itemstack5.isItemEqual(itemstack7) && ItemStack.areItemStackTagsEqual(itemstack5, itemstack7)) {
               if (mouseButton == 0) {
                  if (flag) {
                     itemstack5.setCount(itemstack5.getMaxStackSize());
                  } else if (itemstack5.getCount() < itemstack5.getMaxStackSize()) {
                     itemstack5.grow(1);
                  }
               } else {
                  itemstack5.shrink(1);
               }
            } else if (!itemstack7.isEmpty() && itemstack5.isEmpty()) {
               inventoryplayer.setItemStack(itemstack7.copy());
               itemstack5 = inventoryplayer.getItemStack();
               if (flag) {
                  itemstack5.setCount(itemstack5.getMaxStackSize());
               }
            } else if (mouseButton == 0) {
               inventoryplayer.setItemStack(ItemStack.EMPTY);
            } else {
               inventoryplayer.getItemStack().shrink(1);
            }
         } else if (this.inventorySlots != null) {
            ItemStack itemstack3 = slotIn == null ? ItemStack.EMPTY : this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
            this.inventorySlots.slotClick(slotIn == null ? slotId : slotIn.slotNumber, mouseButton, type, this.mc.player);
            if (Container.getDragEvent(mouseButton) == 2) {
               for(int k = 0; k < 9; ++k) {
                  this.mc.playerController.sendSlotPacket(this.inventorySlots.getSlot(45 + k).getStack(), 36 + k);
               }
            } else if (slotIn != null) {
               ItemStack itemstack4 = this.inventorySlots.getSlot(slotIn.slotNumber).getStack();
               this.mc.playerController.sendSlotPacket(itemstack4, slotIn.slotNumber - this.inventorySlots.inventorySlots.size() + 9 + 36);
               int i = 45 + mouseButton;
               if (type == ClickType.SWAP) {
                  this.mc.playerController.sendSlotPacket(itemstack3, i - this.inventorySlots.inventorySlots.size() + 9 + 36);
               } else if (type == ClickType.THROW && !itemstack3.isEmpty()) {
                  ItemStack itemstack2 = itemstack3.copy();
                  itemstack2.setCount(mouseButton == 0 ? 1 : itemstack2.getMaxStackSize());
                  this.mc.player.dropItem(itemstack2, true);
                  this.mc.playerController.sendPacketDropItem(itemstack2);
               }

               this.mc.player.inventoryContainer.detectAndSendChanges();
            }
         }
      }

   }

   private boolean hasTmpInventory(@Nullable Slot p_208018_1_) {
      return p_208018_1_ != null && p_208018_1_.inventory == TMP_INVENTORY;
   }

   protected void updateActivePotionEffects() {
      int i = this.guiLeft;
      super.updateActivePotionEffects();
      if (this.searchField != null && this.guiLeft != i) {
         this.searchField.x = this.guiLeft + 82;
      }

   }

   protected void initGui() {
      if (this.mc.playerController.isInCreativeMode()) {
         super.initGui();
         this.mc.keyboardListener.enableRepeatEvents(true);
         this.searchField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 82, this.guiTop + 6, 80, this.fontRenderer.FONT_HEIGHT);
         this.searchField.setMaxStringLength(50);
         this.searchField.setEnableBackgroundDrawing(false);
         this.searchField.setVisible(false);
         this.searchField.setTextColor(16777215);
         this.children.add(this.searchField);
         int i = selectedTabIndex;
         selectedTabIndex = -1;
         this.setCurrentCreativeTab(ItemGroup.GROUPS[i]);
         this.listener = new CreativeCrafting(this.mc);
         this.mc.player.inventoryContainer.addListener(this.listener);
      } else {
         this.mc.displayGuiScreen(new GuiInventory(this.mc.player));
      }

   }

   public void onResize(Minecraft mcIn, int w, int h) {
      String s = this.searchField.getText();
      this.setWorldAndResolution(mcIn, w, h);
      this.searchField.setText(s);
      if (!this.searchField.getText().isEmpty()) {
         this.updateCreativeSearch();
      }

   }

   public void onGuiClosed() {
      super.onGuiClosed();
      if (this.mc.player != null && this.mc.player.inventory != null) {
         this.mc.player.inventoryContainer.removeListener(this.listener);
      }

      this.mc.keyboardListener.enableRepeatEvents(false);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (this.field_195377_F) {
         return false;
      } else if (selectedTabIndex != ItemGroup.SEARCH.getIndex()) {
         return false;
      } else {
         String s = this.searchField.getText();
         if (this.searchField.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            if (!Objects.equals(s, this.searchField.getText())) {
               this.updateCreativeSearch();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      this.field_195377_F = false;
      if (selectedTabIndex != ItemGroup.SEARCH.getIndex()) {
         if (this.mc.gameSettings.keyBindChat.matchesKey(p_keyPressed_1_, p_keyPressed_2_)) {
            this.field_195377_F = true;
            this.setCurrentCreativeTab(ItemGroup.SEARCH);
            return true;
         } else {
            return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
         }
      } else {
         boolean flag = !this.hasTmpInventory(this.hoveredSlot) || this.hoveredSlot != null && this.hoveredSlot.getHasStack();
         if (flag && this.func_195363_d(p_keyPressed_1_, p_keyPressed_2_)) {
            this.field_195377_F = true;
            return true;
         } else {
            String s = this.searchField.getText();
            if (this.searchField.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
               if (!Objects.equals(s, this.searchField.getText())) {
                  this.updateCreativeSearch();
               }

               return true;
            } else {
               return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
            }
         }
      }
   }

   public boolean keyReleased(int p_keyReleased_1_, int p_keyReleased_2_, int p_keyReleased_3_) {
      this.field_195377_F = false;
      return super.keyReleased(p_keyReleased_1_, p_keyReleased_2_, p_keyReleased_3_);
   }

   private void updateCreativeSearch() {
      GuiContainerCreative.ContainerCreative guicontainercreative$containercreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      guicontainercreative$containercreative.itemList.clear();
      if (this.searchField.getText().isEmpty()) {
         for(Item item : IRegistry.ITEM) {
            item.fillItemGroup(ItemGroup.SEARCH, guicontainercreative$containercreative.itemList);
         }
      } else {
         guicontainercreative$containercreative.itemList.addAll(this.mc.getSearchTree(SearchTreeManager.ITEMS).search(this.searchField.getText().toLowerCase(Locale.ROOT)));
      }

      this.currentScroll = 0.0F;
      guicontainercreative$containercreative.scrollTo(0.0F);
   }

   protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      ItemGroup itemgroup = ItemGroup.GROUPS[selectedTabIndex];
      if (itemgroup.drawInForegroundOfTab()) {
         GlStateManager.disableBlend();
         this.fontRenderer.drawString(I18n.format(itemgroup.getTranslationKey()), 8.0F, 6.0F, 4210752);
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         double d0 = p_mouseClicked_1_ - (double)this.guiLeft;
         double d1 = p_mouseClicked_3_ - (double)this.guiTop;

         for(ItemGroup itemgroup : ItemGroup.GROUPS) {
            if (this.isMouseOverGroup(itemgroup, d0, d1)) {
               return true;
            }
         }

         if (selectedTabIndex != ItemGroup.INVENTORY.getIndex() && this.func_195376_a(p_mouseClicked_1_, p_mouseClicked_3_)) {
            this.isScrolling = this.needsScrollBars();
            return true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      if (p_mouseReleased_5_ == 0) {
         double d0 = p_mouseReleased_1_ - (double)this.guiLeft;
         double d1 = p_mouseReleased_3_ - (double)this.guiTop;
         this.isScrolling = false;

         for(ItemGroup itemgroup : ItemGroup.GROUPS) {
            if (this.isMouseOverGroup(itemgroup, d0, d1)) {
               this.setCurrentCreativeTab(itemgroup);
               return true;
            }
         }
      }

      return super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_);
   }

   private boolean needsScrollBars() {
      return selectedTabIndex != ItemGroup.INVENTORY.getIndex() && ItemGroup.GROUPS[selectedTabIndex].hasScrollbar() && ((GuiContainerCreative.ContainerCreative)this.inventorySlots).canScroll();
   }

   private void setCurrentCreativeTab(ItemGroup tab) {
      int i = selectedTabIndex;
      selectedTabIndex = tab.getIndex();
      GuiContainerCreative.ContainerCreative guicontainercreative$containercreative = (GuiContainerCreative.ContainerCreative)this.inventorySlots;
      this.dragSplittingSlots.clear();
      guicontainercreative$containercreative.itemList.clear();
      if (tab == ItemGroup.HOTBAR) {
         CreativeSettings creativesettings = this.mc.getCreativeSettings();

         for(int j = 0; j < 9; ++j) {
            HotbarSnapshot hotbarsnapshot = creativesettings.getHotbarSnapshot(j);
            if (hotbarsnapshot.isEmpty()) {
               for(int k = 0; k < 9; ++k) {
                  if (k == j) {
                     ItemStack itemstack = new ItemStack(Items.PAPER);
                     itemstack.getOrCreateChildTag("CustomCreativeLock");
                     String s = this.mc.gameSettings.keyBindsHotbar[j].func_197978_k();
                     String s1 = this.mc.gameSettings.keyBindSaveToolbar.func_197978_k();
                     itemstack.setDisplayName(new TextComponentTranslation("inventory.hotbarInfo", s1, s));
                     guicontainercreative$containercreative.itemList.add(itemstack);
                  } else {
                     guicontainercreative$containercreative.itemList.add(ItemStack.EMPTY);
                  }
               }
            } else {
               guicontainercreative$containercreative.itemList.addAll(hotbarsnapshot);
            }
         }
      } else if (tab != ItemGroup.SEARCH) {
         tab.fill(guicontainercreative$containercreative.itemList);
      }

      if (tab == ItemGroup.INVENTORY) {
         Container container = this.mc.player.inventoryContainer;
         if (this.originalSlots == null) {
            this.originalSlots = guicontainercreative$containercreative.inventorySlots;
         }

         guicontainercreative$containercreative.inventorySlots = Lists.newArrayList();

         for(int l = 0; l < container.inventorySlots.size(); ++l) {
            Slot slot = new GuiContainerCreative.CreativeSlot(container.inventorySlots.get(l), l);
            guicontainercreative$containercreative.inventorySlots.add(slot);
            if (l >= 5 && l < 9) {
               int j1 = l - 5;
               int l1 = j1 / 2;
               int j2 = j1 % 2;
               slot.xPos = 54 + l1 * 54;
               slot.yPos = 6 + j2 * 27;
            } else if (l >= 0 && l < 5) {
               slot.xPos = -2000;
               slot.yPos = -2000;
            } else if (l == 45) {
               slot.xPos = 35;
               slot.yPos = 20;
            } else if (l < container.inventorySlots.size()) {
               int i1 = l - 9;
               int k1 = i1 % 9;
               int i2 = i1 / 9;
               slot.xPos = 9 + k1 * 18;
               if (l >= 36) {
                  slot.yPos = 112;
               } else {
                  slot.yPos = 54 + i2 * 18;
               }
            }
         }

         this.destroyItemSlot = new Slot(TMP_INVENTORY, 0, 173, 112);
         guicontainercreative$containercreative.inventorySlots.add(this.destroyItemSlot);
      } else if (i == ItemGroup.INVENTORY.getIndex()) {
         guicontainercreative$containercreative.inventorySlots = this.originalSlots;
         this.originalSlots = null;
      }

      if (this.searchField != null) {
         if (tab == ItemGroup.SEARCH) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocused(true);
            if (i != tab.getIndex()) {
               this.searchField.setText("");
            }

            this.updateCreativeSearch();
         } else {
            this.searchField.setVisible(false);
            this.searchField.setCanLoseFocus(true);
            this.searchField.setFocused(false);
            this.searchField.setText("");
         }
      }

      this.currentScroll = 0.0F;
      guicontainercreative$containercreative.scrollTo(0.0F);
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_) {
      if (!this.needsScrollBars()) {
         return false;
      } else {
         int i = (((GuiContainerCreative.ContainerCreative)this.inventorySlots).itemList.size() + 9 - 1) / 9 - 5;
         this.currentScroll = (float)((double)this.currentScroll - p_mouseScrolled_1_ / (double)i);
         this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
         return true;
      }
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      boolean flag = p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.xSize) || p_195361_3_ >= (double)(p_195361_6_ + this.ySize);
      this.field_199506_G = flag && !this.isMouseOverGroup(ItemGroup.GROUPS[selectedTabIndex], p_195361_1_, p_195361_3_);
      return this.field_199506_G;
   }

   protected boolean func_195376_a(double p_195376_1_, double p_195376_3_) {
      int i = this.guiLeft;
      int j = this.guiTop;
      int k = i + 175;
      int l = j + 18;
      int i1 = k + 14;
      int j1 = l + 112;
      return p_195376_1_ >= (double)k && p_195376_3_ >= (double)l && p_195376_1_ < (double)i1 && p_195376_3_ < (double)j1;
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.isScrolling) {
         int i = this.guiTop + 18;
         int j = i + 112;
         this.currentScroll = ((float)p_mouseDragged_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.currentScroll = MathHelper.clamp(this.currentScroll, 0.0F, 1.0F);
         ((GuiContainerCreative.ContainerCreative)this.inventorySlots).scrollTo(this.currentScroll);
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      super.render(mouseX, mouseY, partialTicks);

      for(ItemGroup itemgroup : ItemGroup.GROUPS) {
         if (this.renderCreativeInventoryHoveringText(itemgroup, mouseX, mouseY)) {
            break;
         }
      }

      if (this.destroyItemSlot != null && selectedTabIndex == ItemGroup.INVENTORY.getIndex() && this.isPointInRegion(this.destroyItemSlot.xPos, this.destroyItemSlot.yPos, 16, 16, (double)mouseX, (double)mouseY)) {
         this.drawHoveringText(I18n.format("inventory.binSlot"), mouseX, mouseY);
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      this.renderHoveredToolTip(mouseX, mouseY);
   }

   protected void renderToolTip(ItemStack stack, int x, int y) {
      if (selectedTabIndex == ItemGroup.SEARCH.getIndex()) {
         List<ITextComponent> list = stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
         List<String> list1 = Lists.newArrayListWithCapacity(list.size());

         for(ITextComponent itextcomponent : list) {
            list1.add(itextcomponent.getFormattedText());
         }

         ItemGroup itemgroup1 = stack.getItem().getGroup();
         if (itemgroup1 == null && stack.getItem() == Items.ENCHANTED_BOOK) {
            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(stack);
            if (map.size() == 1) {
               Enchantment enchantment = map.keySet().iterator().next();

               for(ItemGroup itemgroup : ItemGroup.GROUPS) {
                  if (itemgroup.hasRelevantEnchantmentType(enchantment.type)) {
                     itemgroup1 = itemgroup;
                     break;
                  }
               }
            }
         }

         if (itemgroup1 != null) {
            list1.add(1, "" + TextFormatting.BOLD + TextFormatting.BLUE + I18n.format(itemgroup1.getTranslationKey()));
         }

         for(int i = 0; i < list1.size(); ++i) {
            if (i == 0) {
               list1.set(i, stack.getRarity().color + (String)list1.get(i));
            } else {
               list1.set(i, TextFormatting.GRAY + (String)list1.get(i));
            }
         }

         this.drawHoveringText(list1, x, y);
      } else {
         super.renderToolTip(stack, x, y);
      }

   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderHelper.enableGUIStandardItemLighting();
      ItemGroup itemgroup = ItemGroup.GROUPS[selectedTabIndex];

      for(ItemGroup itemgroup1 : ItemGroup.GROUPS) {
         this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
         if (itemgroup1.getIndex() != selectedTabIndex) {
            this.drawTab(itemgroup1);
         }
      }

      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + itemgroup.getBackgroundImageName()));
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      this.searchField.drawTextField(mouseX, mouseY, partialTicks);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i = this.guiLeft + 175;
      int j = this.guiTop + 18;
      int k = j + 112;
      this.mc.getTextureManager().bindTexture(CREATIVE_INVENTORY_TABS);
      if (itemgroup.hasScrollbar()) {
         this.drawTexturedModalRect(i, j + (int)((float)(k - j - 17) * this.currentScroll), 232 + (this.needsScrollBars() ? 0 : 12), 0, 12, 15);
      }

      this.drawTab(itemgroup);
      if (itemgroup == ItemGroup.INVENTORY) {
         GuiInventory.drawEntityOnScreen(this.guiLeft + 88, this.guiTop + 45, 20, (float)(this.guiLeft + 88 - mouseX), (float)(this.guiTop + 45 - 30 - mouseY), this.mc.player);
      }

   }

   protected boolean isMouseOverGroup(ItemGroup p_195375_1_, double p_195375_2_, double p_195375_4_) {
      int i = p_195375_1_.getColumn();
      int j = 28 * i;
      int k = 0;
      if (p_195375_1_.isAlignedRight()) {
         j = this.xSize - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      if (p_195375_1_.isOnTopRow()) {
         k = k - 32;
      } else {
         k = k + this.ySize;
      }

      return p_195375_2_ >= (double)j && p_195375_2_ <= (double)(j + 28) && p_195375_4_ >= (double)k && p_195375_4_ <= (double)(k + 32);
   }

   protected boolean renderCreativeInventoryHoveringText(ItemGroup tab, int mouseX, int mouseY) {
      int i = tab.getColumn();
      int j = 28 * i;
      int k = 0;
      if (tab.isAlignedRight()) {
         j = this.xSize - 28 * (6 - i) + 2;
      } else if (i > 0) {
         j += i;
      }

      if (tab.isOnTopRow()) {
         k = k - 32;
      } else {
         k = k + this.ySize;
      }

      if (this.isPointInRegion(j + 3, k + 3, 23, 27, (double)mouseX, (double)mouseY)) {
         this.drawHoveringText(I18n.format(tab.getTranslationKey()), mouseX, mouseY);
         return true;
      } else {
         return false;
      }
   }

   protected void drawTab(ItemGroup tab) {
      boolean flag = tab.getIndex() == selectedTabIndex;
      boolean flag1 = tab.isOnTopRow();
      int i = tab.getColumn();
      int j = i * 28;
      int k = 0;
      int l = this.guiLeft + 28 * i;
      int i1 = this.guiTop;
      int j1 = 32;
      if (flag) {
         k += 32;
      }

      if (tab.isAlignedRight()) {
         l = this.guiLeft + this.xSize - 28 * (6 - i);
      } else if (i > 0) {
         l += i;
      }

      if (flag1) {
         i1 = i1 - 28;
      } else {
         k += 64;
         i1 = i1 + (this.ySize - 4);
      }

      GlStateManager.disableLighting();
      this.drawTexturedModalRect(l, i1, j, k, 28, 32);
      this.zLevel = 100.0F;
      this.itemRender.zLevel = 100.0F;
      l = l + 6;
      i1 = i1 + 8 + (flag1 ? 1 : -1);
      GlStateManager.enableLighting();
      GlStateManager.enableRescaleNormal();
      ItemStack itemstack = tab.getIcon();
      this.itemRender.renderItemAndEffectIntoGUI(itemstack, l, i1);
      this.itemRender.renderItemOverlays(this.fontRenderer, itemstack, l, i1);
      GlStateManager.disableLighting();
      this.itemRender.zLevel = 0.0F;
      this.zLevel = 0.0F;
   }

   public int getSelectedTabIndex() {
      return selectedTabIndex;
   }

   public static void handleHotbarSnapshots(Minecraft client, int index, boolean load, boolean save) {
      EntityPlayerSP entityplayersp = client.player;
      CreativeSettings creativesettings = client.getCreativeSettings();
      HotbarSnapshot hotbarsnapshot = creativesettings.getHotbarSnapshot(index);
      if (load) {
         for(int i = 0; i < InventoryPlayer.getHotbarSize(); ++i) {
            ItemStack itemstack = hotbarsnapshot.get(i).copy();
            entityplayersp.inventory.setInventorySlotContents(i, itemstack);
            client.playerController.sendSlotPacket(itemstack, 36 + i);
         }

         entityplayersp.inventoryContainer.detectAndSendChanges();
      } else if (save) {
         for(int j = 0; j < InventoryPlayer.getHotbarSize(); ++j) {
            hotbarsnapshot.set(j, entityplayersp.inventory.getStackInSlot(j).copy());
         }

         String s = client.gameSettings.keyBindsHotbar[index].func_197978_k();
         String s1 = client.gameSettings.keyBindLoadToolbar.func_197978_k();
         client.ingameGUI.setOverlayMessage(new TextComponentTranslation("inventory.hotbarSaved", s1, s), false);
         creativesettings.save();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class ContainerCreative extends Container {
      public NonNullList<ItemStack> itemList = NonNullList.create();

      public ContainerCreative(EntityPlayer player) {
         InventoryPlayer inventoryplayer = player.inventory;

         for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 9; ++j) {
               this.addSlot(new GuiContainerCreative.LockedSlot(GuiContainerCreative.TMP_INVENTORY, i * 9 + j, 9 + j * 18, 18 + i * 18));
            }
         }

         for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventoryplayer, k, 9 + k * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      public boolean canInteractWith(EntityPlayer playerIn) {
         return true;
      }

      public void scrollTo(float pos) {
         int i = (this.itemList.size() + 9 - 1) / 9 - 5;
         int j = (int)((double)(pos * (float)i) + 0.5D);
         if (j < 0) {
            j = 0;
         }

         for(int k = 0; k < 5; ++k) {
            for(int l = 0; l < 9; ++l) {
               int i1 = l + (k + j) * 9;
               if (i1 >= 0 && i1 < this.itemList.size()) {
                  GuiContainerCreative.TMP_INVENTORY.setInventorySlotContents(l + k * 9, this.itemList.get(i1));
               } else {
                  GuiContainerCreative.TMP_INVENTORY.setInventorySlotContents(l + k * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.itemList.size() > 45;
      }

      public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
         if (index >= this.inventorySlots.size() - 9 && index < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(index);
            if (slot != null && slot.getHasStack()) {
               slot.putStack(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
         return slotIn.yPos > 90;
      }

      public boolean canDragIntoSlot(Slot slotIn) {
         return slotIn.inventory instanceof InventoryPlayer || slotIn.yPos > 90 && slotIn.xPos <= 162;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class CreativeSlot extends Slot {
      private final Slot slot;

      public CreativeSlot(Slot p_i46313_2_, int index) {
         super(p_i46313_2_.inventory, index, 0, 0);
         this.slot = p_i46313_2_;
      }

      public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
         this.slot.onTake(thePlayer, stack);
         return stack;
      }

      public boolean isItemValid(ItemStack stack) {
         return this.slot.isItemValid(stack);
      }

      public ItemStack getStack() {
         return this.slot.getStack();
      }

      public boolean getHasStack() {
         return this.slot.getHasStack();
      }

      public void putStack(ItemStack stack) {
         this.slot.putStack(stack);
      }

      public void onSlotChanged() {
         this.slot.onSlotChanged();
      }

      public int getSlotStackLimit() {
         return this.slot.getSlotStackLimit();
      }

      public int getItemStackLimit(ItemStack stack) {
         return this.slot.getItemStackLimit(stack);
      }

      @Nullable
      public String getSlotTexture() {
         return this.slot.getSlotTexture();
      }

      public ItemStack decrStackSize(int amount) {
         return this.slot.decrStackSize(amount);
      }

      public boolean isHere(IInventory inv, int slotIn) {
         return this.slot.isHere(inv, slotIn);
      }

      public boolean isEnabled() {
         return this.slot.isEnabled();
      }

      public boolean canTakeStack(EntityPlayer playerIn) {
         return this.slot.canTakeStack(playerIn);
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class LockedSlot extends Slot {
      public LockedSlot(IInventory p_i47453_1_, int p_i47453_2_, int p_i47453_3_, int p_i47453_4_) {
         super(p_i47453_1_, p_i47453_2_, p_i47453_3_, p_i47453_4_);
      }

      public boolean canTakeStack(EntityPlayer playerIn) {
         if (super.canTakeStack(playerIn) && this.getHasStack()) {
            return this.getStack().getChildTag("CustomCreativeLock") == null;
         } else {
            return !this.getHasStack();
         }
      }
   }
}
