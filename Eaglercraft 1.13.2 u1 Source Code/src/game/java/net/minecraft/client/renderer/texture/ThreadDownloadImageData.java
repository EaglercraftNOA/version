package net.minecraft.client.renderer.texture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ThreadDownloadImageData extends SimpleTexture {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger TEXTURE_DOWNLOADER_THREAD_ID = new AtomicInteger(0);
   @Nullable
   private final File cacheFile;
   private final String imageUrl;
   @Nullable
   private final IImageBuffer imageBuffer;
   @Nullable
   private Thread imageThread;
   private volatile boolean textureUploaded;

   public ThreadDownloadImageData(@Nullable File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, @Nullable IImageBuffer imageBufferIn) {
      super(textureResourceLocation);
      this.cacheFile = cacheFileIn;
      this.imageUrl = imageUrlIn;
      this.imageBuffer = imageBufferIn;
   }

   private void uploadImage(NativeImage nativeImageIn) {
      TextureUtil.allocateTexture(this.getGlTextureId(), nativeImageIn.getWidth(), nativeImageIn.getHeight());
      nativeImageIn.uploadTextureSub(0, 0, 0, false);
   }

   public void setImage(NativeImage nativeImageIn) {
      if (this.imageBuffer != null) {
         this.imageBuffer.skinAvailable();
      }

      synchronized(this) {
         this.uploadImage(nativeImageIn);
         this.textureUploaded = true;
      }
   }

   public void loadTexture(IResourceManager manager) throws IOException {
      if (!this.textureUploaded) {
         synchronized(this) {
            super.loadTexture(manager);
            this.textureUploaded = true;
         }
      }

      if (this.imageThread == null) {
         if (this.cacheFile != null && this.cacheFile.isFile()) {
            LOGGER.debug("Loading http texture from local cache ({})", (Object)this.cacheFile);
            NativeImage nativeimage = null;

            try {
               nativeimage = NativeImage.read(new FileInputStream(this.cacheFile));
               if (this.imageBuffer != null) {
                  nativeimage = this.imageBuffer.parseUserSkin(nativeimage);
               }

               this.setImage(nativeimage);
            } catch (IOException ioexception) {
               LOGGER.error("Couldn't load skin {}", this.cacheFile, ioexception);
               this.loadTextureFromServer();
            } finally {
               if (nativeimage != null) {
                  nativeimage.close();
               }

            }
         } else {
            this.loadTextureFromServer();
         }
      }

   }

   protected void loadTextureFromServer() {
   }
}
