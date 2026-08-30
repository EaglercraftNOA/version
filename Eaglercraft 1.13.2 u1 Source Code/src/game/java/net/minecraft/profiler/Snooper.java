package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.Map.Entry;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Snooper {
   private final Map<String, Object> snooperStats = Maps.newHashMap();
   private final Map<String, Object> clientStats = Maps.newHashMap();
   private final String uniqueID = UUID.randomUUID().toString();
   private final URL serverUrl;
   private final ISnooperInfo playerStatsCollector;
   private final Timer timer = new Timer("Snooper Timer", true);
   private final Object syncLock = new Object();
   private final long minecraftStartTimeMilis;
   private boolean isRunning;

   public Snooper(String side, ISnooperInfo playerStatCollector, long startTime) {
      try {
         this.serverUrl = new URL("http://snoop.minecraft.net/" + side + "?version=" + 2);
      } catch (MalformedURLException var6) {
         throw new IllegalArgumentException();
      }

      this.playerStatsCollector = playerStatCollector;
      this.minecraftStartTimeMilis = startTime;
   }

   public void start() {
      if (!this.isRunning) {
         ;
      }

   }

   public void addMemoryStatsToSnooper() {
      this.addStatToSnooper("memory_total", EagRuntime.totalMemory());
      this.addStatToSnooper("memory_max", EagRuntime.maxMemory());
      this.addStatToSnooper("memory_free", EagRuntime.freeMemory());
      this.addStatToSnooper("cpu_cores", EagRuntime.availableProcessors());
      this.playerStatsCollector.fillSnooper(this);
   }

   public void addClientStat(String statName, Object statValue) {
      synchronized(this.syncLock) {
         this.clientStats.put(statName, statValue);
      }
   }

   public void addStatToSnooper(String statName, Object statValue) {
      synchronized(this.syncLock) {
         this.snooperStats.put(statName, statValue);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public Map<String, String> getCurrentStats() {
      Map<String, String> map = Maps.newLinkedHashMap();
      synchronized(this.syncLock) {
         this.addMemoryStatsToSnooper();

         for(Entry<String, Object> entry : this.snooperStats.entrySet()) {
            map.put(entry.getKey(), entry.getValue().toString());
         }

         for(Entry<String, Object> entry1 : this.clientStats.entrySet()) {
            map.put(entry1.getKey(), entry1.getValue().toString());
         }

         return map;
      }
   }

   public boolean isSnooperRunning() {
      return this.isRunning;
   }

   public void stop() {
      this.timer.cancel();
   }

   @OnlyIn(Dist.CLIENT)
   public String getUniqueID() {
      return this.uniqueID;
   }

   public long getMinecraftStartTimeMillis() {
      return this.minecraftStartTimeMilis;
   }
}
