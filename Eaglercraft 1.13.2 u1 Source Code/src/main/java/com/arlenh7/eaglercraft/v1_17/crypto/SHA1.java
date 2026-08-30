package com.arlenh7.eaglercraft.v1_17.crypto;

import java.io.IOException;
import java.io.InputStream;

public final class SHA1 {
   private SHA1() {
   }

   public static byte[] digest(byte[] data) {
      SHA1Digest digest = new SHA1Digest();
      digest.update(data, 0, data.length);
      return digest.finish();
   }

   public static byte[] digest(InputStream stream) throws IOException {
      SHA1Digest digest = new SHA1Digest();
      byte[] buffer = new byte[4096];
      int read;
      while ((read = stream.read(buffer)) != -1) {
         digest.update(buffer, 0, read);
      }
      return digest.finish();
   }

   private static final class SHA1Digest {
      private final byte[] block = new byte[64];
      private final int[] schedule = new int[80];
      private long bytes;
      private int blockOffset;
      private int h0 = 0x67452301;
      private int h1 = 0xEFCDAB89;
      private int h2 = 0x98BADCFE;
      private int h3 = 0x10325476;
      private int h4 = 0xC3D2E1F0;

      void update(byte[] data, int offset, int length) {
         this.bytes += length;
         while (length > 0) {
            int copy = Math.min(length, 64 - this.blockOffset);
            System.arraycopy(data, offset, this.block, this.blockOffset, copy);
            this.blockOffset += copy;
            offset += copy;
            length -= copy;
            if (this.blockOffset == 64) {
               processBlock();
               this.blockOffset = 0;
            }
         }
      }

      byte[] finish() {
         long bitLength = this.bytes << 3;
         this.block[this.blockOffset++] = (byte)0x80;
         if (this.blockOffset > 56) {
            while (this.blockOffset < 64) {
               this.block[this.blockOffset++] = 0;
            }
            processBlock();
            this.blockOffset = 0;
         }

         while (this.blockOffset < 56) {
            this.block[this.blockOffset++] = 0;
         }

         for (int i = 7; i >= 0; --i) {
            this.block[this.blockOffset++] = (byte)(bitLength >>> (i * 8));
         }
         processBlock();

         byte[] out = new byte[20];
         writeInt(out, 0, this.h0);
         writeInt(out, 4, this.h1);
         writeInt(out, 8, this.h2);
         writeInt(out, 12, this.h3);
         writeInt(out, 16, this.h4);
         return out;
      }

      private void processBlock() {
         for (int i = 0; i < 16; ++i) {
            int j = i << 2;
            this.schedule[i] = (this.block[j] & 255) << 24 | (this.block[j + 1] & 255) << 16 | (this.block[j + 2] & 255) << 8 | this.block[j + 3] & 255;
         }

         for (int i = 16; i < 80; ++i) {
            this.schedule[i] = Integer.rotateLeft(this.schedule[i - 3] ^ this.schedule[i - 8] ^ this.schedule[i - 14] ^ this.schedule[i - 16], 1);
         }

         int a = this.h0;
         int b = this.h1;
         int c = this.h2;
         int d = this.h3;
         int e = this.h4;

         for (int i = 0; i < 80; ++i) {
            int f;
            int k;
            if (i < 20) {
               f = b & c | ~b & d;
               k = 0x5A827999;
            } else if (i < 40) {
               f = b ^ c ^ d;
               k = 0x6ED9EBA1;
            } else if (i < 60) {
               f = b & c | b & d | c & d;
               k = 0x8F1BBCDC;
            } else {
               f = b ^ c ^ d;
               k = 0xCA62C1D6;
            }

            int temp = Integer.rotateLeft(a, 5) + f + e + k + this.schedule[i];
            e = d;
            d = c;
            c = Integer.rotateLeft(b, 30);
            b = a;
            a = temp;
         }

         this.h0 += a;
         this.h1 += b;
         this.h2 += c;
         this.h3 += d;
         this.h4 += e;
      }

      private static void writeInt(byte[] out, int offset, int value) {
         out[offset] = (byte)(value >>> 24);
         out[offset + 1] = (byte)(value >>> 16);
         out[offset + 2] = (byte)(value >>> 8);
         out[offset + 3] = (byte)value;
      }
   }
}
