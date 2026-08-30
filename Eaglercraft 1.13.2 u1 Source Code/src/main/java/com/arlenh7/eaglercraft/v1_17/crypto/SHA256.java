package com.arlenh7.eaglercraft.v1_17.crypto;

public final class SHA256 {
   private static final int[] K = new int[]{
      0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
      0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
      0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
      0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
      0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
      0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
      0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
      0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2
   };

   private SHA256() {
   }

   public static byte[] digest(byte[] data) {
      int blocks = ((data.length + 8) >>> 6) + 1;
      byte[] padded = new byte[blocks << 6];
      System.arraycopy(data, 0, padded, 0, data.length);
      padded[data.length] = (byte)0x80;
      long bits = (long)data.length << 3;
      for (int i = 0; i < 8; ++i) {
         padded[padded.length - 1 - i] = (byte)(bits >>> (i * 8));
      }

      int h0 = 0x6a09e667;
      int h1 = 0xbb67ae85;
      int h2 = 0x3c6ef372;
      int h3 = 0xa54ff53a;
      int h4 = 0x510e527f;
      int h5 = 0x9b05688c;
      int h6 = 0x1f83d9ab;
      int h7 = 0x5be0cd19;
      int[] w = new int[64];

      for (int block = 0; block < padded.length; block += 64) {
         for (int i = 0; i < 16; ++i) {
            int j = block + (i << 2);
            w[i] = (padded[j] & 255) << 24 | (padded[j + 1] & 255) << 16 | (padded[j + 2] & 255) << 8 | padded[j + 3] & 255;
         }
         for (int i = 16; i < 64; ++i) {
            int s0 = Integer.rotateRight(w[i - 15], 7) ^ Integer.rotateRight(w[i - 15], 18) ^ (w[i - 15] >>> 3);
            int s1 = Integer.rotateRight(w[i - 2], 17) ^ Integer.rotateRight(w[i - 2], 19) ^ (w[i - 2] >>> 10);
            w[i] = w[i - 16] + s0 + w[i - 7] + s1;
         }

         int a = h0;
         int b = h1;
         int c = h2;
         int d = h3;
         int e = h4;
         int f = h5;
         int g = h6;
         int h = h7;

         for (int i = 0; i < 64; ++i) {
            int s1 = Integer.rotateRight(e, 6) ^ Integer.rotateRight(e, 11) ^ Integer.rotateRight(e, 25);
            int ch = (e & f) ^ (~e & g);
            int temp1 = h + s1 + ch + K[i] + w[i];
            int s0 = Integer.rotateRight(a, 2) ^ Integer.rotateRight(a, 13) ^ Integer.rotateRight(a, 22);
            int maj = (a & b) ^ (a & c) ^ (b & c);
            int temp2 = s0 + maj;
            h = g;
            g = f;
            f = e;
            e = d + temp1;
            d = c;
            c = b;
            b = a;
            a = temp1 + temp2;
         }

         h0 += a;
         h1 += b;
         h2 += c;
         h3 += d;
         h4 += e;
         h5 += f;
         h6 += g;
         h7 += h;
      }

      byte[] out = new byte[32];
      write(out, 0, h0);
      write(out, 4, h1);
      write(out, 8, h2);
      write(out, 12, h3);
      write(out, 16, h4);
      write(out, 20, h5);
      write(out, 24, h6);
      write(out, 28, h7);
      return out;
   }

   private static void write(byte[] out, int offset, int value) {
      out[offset] = (byte)(value >>> 24);
      out[offset + 1] = (byte)(value >>> 16);
      out[offset + 2] = (byte)(value >>> 8);
      out[offset + 3] = (byte)value;
   }
}
