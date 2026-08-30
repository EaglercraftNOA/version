/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.profile;

import java.io.Closeable;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.sp.server.export.EPKDecompiler;
import net.lax1dude.eaglercraft.sp.server.export.EPKDecompiler.FileEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.resources.EaglerFolderResourcePack;

public class ProfileImporter implements Closeable {

	private static final Logger logger = LogManager.getLogger("ProfileImporter");

	private byte[] data;
	private EPKDecompiler epkDecompiler;
	private boolean headerHasProfile;
	private boolean headerHasSettings;
	private boolean headerHasServers;
	private boolean headerHasResourcePacks;

	public ProfileImporter(byte[] data) {
		this.data = data;
	}

	public void readHeader() throws IOException {
		logger.info("Reading EPK file header...");
		epkDecompiler = new EPKDecompiler(data);

		FileEntry etr = epkDecompiler.readFile();
		if (etr == null || !etr.type.equals("HEAD") || !etr.name.equals("file-type")
				|| !EPKDecompiler.readASCII(etr.data).equals("epk/profile188")) {
			throw new IOException("EPK file is not a profile backup!");
		}

		etr = epkDecompiler.readFile();
		if (etr == null || !etr.type.equals("HEAD") || !etr.name.equals("file-exports") || etr.data.length != 1) {
			throw new IOException("EPK file is not a profile backup!");
		}

		headerHasProfile = (etr.data[0] & 1) != 0;
		headerHasSettings = (etr.data[0] & 2) != 0;
		headerHasServers = (etr.data[0] & 4) != 0;
		headerHasResourcePacks = (etr.data[0] & 8) != 0;
	}

	public boolean hasProfile() {
		return headerHasProfile;
	}

	public boolean hasSettings() {
		return headerHasSettings;
	}

	public boolean hasServers() {
		return headerHasServers;
	}

	public boolean hasResourcePacks() {
		return headerHasResourcePacks;
	}

	/**
	 * Note: this function is sensitive to the order file appear in the EPK
	 */
	public void importProfileAndSettings(boolean doImportProfile, boolean doImportSettings, boolean doImportServers,
			boolean doImportResourcePacks) throws IOException {
		doImportProfile &= headerHasProfile;
		doImportSettings &= headerHasSettings;
		doImportServers &= headerHasServers;
		doImportResourcePacks &= headerHasResourcePacks && EaglerFolderResourcePack.isSupported();
		FileEntry etr;
		vigg: while ((etr = epkDecompiler.readFile()) != null) {
			if (etr.type.equals("FILE")) {
				switch (etr.name) {
				case "_eaglercraftX.p":
					if (doImportProfile) {
						logger.info("Importing profile...");
						EaglerProfile.read(etr.data);
						EagRuntime.setStorage("p", etr.data);
					}
					break;
				case "_eaglercraftX.g":
					if (doImportSettings) {
						logger.info("Importing settings...");
						Minecraft.getMinecraft().gameSettings.loadOptions();
						EagRuntime.setStorage("g", etr.data);
					}
					break;
				case "_eaglercraftX.r":
					if (doImportSettings) {
						logger.info("Importing relays...");
						RelayManager.relayManager.load(etr.data);
						EagRuntime.setStorage("r", etr.data);
					}
					break;

				}
			}
		}
		logger.info("Import complete!");
	}

	@Override
	public void close() throws IOException {
		epkDecompiler.close();
	}
}