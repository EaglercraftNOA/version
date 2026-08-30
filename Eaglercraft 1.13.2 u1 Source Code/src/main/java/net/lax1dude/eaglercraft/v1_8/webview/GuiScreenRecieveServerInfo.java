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

package net.lax1dude.eaglercraft.v1_8.webview;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerZLIB;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.IOUtils;
import net.lax1dude.eaglercraft.v1_8.crypto.SHA1Digest;
import net.lax1dude.eaglercraft.v1_8.internal.WebViewOptions;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenGenericErrorMessage;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.pkt.client.CPacketRequestServerInfoV4EAG;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;

public class GuiScreenRecieveServerInfo extends GuiScreen {

	private static final Logger logger = LogManager.getLogger("GuiScreenRecieveServerInfo");

	protected final GuiScreen parent;
	protected final byte[] expectHash;
	protected final IDisplayWebviewProc proc;
	protected int timer;
	protected int timer2;
	protected String statusString = "recieveServerInfo.checkingCache";

	public static interface IDisplayWebviewProc {
		GuiScreen display(GuiScreen parent, byte[] blob, EaglercraftUUID permissionsOriginUUID);
	}

	public GuiScreenRecieveServerInfo(GuiScreen parent, byte[] expectHash) {
		this(parent, expectHash, GuiScreenServerInfo::createForCurrentState);
	}

	public GuiScreenRecieveServerInfo(GuiScreen parent, byte[] expectHash, IDisplayWebviewProc proc) {
		this.parent = parent;
		this.expectHash = expectHash;
		this.proc = proc;
	}

	@Override
	protected void initGui() {
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 106, 200, 20,
				I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenRecieveServerInfo.this.mc.displayGuiScreen(GuiScreenRecieveServerInfo.this.parent);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		drawCenteredString(this.fontRenderer, I18n.format("recieveServerInfo.title"), this.width / 2, 70, 11184810);
		drawCenteredString(this.fontRenderer, I18n.format(statusString), this.width / 2, 90, 16777215);
		if (Arrays.equals(ServerInfoCache.chunkRecieveHash, expectHash) && ServerInfoCache.chunkFinalSize > 0) {
			int progress = ServerInfoCache.chunkCurrentSize * 100 / ServerInfoCache.chunkFinalSize;
			if (progress < 0) {
				progress = 0;
			}
			if (progress > 100) {
				progress = 100;
			}
			if (ServerInfoCache.hasLastChunk) {
				progress = 100;
			}
			int barWidth = 100;
			int barHeight = 2;
			int x = width / 2 - barWidth / 2;
			int y = 103;
			drawRect(x, y, x + barWidth, y + barHeight, 0xFF808080);
			drawRect(x, y, x + progress, y + barHeight, 0xFF80FF80);
		}
		super.render(mouseX, mouseY, partialTick);
	}

	@Override
	public void tick() {
		if (this.mc.player == null) {
			this.mc.displayGuiScreen(parent);
			return;
		}
		NetHandlerPlayClient sendQueue = this.mc.player.connection;
		++timer;
		if (timer == 1) {
			byte[] data = ServerInfoCache.loadFromCache(expectHash);
			if (data != null) {
				this.mc.displayGuiScreen(proc.display(parent, data, WebViewOptions.getEmbedOriginUUID(expectHash)));
			} else {
				byte[] cachedData = sendQueue.cachedServerInfoData;
				if (cachedData != null && Arrays.equals(expectHash, sendQueue.cachedServerInfoHash)) {
					if (cachedData.length == 0) {
						this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("serverInfoFailure.title",
								"serverInfoFailure.desc", parent));
					} else {
						ServerInfoCache.storeInCache(expectHash, cachedData);
						this.mc.displayGuiScreen(proc.display(parent, cachedData, WebViewOptions.getEmbedOriginUUID(expectHash)));
					}
				} else {
					statusString = "recieveServerInfo.contactingServer";
					if (!ServerInfoCache.hasLastChunk || !Arrays.equals(ServerInfoCache.chunkRecieveHash, expectHash)) {
						ServerInfoCache.clearDownload();
						sendQueue.sendEaglerMessage(new CPacketRequestServerInfoV4EAG(expectHash));
					}
				}
			}
		} else if (timer > 1) {
			if (Arrays.equals(ServerInfoCache.chunkRecieveHash, expectHash)) {
				if (ServerInfoCache.hasLastChunk) {
					statusString = "recieveServerInfo.decompressing";
					++timer2;
					if (timer2 == 2) {
						processFinishedDownload(sendQueue);
					}
				} else {
					statusString = "recieveServerInfo.recievingData";
				}
			} else {
				statusString = "recieveServerInfo.contactingServer";
			}
		}
	}

	private void processFinishedDownload(NetHandlerPlayClient sendQueue) {
		byte[] finalData = new byte[ServerInfoCache.chunkCurrentSize];
		int i = 0;
		for (byte[] b : ServerInfoCache.chunkRecieveBuffer) {
			System.arraycopy(b, 0, finalData, i, b.length);
			i += b.length;
		}
		if (i != ServerInfoCache.chunkCurrentSize) {
			logger.error("An unknown error occured!");
			cacheFailure(sendQueue);
			return;
		}
		ServerInfoCache.clearDownload();
		try {
			EaglerInputStream bis = new EaglerInputStream(finalData);
			int finalSize = (new DataInputStream(bis)).readInt();
			if (finalSize < 0) {
				logger.error("The response data was corrupt, decompressed size is negative!");
				cacheFailure(sendQueue);
				return;
			}
			if (finalSize > ServerInfoCache.CACHE_MAX_SIZE * 2) {
				logger.error("Failed to decompress/verify server info response! Size is massive, {} bytes reported!",
						finalSize);
				logger.error("Aborting decompression. Rejoin the server to try again.");
				cacheFailure(sendQueue);
				return;
			}
			byte[] decompressed = new byte[finalSize];
			try (InputStream is = EaglerZLIB.newGZIPInputStream(bis)) {
				IOUtils.readFully(is, decompressed);
			}
			SHA1Digest digest = new SHA1Digest();
			digest.update(decompressed, 0, decompressed.length);
			byte[] csum = new byte[20];
			digest.doFinal(csum, 0);
			if (Arrays.equals(csum, expectHash)) {
				ServerInfoCache.storeInCache(csum, decompressed);
				sendQueue.cachedServerInfoHash = expectHash;
				sendQueue.cachedServerInfoData = decompressed;
				this.mc.displayGuiScreen(proc.display(parent, decompressed, WebViewOptions.getEmbedOriginUUID(expectHash)));
			} else {
				logger.error("The data recieved from the server did not have the correct SHA1 checksum! Rejoin the server to try again.");
				cacheFailure(sendQueue);
			}
		} catch (IOException ex) {
			logger.error("Failed to decompress/verify server info response! Rejoin the server to try again.");
			logger.error(ex);
			cacheFailure(sendQueue);
		}
	}

	private void cacheFailure(NetHandlerPlayClient sendQueue) {
		sendQueue.cachedServerInfoHash = expectHash;
		sendQueue.cachedServerInfoData = new byte[0];
		this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("serverInfoFailure.title", "serverInfoFailure.desc", parent));
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}
