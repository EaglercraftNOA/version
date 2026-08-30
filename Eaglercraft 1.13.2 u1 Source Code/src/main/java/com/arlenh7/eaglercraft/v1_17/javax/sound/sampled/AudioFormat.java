package com.arlenh7.eaglercraft.v1_17.javax.sound.sampled;

public class AudioFormat {
   protected Encoding encoding;
   protected float sampleRate;
   protected int sampleSizeInBits;
   protected int channels;
   protected int frameSize;
   protected float frameRate;
   protected boolean bigEndian;

   public AudioFormat(Encoding encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize, float frameRate, boolean bigEndian) {
      this.encoding = encoding;
      this.sampleRate = sampleRate;
      this.sampleSizeInBits = sampleSizeInBits;
      this.channels = channels;
      this.frameSize = frameSize;
      this.frameRate = frameRate;
      this.bigEndian = bigEndian;
   }

   public AudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian) {
      this(signed ? Encoding.PCM_SIGNED : Encoding.PCM_UNSIGNED, sampleRate, sampleSizeInBits, channels,
            channels != -1 && sampleSizeInBits != -1 ? (sampleSizeInBits + 7) / 8 * channels : -1,
            sampleRate, bigEndian);
   }

   public Encoding getEncoding() {
      return encoding;
   }

   public float getSampleRate() {
      return sampleRate;
   }

   public int getSampleSizeInBits() {
      return sampleSizeInBits;
   }

   public int getChannels() {
      return channels;
   }

   public int getFrameSize() {
      return frameSize;
   }

   public float getFrameRate() {
      return frameRate;
   }

   public boolean isBigEndian() {
      return bigEndian;
   }

   public String toString() {
      return encoding + " " + sampleRate + " Hz, " + sampleSizeInBits + " bit, " + channels + " channels, " + (bigEndian ? "big-endian" : "little-endian");
   }

   public static class Encoding {
      public static final Encoding PCM_SIGNED = new Encoding("PCM_SIGNED");
      public static final Encoding PCM_UNSIGNED = new Encoding("PCM_UNSIGNED");
      public static final Encoding PCM_FLOAT = new Encoding("PCM_FLOAT");
      public static final Encoding ULAW = new Encoding("ULAW");
      public static final Encoding ALAW = new Encoding("ALAW");

      private final String name;

      public Encoding(String name) {
         this.name = name;
      }

      public String toString() {
         return name;
      }

      public boolean equals(Object obj) {
         if (this == obj) return true;
         if (!(obj instanceof Encoding)) return false;
         return name.equals(((Encoding) obj).name);
      }

      public int hashCode() {
         return name.hashCode();
      }
   }
}
