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

package net.lax1dude.eaglercraft.sp.server.socket.protocol;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.socket.protocol.pkt.client.*;
import net.lax1dude.eaglercraft.sp.server.voice.IntegratedVoiceService;
import net.minecraft.network.NetHandlerPlayServer;

public class ServerV3MessageHandler extends ServerMessageHandler {

	public ServerV3MessageHandler(NetHandlerPlayServer netHandler) {
		super(netHandler);
	}

	public void handleClient(CPacketGetOtherCapeEAG packet) {

	}

	public void handleClient(CPacketGetOtherSkinEAG packet) {

	}

	public void handleClient(CPacketGetSkinByURLEAG packet) {

	}

	public void handleClient(CPacketInstallSkinSPEAG packet) {
	}

	public void handleClient(CPacketVoiceSignalConnectEAG packet) {

	}

	public void handleClient(CPacketVoiceSignalDescEAG packet) {

	}

	public void handleClient(CPacketVoiceSignalDisconnectV3EAG packet) {

	}

	public void handleClient(CPacketVoiceSignalICEEAG packet) {

	}

	public void handleClient(CPacketVoiceSignalRequestEAG packet) {

	}

}