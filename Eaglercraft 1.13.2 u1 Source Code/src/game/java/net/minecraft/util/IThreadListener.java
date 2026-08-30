package net.minecraft.util;

import net.lax1dude.eaglercraft.v1_8.futures.ListenableFuture;

public interface IThreadListener {
   ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule);

   boolean isCallingFromMinecraftThread();
}
