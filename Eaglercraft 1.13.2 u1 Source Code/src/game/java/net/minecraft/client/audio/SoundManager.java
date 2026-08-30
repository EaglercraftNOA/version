package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EaglercraftSoundManager;
import net.minecraft.client.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@OnlyIn(Dist.CLIENT)
public class SoundManager {
   private static final Marker LOG_MARKER = MarkerManager.getMarker("SOUNDS");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<ResourceLocation> UNABLE_TO_PLAY = Sets.newHashSet();
   private final SoundHandler sndHandler;
   private final EaglercraftSoundManager eaglerManager;
   private final List<ISoundEventListener> listeners = Lists.newArrayList();
   private final List<Sound> soundsToPreload = Lists.newArrayList();
   private boolean loaded;

   public SoundManager(SoundHandler handler, GameSettings settings) {
      this.sndHandler = handler;
      this.eaglerManager = new EaglercraftSoundManager(settings, handler);
   }

   public void reload() {
      UNABLE_TO_PLAY.clear();

      for(SoundEvent soundevent : IRegistry.SOUND_EVENT) {
         ResourceLocation resourcelocation = soundevent.getName();
         if (this.sndHandler.getAccessor(resourcelocation) == null) {
            LOGGER.warn("Missing sound for event: {}", (Object)IRegistry.SOUND_EVENT.getKey(soundevent));
            UNABLE_TO_PLAY.add(resourcelocation);
         }
      }

      this.unload();
      this.loaded = true;
      this.eaglerManager.reloadSoundSystem();

      for(Sound sound : this.soundsToPreload) {
         this.eaglerManager.preloadSound(sound);
      }

      this.soundsToPreload.clear();
   }

   public void unload() {
      if (this.loaded) {
         this.stopAllSounds();
         this.eaglerManager.unloadSoundSystem();
         this.loaded = false;
      }
   }

   public void stopAllSounds() {
      this.eaglerManager.stopAllSounds();
   }

   public void addListener(ISoundEventListener listener) {
      this.listeners.add(listener);
   }

   public void removeListener(ISoundEventListener listener) {
      this.listeners.remove(listener);
   }

   public void tick() {
      if (this.loaded) {
         this.eaglerManager.updateAllSounds();
      }
   }

   public boolean isPlaying(ISound sound) {
      return this.loaded && this.eaglerManager.isSoundPlaying(sound);
   }

   public void stop(ISound sound) {
      if (this.loaded) {
         this.eaglerManager.stopSound(sound);
      }
   }

   public void play(ISound sound) {
      if (this.loaded) {
         SoundEventAccessor soundeventaccessor = sound.createAccessor(this.sndHandler);
         ResourceLocation resourcelocation = sound.getSoundLocation();
         if (soundeventaccessor == null) {
            if (UNABLE_TO_PLAY.add(resourcelocation)) {
               LOGGER.warn(LOG_MARKER, "Unable to play unknown soundEvent: {}", (Object)resourcelocation);
            }
         } else {
            if (!this.listeners.isEmpty()) {
               for(ISoundEventListener isoundeventlistener : this.listeners) {
                  isoundeventlistener.onPlaySound(sound, soundeventaccessor);
               }
            }

            Sound sound1 = sound.getSound();
            if (sound1 == SoundHandler.MISSING_SOUND) {
               if (UNABLE_TO_PLAY.add(resourcelocation)) {
                  LOGGER.warn(LOG_MARKER, "Unable to play empty soundEvent: {}", (Object)resourcelocation);
               }
            } else {
               this.eaglerManager.playSound(sound);
            }
         }
      }
   }

   public void enqueuePreload(Sound soundIn) {
      if (this.loaded) {
         this.eaglerManager.preloadSound(soundIn);
      } else {
         this.soundsToPreload.add(soundIn);
      }
   }

   public void pause() {
      this.eaglerManager.pauseAllSounds();
   }

   public void resume() {
      this.eaglerManager.resumeAllSounds();
   }

   public void playDelayed(ISound sound, int delay) {
      if (this.loaded) {
         this.eaglerManager.playDelayedSound(sound, delay);
      }
   }

   public void setListener(EntityPlayer player, float partialTicks) {
      if (this.loaded) {
         this.eaglerManager.setListener(player, partialTicks);
      }
   }

   public void stop(@Nullable ResourceLocation soundName, @Nullable SoundCategory category) {
      if (this.loaded) {
         this.eaglerManager.stopSound(soundName, category);
      }
   }

   public void setVolume(SoundCategory category, float volume) {
      this.eaglerManager.setSoundCategoryVolume(category, volume);
   }
}
