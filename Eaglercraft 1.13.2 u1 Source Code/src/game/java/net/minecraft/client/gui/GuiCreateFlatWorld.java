package net.minecraft.client.gui;

import com.mojang.serialization.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.FlatGenSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCreateFlatWorld extends GuiScreen {
   private final GuiCreateWorld createWorldGui;
   private FlatGenSettings generatorInfo = FlatGenSettings.getDefaultFlatGenerator();
   private String flatWorldTitle;
   private String materialText;
   private String heightText;
   private GuiCreateFlatWorld.Details createFlatWorldListSlotGui;
   private GuiButton addLayerButton;
   private GuiButton editLayerButton;
   private GuiButton removeLayerButton;

   public GuiCreateFlatWorld(GuiCreateWorld parent, NBTTagCompound generatorOptions) {
      this.createWorldGui = parent;
      this.setGeneratorOptions(generatorOptions);
   }

   public String getPreset() {
      return this.generatorInfo.toString();
   }

   public NBTTagCompound getGeneratorOptions() {
      return (NBTTagCompound)this.generatorInfo.func_210834_a(NBTDynamicOps.INSTANCE).getValue();
   }

   public void setPreset(String preset) {
      this.generatorInfo = FlatGenSettings.createFlatGeneratorFromString(preset);
   }

   public void setGeneratorOptions(NBTTagCompound nbt) {
      this.generatorInfo = FlatGenSettings.createFlatGenerator(new Dynamic<>(NBTDynamicOps.INSTANCE, nbt));
   }

   protected void initGui() {
      this.flatWorldTitle = I18n.format("createWorld.customize.flat.title");
      this.materialText = I18n.format("createWorld.customize.flat.tile");
      this.heightText = I18n.format("createWorld.customize.flat.height");
      this.createFlatWorldListSlotGui = new GuiCreateFlatWorld.Details();
      this.children.add(this.createFlatWorldListSlotGui);
      this.addLayerButton = this.addButton(new GuiButton(2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.addLayer") + " (NYI)") {
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.editLayerButton = this.addButton(new GuiButton(3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.editLayer") + " (NYI)") {
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.removeLayerButton = this.addButton(new GuiButton(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer")) {
         public void onClick(double mouseX, double mouseY) {
            if (GuiCreateFlatWorld.this.hasSelectedLayer()) {
               List<FlatLayerInfo> list = GuiCreateFlatWorld.this.generatorInfo.getFlatLayers();
               int i = list.size() - GuiCreateFlatWorld.this.createFlatWorldListSlotGui.selectedLayer - 1;
               list.remove(i);
               GuiCreateFlatWorld.this.createFlatWorldListSlotGui.selectedLayer = Math.min(GuiCreateFlatWorld.this.createFlatWorldListSlotGui.selectedLayer, list.size() - 1);
               GuiCreateFlatWorld.this.generatorInfo.updateLayers();
               GuiCreateFlatWorld.this.onLayersChanged();
            }
         }
      });
      this.addButton(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")) {
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.createWorldGui.chunkProviderSettingsJson = GuiCreateFlatWorld.this.getGeneratorOptions();
            GuiCreateFlatWorld.this.mc.displayGuiScreen(GuiCreateFlatWorld.this.createWorldGui);
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.addButton(new GuiButton(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets")) {
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.mc.displayGuiScreen(new GuiFlatPresets(GuiCreateFlatWorld.this));
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.addButton(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")) {
         public void onClick(double mouseX, double mouseY) {
            GuiCreateFlatWorld.this.mc.displayGuiScreen(GuiCreateFlatWorld.this.createWorldGui);
            GuiCreateFlatWorld.this.generatorInfo.updateLayers();
            GuiCreateFlatWorld.this.onLayersChanged();
         }
      });
      this.addLayerButton.visible = false;
      this.editLayerButton.visible = false;
      this.generatorInfo.updateLayers();
      this.onLayersChanged();
   }

   public void onLayersChanged() {
      boolean flag = this.hasSelectedLayer();
      this.removeLayerButton.enabled = flag;
      this.editLayerButton.enabled = flag;
      this.editLayerButton.enabled = false;
      this.addLayerButton.enabled = false;
   }

   private boolean hasSelectedLayer() {
      return this.createFlatWorldListSlotGui.selectedLayer > -1 && this.createFlatWorldListSlotGui.selectedLayer < this.generatorInfo.getFlatLayers().size();
   }

   @Nullable
   public IGuiEventListener getFocused() {
      return this.createFlatWorldListSlotGui;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, this.flatWorldTitle, this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      this.drawString(this.fontRenderer, this.materialText, i, 32, 16777215);
      this.drawString(this.fontRenderer, this.heightText, i + 2 + 213 - this.fontRenderer.getStringWidth(this.heightText), 32, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   class Details extends GuiSlot {
      public int selectedLayer = -1;

      public Details() {
         super(GuiCreateFlatWorld.this.mc, GuiCreateFlatWorld.this.width, GuiCreateFlatWorld.this.height, 43, GuiCreateFlatWorld.this.height - 60, 24);
      }

      private void drawItem(int x, int z, ItemStack itemToDraw) {
         this.drawItemBackground(x + 1, z + 1);
         GlStateManager.enableRescaleNormal();
         if (!itemToDraw.isEmpty()) {
            RenderHelper.enableGUIStandardItemLighting();
            GuiCreateFlatWorld.this.itemRender.renderItemIntoGUI(itemToDraw, x + 2, z + 2);
            RenderHelper.disableStandardItemLighting();
         }

         GlStateManager.disableRescaleNormal();
      }

      private void drawItemBackground(int x, int y) {
         this.drawItemBackground(x, y, 0, 0);
      }

      private void drawItemBackground(int x, int z, int textureX, int textureY) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(STAT_ICONS);
         float f = 0.0078125F;
         float f1 = 0.0078125F;
         int i = 18;
         int j = 18;
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.getBuffer();
         bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
         bufferbuilder.pos((double)(x + 0), (double)(z + 18), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.0078125F), (double)((float)(textureY + 18) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(x + 18), (double)(z + 18), (double)this.zLevel).tex((double)((float)(textureX + 18) * 0.0078125F), (double)((float)(textureY + 18) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(x + 18), (double)(z + 0), (double)this.zLevel).tex((double)((float)(textureX + 18) * 0.0078125F), (double)((float)(textureY + 0) * 0.0078125F)).endVertex();
         bufferbuilder.pos((double)(x + 0), (double)(z + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * 0.0078125F), (double)((float)(textureY + 0) * 0.0078125F)).endVertex();
         tessellator.draw();
      }

      protected int getSize() {
         return GuiCreateFlatWorld.this.generatorInfo.getFlatLayers().size();
      }

      protected boolean mouseClicked(int index, int button, double mouseX, double mouseY) {
         this.selectedLayer = index;
         GuiCreateFlatWorld.this.onLayersChanged();
         return true;
      }

      protected boolean isSelected(int slotIndex) {
         return slotIndex == this.selectedLayer;
      }

      protected void drawBackground() {
      }

      protected void drawSlot(int slotIndex, int xPos, int yPos, int heightIn, int mouseXIn, int mouseYIn, float partialTicks) {
         FlatLayerInfo flatlayerinfo = GuiCreateFlatWorld.this.generatorInfo.getFlatLayers().get(GuiCreateFlatWorld.this.generatorInfo.getFlatLayers().size() - slotIndex - 1);
         IBlockState iblockstate = flatlayerinfo.getLayerMaterial();
         Block block = iblockstate.getBlock();
         Item item = block.asItem();
         if (item == Items.AIR) {
            if (block == Blocks.WATER) {
               item = Items.WATER_BUCKET;
            } else if (block == Blocks.LAVA) {
               item = Items.LAVA_BUCKET;
            }
         }

         ItemStack itemstack = new ItemStack(item);
         String s = item.getDisplayName(itemstack).getFormattedText();
         this.drawItem(xPos, yPos, itemstack);
         GuiCreateFlatWorld.this.fontRenderer.drawString(s, (float)(xPos + 18 + 5), (float)(yPos + 3), 16777215);
         String s1;
         if (slotIndex == 0) {
            s1 = I18n.format("createWorld.customize.flat.layer.top", flatlayerinfo.getLayerCount());
         } else if (slotIndex == GuiCreateFlatWorld.this.generatorInfo.getFlatLayers().size() - 1) {
            s1 = I18n.format("createWorld.customize.flat.layer.bottom", flatlayerinfo.getLayerCount());
         } else {
            s1 = I18n.format("createWorld.customize.flat.layer", flatlayerinfo.getLayerCount());
         }

         GuiCreateFlatWorld.this.fontRenderer.drawString(s1, (float)(xPos + 2 + 213 - GuiCreateFlatWorld.this.fontRenderer.getStringWidth(s1)), (float)(yPos + 3), 16777215);
      }

      protected int getScrollBarX() {
         return this.width - 70;
      }
   }
}
