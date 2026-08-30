package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglerInputStream;
import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.internal.IAudioCacheLoader;
import net.lax1dude.eaglercraft.internal.IAudioHandle;
import net.lax1dude.eaglercraft.internal.IAudioResource;
import net.lax1dude.eaglercraft.internal.PlatformAudio;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound.AttenuationType;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SoundManager {

	protected class ActiveSoundEvent {

		protected final SoundManager manager;

		protected final ISound soundInstance;
		protected final SoundCategory soundCategory;
		protected IAudioHandle soundHandle;

		protected float activeX;
		protected float activeY;
		protected float activeZ;

		protected float activePitch;
		protected float activeGain;

		protected int repeatCounter = 0;
		protected boolean paused = false;

		protected ActiveSoundEvent(SoundManager manager, ISound soundInstance, SoundCategory soundCategory,
				IAudioHandle soundHandle) {
			this.manager = manager;
			this.soundInstance = soundInstance;
			this.soundCategory = soundCategory;
			this.soundHandle = soundHandle;
			this.activeX = soundInstance.getXPosF();
			this.activeY = soundInstance.getYPosF();
			this.activeZ = soundInstance.getZPosF();
			this.activePitch = soundInstance.getPitch();
			this.activeGain = soundInstance.getVolume();
		}

		protected void updateLocation() {
			float x = soundInstance.getXPosF();
			float y = soundInstance.getYPosF();
			float z = soundInstance.getZPosF();
			float pitch = soundInstance.getPitch();
			float gain = soundInstance.getVolume();
			if (x != activeX || y != activeY || z != activeZ) {
				soundHandle.move(x, y, z);
				activeX = x;
				activeY = y;
				activeZ = z;
			}
			if (pitch != activePitch) {
				soundHandle.pitch(SoundManager.this.getClampedPitch(soundInstance));
				activePitch = pitch;
			}
			if (gain != activeGain) {
				soundHandle.gain(SoundManager.this.getClampedVolume(soundInstance));
				activeGain = gain;
			}
		}

	}

	protected static class WaitingSoundEvent {

		protected final ISound playSound;
		protected int playTicks;
		protected boolean paused = false;

		private WaitingSoundEvent(ISound playSound, int playTicks) {
			this.playSound = playSound;
			this.playTicks = playTicks;
		}

	}

	private static final Logger LOGGER = LogManager.getLogger();
	private final GameSettings options;
	private final SoundHandler sndHandler;

	private final float[] categoryVolumes;
	private final List<ISoundEventListener> listeners;
	private final List<ActiveSoundEvent> activeSounds;
	private final List<WaitingSoundEvent> queuedSounds;

	public SoundManager(SoundHandler p_i45119_1_, GameSettings p_i45119_2_) {
		this.sndHandler = p_i45119_1_;
		this.options = p_i45119_2_;
		categoryVolumes = new float[] { options.getSoundLevel(SoundCategory.MASTER),
				options.getSoundLevel(SoundCategory.MUSIC), options.getSoundLevel(SoundCategory.RECORDS),
				options.getSoundLevel(SoundCategory.WEATHER), options.getSoundLevel(SoundCategory.BLOCKS),
				options.getSoundLevel(SoundCategory.HOSTILE), options.getSoundLevel(SoundCategory.NEUTRAL),
				options.getSoundLevel(SoundCategory.PLAYERS), options.getSoundLevel(SoundCategory.AMBIENT),
				options.getSoundLevel(SoundCategory.VOICE) };
		this.listeners = Lists.<ISoundEventListener>newArrayList();
		activeSounds = new LinkedList<>();
		queuedSounds = new LinkedList<>();
	}

	public void reloadSoundSystem() {
		PlatformAudio.flushAudioCache();
	}

	private float getVolume(SoundCategory category) {
		return category != null && category != SoundCategory.MASTER ? this.options.getSoundLevel(category) : 1.0F;
	}

	public void setVolume(SoundCategory category, float volume) {
		categoryVolumes[category.getCategoryId()] = volume;
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while (soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if ((category == SoundCategory.MASTER || evt.soundCategory == category) && !evt.soundHandle.shouldFree()) {
				float newVolume = getClampedVolume(evt.soundInstance);
				if (newVolume > 0.0f) {
					evt.soundHandle.gain(newVolume);
				} else {
					evt.soundHandle.end();
					soundItr.remove();
				}
			}
		}
	}

	/**
	 * Cleans up the Sound System
	 */
	public void unloadSoundSystem() {
	}

	/**
	 * Stops all currently playing sounds
	 */
	public void stopAllSounds() {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while (soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if (!evt.soundHandle.shouldFree()) {
				evt.soundHandle.end();
			}
		}
		activeSounds.clear();
	}

	public void addListener(ISoundEventListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(ISoundEventListener listener) {
		this.listeners.remove(listener);
	}

	public void updateAllSounds() {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while (soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			boolean persist = false;
			if (!evt.paused && (evt.soundInstance instanceof ITickableSound)) {
				boolean destroy = false;
				ITickableSound snd = (ITickableSound) evt.soundInstance;
				lbl: {
					try {
						snd.update();
						if (snd.isDonePlaying()) {
							destroy = true;
							break lbl;
						}
						persist = true;
					} catch (Throwable t) {
						LOGGER.error("Error ticking sound: {}", t.toString());
						LOGGER.error(t);
						destroy = true;
					}
				}
				if (destroy) {
					if (!evt.soundHandle.shouldFree()) {
						evt.soundHandle.end();
					}
					soundItr.remove();
					continue;
				}
			}
			if (evt.soundHandle.shouldFree()) {
				if (!persist) {
					soundItr.remove();
				}
			} else {
				evt.updateLocation();
			}
		}
		Iterator<WaitingSoundEvent> soundItr2 = queuedSounds.iterator();
		while (soundItr2.hasNext()) {
			WaitingSoundEvent evt = soundItr2.next();
			if (!evt.paused && --evt.playTicks <= 0) {
				soundItr2.remove();
				playSound(evt.playSound);
			}
		}
		PlatformAudio.clearAudioCache();
	}

	/**
	 * Returns true if the sound is playing or still within time
	 */
	public boolean isSoundPlaying(ISound sound) {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while (soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if (evt.soundInstance == sound) {
				return !evt.soundHandle.shouldFree();
			}
		}
		return false;
	}

	public void stopSound(ISound sound) {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while (soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if (evt.soundInstance == sound) {
				if (!evt.soundHandle.shouldFree()) {
					evt.soundHandle.end();
					soundItr.remove();
					return;
				}
			}
		}
		Iterator<WaitingSoundEvent> soundItr2 = queuedSounds.iterator();
		while (soundItr2.hasNext()) {
			if (soundItr2.next().playSound == sound) {
				soundItr2.remove();
			}
		}
	}

	private final IAudioCacheLoader browserResourcePackLoader = filename -> {
		try {
			return EaglerInputStream.inputStreamToBytesQuiet(Minecraft.getMinecraft().getResourceManager()
					.getResource(new ResourceLocation(filename)).getInputStream());
		} catch (Throwable t) {
			return null;
		}
	};

	public void playSound(ISound p_sound) {
		if (!PlatformAudio.available()) {
			return;
		}
		if (p_sound != null && categoryVolumes[SoundCategory.MASTER.getCategoryId()] > 0.0f) {
			SoundEventAccessor accessor = p_sound.createAccessor(this.sndHandler);
			if (accessor == null) {
				LOGGER.warn("Unable to play unknown soundEvent(1): {}", p_sound.getSoundLocation().toString());
			} else {
				if (!this.listeners.isEmpty()) {
					for (ISoundEventListener isoundeventlistener : this.listeners) {
						isoundeventlistener.soundPlay(p_sound, accessor);
					}
				}

				Sound etr = accessor.cloneEntry();
				if (etr == SoundHandler.MISSING_SOUND) {
					LOGGER.warn("Unable to play empty soundEvent(2): {}", etr.getSoundAsOggLocation().toString());
				} else {
					ResourceLocation lc = etr.getSoundAsOggLocation();
					IAudioResource trk;
					if (EagRuntime.getPlatformType() != EnumPlatformType.DESKTOP) {
						trk = PlatformAudio.loadAudioDataNew(lc.toString(), !etr.isStreaming(),
								browserResourcePackLoader);
					} else {
						trk = PlatformAudio.loadAudioData(lc.getResourcePath(), !etr.isStreaming());
					}
					if (trk == null) {
						LOGGER.warn("Unable to play unknown soundEvent(3): {}", p_sound.getSoundLocation().toString());
					} else {

						ActiveSoundEvent newSound = new ActiveSoundEvent(this, p_sound, p_sound.getCategory(), null);

						float pitch = getClampedPitch(p_sound);
						float attenuatedGain = getClampedVolume(p_sound);
						boolean repeat = p_sound.canRepeat();

						AttenuationType tp = p_sound.getAttenuationType();
						if (tp == AttenuationType.LINEAR) {
							newSound.soundHandle = PlatformAudio.beginPlayback(trk, newSound.activeX, newSound.activeY,
									newSound.activeZ, attenuatedGain, pitch, repeat);
						} else {
							newSound.soundHandle = PlatformAudio.beginPlaybackStatic(trk, attenuatedGain, pitch,
									repeat);
						}

						if (newSound.soundHandle == null) {
							LOGGER.error("Unable to play soundEvent(4): {}", p_sound.getSoundLocation().toString());
						} else {
							activeSounds.add(newSound);
						}
					}
				}
			}
		}
	}

	private float getClampedPitch(ISound soundIn) {
		return MathHelper.clamp(soundIn.getPitch(), 0.5F, 2.0F);
	}

	private float getClampedVolume(ISound soundIn) {
		return MathHelper.clamp(soundIn.getVolume() * this.getVolume(soundIn.getCategory()), 0.0F, 1.0F);
	}

	/**
	 * Pauses all currently playing sounds
	 */
	public void pauseAllSounds() {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while (soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if (!evt.soundHandle.shouldFree()) {
				evt.soundHandle.pause(true);
				evt.paused = true;
			}
		}
		Iterator<WaitingSoundEvent> soundItr2 = queuedSounds.iterator();
		while (soundItr2.hasNext()) {
			soundItr2.next().paused = true;
		}
	}

	/**
	 * Resumes playing all currently playing sounds (after pauseAllSounds)
	 */
	public void resumeAllSounds() {
		Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
		while (soundItr.hasNext()) {
			ActiveSoundEvent evt = soundItr.next();
			if (!evt.soundHandle.shouldFree()) {
				evt.soundHandle.pause(false);
				evt.paused = false;
			}
		}
		Iterator<WaitingSoundEvent> soundItr2 = queuedSounds.iterator();
		while (soundItr2.hasNext()) {
			soundItr2.next().paused = false;
		}
	}

	/**
	 * Adds a sound to play in n tick
	 */
	public void playDelayedSound(ISound sound, int delay) {
		queuedSounds.add(new WaitingSoundEvent(sound, delay));
	}

	/**
	 * Sets the listener of sounds
	 */
	public void setListener(EntityPlayer player, float p_148615_2_) {
		if (!PlatformAudio.available()) {
			return;
		}
		if (player != null) {
			try {
				float f = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * p_148615_2_;
				float f1 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * p_148615_2_;
				double d0 = player.prevPosX + (player.posX - player.prevPosX) * (double) p_148615_2_;
				double d1 = player.prevPosY + (player.posY - player.prevPosY) * (double) p_148615_2_
						+ (double) player.getEyeHeight();
				double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * (double) p_148615_2_;
				PlatformAudio.setListener((float) d0, (float) d1, (float) d2, f, f1);
			} catch (Throwable t) {
				// eaglercraft 1.5.2 had Infinity/NaN crashes for this function which
				// couldn't be resolved via if statement checks in the above variables
			}
		}
	}

	public void stop(String p_189567_1_, SoundCategory p_189567_2_) {
		if (p_189567_2_ != null) {
			Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
			while (soundItr.hasNext()) {
				ActiveSoundEvent evt = soundItr.next();
				if ((p_189567_2_ == SoundCategory.MASTER || evt.soundCategory == p_189567_2_)
						&& !evt.soundHandle.shouldFree()) {
					ISound isound = evt.soundInstance;

					if (p_189567_1_.isEmpty()) {
						this.stopSound(isound);
					} else if (isound.getSoundLocation().equals(new ResourceLocation(p_189567_1_))) {
						this.stopSound(isound);
					}
				}
			}
		} else if (p_189567_1_.isEmpty()) {
			this.stopAllSounds();
		} else {
			Iterator<ActiveSoundEvent> soundItr = activeSounds.iterator();
			while (soundItr.hasNext()) {
				ActiveSoundEvent evt = soundItr.next();
				ISound isound1 = evt.soundInstance;

				if (isound1.getSoundLocation().equals(new ResourceLocation(p_189567_1_))) {
					this.stopSound(isound1);
				}
			}
		}
	}
}