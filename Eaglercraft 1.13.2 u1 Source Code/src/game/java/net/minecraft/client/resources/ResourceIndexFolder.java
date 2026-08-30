package net.minecraft.client.resources;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourceIndexFolder extends ResourceIndex {
   private final File baseDir;

   public ResourceIndexFolder(File folder) {
      this.baseDir = folder;
   }

   public File getFile(ResourceLocation location) {
      return new File(this.baseDir, location.toString().replace(':', '/'));
   }

   public File getFile(String p_200009_1_) {
      return new File(this.baseDir, p_200009_1_);
   }

   public Collection<String> getFiles(String p_211685_1_, int p_211685_2_, Predicate<String> p_211685_3_) {
      return Collections.emptyList();
   }
}
