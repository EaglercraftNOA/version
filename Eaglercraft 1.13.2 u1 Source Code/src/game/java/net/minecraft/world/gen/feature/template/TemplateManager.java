package net.minecraft.world.gen.feature.template;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TemplateManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Template> templates = Maps.newHashMap();
   private final DataFixer fixer;
   private final MinecraftServer minecraftServer;
   private final VFile2 folderGenerated;

   public TemplateManager(MinecraftServer server, VFile2 templateFolder, DataFixer fixerIn) {
      this.minecraftServer = server;
      this.fixer = fixerIn;
      this.folderGenerated = WorldsDB.newVFile(templateFolder, "generated");
      server.getResourceManager().addReloadListener(this);
   }

   public Template getTemplateDefaulted(ResourceLocation p_200220_1_) {
      Template template = this.getTemplate(p_200220_1_);
      if (template == null) {
         template = new Template();
         this.templates.put(p_200220_1_, template);
      }

      return template;
   }

   @Nullable
   public Template getTemplate(ResourceLocation p_200219_1_) {
      return this.templates.computeIfAbsent(p_200219_1_, (p_209204_1_) -> {
         Template template = this.loadTemplateFile(p_209204_1_);
         return template != null ? template : this.loadTemplateResource(p_209204_1_);
      });
   }

   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.templates.clear();
   }

   @Nullable
   private Template loadTemplateResource(ResourceLocation p_209201_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(p_209201_1_.getNamespace(), "structures/" + p_209201_1_.getPath() + ".nbt");

      try (IResource iresource = this.minecraftServer.getResourceManager().getResource(resourcelocation)) {
         Template template = this.loadTemplate(iresource.getInputStream());
         return template;
      } catch (IOException var18) {
         return null;
      } catch (Throwable throwable) {
         LOGGER.error("Couldn't load structure {}: {}", p_209201_1_, throwable.toString());
         return null;
      }
   }

   @Nullable
   private Template loadTemplateFile(ResourceLocation locationIn) {
      if (!this.folderGenerated.exists()) {
         return null;
      } else {
         VFile2 templateFile = this.resolvePath(locationIn, ".nbt");

         try (InputStream inputstream = templateFile.getInputStream()) {
            return this.loadTemplate(inputstream);
         } catch (IOException ioexception) {
            LOGGER.error("Couldn't load structure from {}", templateFile, ioexception);
            return null;
         }
      }
   }

   private Template loadTemplate(InputStream inputStreamIn) throws IOException {
      NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(inputStreamIn);
      if (!nbttagcompound.contains("DataVersion", 99)) {
         nbttagcompound.putInt("DataVersion", 500);
      }

      Template template = new Template();
      template.read(NBTUtil.update(this.fixer, DataFixTypes.STRUCTURE, nbttagcompound, nbttagcompound.getInt("DataVersion")));
      return template;
   }

   public boolean writeToFile(ResourceLocation templateName) {
      Template template = this.templates.get(templateName);
      if (template == null) {
         return false;
      } else {
         VFile2 templateFile = this.resolvePath(templateName, ".nbt");
         NBTTagCompound nbttagcompound = template.writeToNBT(new NBTTagCompound());

         try (OutputStream outputstream = templateFile.getOutputStream()) {
            CompressedStreamTools.writeCompressed(nbttagcompound, outputstream);
            return true;
         } catch (Throwable var21) {
            return false;
         }
      }
   }

   private VFile2 resolvePath(ResourceLocation locationIn, String extIn) {
      String s = locationIn.getPath();
      if (s.contains("//") || s.contains("..") || s.contains("\\") || s.isEmpty()) {
         throw new ResourceLocationException("Invalid resource path: " + locationIn);
      } else {
         return WorldsDB.newVFile(this.folderGenerated, locationIn.getNamespace(), "structures", s + extIn);
      }
   }

   public void remove(ResourceLocation templatePath) {
      this.templates.remove(templatePath);
   }
}
