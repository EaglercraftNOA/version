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

package net.lax1dude.eaglercraft.v1_8.sp.server.socket;

import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerMinecraftServer;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetHandlerLoginServer;
import net.minecraft.network.NetHandlerStatusServer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class NetHandlerHandshakeEagler implements INetHandlerHandshakeServer {

	private final EaglerMinecraftServer mcServer;
	private final IntegratedServerPlayerNetworkManager networkManager;

	public NetHandlerHandshakeEagler(EaglerMinecraftServer parMinecraftServer, IntegratedServerPlayerNetworkManager parNetworkManager) {
		this.mcServer = parMinecraftServer;
		this.networkManager = parNetworkManager;
	}

	@Override
	public void onDisconnect(ITextComponent var1) {
		
	}

	@Override
	public void processHandshake(CPacketHandshake var1) {
		switch(var1.getRequestedState()) {
		case LOGIN:
			this.networkManager.setConnectionState(EnumConnectionState.LOGIN);
			if(var1.getProtocolVersion() > 404) {
				ITextComponent itextcomponent = new TextComponentTranslation("multiplayer.disconnect.outdated_server", "1.13.2");
				this.networkManager.sendPacket(new SPacketDisconnectLogin(itextcomponent));
				this.networkManager.closeChannel(itextcomponent);
			}else if(var1.getProtocolVersion() < 404) {
				ITextComponent itextcomponent1 = new TextComponentTranslation("multiplayer.disconnect.outdated_client", "1.13.2");
				this.networkManager.sendPacket(new SPacketDisconnectLogin(itextcomponent1));
				this.networkManager.closeChannel(itextcomponent1);
			}else {
				this.networkManager.setNetHandler(new NetHandlerLoginServer(this.mcServer, this.networkManager));
			}
			break;
		case STATUS:
			this.networkManager.setConnectionState(EnumConnectionState.STATUS);
			this.networkManager.setNetHandler(new NetHandlerStatusServer(this.mcServer, this.networkManager));
			break;
		default:
			throw new UnsupportedOperationException("Invalid intention " + var1.getRequestedState());
		}
	}

}
