package net.minecraft.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ScreenShotHelper {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");

   public static void saveScreenshot(File gameDirectory, int width, int height, Framebuffer buffer, Consumer<ITextComponent> p_148260_4_) {
      saveScreenshot(gameDirectory, (String)null, width, height, buffer, p_148260_4_);
   }

   public static void saveScreenshot(File gameDirectory, @Nullable String screenshotName, int width, int height, Framebuffer buffer, Consumer<ITextComponent> p_148259_5_) {
      NativeImage nativeimage = createScreenshot(width, height, buffer);
      String filename = screenshotName == null ? DATE_FORMAT.format(new Date()) + ".png" : screenshotName;

      try {
         EaglerOutputStream bytes = new EaglerOutputStream();
         nativeimage.write(bytes, filename);
         EagRuntime.downloadFileWithName(filename, bytes.toByteArray());
         ITextComponent itextcomponent = (new TextComponentString(filename)).applyTextStyle(TextFormatting.UNDERLINE);
         p_148259_5_.accept(new TextComponentTranslation("screenshot.success", itextcomponent));
      } catch (Exception exception) {
         LOGGER.warn("Couldn't save screenshot", (Throwable)exception);
         p_148259_5_.accept(new TextComponentTranslation("screenshot.failure", exception.getMessage()));
      } finally {
         nativeimage.close();
      }

   }

   public static NativeImage createScreenshot(int width, int height, Framebuffer framebufferIn) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         width = framebufferIn.framebufferTextureWidth;
         height = framebufferIn.framebufferTextureHeight;
      }

      NativeImage nativeimage = new NativeImage(width, height, false);
      if (OpenGlHelper.isFramebufferEnabled()) {
         framebufferIn.bindFramebuffer(true);
         nativeimage.downloadFromFramebuffer(true);
         framebufferIn.unbindFramebuffer();
      } else {
         nativeimage.downloadFromFramebuffer(true);
      }

      nativeimage.flip();
      return nativeimage;
   }
}
