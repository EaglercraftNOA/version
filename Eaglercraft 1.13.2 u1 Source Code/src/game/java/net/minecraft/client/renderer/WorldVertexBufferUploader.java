package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.VertexFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WorldVertexBufferUploader {
   public void draw(BufferBuilder bufferBuilderIn) {
      int cnt = bufferBuilderIn.getVertexCount();
      if (cnt > 0) {
         VertexFormat fmt = bufferBuilderIn.getVertexFormat();
         ByteBuffer buf = bufferBuilderIn.getByteBuffer();
         buf.position(0).limit(cnt * fmt.attribStride);
         EaglercraftGPU.renderBuffer(buf, fmt.eaglercraftAttribBits, bufferBuilderIn.getDrawMode(), cnt);
      }

      bufferBuilderIn.reset();
      bufferBuilderIn.discardLargeBuffer();
   }

   public static void uploadDisplayList(int displayList, BufferBuilder bufferBuilderIn) {
      int cnt = bufferBuilderIn.getVertexCount();
      if (cnt > 0) {
         VertexFormat fmt = bufferBuilderIn.getVertexFormat();
         ByteBuffer buf = bufferBuilderIn.getByteBuffer();
         buf.position(0).limit(cnt * fmt.attribStride);
         EaglercraftGPU.uploadListDirect(displayList, buf, fmt.eaglercraftAttribBits, bufferBuilderIn.getDrawMode(), cnt);
      } else {
         EaglercraftGPU.flushDisplayList(displayList, false);
      }

      bufferBuilderIn.reset();
      bufferBuilderIn.discardLargeBuffer();
   }
}
