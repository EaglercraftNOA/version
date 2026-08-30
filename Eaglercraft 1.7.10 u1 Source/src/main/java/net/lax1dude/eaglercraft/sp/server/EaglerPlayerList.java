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

package net.lax1dude.eaglercraft.sp.server;

import net.lax1dude.eaglercraft.sp.server.socket.IntegratedServerPlayerNetworkManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.nbt.NBTTagCompound;

public class EaglerPlayerList extends ServerConfigurationManager {

	private NBTTagCompound hostPlayerNBT = null;

	public EaglerPlayerList(MinecraftServer par1MinecraftServer, int viewDistance) {
		super(par1MinecraftServer);
		this.func_152611_a(viewDistance);
	}

	protected void writePlayerData(EntityPlayerMP par1EntityPlayerMP) {
		if (par1EntityPlayerMP.getCommandSenderName().equals(this.getServerInstance().getServerOwner())) {
			this.hostPlayerNBT = new NBTTagCompound();
			par1EntityPlayerMP.writeToNBT(hostPlayerNBT);
		}
		super.writePlayerData(par1EntityPlayerMP);
	}

	public NBTTagCompound getHostPlayerData() {
		return this.hostPlayerNBT;
	}

	public void playerLoggedOut(EntityPlayerMP playerIn) {
		super.playerLoggedOut(playerIn);
	}

	@Override
	public void initializeConnectionToPlayer(IntegratedServerPlayerNetworkManager netManager,
			net.minecraft.entity.player.EntityPlayerMP player) {
		System.out.println("initializeConnectionToPlayer: " + player.getCommandSenderName());
		// Set up the NetHandlerPlayServer for the player
		net.minecraft.network.NetHandlerPlayServer playHandler = new net.minecraft.network.NetHandlerPlayServer(
				this.getServerInstance(), netManager, player);
		player.playerNetServerHandler = playHandler;
		netManager.setNetHandler(playHandler);

		// Add the player to the world and send join packets
		super.initializeConnectionToPlayer(netManager, player);
	}
}
