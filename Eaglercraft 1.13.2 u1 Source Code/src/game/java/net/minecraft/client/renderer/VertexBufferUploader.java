package net.minecraft.client.renderer;

import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexBufferUploader extends WorldVertexBufferUploader {
   private VertexBuffer vertexBuffer;

   public void draw(BufferBuilder bufferBuilderIn) {
      this.vertexBuffer.bufferData(bufferBuilderIn.getByteBuffer());
      bufferBuilderIn.reset();
      bufferBuilderIn.discardLargeBuffer();
   }

   public void setVertexBuffer(VertexBuffer vertexBufferIn) {
      this.vertexBuffer = vertexBufferIn;
   }
}
