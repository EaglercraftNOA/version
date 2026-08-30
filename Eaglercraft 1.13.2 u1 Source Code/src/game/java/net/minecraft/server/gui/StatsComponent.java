package net.minecraft.server.gui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.Locale;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.Util;

public class StatsComponent implements ITickable {
   private static final DecimalFormat FORMATTER = Util.make(new DecimalFormat("########0.000"), (p_212730_0_) -> {
      p_212730_0_.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
   });
   private final int[] values = new int[256];
   private int vp;
   private final String[] msgs = new String[11];
   private final MinecraftServer server;
   private int ticks;

   public StatsComponent(MinecraftServer serverIn) {
      this.server = serverIn;
      serverIn.registerTickable(this);
      this.tickStats();
   }

   public void tick() {
      if (this.ticks++ % 10 == 0) {
         this.tickStats();
      }
   }

   private void tickStats() {
      long i = EagRuntime.totalMemory() - EagRuntime.freeMemory();
      this.msgs[0] = "Memory use: " + i / 1024L / 1024L + " mb (" + EagRuntime.freeMemory() * 100L / EagRuntime.maxMemory() + "% free)";
      this.msgs[1] = "Avg tick: " + FORMATTER.format(this.mean(this.server.tickTimeArray) * 1.0E-6D) + " ms";
      this.values[this.vp++ & 255] = (int)(i * 100L / EagRuntime.maxMemory());
   }

   private double mean(long[] valuesIn) {
      long i = 0L;

      for(long j : valuesIn) {
         i += j;
      }

      return (double)i / (double)valuesIn.length;
   }

   public int[] getValues() {
      return Arrays.copyOf(this.values, this.values.length);
   }

   public String[] getMessages() {
      return Arrays.copyOf(this.msgs, this.msgs.length);
   }
}
