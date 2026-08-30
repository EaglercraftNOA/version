package net.minecraft.client.renderer.texture;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import net.minecraft.resources.IResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PngSizeInfo {
   public final int width;
   public final int height;

   public PngSizeInfo(IResource p_i48120_1_) throws IOException {
      byte[] header = new byte[24];
      try (InputStream inputstream = p_i48120_1_.getInputStream()) {
         readFully(inputstream, header);
      }
      if (header[0] != (byte)137 || header[1] != 80 || header[2] != 78 || header[3] != 71 || header[4] != 13 || header[5] != 10 || header[6] != 26 || header[7] != 10 || header[12] != 73 || header[13] != 72 || header[14] != 68 || header[15] != 82) {
         throw new IOException("Could not read info from the PNG file " + p_i48120_1_);
      }
      this.width = readInt(header, 16);
      this.height = readInt(header, 20);
   }

   private static void readFully(InputStream inputstream, byte[] header) throws IOException {
      int offset = 0;
      while(offset < header.length) {
         int read = inputstream.read(header, offset, header.length - offset);
         if(read < 0) {
            throw new EOFException();
         }
         offset += read;
      }
   }

   private static int readInt(byte[] header, int offset) {
      return (header[offset] & 255) << 24 | (header[offset + 1] & 255) << 16 | (header[offset + 2] & 255) << 8 | header[offset + 3] & 255;
   }
}
