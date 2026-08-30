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

package net.lax1dude.eaglercraft.v1_8.sp.socket;

import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.socket.EaglercraftNetworkManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.update.UpdateService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.ResourceLocation;

public class NetHandlerSingleplayerLogin implements INetHandlerLoginClient {

	private final Minecraft mc;
	private final GuiScreen previousGuiScreen;
	private final EaglercraftNetworkManager networkManager;

	private static final Logger logger = LogManager.getLogger("NetHandlerSingleplayerLogin");

	public NetHandlerSingleplayerLogin(EaglercraftNetworkManager parNetworkManager, Minecraft mcIn, GuiScreen parGuiScreen) {
		this.networkManager = parNetworkManager;
		this.mc = mcIn;
		this.previousGuiScreen = parGuiScreen;
	}

	@Override
	public void onDisconnect(ITextComponent var1) {
		this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", var1));
	}

	@Override
	public void handleEncryptionRequest(SPacketEncryptionRequest var1) {
		
	}

	@Override
	public void handleLoginSuccess(SPacketLoginSuccess var1) {
		this.networkManager.setConnectionState(EnumConnectionState.PLAY);
		int p = var1.getSelectedProtocol();
		GamePluginMessageProtocol mp = GamePluginMessageProtocol.getByVersion(p);
		if(mp == null) {
			this.networkManager.closeChannel(new TextComponentString("Unknown protocol selected: " + p));
			return;
		}
		logger.info("Server is using protocol: {}", p);
		this.networkManager.setLANInfo(p);
		NetHandlerPlayClient netHandler = new NetHandlerPlayClient(this.mc, this.previousGuiScreen, this.networkManager,
				var1.getProfile(), mp);
		this.networkManager.setNetHandler(netHandler);
		byte[] b = UpdateService.getClientSignatureData();
		if(b != null) {
			this.networkManager.sendPacket(new CPacketCustomPayload(encodeEaglerLegacyChannel("EAG|MyUpdCert-1.8"),
					new PacketBuffer(Unpooled.wrappedBuffer(b))));
		}
	}

	@Override
	public void handleDisconnect(SPacketDisconnectLogin var1) {
		networkManager.closeChannel(var1.getReason());
	}

	@Override
	public void handleEnableCompression(SPacketEnableCompression var1) {
		
	}

	@Override
	public void handleCustomPayloadLogin(SPacketCustomPayloadLogin var1) {
		this.networkManager.sendPacket(new CPacketCustomPayloadLogin(var1.getTransaction(), (PacketBuffer)null));
	}

	private static ResourceLocation encodeEaglerLegacyChannel(String channel) {
		return NetHandlerPlayClient.encodeEaglerLegacyChannel(channel);
	}
}
