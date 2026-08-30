package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Timer {
   public int elapsedTicks;
   public float renderPartialTicks;
   public float elapsedPartialTicks;
   private long lastSyncSysClock;
   private final float tickLength;

   public Timer(float p_i49528_1_, long p_i49528_2_) {
      this.tickLength = 1000.0F / p_i49528_1_;
      this.lastSyncSysClock = p_i49528_2_;
   }

	public void updateTimer(long p_74275_1_) {
		long dt = p_74275_1_ - this.lastSyncSysClock;
		if(dt > 500L) {
			dt = 0L;
		}
		this.elapsedPartialTicks = (float)dt / this.tickLength;
		this.lastSyncSysClock = p_74275_1_;
		this.renderPartialTicks += this.elapsedPartialTicks;
		if(this.renderPartialTicks > 20.0F) {
			this.renderPartialTicks = 0.0F;
		}
		this.elapsedTicks = (int)this.renderPartialTicks;
		this.renderPartialTicks -= (float)this.elapsedTicks;
	}
}
