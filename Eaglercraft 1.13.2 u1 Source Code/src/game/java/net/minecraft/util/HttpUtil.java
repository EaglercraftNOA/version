package net.minecraft.util;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.futures.Futures;
import net.lax1dude.eaglercraft.v1_8.futures.ListenableFuture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final HttpUtil.DownloaderExecutor DOWNLOADER_EXECUTOR = new HttpUtil.DownloaderExecutor();

   @OnlyIn(Dist.CLIENT)
   public static ListenableFuture<?> downloadResourcePack(File saveFile, String packUrl, Map<String, String> p_180192_2_, int maxSize, @Nullable IProgressUpdate p_180192_4_, Proxy p_180192_5_) {
      if (p_180192_4_ != null) {
         p_180192_4_.setDoneWorking();
      }
      return Futures.immediateFailedFuture(new IOException("Server resource pack download failed: " + packUrl));
   }

   public static int getSuitableLanPort() {
      return 25564;
   }

   public static class DownloaderExecutor {
      public ListenableFuture<Object> submit(Runnable runnable) {
         try {
            runnable.run();
            return Futures.immediateFuture((Object)null);
         } catch (Throwable throwable) {
            return Futures.immediateFailedFuture(throwable);
         }
      }
   }
}
