package net.lax1dude.eaglercraft.v1_8.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.dom.xml.Document;
import org.teavm.jso.typedarrays.Uint8ClampedArray;

import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.TeaVMBlobURLHandle;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.TeaVMBlobURLManager;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.TeaVMUtils;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;

/**
 * Copyright (c) 2022-2023 lax1dude. All Rights Reserved.
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
public class PlatformAssets {
	
	private static final byte[] MISSING_FILE = new byte[0];
	private static final String RESOURCES_PREFIX = "resources/";

	public static Map<String,byte[]> assets = new HashMap<>();
	public static Map<String,byte[]> serverAssets = new HashMap<>();

	public static void loadServerAssets(Map<String,byte[]> map) {
		assets = map;
	}

	private static String discoveredLookupPrefix = null;
	private static boolean discoveredLookupPrefixReady = false;
	private static Map<String,byte[]> discoveredLookupPrefixSource = assets;

	private static synchronized void ensureLookupPrefixState() {
		if(discoveredLookupPrefixSource != assets) {
			discoveredLookupPrefixSource = assets;
			discoveredLookupPrefix = null;
			discoveredLookupPrefixReady = false;
		}
	}

	private static String normalizePath(String path) {
		if(path == null || path.isEmpty()) {
			return "";
		}
		path = path.replace('\\', '/');
		while(path.startsWith("./")) {
			path = path.substring(2);
		}
		while(path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	private static byte[] lookupAssetRawNoDiscovery(String normalizedPath) {
		byte[] ret = assets.get(normalizedPath);
		if(ret != null) {
			return ret;
		}
		ret = assets.get("/" + normalizedPath);
		if(ret != null) {
			return ret;
		}

		if(normalizedPath.startsWith(RESOURCES_PREFIX)) {
			String withoutResources = normalizedPath.substring(RESOURCES_PREFIX.length());
			ret = assets.get(withoutResources);
			if(ret != null) {
				return ret;
			}
			ret = assets.get("/" + withoutResources);
			if(ret != null) {
				return ret;
			}
		}else {
			String withResources = RESOURCES_PREFIX + normalizedPath;
			ret = assets.get(withResources);
			if(ret != null) {
				return ret;
			}
			ret = assets.get("/" + withResources);
			if(ret != null) {
				return ret;
			}
		}

		return null;
	}

	private static synchronized String discoverLookupPrefix(String normalizedPath) {
		ensureLookupPrefixState();
		if(discoveredLookupPrefixReady) {
			return discoveredLookupPrefix;
		}

		String[] probes = new String[] {
				normalizedPath,
				"assets/minecraft/textures/gui/widgets.png",
				"assets/minecraft/textures/gui/title/background/panorama_0.png",
				"icons/icon_16x16.png",
				"pack.mcmeta"
		};

		for(String key : assets.keySet()) {
			String normalizedKey = normalizePath(key);
			for(int i = 0; i < probes.length; ++i) {
				String probe = probes[i];
				if(probe.isEmpty() || !normalizedKey.endsWith(probe) || normalizedKey.length() <= probe.length()) {
					continue;
				}
				String prefix = normalizedKey.substring(0, normalizedKey.length() - probe.length());
				if(!prefix.isEmpty() && !prefix.endsWith("/")) {
					prefix = prefix + "/";
				}
				if(!prefix.isEmpty()) {
					discoveredLookupPrefix = prefix;
					discoveredLookupPrefixReady = true;
					return discoveredLookupPrefix;
				}
			}
		}

		discoveredLookupPrefixReady = true;
		discoveredLookupPrefix = null;
		return null;
	}

	private static byte[] lookupAssetRaw(String normalizedPath) {
		ensureLookupPrefixState();
		byte[] ret = lookupAssetRawNoDiscovery(normalizedPath);
		if(ret != null) {
			return ret;
		}

		String discoveredPrefix = discoverLookupPrefix(normalizedPath);
		if(discoveredPrefix != null) {
			ret = assets.get(discoveredPrefix + normalizedPath);
			if(ret != null) {
				return ret;
			}
			if(normalizedPath.startsWith(RESOURCES_PREFIX)) {
				String withoutResources = normalizedPath.substring(RESOURCES_PREFIX.length());
				ret = assets.get(discoveredPrefix + withoutResources);
				if(ret != null) {
					return ret;
				}
			}
		}

		return null;
	}

	public static boolean getResourceExists(String path) {
		byte[] ret = lookupAssetRaw(normalizePath(path));
		return ret != null && ret != MISSING_FILE;
	}
	
	public static byte[] getResourceBytes(String path) {
		byte[] data = lookupAssetRaw(normalizePath(path));
		return data == MISSING_FILE ? null : data;
	}

	public static synchronized List<String> listResources(String pathPrefix) {
		String wantDir = normalizePath(pathPrefix) + "/";
		List<String> ret = new ArrayList<>();
		for(String key : assets.keySet()) {
			String norm = normalizePath(key);
			int idx = norm.indexOf(wantDir);
			if(idx >= 0 && (idx == 0 || norm.charAt(idx - 1) == '/') && assets.get(key) != MISSING_FILE) {
				ret.add(norm.substring(idx));
			}
		}
		return ret;
	}
	
	public static ImageData loadImageFile(InputStream data) {
		return loadImageFile(data, "image/png");
	}
	
	public static ImageData loadImageFile(InputStream data, String mime) {
		byte[] b = EaglerInputStream.inputStreamToBytesQuiet(data);
		if(b != null) {
			return loadImageFile(b, mime);
		}else {
			return null;
		}
	}
	
	private static HTMLCanvasElement imageLoadCanvas = null;
	private static CanvasRenderingContext2D imageLoadContext = null;
	
	public static ImageData loadImageFile(byte[] data) {
		return loadImageFile(data, "image/png");
	}
	
	@JSBody(params = { }, script = "return { willReadFrequently: true };")
	static native JSObject youEagler();
	
	@JSBody(params = { "ctx" }, script = "ctx.imageSmoothingEnabled = false;")
	private static native void disableImageSmoothing(CanvasRenderingContext2D ctx);
	
	@Async
	public static native ImageData loadImageFile(byte[] data, String mime);
	
	private static void loadImageFile(byte[] data, String mime, final AsyncCallback<ImageData> ret) {
		final Document doc = Window.current().getDocument();
		final HTMLImageElement toLoad = (HTMLImageElement) doc.createElement("img");
		final TeaVMBlobURLHandle[] src = new TeaVMBlobURLHandle[1];
		toLoad.addEventListener("load", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				if(imageLoadCanvas == null) {
					imageLoadCanvas = (HTMLCanvasElement) doc.createElement("canvas");
				}
				if(imageLoadCanvas.getWidth() < toLoad.getWidth()) {
					imageLoadCanvas.setWidth(toLoad.getWidth());
				}
				if(imageLoadCanvas.getHeight() < toLoad.getHeight()) {
					imageLoadCanvas.setHeight(toLoad.getHeight());
				}
				if(imageLoadContext == null) {
					imageLoadContext = (CanvasRenderingContext2D) imageLoadCanvas.getContext("2d", youEagler());
					disableImageSmoothing(imageLoadContext);
				}
				imageLoadContext.clearRect(0, 0, toLoad.getWidth(), toLoad.getHeight());
				imageLoadContext.drawImage(toLoad, 0, 0, toLoad.getWidth(), toLoad.getHeight());
				org.teavm.jso.canvas.ImageData pxlsDat = imageLoadContext.getImageData(0, 0, toLoad.getWidth(), toLoad.getHeight());
				Uint8ClampedArray pxls = pxlsDat.getData();
				int totalPixels = pxlsDat.getWidth() * pxlsDat.getHeight();
				TeaVMBlobURLManager.releaseURL(src[0]);
				if(pxls.getByteLength() < totalPixels << 2) {
					ret.complete(null);
					return;
				}
				ret.complete(new ImageData(pxlsDat.getWidth(), pxlsDat.getHeight(), TeaVMUtils.wrapIntArrayBuffer(pxls.getBuffer()), true));
			}
		});
		toLoad.addEventListener("error", new EventListener<Event>() {
			@Override
			public void handleEvent(Event evt) {
				TeaVMBlobURLManager.releaseURL(src[0]);
				ret.complete(null);
			}
		});
		src[0] = TeaVMBlobURLManager.registerNewURLByte(data, mime);
		if(src[0] != null) {
			toLoad.setSrc(src[0].toExternalForm());
		}else {
			ret.complete(null);
		}
	}

	public static void freeAssetRepoTeaVM() {
		assets = new HashMap<>();
		discoveredLookupPrefix = null;
		discoveredLookupPrefixReady = false;
		discoveredLookupPrefixSource = assets;
	}
}
