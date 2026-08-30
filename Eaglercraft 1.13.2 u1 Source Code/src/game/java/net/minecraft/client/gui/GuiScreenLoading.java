package net.minecraft.client.gui;

import java.io.IOException;
import java.io.InputStream;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiScreenLoading extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation field_195190_f = new ResourceLocation("textures/gui/title/mojang.png");
   private ResourceLocation field_195191_g;

   protected void initGui() {
      try {
         InputStream inputstream = this.mc.getPackFinder().getVanillaPack().getResourceStream(ResourcePackType.CLIENT_RESOURCES, field_195190_f);
         if (inputstream != null) {
            this.field_195191_g = this.mc.getTextureManager().getDynamicTextureLocation("logo", new DynamicTexture(NativeImage.read(inputstream)));
         }
      } catch (IOException ioexception) {
         LOGGER.error("Unable to load logo: {}", field_195190_f, ioexception);
      }

      if (this.field_195191_g == null) {
         this.field_195191_g = TextureManager.RESOURCE_LOCATION_EMPTY;
      }
   }

   public void onGuiClosed() {
      if (!TextureManager.RESOURCE_LOCATION_EMPTY.equals(this.field_195191_g)) {
         this.mc.getTextureManager().deleteTexture(this.field_195191_g);
      }
      this.field_195191_g = null;
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.disableDepthTest();
      GlStateManager.enableAlphaTest();
      GlStateManager.enableTexture2D();
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      drawRect(0, 0, this.width, this.height, -1);
      this.mc.getTextureManager().bindTexture(this.field_195191_g);
      GlStateManager.enableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.draw((this.mc.mainWindow.getScaledWidth() - 256) / 2, (this.mc.mainWindow.getScaledHeight() - 256) / 2, 0, 0, 256, 256, 255, 255, 255, 255);
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.1F);
   }
}
