/*
 * Copyright (c) 2024-2025 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.minecraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.lax1dude.eaglercraft.v1_8.ArrayUtils;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.crypto.SHA1Digest;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.resources.AbstractResourcePack;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.resources.data.IMetadataSectionSerializer;

public class EaglerFolderResourcePack implements IResourcePack {

	public static final Logger logger = LogManager.getLogger("EaglerFolderResourcePack");

	public static final String SERVER_RESOURCE_PACKS = "srp";
	public static final String RESOURCE_PACKS = "resourcepacks";

	private final String prefix;
	private final String displayName;
	private final String resourcePackFile;
	private final Set<String> domains;
	private final long timestamp;
	private final VFile2 folderBaseDir;
	public final ResourceIndex resourceIndex;

	private static boolean isSupported = false;

	public static void setSupported(boolean supported) {
		isSupported = supported;
	}

	public static boolean isSupported() {
		return isSupported;
	}

	public EaglerFolderResourcePack(String resourcePackFileIn, String displayName, String prefix, Set<String> domains, long timestamp) {
		this(null, resourcePackFileIn, displayName, prefix, domains, timestamp);
	}

	public EaglerFolderResourcePack(VFile2 folderBaseDir, String displayName, Set<String> domains, long timestamp) {
		this(folderBaseDir, null, displayName, null, domains, timestamp);
	}

	private EaglerFolderResourcePack(VFile2 folderBaseDir, String resourcePackFileIn, String displayName, String prefix, Set<String> domains, long timestamp) {
		this.folderBaseDir = folderBaseDir;
		this.resourcePackFile = resourcePackFileIn;
		this.displayName = displayName;
		this.prefix = prefix;
		this.domains = domains;
		this.timestamp = timestamp;
		this.resourceIndex = new ResourceIndex() {

			@Override
			protected Collection<String> getPropertiesFiles0() {
				VFile2 file = getFolderFile("assets/minecraft/optifine/_property_files_index.json");
				String str = file.getAllChars();
				Collection<String> propertyFiles = null;
				if(str != null) {
					propertyFiles = loadPropertyFileList(str);
					if(propertyFiles != null) {
						return propertyFiles;
					}
				}
				VFile2 mcDir = getFolderFile("assets/minecraft");
				int pfxLen = mcDir.getPath().length() + 1;
				List<String> philes = mcDir.listFilenames(true);
				propertyFiles = new ArrayList<>();
				JSONArray arr = new JSONArray();
				for(int i = 0, l = philes.size(); i < l; ++i) {
					String name = philes.get(i);
					if(name.length() > pfxLen && name.endsWith(".properties")) {
						name = name.substring(pfxLen);
						propertyFiles.add(name);
						arr.put(name);
					}
				}
				JSONObject json = new JSONObject();
				json.put("propertyFiles", arr);
				file.setAllChars(json.toString());
				return propertyFiles;
			}

			@Override
			protected Collection<String> getCITPotionsFiles0() {
				VFile2 file = getFolderFile("assets/minecraft/mcpatcher/cit/potion/_potions_files_index.json");
				String str = file.getAllChars();
				Collection<String> propertyFiles = null;
				if(str != null) {
					propertyFiles = loadPotionsFileList(str);
					if(propertyFiles != null) {
						return propertyFiles;
					}
				}
				VFile2 mcDir = getFolderFile("assets/minecraft/mcpatcher/cit/potion");
				int pfxLen = mcDir.getPath().length() - 20;
				List<String> philes = mcDir.listFilenames(true);
				propertyFiles = new ArrayList<>();
				JSONArray arr = new JSONArray();
				for(int i = 0, l = philes.size(); i < l; ++i) {
					String name = philes.get(i);
					if(name.length() > pfxLen && name.endsWith(".png")) {
						name = name.substring(pfxLen);
						propertyFiles.add(name);
						arr.put(name);
					}
				}
				mcDir = getFolderFile("assets/minecraft/optifine/cit/potion");
				pfxLen = mcDir.getPath().length() - 19;
				philes = mcDir.listFilenames(true);
				for(int i = 0, l = philes.size(); i < l; ++i) {
					String name = philes.get(i);
					if(name.length() > pfxLen && name.endsWith(".png")) {
						name = name.substring(pfxLen);
						propertyFiles.add(name);
						arr.put(name);
					}
				}
				JSONObject json = new JSONObject();
				json.put("potionsFiles", arr);
				file.setAllChars(json.toString());
				return propertyFiles;
			}

		};
	}

	private VFile2 getFolderFile(Object... path) {
		if(folderBaseDir != null) {
			Object[] args = new Object[path.length + 1];
			args[0] = folderBaseDir;
			System.arraycopy(path, 0, args, 1, path.length);
			return net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB.newVFile(args);
		}else {
			Object[] args = new Object[path.length + 2];
			args[0] = prefix;
			args[1] = resourcePackFile;
			System.arraycopy(path, 0, args, 2, path.length);
			return new VFile2(args);
		}
	}

	@Override
	public InputStream getRootResourceStream(String fileName) throws IOException {
		if(!fileName.contains("/") && !fileName.contains("\\")) {
			return getResource(fileName);
		}else {
			throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
		}
	}

	@Override
	public InputStream getResourceStream(ResourcePackType type, ResourceLocation location) throws IOException {
		return getResource(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath());
	}

	@Override
	public boolean resourceExists(ResourcePackType type, ResourceLocation location) {
		return hasResource(type.getDirectoryName() + "/" + location.getNamespace() + "/" + location.getPath());
	}

	@Override
	public Set<String> getResourceNamespaces(ResourcePackType type) {
		return domains;
	}

	private InputStream getResource(String path) throws IOException {
		VFile2 file = getFolderFile(path);
		if(!file.exists()) {
			throw new FileNotFoundException(path);
		}
		return file.getInputStream();
	}

	private boolean hasResource(String path) {
		return getFolderFile(path).exists();
	}

	@Override
	public Collection<ResourceLocation> getAllResourceLocations(ResourcePackType type, String path, int maxDepth, Predicate<String> filter) {
		List<ResourceLocation> ret = new ArrayList<>();
		for(String namespace : domains) {
			getResources(type, namespace, path, maxDepth, filter, ret);
		}
		return ret;
	}

	private void getResources(ResourcePackType type, String namespace, String path, int maxDepth, Predicate<String> filter, List<ResourceLocation> ret) {
		VFile2 baseDir = getFolderFile(type.getDirectoryName(), namespace, path);
		String basePath = baseDir.getPath();
		if(basePath == null) {
			return;
		}
		int prefixLen = basePath.length() + 1;
		List<String> filenames = baseDir.listFilenames(true);
		for(int i = 0, l = filenames.size(); i < l; ++i) {
			String name = filenames.get(i);
			if(name.length() <= prefixLen) {
				continue;
			}
			String rel = name.substring(prefixLen);
			if(rel.endsWith(".mcmeta")) {
				continue;
			}
			int slashCount = 0;
			for(int j = 0; j < rel.length(); ++j) {
				if(rel.charAt(j) == '/') {
					++slashCount;
				}
			}
			if(slashCount > maxDepth) {
				continue;
			}
			String leaf = VFile2.getNameFromPath(rel);
			if(!filter.test(leaf)) {
				continue;
			}
			try {
				ret.add(new ResourceLocation(namespace, path.isEmpty() ? rel : path + "/" + rel));
			}catch(ResourceLocationException ex) {
				logger.error(ex.getMessage());
			}
		}
	}

	@Override
	@Nullable
	public <T> T getMetadata(IMetadataSectionSerializer<T> deserializer) throws IOException {
		try(InputStream inputstream = getResource("pack.mcmeta")) {
			return AbstractResourcePack.getResourceMetadata(deserializer, inputstream);
		}catch(FileNotFoundException ex) {
			return null;
		}
	}

	@Override
	public void close() {
	}

	@Override
	public String getName() {
		return displayName;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getFolderName() {
		return resourcePackFile;
	}

	public static List<EaglerFolderResourcePack> getFolderResourcePacks(String prefix) {
		if(!isSupported) {
			return Collections.emptyList();
		}
		String str = (new VFile2(prefix, "manifest.json")).getAllChars();
		if(str == null) {
			return Collections.emptyList();
		}
		try {
			JSONArray json = (new JSONObject(str)).getJSONArray("resourcePacks");
			List<EaglerFolderResourcePack> ret = new ArrayList<>(json.length());
			for(int i = 0, l = json.length(); i < l; ++i) {
				JSONObject jp = json.getJSONObject(i);
				String folderName = jp.getString("folder");
				String displayName = jp.optString("name", folderName);
				long timestamp = jp.getLong("timestamp");
				Set<String> domains = Sets.newHashSet();
				JSONArray jsonDomains = jp.getJSONArray("domains");
				for(int j = 0, k = jsonDomains.length(); j < k; ++j) {
					domains.add(jsonDomains.getString(j));
				}
				ret.add(new EaglerFolderResourcePack(folderName, displayName, prefix, domains, timestamp));
			}
			return ret;
		}catch(JSONException ex) {
			logger.error("Failed to load resource pack manifest!");
			logger.error(ex);
			return Collections.emptyList();
		}
	}

	public static EaglerFolderResourcePack importResourcePack(String name, String prefix, byte[] file) throws IOException {
		if(!isSupported) {
			return null;
		}
		logger.info("Importing resource pack: {}", name);
		int idx = name.lastIndexOf('.');
		if(idx != -1) {
			name = name.substring(0, idx);
		}
		String folderName = name.replaceAll("[^A-Za-z0-9\\-_ \\(\\)]", "_");
		
		final List<EaglerFolderResourcePack> existingLst = getFolderResourcePacks(RESOURCE_PACKS);
		
		vigg: for(;;) {
			for(int i = 0, l = existingLst.size(); i < l; ++i) {
				EaglerFolderResourcePack rp = existingLst.get(i);
				if(rp.resourcePackFile.equalsIgnoreCase(folderName)) {
					folderName = folderName + "-";
					continue vigg;
				}
			}
			break;
		}
		
		List<String> fileNames = Lists.newArrayList();
		
		logger.info("Counting files...");
		ZipEntry zipEntry;
		try(ZipInputStream ziss = new ZipInputStream(new EaglerInputStream(file))) {
			while ((zipEntry = ziss.getNextEntry()) != null) {
				if (!zipEntry.isDirectory()) {
					fileNames.add(zipEntry.getName());
				}
			}
		}
		
		int prefixLen = Integer.MAX_VALUE;
		for(int i = 0, l = fileNames.size(); i < l; ++i) {
			String fn = fileNames.get(i);
			if(fn.equals("pack.mcmeta") || fn.endsWith("/pack.mcmeta")) {
				int currPrefixLen = fn.length() - 11;
				if (prefixLen > currPrefixLen) {
					prefixLen = currPrefixLen;
				}
			}
		}
		if (prefixLen == Integer.MAX_VALUE) {
			prefixLen = 0;
		}
		
		Set<String> domainsList = Sets.newHashSet();
		JSONArray propertyFiles = new JSONArray();
		JSONArray potionsFiles = new JSONArray();
		String fn;
		for(int i = 0, l = fileNames.size(); i < l; ++i) {
			fn = fileNames.get(i);
			if(fn.length() > prefixLen + 7) {
				fn = fn.substring(prefixLen + 7);
				int j = fn.indexOf('/');
				if(j != -1) {
					String dm = fn.substring(0, j);
					domainsList.add(dm);
					if("minecraft".equals(dm)) {
						if(fn.endsWith(".properties")) {
							propertyFiles.put(fn.substring(10));
						}else if((fn.startsWith("minecraft/mcpatcher/cit/potion/")
								|| fn.startsWith("minecraft/mcpatcher/cit/Potion/")) && fn.endsWith(".png")) {
							potionsFiles.put(fn.substring(10));
						}else if(fn.startsWith("minecraft/optifine/cit/potion/") && fn.endsWith(".png")) {
							potionsFiles.put(fn.substring(10));
						}
					}
				}
			}
		}
		
		VFile2 dstDir = new VFile2(prefix, folderName);
		logger.info("Extracting to: {}", dstDir.getPath());
		
		try {
			int totalSize = 0;
			int totalFiles = 0;
			int lastProg = 0;
			try(ZipInputStream ziss = new ZipInputStream(new EaglerInputStream(file))) {
				int sz;
				while ((zipEntry = ziss.getNextEntry()) != null) {
					if (!zipEntry.isDirectory()) {
						fn = zipEntry.getName();
						if(fn.length() > prefixLen) {
							byte[] buffer;
							sz = (int)zipEntry.getSize();
							if(sz >= 0) {
								buffer = new byte[sz];
								int i = 0, j;
								while(i < buffer.length && (j = ziss.read(buffer, i, buffer.length - i)) != -1) {
									i += j;
								}
							}else {
								buffer = EaglerInputStream.inputStreamToBytesNoClose(ziss);
							}
							(new VFile2(prefix, folderName, fn.substring(prefixLen))).setAllBytes(buffer);
							totalSize += buffer.length;
							++totalFiles;
							if(totalSize - lastProg > 25000) {
								lastProg = totalSize;
								logger.info("Extracted {} files, {} bytes from ZIP file...", totalFiles, totalSize);
							}
						}
					}
				}
			}
		}catch(IOException ex) {
			logger.error("Encountered an error extracting zip file, deleting extracted files...");
			for(int i = 0, l = fileNames.size(); i < l; ++i) {
				fn = fileNames.get(i);
				if(fn.length() > prefixLen) {
					(new VFile2(dstDir, fn.substring(prefixLen))).delete();
				}
			}
			throw ex;
		}

		logger.info("Updating manifest...");
		
		JSONObject json = new JSONObject();
		json.put("propertyFiles", propertyFiles);
		(new VFile2(prefix, folderName, "assets/minecraft/optifine/_property_files_index.json"))
				.setAllChars(json.toString());
		
		json = new JSONObject();
		json.put("potionsFiles", propertyFiles);
		(new VFile2(prefix, folderName, "assets/minecraft/mcpatcher/cit/potion/_potions_files_index.json"))
				.setAllChars(json.toString());
		
		VFile2 manifestFile = new VFile2(prefix, "manifest.json");
		String str = manifestFile.getAllChars();
		JSONArray arr = null;
		if(str != null) {
			try {
				arr = (new JSONObject(str)).getJSONArray("resourcePacks");
			}catch(JSONException ex) {
			}
		}
		
		if(arr == null) {
			arr = new JSONArray();
		}
		
		JSONObject manifestEntry = new JSONObject();
		manifestEntry.put("folder", folderName);
		manifestEntry.put("name", name);
		long timestamp = System.currentTimeMillis();
		manifestEntry.put("timestamp", timestamp);
		JSONArray domainsListJson = new JSONArray();
		for(String str2 : domainsList) {
			domainsListJson.put(str2);
		}
		manifestEntry.put("domains", domainsListJson);
		arr.put(manifestEntry);
		
		manifestFile.setAllChars((new JSONObject()).put("resourcePacks", arr).toString());
		
		logger.info("Done!");
		return new EaglerFolderResourcePack(folderName, name, prefix, domainsList, timestamp);
	}

	public static void loadRemoteResourcePack(String url, String hash, Consumer<EaglerFolderResourcePack> cb, Consumer<Runnable> ast, Runnable loading) {
		if (!isSupported || !hash.matches("^[a-f0-9]{40}$")) {
			logger.error("Invalid character in resource pack hash! (is it lowercase?)");
			cb.accept(null);
			return;
		}
		final List<EaglerFolderResourcePack> lst = getFolderResourcePacks(SERVER_RESOURCE_PACKS);
		for(int i = 0, l = lst.size(); i < l; ++i) {
			EaglerFolderResourcePack rp = lst.get(i);
			if(rp.resourcePackFile.equals(hash)) {
				cb.accept(rp);
				return;
			}
		}
		PlatformRuntime.downloadRemoteURIByteArray(url, true, arr -> {
			ast.accept(() -> {
				if (arr == null) {
					cb.accept(null);
					return;
				}
				SHA1Digest digest = new SHA1Digest();
				digest.update(arr, 0, arr.length);
				byte[] hashOut = new byte[20];
				digest.doFinal(hashOut, 0);
				String hashOutStr = ArrayUtils.hexString(hashOut);
				if(!hash.equals(hashOutStr)) {
					logger.error("Downloaded resource pack hash does not equal expected resource pack hash! ({} != {})", hashOutStr, hash);
					cb.accept(null);
					return;
				}
				if(lst.size() >= 5) {
					lst.sort(Comparator.comparingLong(pack -> pack.timestamp));
					for(int i = 0; i < lst.size() - 5; i++) {
						deleteResourcePack(SERVER_RESOURCE_PACKS, lst.get(i).resourcePackFile);
					}
				}
				loading.run();
				try {
					cb.accept(importResourcePack(hash, SERVER_RESOURCE_PACKS, arr));
				}catch(IOException ex) {
					logger.error("Failed to load resource pack downloaded from server!");
					logger.error(ex);
					cb.accept(null);
				}
			});
		});
	}

	public static void deleteResourcePack(EaglerFolderResourcePack pack) {
		deleteResourcePack(pack.prefix, pack.resourcePackFile);
	}

	public static void deleteResourcePack(String prefix, String name) {
		if (!isSupported) {
			return;
		}
		logger.info("Deleting resource pack: {}/{}", prefix, name);
		(new VFile2(prefix, name)).listFiles(true).forEach(VFile2::delete);
		VFile2 manifestFile = new VFile2(prefix, "manifest.json");
		String str = manifestFile.getAllChars();
		if(str != null) {
			try {
				JSONArray json = (new JSONObject(str)).getJSONArray("resourcePacks");
				boolean changed = false;
				for(int i = 0, l = json.length(); i < l; ++i) {
					if(json.getJSONObject(i).getString("folder").equals(name)) {
						json.remove(i);
						changed = true;
						break;
					}
				}
				if(changed) {
					manifestFile.setAllChars((new JSONObject()).put("resourcePacks", json).toString());
				}else {
					logger.warn("Failed to remove pack \"{}\" from manifest, it wasn't found in the list for some reason", name);
				}
			}catch(JSONException ex) {
			}
		}
	}

	public static void deleteOldResourcePacks(String prefix, long maxAge) {
		if (!isSupported) {
			return;
		}
		long millis = System.currentTimeMillis();
		List<EaglerFolderResourcePack> lst = getFolderResourcePacks(prefix);
		for(int i = 0, l = lst.size(); i < l; ++i) {
			EaglerFolderResourcePack rp = lst.get(i);
			if(millis - rp.timestamp > maxAge) {
				deleteResourcePack(rp);
			}
		}
	}

	public static Collection<String> loadPropertyFileList(String str) {
		try {
			JSONObject json = new JSONObject(str);
			JSONArray arr = json.getJSONArray("propertyFiles");
			int l = arr.length();
			Collection<String> ret = new ArrayList<>(l);
			for(int i = 0; i < l; ++i) {
				ret.add(arr.getString(i));
			}
			return ret;
		}catch(JSONException ex) {
			EagRuntime.debugPrintStackTrace(ex);
			return null;
		}
	}

	public static Collection<String> loadPotionsFileList(String str) {
		try {
			JSONObject json = new JSONObject(str);
			JSONArray arr = json.getJSONArray("potionsFiles");
			int l = arr.length();
			Collection<String> ret = new ArrayList<>(l);
			for(int i = 0; i < l; ++i) {
				ret.add(arr.getString(i));
			}
			return ret;
		}catch(JSONException ex) {
			EagRuntime.debugPrintStackTrace(ex);
			return null;
		}
	}

}
