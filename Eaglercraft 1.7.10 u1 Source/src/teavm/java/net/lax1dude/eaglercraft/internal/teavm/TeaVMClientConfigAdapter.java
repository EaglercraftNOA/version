package net.lax1dude.eaglercraft.internal.teavm;

import net.lax1dude.eaglercraft.EaglercraftVersion;

import net.lax1dude.eaglercraft.Random;
import net.lax1dude.eaglercraft.sp.relay.RelayEntry;
import org.json.JSONObject;
import org.teavm.jso.JSObject;

import net.lax1dude.eaglercraft.internal.IClientConfigAdapter;
import net.lax1dude.eaglercraft.internal.IClientConfigAdapterHooks;
import net.lax1dude.eaglercraft.internal.teavm.opts.JSEaglercraftXOptsHooks;
import net.lax1dude.eaglercraft.internal.teavm.opts.JSEaglercraftXOptsRoot;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */
public class TeaVMClientConfigAdapter implements IClientConfigAdapter {

	public static final IClientConfigAdapter instance = new TeaVMClientConfigAdapter();

	private String worldsDB = "worlds";
	private String resourcePacksDB = "resourcePacks";
	private boolean checkGLErrors = false;
	private String localStorageNamespace = "_eaglercraftX";
	private final TeaVMClientConfigAdapterHooks hooks = new TeaVMClientConfigAdapterHooks();
	private boolean forceWebGL1 = false;
	private boolean forceWebGL2 = false;
	private boolean allowExperimentalWebGL1 = true;
	private boolean useWebGLExt = true;
	private boolean useDelayOnSwap = false;
	private boolean useJOrbisAudioDecoder = false;
	private boolean useXHRFetch = false;
	private boolean useVisualViewport = true;
	private boolean deobfStackTraces = true;
	private boolean disableBlobURLs = false;
	private boolean eaglerNoDelay = false;
	private boolean ramdiskMode = false;
	private boolean enableEPKVersionCheck = true;
	private JSONObject integratedServerOpts;

	private boolean keepAliveHack = true;
	private boolean finishOnSwap = true;

	public void loadNative(JSObject jsObject) {
		JSEaglercraftXOptsRoot eaglercraftXOpts = (JSEaglercraftXOptsRoot)jsObject;
		integratedServerOpts = new JSONObject();

		worldsDB = eaglercraftXOpts.getWorldsDB("worlds");
		resourcePacksDB = eaglercraftXOpts.getResourcePacksDB("resourcePacks");
		checkGLErrors = eaglercraftXOpts.getCheckGLErrors(false);
		localStorageNamespace = eaglercraftXOpts.getLocalStorageNamespace(EaglercraftVersion.localStorageNamespace);
		forceWebGL1 = eaglercraftXOpts.getForceWebGL1(false);
		forceWebGL2 = eaglercraftXOpts.getForceWebGL2(false);
		allowExperimentalWebGL1 = eaglercraftXOpts.getAllowExperimentalWebGL1(true);
		useWebGLExt = eaglercraftXOpts.getUseWebGLExt(true);
		useDelayOnSwap = eaglercraftXOpts.getUseDelayOnSwap(false);
		useJOrbisAudioDecoder = eaglercraftXOpts.getUseJOrbisAudioDecoder(false);
		useXHRFetch = eaglercraftXOpts.getUseXHRFetch(false);
		useVisualViewport = eaglercraftXOpts.getUseVisualViewport(true);
		deobfStackTraces = eaglercraftXOpts.getDeobfStackTraces(true);
		disableBlobURLs = eaglercraftXOpts.getDisableBlobURLs(false);
		eaglerNoDelay = eaglercraftXOpts.getEaglerNoDelay(false);
		ramdiskMode = eaglercraftXOpts.getRamdiskMode(false);
		enableEPKVersionCheck = eaglercraftXOpts.getEnableEPKVersionCheck(true);
		keepAliveHack = eaglercraftXOpts.getKeepAliveHack(true);
		finishOnSwap = eaglercraftXOpts.getFinishOnSwap(true);


		integratedServerOpts.put("worldsDB", worldsDB);
		JSEaglercraftXOptsHooks hooksObj = eaglercraftXOpts.getHooks();
		if(hooksObj != null) {
			hooks.loadHooks(hooksObj);
		}
	}

	@Override
	public String getDefaultLocale() {
		return "";
	}

	@Override
	public List<DefaultServer> getDefaultServerList() {
		return List.of();
	}

	@Override
	public String getServerToJoin() {
		return "";
	}

	@Override
	public String getWorldsDB() {
		return worldsDB;
	}

	@Override
	public boolean isCheckGLErrors() {
		return checkGLErrors;
	}

	@Override
	public boolean isCheckShaderGLErrors() {
		return false;
	}

	@Override
	public boolean isDemo() {
		return false;
	}

	@Override
	public boolean allowUpdateSvc() {
		return false;
	}

	@Override
	public boolean allowUpdateDL() {
		return false;
	}

	@Override
	public boolean isEnableDownloadOfflineButton() {
		return false;
	}

	@Override
	public String getDownloadOfflineButtonLink() {
		return "";
	}

	@Override
	public boolean useSpecialCursors() {
		return false;
	}

	@Override
	public boolean isLogInvalidCerts() {
		return false;
	}

	@Override
	public boolean isCheckRelaysForUpdates() {
		return false;
	}

	@Override
	public boolean isEnableSignatureBadge() {
		return false;
	}

	@Override
	public boolean isAllowVoiceClient() {
		return false;
	}

	@Override
	public boolean isAllowFNAWSkins() {
		return false;
	}

	@Override
	public String getLocalStorageNamespace() {
		return localStorageNamespace;
	}

	@Override
	public boolean isEnableMinceraft() {
		return false;
	}

	public boolean isForceWebGL1TeaVM() {
		return forceWebGL1;
	}

	public boolean isForceWebGL2TeaVM() {
		return forceWebGL2;
	}

	public boolean isAllowExperimentalWebGL1TeaVM() {
		return allowExperimentalWebGL1;
	}

	public boolean isUseWebGLExtTeaVM() {
		return useWebGLExt;
	}

	public boolean isUseDelayOnSwapTeaVM() {
		return useDelayOnSwap;
	}

	public boolean isUseJOrbisAudioDecoderTeaVM() {
		return useJOrbisAudioDecoder;
	}

	public boolean isUseXHRFetchTeaVM() {
		return useXHRFetch;
	}

	public boolean isDeobfStackTracesTeaVM() {
		return deobfStackTraces;
	}

	public boolean isUseVisualViewportTeaVM() {
		return useVisualViewport;
	}

	public boolean isDisableBlobURLsTeaVM() {
		return disableBlobURLs;
	}

	public boolean isEnableEPKVersionCheckTeaVM() {
		return enableEPKVersionCheck;
	}

	@Override
	public boolean isEaglerNoDelay() {
		return eaglerNoDelay;
	}

	@Override
	public boolean isRamdiskMode() {
		return ramdiskMode;
	}

	@Override
	public boolean isEnforceVSync() {
		return false;
	}

	@Override
	public IClientConfigAdapterHooks getHooks() {
		return hooks;
	}

	@Override
	public boolean isEnableServerCookies() {
		return false;
	}

	@Override
	public boolean isAllowServerRedirects() {
		return false;
	}

	@Override
	public boolean isOpenDebugConsoleOnLaunch() {
		return false;
	}


	@Override
	public boolean isForceWebViewSupport() {
		return false;
	}

	@Override
	public boolean isEnableWebViewCSP() {
		return false;
	}

	@Override
	public boolean isAllowBootMenu() {
		return false;
	}

	@Override
	public boolean isForceProfanityFilter() {
		return false;
	}

	@Override
	public JSONObject getIntegratedServerOpts() {
		return integratedServerOpts;
	}
	private final List<RelayEntry> relays = new ArrayList<>();
	@Override
	public List<RelayEntry> getRelays() {
		if (relays.isEmpty()) {
			int relayId = (new Random()).nextInt(3);
			relays.add(new RelayEntry("wss://relay.deev.is/", "lax1dude relay #1", relayId == 0));
			relays.add(new RelayEntry("wss://relay.lax1dude.net/", "lax1dude relay #2", relayId == 1));
			relays.add(new RelayEntry("wss://relay.shhnowisnottheti.me/", "ayunami relay #1", relayId == 2));
		}
		return relays;
	}

	@Override
	public String getResourcePacksDB() {
		return this.resourcePacksDB;
	}

	public boolean isKeepAliveHackTeaVM() {
		return keepAliveHack;
	}

	public boolean isFinishOnSwapTeaVM() {
		return finishOnSwap;
	}

    public boolean isFixDebugConsoleUnloadListenerTeaVM() {
    	return false;
	}
}