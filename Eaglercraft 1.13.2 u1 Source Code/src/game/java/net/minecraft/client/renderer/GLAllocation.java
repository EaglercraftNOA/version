package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GLAllocation {
   public static int generateDisplayLists(int range) {
      return EaglercraftGPU.glGenLists(range);
   }

   public static void deleteDisplayLists(int list, int range) {
      GlStateManager.deleteLists(list, range);
   }

   public static void deleteDisplayLists(int list) {
      deleteDisplayLists(list, 1);
   }

   public static ByteBuffer createDirectByteBuffer(int capacity) {
      return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
   }

   public static FloatBuffer createDirectFloatBuffer(int capacity) {
      return createDirectByteBuffer(capacity << 2).asFloatBuffer();
   }
}
