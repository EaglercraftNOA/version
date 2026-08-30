package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
   private static final FloatBuffer COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(4);
   private static final Vec3d LIGHT0_POS = (new Vec3d((double)0.2F, 1.0D, (double)-0.7F)).normalize();
   private static final Vec3d LIGHT1_POS = (new Vec3d((double)-0.2F, 1.0D, (double)0.7F)).normalize();

   public static void disableStandardItemLighting() {
      GlStateManager.disableLighting();
      GlStateManager.disableMCLight(0);
      GlStateManager.disableMCLight(1);
      GlStateManager.disableColorMaterial();
   }

   public static void enableStandardItemLighting() {
      GlStateManager.enableLighting();
      GlStateManager.enableMCLight(0, 0.6F, LIGHT0_POS.x, LIGHT0_POS.y, LIGHT0_POS.z, 0.0D);
      GlStateManager.enableMCLight(1, 0.6F, LIGHT1_POS.x, LIGHT1_POS.y, LIGHT1_POS.z, 0.0D);
      GlStateManager.setMCLightAmbient(0.4F, 0.4F, 0.4F);
      GlStateManager.enableColorMaterial();
   }

   private static FloatBuffer setColorBuffer(double red, double green, double blue, double alpha) {
      return setColorBuffer((float)red, (float)green, (float)blue, (float)alpha);
   }

   public static FloatBuffer setColorBuffer(float red, float green, float blue, float alpha) {
      COLOR_BUFFER.clear();
      COLOR_BUFFER.put(red).put(green).put(blue).put(alpha);
      COLOR_BUFFER.flip();
      return COLOR_BUFFER;
   }

   public static void enableGUIStandardItemLighting() {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(-30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(165.0F, 1.0F, 0.0F, 0.0F);
      enableStandardItemLighting();
      GlStateManager.popMatrix();
   }
}
