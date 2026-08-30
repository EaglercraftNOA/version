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

package net.lax1dude.eaglercraft.v1_8.sp.server;

import com.mojang.authlib.GameProfile;
import net.lax1dude.eaglercraft.v1_8.EaglerSocketAddress;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EaglerPlayerList extends PlayerList {
	
	private NBTTagCompound hostPlayerNBT = null;

	public EaglerPlayerList(MinecraftServer server, int viewDistance) {
		super(server);
		this.setViewDistance(viewDistance);
	}

	@Override
	protected void writePlayerData(EntityPlayerMP player) {
		String owner = this.getServer().getServerOwner();
		if(owner != null && player.getName().getString().equals(owner)) {
			this.hostPlayerNBT = player.writeWithoutTypeId(new NBTTagCompound());
		}
		super.writePlayerData(player);
	}

	@Override
	public ITextComponent canPlayerLogin(EaglerSocketAddress address, GameProfile profile) {
		return profile.getName().equalsIgnoreCase(this.getServer().getServerOwner()) && this.getPlayerByUsername(profile.getName()) != null ? new TextComponentTranslation("multiplayer.disconnect.name_taken") : super.canPlayerLogin(address, profile);
	}
	
	@Override
	public NBTTagCompound getHostPlayerData() {
		return this.hostPlayerNBT;
	}

	public NBTTagCompound getSingleplayerData() {
		return this.hostPlayerNBT;
	}

	@Override
	public void playerLoggedIn(EntityPlayerMP player) {
		super.playerLoggedIn(player);
		if(this.getServer() instanceof EaglerMinecraftServer) {
			EaglerMinecraftServer server = (EaglerMinecraftServer)this.getServer();
			if(server.getVoiceService() != null) {
				server.getVoiceService().handlePlayerLoggedIn(player);
			}
		}
	}

	@Override
	public void playerLoggedOut(EntityPlayerMP player) {
		if(this.getServer() instanceof EaglerMinecraftServer) {
			EaglerMinecraftServer server = (EaglerMinecraftServer)this.getServer();
			if(server.getVoiceService() != null) {
				server.getVoiceService().handlePlayerLoggedOut(player);
			}
		}
		super.playerLoggedOut(player);
	}
}
