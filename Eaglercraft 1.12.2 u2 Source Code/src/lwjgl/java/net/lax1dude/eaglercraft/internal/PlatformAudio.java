package net.lax1dude.eaglercraft.internal;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.internal.paulscode.lwjgl3.LibraryLWJGLOpenAL;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.codecs.CodecWav;

/**
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class PlatformAudio {

	protected static class PaulscodeAudioResource implements IAudioResource {

		protected final URL resourceLoc;

		protected PaulscodeAudioResource(URL resourceLoc) {
			this.resourceLoc = resourceLoc;
		}

	}

	protected static class PaulscodeAudioHandle implements IAudioHandle {

		protected final String sourceName;
		protected long stall;
		protected boolean paused = false;

		protected PaulscodeAudioHandle(String sourceName) {
			this.sourceName = sourceName;
			this.stall = PlatformRuntime.steadyTimeMillis();
		}

		@Override
		public void pause(boolean setPaused) {
			if (setPaused) {
				if (!paused) {
					sndSystem.pause(sourceName);
					paused = true;
				}
			} else {
				if (paused) {
					sndSystem.play(sourceName);
					paused = false;
				}
			}
		}

		@Override
		public void repeat(boolean en) {
			sndSystem.setLooping(sourceName, en);
		}

		@Override
		public void restart() {
			this.stall = PlatformRuntime.steadyTimeMillis();
			sndSystem.rewind(sourceName);
			sndSystem.play(sourceName);
			paused = false;
		}

		@Override
		public void move(float x, float y, float z) {
			sndSystem.setPosition(sourceName, x, y, z);
		}

		@Override
		public void pitch(float f) {
			sndSystem.setPitch(sourceName, f);
		}

		@Override
		public void gain(float f) {
			sndSystem.setVolume(sourceName, f);
		}

		@Override
		public void end() {
			sndSystem.stop(sourceName);
			paused = false;
		}

		@Override
		public boolean shouldFree() {
			return !paused && !sndSystem.playing(sourceName) && PlatformRuntime.steadyTimeMillis() - this.stall > 250l; // TODO:
																														// I
																														// hate
																														// this
																														// hack
		}

	}

	public static IAudioResource loadAudioData(String filename, boolean holdInCache) {
		URL ret = getURLForSoundResource(new ResourceLocation(filename));
		if (ret != null) {
			return new PaulscodeAudioResource(ret);
		} else {
			return null;
		}
	}
	
	private static URL getURLForSoundResource(final ResourceLocation p_148612_0_) {
		final InputStream is;
		try {
			is = Minecraft.getMinecraft().getResourceManager().getResource(p_148612_0_).getInputStream();
			if(is == null) {
				return null;
			}
		} catch(Exception e) {
			return null;
		}
		
		try {
			String s = String.format("%s:%s:%s", "mcsounddomain", p_148612_0_.getResourceDomain(), p_148612_0_.getResourcePath());
			URLStreamHandler urlstreamhandler = new URLStreamHandler() {
				protected URLConnection openConnection(URL p_openConnection_1_) {
					return new URLConnection(p_openConnection_1_) {
						public void connect() throws IOException {
						}
						public InputStream getInputStream() throws IOException {
							return is;
						}
					};
				}
			};
        	
			try {
				return new URL((URL)null, s, urlstreamhandler);
			} catch (MalformedURLException var4) {
				throw new Error("TODO: Sanely handle url exception! :D");
			}
		} catch(Exception e) {
			return null;
		}
	}

	public static void clearAudioCache() {
		// browser only
	}

	public static void flushAudioCache() {

	}

	public static IAudioResource loadAudioDataNew(String filename, boolean holdInCache, IAudioCacheLoader loader) {
		throw new UnsupportedOperationException("Browser only!");
	}

	private static final Logger logger = LogManager.getLogger("EaglercraftPlatformAudio");
	private static SoundSystem sndSystem = null;

	static void platformInitialize() {
		logger.info("Eaglercraft uses Paul Lamb's SoundSystem (with LWJGL3)");
		logger.info("    \"Author: Paul Lamb, www.paulscode.com\"");
		try {
			SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
			SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
			SoundSystemConfig.setCodec("wav", CodecWav.class);
			SoundSystemConfig.setLogger(new SoundSystemLogger() {
				public void message(String parString1, int parInt1) {
					if (!parString1.isEmpty()) {
						logger.info(parString1);
					}
				}

				public void importantMessage(String parString1, int parInt1) {
					if (!parString1.isEmpty()) {
						logger.warn(parString1);
					}
				}

				public void errorMessage(String parString1, String parString2, int parInt1) {
					if (!parString2.isEmpty()) {
						logger.error("Error in class \"{}\"!", parString1);
						logger.error(parString2);
					}
				}
			});
			sndSystem = new SoundSystem();
		} catch (Throwable t) {
			logger.error("Could not initialize Paulscode SoundSystem! Is this system's OpenAL installed correctly?");
			logger.error(t);
			sndSystem = null;
		}
	}

	static void platformShutdown() {
		if (sndSystem != null) {
			sndSystem.cleanup();
			sndSystem = null;
		}
	}

	public static boolean available() {
		return sndSystem != null;
	}

	private static int sourceCounter = 0;

	public static IAudioHandle beginPlayback(IAudioResource track, float x, float y, float z, float volume, float pitch,
			boolean repeat) {
		if (sndSystem == null) {
			return null;
		}

		float f1 = 16.0F;
		if (volume > 1.0F) {
			f1 *= volume;
		}

		String srcName = "src" + ++sourceCounter;
		sndSystem.newSource(false, srcName, ((PaulscodeAudioResource) track).resourceLoc,
				((PaulscodeAudioResource) track).resourceLoc.getPath(), false, x, y, z, 2, f1);
		sndSystem.setTemporary(srcName, true);
		sndSystem.setPitch(srcName, pitch);
		sndSystem.setVolume(srcName, volume);
		sndSystem.setLooping(srcName, repeat);
		sndSystem.play(srcName);

		return new PaulscodeAudioHandle(srcName);
	}

	public static IAudioHandle beginPlaybackStatic(IAudioResource track, float volume, float pitch, boolean repeat) {
		if (sndSystem == null) {
			return null;
		}

		String srcName = "src" + ++sourceCounter;
		sndSystem.newSource(false, srcName, ((PaulscodeAudioResource) track).resourceLoc,
				((PaulscodeAudioResource) track).resourceLoc.getPath(), false, 0.0f, 0.0f, 0.0f, 0, 0.0f);
		sndSystem.setTemporary(srcName, true);
		sndSystem.setPitch(srcName, pitch);
		sndSystem.setVolume(srcName, volume);
		sndSystem.setLooping(srcName, repeat);
		sndSystem.play(srcName);

		return new PaulscodeAudioHandle(srcName);
	}

	public static void setListener(float x, float y, float z, float pitchDegrees, float yawDegrees) {
		if (sndSystem == null) {
			return;
		}
		float f2 = MathHelper.cos((yawDegrees + 90.0F) * 0.017453292F);
		float f3 = MathHelper.sin((yawDegrees + 90.0F) * 0.017453292F);
		float f4 = MathHelper.cos(-pitchDegrees * 0.017453292F);
		float f5 = MathHelper.sin(-pitchDegrees * 0.017453292F);
		float f6 = MathHelper.cos((-pitchDegrees + 90.0F) * 0.017453292F);
		float f7 = MathHelper.sin((-pitchDegrees + 90.0F) * 0.017453292F);
		float f8 = f2 * f4;
		float f9 = f3 * f4;
		float f10 = f2 * f6;
		float f11 = f3 * f6;
		sndSystem.setListenerPosition(x, y, z);
		sndSystem.setListenerOrientation(f8, f5, f9, f10, f7, f11);
	}

	public static void setMicVol(float vol) {
		// nope
	}

}