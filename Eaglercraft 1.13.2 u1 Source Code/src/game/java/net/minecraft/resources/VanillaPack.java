package net.minecraft.resources;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VanillaPack implements IResourcePack {
   private static final Logger LOGGER = LogManager.getLogger();
   public static Class<?> baseClass;
   public final Set<String> resourceNamespaces;

   public VanillaPack(String... resourceNamespacesIn) {
      this.resourceNamespaces = ImmutableSet.copyOf(resourceNamespacesIn);
   }

   public InputStream getRootResourceStream(String fileName) throws IOException {
      if (!fileName.contains("/") && !fileName.contains("\\")) {
         InputStream eaglerinputstream = EagRuntime.getResourceStream(fileName);
         if (eaglerinputstream == null) {
            eaglerinputstream = EagRuntime.getResourceStream("/" + fileName);
         }
         if (eaglerinputstream == null) {
            eaglerinputstream = EagRuntime.getResourceStream("assets/" + fileName);
         }
         if (eaglerinputstream != null) {
            return eaglerinputstream;
         }

         return this.getInputStreamVanilla(fileName);
      } else {
         throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
      }
   }

   public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
      InputStream inputstream = this.getInputStreamVanilla(type, location);
      if (inputstream != null) {
         return inputstream;
      } else {
         throw new FileNotFoundException(location.getPath());
      }
   }

   public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String pathIn, int maxDepth, Predicate<String> filter) {
      Set<ResourceLocation> set = Sets.newHashSet();

      for(String s : this.resourceNamespaces) {
         String s1 = type.getDirectoryName() + "/" + s + "/";
         for(String s2 : EagRuntime.listResources(s1 + pathIn)) {
            if (!s2.endsWith(".mcmeta")) {
               String s3 = s2.substring(s1.length());
               if (filter.test(s3.substring(s3.lastIndexOf('/') + 1))) {
                  set.add(new ResourceLocation(s, s3));
               }
            }
         }
      }

      return set;
   }

   @Nullable
   protected InputStream getInputStreamVanilla(ResourcePackType type, ResourceLocation location) {
      String s = "/" + type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath();
      InputStream eaglerinputstream = EagRuntime.getResourceStream(s);
      if (eaglerinputstream == null) {
         eaglerinputstream = EagRuntime.getResourceStream(s.substring(1));
      }

      return eaglerinputstream;
   }

   @Nullable
   protected InputStream getInputStreamVanilla(String pathIn) {
      InputStream eaglerinputstream = EagRuntime.getResourceStream(pathIn);
      return eaglerinputstream != null ? eaglerinputstream : EagRuntime.getResourceStream("/" + pathIn);
   }

   public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
      InputStream inputstream = this.getInputStreamVanilla(type, location);
      boolean flag = inputstream != null;
      IOUtils.closeQuietly(inputstream);
      return flag;
   }

   public Set<String> getResourceNamespaces(ResourcePackType type) {
      return this.resourceNamespaces;
   }

   @Nullable
   public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
      try (InputStream inputstream = this.getRootResourceStream("pack.mcmeta")) {
         Object object = AbstractResourcePack.<T>getResourceMetadata(deserializer, inputstream);
         return (T)object;
      } catch (FileNotFoundException | RuntimeException var16) {
         return (T)null;
      }
   }

   public String getName() {
      return "Default";
   }

   public void close() {
   }
}
