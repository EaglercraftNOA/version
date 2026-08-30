package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldBorder {
   private final List<IBorderListener> listeners = Lists.newArrayList();
   private double damagePerBlock = 0.2D;
   private double damageBuffer = 5.0D;
   private int warningTime = 15;
   private int warningDistance = 5;
   private double centerX;
   private double centerZ;
   private int worldSize = 29999984;
   private WorldBorder.IBorderInfo state = new WorldBorder.StationaryBorderInfo(6.0E7D);

   public boolean contains(BlockPos pos) {
      return (double)(pos.getX() + 1) > this.minX() && (double)pos.getX() < this.maxX() && (double)(pos.getZ() + 1) > this.minZ() && (double)pos.getZ() < this.maxZ();
   }

   public boolean contains(ChunkPos range) {
      return (double)range.getXEnd() > this.minX() && (double)range.getXStart() < this.maxX() && (double)range.getZEnd() > this.minZ() && (double)range.getZStart() < this.maxZ();
   }

   public boolean contains(AxisAlignedBB bb) {
      return bb.maxX > this.minX() && bb.minX < this.maxX() && bb.maxZ > this.minZ() && bb.minZ < this.maxZ();
   }

   public double getClosestDistance(Entity entityIn) {
      return this.getClosestDistance(entityIn.posX, entityIn.posZ);
   }

   public double getClosestDistance(double x, double z) {
      double d0 = z - this.minZ();
      double d1 = this.maxZ() - z;
      double d2 = x - this.minX();
      double d3 = this.maxX() - x;
      double d4 = Math.min(d2, d3);
      d4 = Math.min(d4, d0);
      return Math.min(d4, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public EnumBorderStatus getStatus() {
      return this.state.getStatus();
   }

   public double minX() {
      return this.state.getMinX();
   }

   public double minZ() {
      return this.state.getMinZ();
   }

   public double maxX() {
      return this.state.getMaxX();
   }

   public double maxZ() {
      return this.state.getMaxZ();
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double x, double z) {
      this.centerX = x;
      this.centerZ = z;
      this.state.onCenterChanged();

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onCenterChanged(this, x, z);
      }

   }

   public double getDiameter() {
      return this.state.getSize();
   }

   public long getTimeUntilTarget() {
      return this.state.getTimeUntilTarget();
   }

   public double getTargetSize() {
      return this.state.getTargetSize();
   }

   public void setTransition(double newSize) {
      this.state = new WorldBorder.StationaryBorderInfo(newSize);

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onSizeChanged(this, newSize);
      }

   }

   public void setTransition(double oldSize, double newSize, long time) {
      this.state = (WorldBorder.IBorderInfo)(oldSize != newSize ? new WorldBorder.MovingBorderInfo(oldSize, newSize, time) : new WorldBorder.StationaryBorderInfo(newSize));

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onTransitionStarted(this, oldSize, newSize, time);
      }

   }

   protected List<IBorderListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(IBorderListener listener) {
      this.listeners.add(listener);
   }

   public void setSize(int size) {
      this.worldSize = size;
      this.state.onSizeChanged();
   }

   public int getSize() {
      return this.worldSize;
   }

   public double getDamageBuffer() {
      return this.damageBuffer;
   }

   public void setDamageBuffer(double bufferSize) {
      this.damageBuffer = bufferSize;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onDamageBufferChanged(this, bufferSize);
      }

   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double newAmount) {
      this.damagePerBlock = newAmount;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onDamageAmountChanged(this, newAmount);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public double getResizeSpeed() {
      return this.state.getResizeSpeed();
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int warningTime) {
      this.warningTime = warningTime;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onWarningTimeChanged(this, warningTime);
      }

   }

   public int getWarningDistance() {
      return this.warningDistance;
   }

   public void setWarningDistance(int warningDistance) {
      this.warningDistance = warningDistance;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onWarningDistanceChanged(this, warningDistance);
      }

   }

   public void tick() {
      this.state = this.state.tick();
   }

   interface IBorderInfo {
      double getMinX();

      double getMaxX();

      double getMinZ();

      double getMaxZ();

      double getSize();

      @OnlyIn(Dist.CLIENT)
      double getResizeSpeed();

      long getTimeUntilTarget();

      double getTargetSize();

      @OnlyIn(Dist.CLIENT)
      EnumBorderStatus getStatus();

      void onSizeChanged();

      void onCenterChanged();

      WorldBorder.IBorderInfo tick();
   }

   class MovingBorderInfo implements WorldBorder.IBorderInfo {
      private final double oldSize;
      private final double newSize;
      private final long endTime;
      private final long startTime;
      private final double transitionTime;

      private MovingBorderInfo(double p_i49838_2_, double p_i49838_4_, long p_i49838_6_) {
         this.oldSize = p_i49838_2_;
         this.newSize = p_i49838_4_;
         this.transitionTime = (double)p_i49838_6_;
         this.startTime = Util.milliTime();
         this.endTime = this.startTime + p_i49838_6_;
      }

      public double getMinX() {
         return Math.max(WorldBorder.this.getCenterX() - this.getSize() / 2.0D, (double)(-WorldBorder.this.worldSize));
      }

      public double getMinZ() {
         return Math.max(WorldBorder.this.getCenterZ() - this.getSize() / 2.0D, (double)(-WorldBorder.this.worldSize));
      }

      public double getMaxX() {
         return Math.min(WorldBorder.this.getCenterX() + this.getSize() / 2.0D, (double)WorldBorder.this.worldSize);
      }

      public double getMaxZ() {
         return Math.min(WorldBorder.this.getCenterZ() + this.getSize() / 2.0D, (double)WorldBorder.this.worldSize);
      }

      public double getSize() {
         double d0 = (double)(Util.milliTime() - this.startTime) / this.transitionTime;
         return d0 < 1.0D ? this.oldSize + (this.newSize - this.oldSize) * d0 : this.newSize;
      }

      @OnlyIn(Dist.CLIENT)
      public double getResizeSpeed() {
         return Math.abs(this.oldSize - this.newSize) / (double)(this.endTime - this.startTime);
      }

      public long getTimeUntilTarget() {
         return this.endTime - Util.milliTime();
      }

      public double getTargetSize() {
         return this.newSize;
      }

      @OnlyIn(Dist.CLIENT)
      public EnumBorderStatus getStatus() {
         return this.newSize < this.oldSize ? EnumBorderStatus.SHRINKING : EnumBorderStatus.GROWING;
      }

      public void onCenterChanged() {
      }

      public void onSizeChanged() {
      }

      public WorldBorder.IBorderInfo tick() {
         return (WorldBorder.IBorderInfo)(this.getTimeUntilTarget() <= 0L ? WorldBorder.this.new StationaryBorderInfo(this.newSize) : this);
      }
   }

   class StationaryBorderInfo implements WorldBorder.IBorderInfo {
      private final double size;
      private double minX;
      private double minZ;
      private double maxX;
      private double maxZ;

      public StationaryBorderInfo(double p_i49837_2_) {
         this.size = p_i49837_2_;
         this.func_212665_m();
      }

      public double getMinX() {
         return this.minX;
      }

      public double getMaxX() {
         return this.maxX;
      }

      public double getMinZ() {
         return this.minZ;
      }

      public double getMaxZ() {
         return this.maxZ;
      }

      public double getSize() {
         return this.size;
      }

      @OnlyIn(Dist.CLIENT)
      public EnumBorderStatus getStatus() {
         return EnumBorderStatus.STATIONARY;
      }

      @OnlyIn(Dist.CLIENT)
      public double getResizeSpeed() {
         return 0.0D;
      }

      public long getTimeUntilTarget() {
         return 0L;
      }

      public double getTargetSize() {
         return this.size;
      }

      private void func_212665_m() {
         this.minX = Math.max(WorldBorder.this.getCenterX() - this.size / 2.0D, (double)(-WorldBorder.this.worldSize));
         this.minZ = Math.max(WorldBorder.this.getCenterZ() - this.size / 2.0D, (double)(-WorldBorder.this.worldSize));
         this.maxX = Math.min(WorldBorder.this.getCenterX() + this.size / 2.0D, (double)WorldBorder.this.worldSize);
         this.maxZ = Math.min(WorldBorder.this.getCenterZ() + this.size / 2.0D, (double)WorldBorder.this.worldSize);
      }

      public void onSizeChanged() {
         this.func_212665_m();
      }

      public void onCenterChanged() {
         this.func_212665_m();
      }

      public WorldBorder.IBorderInfo tick() {
         return this;
      }
   }
}
