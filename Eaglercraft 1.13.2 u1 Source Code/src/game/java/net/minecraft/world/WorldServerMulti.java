package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.village.VillageCollection;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
   public WorldServerMulti(MinecraftServer server, ISaveHandler p_i49820_2_, DimensionType p_i49820_3_, WorldServer p_i49820_4_, Profiler p_i49820_5_) {
      super(server, p_i49820_2_, p_i49820_4_.getSavedDataStorage(), new DerivedWorldInfo(p_i49820_4_.getWorldInfo()), p_i49820_3_, p_i49820_5_);
      p_i49820_4_.getWorldBorder().addListener(new IBorderListener() {
         public void onSizeChanged(WorldBorder border, double newSize) {
            WorldServerMulti.this.getWorldBorder().setTransition(newSize);
         }

         public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            WorldServerMulti.this.getWorldBorder().setTransition(oldSize, newSize, time);
         }

         public void onCenterChanged(WorldBorder border, double x, double z) {
            WorldServerMulti.this.getWorldBorder().setCenter(x, z);
         }

         public void onWarningTimeChanged(WorldBorder border, int newTime) {
            WorldServerMulti.this.getWorldBorder().setWarningTime(newTime);
         }

         public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
            WorldServerMulti.this.getWorldBorder().setWarningDistance(newDistance);
         }

         public void onDamageAmountChanged(WorldBorder border, double newAmount) {
            WorldServerMulti.this.getWorldBorder().setDamagePerBlock(newAmount);
         }

         public void onDamageBufferChanged(WorldBorder border, double newSize) {
            WorldServerMulti.this.getWorldBorder().setDamageBuffer(newSize);
         }
      });
   }

   protected void saveLevel() {
   }

   public WorldServerMulti init() {
      String s = VillageCollection.fileNameForProvider(this.dimension);
      VillageCollection villagecollection = this.getSavedData(DimensionType.OVERWORLD, VillageCollection::new, s);
      if (villagecollection == null) {
         this.villageCollection = new VillageCollection(this);
         this.setSavedData(DimensionType.OVERWORLD, s, this.villageCollection);
      } else {
         this.villageCollection = villagecollection;
         this.villageCollection.setWorld(this);
      }

      return this;
   }

   public void saveAdditionalData() {
      this.dimension.onWorldSave();
   }
}
