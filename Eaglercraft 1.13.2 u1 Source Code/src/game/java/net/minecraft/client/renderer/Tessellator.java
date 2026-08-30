package net.minecraft.client.renderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tessellator {
   private final BufferBuilder buffer;
   private final WorldVertexBufferUploader vboUploader = new WorldVertexBufferUploader();
	private static final Tessellator INSTANCE = new Tessellator(524288);

   public static Tessellator getInstance() {
      return INSTANCE;
   }

   public Tessellator(int bufferSize) {
      this.buffer = new BufferBuilder(bufferSize);
   }

   public void draw() {
      this.buffer.finishDrawing();
      this.vboUploader.draw(this.buffer);
   }

   public BufferBuilder getBuffer() {
      return this.buffer;
   }
}
