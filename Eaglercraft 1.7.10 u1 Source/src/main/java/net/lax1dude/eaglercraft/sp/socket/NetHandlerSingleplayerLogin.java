/*
 * Copyright (c) 2023-2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.sp.socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import io.netty.buffer.Unpooled;
import net.eymenwsmc.Sys;
import net.lax1dude.eaglercraft.socket.EaglercraftNetworkManager;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageProtocol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NetHandlerSingleplayerLogin implements INetHandlerLoginClient {

	private final Minecraft mc;
	private final GuiScreen previousGuiScreen;
	private final EaglercraftNetworkManager networkManager;

	private static final Logger logger = LogManager.getLogger("NetHandlerSingleplayerLogin");

	public NetHandlerSingleplayerLogin(EaglercraftNetworkManager parNetworkManager, Minecraft mcIn,
			GuiScreen parGuiScreen) {
		System.out.println("[CLIENT] NetHandlerSingleplayerLogin oluşturuldu!");
		this.networkManager = parNetworkManager;
		this.mc = mcIn;
		this.previousGuiScreen = parGuiScreen;
	}

	@Override
	public void onDisconnect(IChatComponent var1) {
		this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", var1));
	}

	@Override
	public void onConnectionStateTransition(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_) {

	}

	@Override
	public void onNetworkTick() {

	}

	@Override
	public void handleEncryptionRequest(S01PacketEncryptionRequest var1) {

	}

	@Override
	public void handleLoginSuccess(S02PacketLoginSuccess var1) {
		System.out.println("[CLIENT] handleLoginSuccess çağrıldı!");
		this.networkManager.setConnectionState(EnumConnectionState.PLAY);
		int p = var1.getSelectedProtocol();
		GamePluginMessageProtocol mp = GamePluginMessageProtocol.getByVersion(p);
		if (mp == null) {
			this.networkManager.closeChannel(new ChatComponentText("Unknown protocol selected: " + p));
			return;
		}
		logger.info("Server is using protocol: {}", p);
		// Zincir: PLAY state'e geçince yeni handler ata
		NetHandlerPlayClient netHandler = new NetHandlerPlayClient(this.mc, this.previousGuiScreen,
				this.networkManager);
		this.networkManager.setNetHandler(netHandler);
		try {
			net.minecraft.network.play.client.C16PacketClientStatus readyPacket = new net.minecraft.network.play.client.C16PacketClientStatus(
					net.minecraft.network.play.client.C16PacketClientStatus.EnumState.PERFORM_RESPAWN);
			this.networkManager.sendPacket(readyPacket);
		} catch (Throwable t) {
			logger.error("Failed to send join game trigger packet!", t);
		}
	}

	@Override
	public void handleDisconnect(S00PacketDisconnect var1) {
		networkManager.closeChannel(var1.func_149603_c());
	}

}