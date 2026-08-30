package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.FileChooserResult;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerFolderResourcePack;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenGenericErrorMessage;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.LegacyResourcePackWrapper;
import net.minecraft.client.resources.ResourcePackInfoClient;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.resources.EaglerFolderDataPackFinder;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiScreenDatapacks extends GuiScreenResourcePacks {
   private static final Logger LOGGER = LogManager.getLogger();
   private final GuiScreen parentScreen;
   private final List<String> selectedDataPacks;
   private GuiResourcePackAvailable availableResourcePacksList;
   private GuiResourcePackSelected selectedResourcePacksList;
   private boolean changed;

   public GuiScreenDatapacks(GuiScreen parentScreenIn, List<String> selectedDataPacksIn) {
      super(parentScreenIn);
      this.parentScreen = parentScreenIn;
      this.selectedDataPacks = selectedDataPacksIn;
   }

   protected void initGui() {
      this.addButton(new GuiOptionButton(2, this.width / 2 - 154, this.height - 48, I18n.format("datapack.openFolder")) {
         public void onClick(double mouseX, double mouseY) {
            EagRuntime.displayFileChooser("application/zip", "zip");
         }
      });
      this.addButton(new GuiOptionButton(1, this.width / 2 + 4, this.height - 48, I18n.format("gui.done")) {
         public void onClick(double mouseX, double mouseY) {
            GuiScreenDatapacks.this.selectedDataPacks.clear();
            for(ResourcePackListEntryFound resourcepacklistentryfound : GuiScreenDatapacks.this.selectedResourcePacksList.getChildren()) {
               String s = resourcepacklistentryfound.func_195017_i().getName();
               if (s.startsWith("datapack/")) {
                  GuiScreenDatapacks.this.selectedDataPacks.add(s.substring("datapack/".length()));
               }
            }
            GuiScreenDatapacks.this.mc.displayGuiScreen(GuiScreenDatapacks.this.parentScreen);
         }
      });
      GuiResourcePackAvailable guiresourcepackavailable = this.availableResourcePacksList;
      GuiResourcePackSelected guiresourcepackselected = this.selectedResourcePacksList;
      this.availableResourcePacksList = new GuiResourcePackAvailable(this.mc, 200, this.height);
      this.availableResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 - 4 - 200);
      if (guiresourcepackavailable != null) {
         this.availableResourcePacksList.getChildren().addAll(guiresourcepackavailable.getChildren());
      }

      this.children.add(this.availableResourcePacksList);
      this.selectedResourcePacksList = new GuiResourcePackSelected(this.mc, 200, this.height);
      this.selectedResourcePacksList.setSlotXBoundsFromLeft(this.width / 2 + 4);
      if (guiresourcepackselected != null) {
         this.selectedResourcePacksList.getChildren().addAll(guiresourcepackselected.getChildren());
      }

      this.children.add(this.selectedResourcePacksList);
      if (!this.changed) {
         this.availableResourcePacksList.getChildren().clear();
         this.selectedResourcePacksList.getChildren().clear();
         ResourcePackList<ResourcePackInfoClient> resourcepacklist = new ResourcePackList<>((p_211818_0_, p_211818_1_, p_211818_2_, p_211818_3_, p_211818_4_, p_211818_5_) -> {
            Supplier<IResourcePack> supplier;
            if (p_211818_4_.getPackFormat() < 4) {
               supplier = () -> new LegacyResourcePackWrapper(p_211818_2_.get(), LegacyResourcePackWrapper.NEW_TO_LEGACY_MAP);
            } else {
               supplier = p_211818_2_;
            }

            return new ResourcePackInfoClient(p_211818_0_, p_211818_1_, supplier, p_211818_3_, p_211818_4_, p_211818_5_);
         });
         resourcepacklist.addPackFinder(new EaglerFolderDataPackFinder());
         resourcepacklist.reloadPacksFromFinders();
         Set<String> set = Sets.newHashSet();
         for(String s1 : this.selectedDataPacks) {
            set.add("datapack/" + s1);
         }

         List<ResourcePackInfoClient> list = Lists.newArrayList(resourcepacklist.getAllPacks());
         for(ResourcePackInfoClient resourcepackinfoclient : list) {
            if (set.contains(resourcepackinfoclient.getName())) {
               this.selectedResourcePacksList.func_195095_a(new ResourcePackListEntryFound(this, resourcepackinfoclient));
            } else {
               this.availableResourcePacksList.func_195095_a(new ResourcePackListEntryFound(this, resourcepackinfoclient));
            }
         }
      }

   }

   public void func_195301_a(ResourcePackListEntryFound p_195301_1_) {
      this.availableResourcePacksList.getChildren().remove(p_195301_1_);
      p_195301_1_.func_195020_a(this.selectedResourcePacksList);
      this.markChanged();
   }

   public void func_195305_b(ResourcePackListEntryFound p_195305_1_) {
      this.selectedResourcePacksList.getChildren().remove(p_195305_1_);
      this.availableResourcePacksList.func_195095_a(p_195305_1_);
      this.markChanged();
   }

   public boolean func_195312_c(ResourcePackListEntryFound p_195312_1_) {
      return this.selectedResourcePacksList.getChildren().contains(p_195312_1_);
   }

   public void markChanged() {
      this.changed = true;
   }

   public void refreshResourcePacks() {
      this.changed = false;
      this.mc.displayGuiScreen(this);
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawBackground(0);
      this.availableResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
      this.selectedResourcePacksList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, I18n.format("datapack.title"), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.fontRenderer, I18n.format("datapack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      for(int i = 0; i < this.buttons.size(); ++i) {
         this.buttons.get(i).render(mouseX, mouseY, partialTicks);
      }
   }

   public void tick() {
      if (EaglerFolderResourcePack.isSupported() && EagRuntime.fileChooserHasResult()) {
         FileChooserResult filechooseresult = EagRuntime.getFileChooserResult();
         if (filechooseresult != null) {
            this.mc.loadingScreen.eaglerShow(I18n.format("eaglercraft.datapack.load.loading"), filechooseresult.fileName);

            try {
               EaglerFolderResourcePack.importResourcePack(filechooseresult.fileName, EaglerFolderDataPackFinder.DATA_PACKS, filechooseresult.fileData);
            } catch (IOException ioexception) {
               LOGGER.error("Could not load datapack: {}", filechooseresult.fileName);
               LOGGER.error(ioexception);
               this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("eaglercraft.datapack.importFailed.1", "eaglercraft.datapack.importFailed.2", this.parentScreen));
               return;
            }

            this.refreshResourcePacks();
         }
      }

   }
}
