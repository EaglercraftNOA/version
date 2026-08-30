package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerZLIB;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.export.RandomAccessMemoryFile;
import net.minecraft.util.Util;

public class RegionFile {
   private static final byte[] EMPTY_SECTOR = new byte[4096];
   private final VFile2 fileName;
   private RandomAccessMemoryFile dataFile;
   private final int[] offsets = new int[1024];
   private final int[] chunkTimestamps = new int[1024];
   private List<Boolean> sectorFree;
   private int sizeDelta;
   private long lastModified;

   public RegionFile(VFile2 fileNameIn) {
      this.fileName = fileNameIn;
      this.sizeDelta = 0;
      byte[] abyte = fileNameIn.getAllBytes();
      this.dataFile = abyte != null ? new RandomAccessMemoryFile(abyte, abyte.length) : new RandomAccessMemoryFile(new byte[0], 0);
      this.setupRegionFile();
   }

   private void setupRegionFile() {
      try {
         if (this.dataFile.getLength() < 4096) {
            this.dataFile.write(EMPTY_SECTOR);
            this.dataFile.write(EMPTY_SECTOR);
            this.sizeDelta += 8192;
         }

         if ((this.dataFile.getLength() & 4095) != 0) {
            for(int i = 0; i < (this.dataFile.getLength() & 4095); ++i) {
               this.dataFile.write(0);
            }
         }

         int i1 = this.dataFile.getLength() / 4096;
         this.sectorFree = Lists.newArrayListWithCapacity(i1);

         for(int j = 0; j < i1; ++j) {
            this.sectorFree.add(true);
         }

         this.sectorFree.set(0, false);
         this.sectorFree.set(1, false);
         this.dataFile.seek(0);

         for(int j1 = 0; j1 < 1024; ++j1) {
            int k = this.dataFile.readInt();
            this.offsets[j1] = k;
            if (k != 0 && (k >> 8) + (k & 255) <= this.sectorFree.size()) {
               for(int l = 0; l < (k & 255); ++l) {
                  this.sectorFree.set((k >> 8) + l, false);
               }
            }
         }

         for(int k1 = 0; k1 < 1024; ++k1) {
            int l1 = this.dataFile.readInt();
            this.chunkTimestamps[k1] = l1;
         }
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

   }

   @Nullable
   public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
      if (this.outOfBounds(x, z)) {
         return null;
      } else {
         try {
            int i = this.getOffset(x, z);
            if (i == 0) {
               return null;
            } else {
               int j = i >> 8;
               int k = i & 255;
               if (j + k > this.sectorFree.size()) {
                  return null;
               } else {
                  this.dataFile.seek(j * 4096);
                  int l = this.dataFile.readInt();
                  if (l > 4096 * k) {
                     return null;
                  } else if (l <= 0) {
                     return null;
                  } else {
                     byte b0 = this.dataFile.readByte();
                     if (b0 == 1) {
                        byte[] abyte1 = new byte[l - 1];
                        this.dataFile.read(abyte1);
                        return new DataInputStream(new BufferedInputStream(EaglerZLIB.newGZIPInputStream(new EaglerInputStream(abyte1))));
                     } else if (b0 == 2) {
                        byte[] abyte = new byte[l - 1];
                        this.dataFile.read(abyte);
                        return new DataInputStream(new BufferedInputStream(EaglerZLIB.newInflaterInputStream(new EaglerInputStream(abyte))));
                     } else {
                        return null;
                     }
                  }
               }
            }
         } catch (IOException var9) {
            return null;
         }
      }
   }

   public boolean doesChunkExist(int p_212167_1_, int p_212167_2_) {
      if (this.outOfBounds(p_212167_1_, p_212167_2_)) {
         return false;
      } else {
         int i = this.getOffset(p_212167_1_, p_212167_2_);
         if (i == 0) {
            return false;
         } else {
            int j = i >> 8;
            int k = i & 255;
            if (j + k > this.sectorFree.size()) {
               return false;
            } else {
               try {
                  this.dataFile.seek(j * 4096);
                  int l = this.dataFile.readInt();
                  if (l > 4096 * k) {
                     return false;
                  } else {
                     return l > 0;
                  }
               } catch (IOException var7) {
                  return false;
               }
            }
         }
      }
   }

   @Nullable
   public DataOutputStream getChunkDataOutputStream(int x, int z) throws IOException {
      return this.outOfBounds(x, z) ? null : new DataOutputStream(new BufferedOutputStream(EaglerZLIB.newDeflaterOutputStream(new RegionFile.ChunkBuffer(x, z))));
   }

   protected synchronized void write(int x, int z, byte[] data, int length) {
      try {
         int i = this.getOffset(x, z);
         int j = i >> 8;
         int k = i & 255;
         int l = (length + 5) / 4096 + 1;
         if (l >= 256) {
            return;
         }

         if (j != 0 && k == l) {
            this.write(j, data, length);
         } else {
            for(int i1 = 0; i1 < k; ++i1) {
               this.sectorFree.set(j + i1, true);
            }

            int l1 = this.sectorFree.indexOf(true);
            int j1 = 0;
            if (l1 != -1) {
               for(int k1 = l1; k1 < this.sectorFree.size(); ++k1) {
                  if (j1 != 0) {
                     if (this.sectorFree.get(k1)) {
                        ++j1;
                     } else {
                        j1 = 0;
                     }
                  } else if (this.sectorFree.get(k1)) {
                     l1 = k1;
                     j1 = 1;
                  }

                  if (j1 >= l) {
                     break;
                  }
               }
            }

            if (j1 >= l) {
               j = l1;
               this.setOffset(x, z, l1 << 8 | l);

               for(int j2 = 0; j2 < l; ++j2) {
                  this.sectorFree.set(j + j2, false);
               }

               this.write(j, data, length);
            } else {
               this.dataFile.seek(this.dataFile.getLength());
               j = this.sectorFree.size();

               for(int i2 = 0; i2 < l; ++i2) {
                  this.dataFile.write(EMPTY_SECTOR);
                  this.sectorFree.add(false);
               }

               this.sizeDelta += 4096 * l;
               this.write(j, data, length);
               this.setOffset(x, z, j << 8 | l);
            }
         }

         this.setChunkTimestamp(x, z, (int)(Util.millisecondsSinceEpoch() / 1000L));
      } catch (IOException ioexception) {
         ioexception.printStackTrace();
      }

   }

   private void write(int sectorNumber, byte[] data, int length) throws IOException {
      this.dataFile.seek(sectorNumber * 4096);
      this.dataFile.writeInt(length + 1);
      this.dataFile.writeByte(2);
      this.dataFile.write(data, 0, length);
   }

   private boolean outOfBounds(int x, int z) {
      return x < 0 || x >= 32 || z < 0 || z >= 32;
   }

   private int getOffset(int x, int z) {
      return this.offsets[x + z * 32];
   }

   public boolean isChunkSaved(int x, int z) {
      return this.getOffset(x, z) != 0;
   }

   private void setOffset(int x, int z, int offset) throws IOException {
      this.offsets[x + z * 32] = offset;
      this.dataFile.seek((x + z * 32) * 4);
      this.dataFile.writeInt(offset);
   }

   private void setChunkTimestamp(int x, int z, int timestamp) throws IOException {
      this.chunkTimestamps[x + z * 32] = timestamp;
      this.dataFile.seek(4096 + (x + z * 32) * 4);
      this.dataFile.writeInt(timestamp);
   }

   public void close() throws IOException {
      if (this.dataFile != null) {
         this.fileName.setAllBytes(this.dataFile.getByteArray());
         this.dataFile = null;
      }

   }

   class ChunkBuffer extends EaglerOutputStream {
      private final int chunkX;
      private final int chunkZ;

      public ChunkBuffer(int x, int z) {
         super(8096);
         this.chunkX = x;
         this.chunkZ = z;
      }

      public void close() {
         RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
      }
   }
}
