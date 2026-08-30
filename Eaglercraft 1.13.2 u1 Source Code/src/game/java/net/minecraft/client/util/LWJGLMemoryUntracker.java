package net.minecraft.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.system.Pointer;

@OnlyIn(Dist.CLIENT)
public class LWJGLMemoryUntracker {
   public static void untrack(long memAddr) {
   }

   public static void untrack(Pointer pointer) {
      untrack(pointer.address());
   }
}
