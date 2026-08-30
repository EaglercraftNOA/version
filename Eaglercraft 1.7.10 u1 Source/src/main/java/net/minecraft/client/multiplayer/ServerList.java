package net.minecraft.client.multiplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.EnumServerRateLimit;
import net.lax1dude.eaglercraft.internal.QueryResponse;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.socket.AddressResolver;
import net.lax1dude.eaglercraft.socket.RateLimitTracker;
import net.lax1dude.eaglercraft.socket.ServerQueryDispatch;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumChatFormatting;

public class ServerList {
	private static final Logger logger = LogManager.getLogger();

	/** The Minecraft instance. */
	private final Minecraft mc;
	/** List of ServerData instances. */
	private final List servers = new ArrayList();
	private static final String __OBFID = "CL_00000891";

	public ServerList(Minecraft p_i1194_1_) {
		this.mc = p_i1194_1_;
		this.loadServerList();
	}



	/**
	 * Loads a list of servers from servers.dat, by running
	 * ServerData.getServerDataFromNBTCompound on each NBT compound found in the
	 * "servers" tag list.
	 */
	public void loadServerList() {

		try {
			NBTTagCompound var1 = CompressedStreamTools.read(new VFile2(this.mc.mcDataDir, "servers.dat"));
			NBTTagList var2 = var1.getTagList("servers", 10);
			this.servers.clear();

			for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
				this.servers.add(ServerData.getServerDataFromNBTCompound((NBTTagCompound) var2.getCompoundTagAt(var3)));
			}

		} catch (Exception var4) {
			var4.printStackTrace();
		}
	}

	/**
	 * Runs getNBTCompound on each ServerData instance, puts everything into a
	 * "servers" NBT list and writes it to servers.dat.
	 */
	public void saveServerList() {

		try {
			NBTTagList var1 = new NBTTagList();
			Iterator var2 = this.servers.iterator();

			while (var2.hasNext()) {
				ServerData var3 = (ServerData) var2.next();
				var1.appendTag(var3.getNBTCompound());
			}

			NBTTagCompound var5 = new NBTTagCompound();
			var5.setTag("servers", var1);
			CompressedStreamTools.safeWrite(var5, new VFile2(this.mc.mcDataDir, "servers.dat"));
		} catch (Exception var4) {
			var4.printStackTrace();
		}
	}

	/**
	 * Gets the ServerData instance stored for the given index in the list.
	 */
	public ServerData getServerData(int p_78850_1_) {
		return (ServerData) this.servers.get(p_78850_1_);
	}



	/**
	 * Removes the ServerData instance stored for the given index in the list.
	 */
	public void removeServerData(int p_78851_1_) {
		this.servers.remove(p_78851_1_);
	}

	/**
	 * Adds the given ServerData instance to the list.
	 */
	public void addServerData(ServerData p_78849_1_) {
		this.servers.add(p_78849_1_);
	}

	/**
	 * Counts the number of ServerData instances in the list.
	 */
	public int countServers() {
		return this.servers.size();
	}

	/**
	 * Takes two list indexes, and swaps their order around.
	 */
	public void swapServers(int p_78857_1_, int p_78857_2_) {
		ServerData var3 = this.getServerData(p_78857_1_);
		this.servers.set(p_78857_1_, this.getServerData(p_78857_2_));
		this.servers.set(p_78857_2_, var3);
		this.saveServerList();
	}

	public void func_147413_a(int p_147413_1_, ServerData p_147413_2_) {
		this.servers.set(p_147413_1_, p_147413_2_);
	}

	public static void func_147414_b(ServerData p_147414_0_) {
		ServerList var1 = new ServerList(Minecraft.getMinecraft());
		var1.loadServerList();

		for (int var2 = 0; var2 < var1.countServers(); ++var2) {
			ServerData var3 = var1.getServerData(var2);

			if (var3.serverName.equals(p_147414_0_.serverName) && var3.serverIP.equals(p_147414_0_.serverIP)) {
				var1.func_147413_a(var2, p_147414_0_);
				break;
			}
		}

		var1.saveServerList();
	}

	public void updateServerPing() {
		int total = 0;
		for (int i = 0, l = this.servers.size(); i < l; ++i) {
			ServerData dat = (ServerData) this.servers.get(i);
			if (dat.pingSentTime <= 0l) {
				dat.pingSentTime = EagRuntime.steadyTimeMillis();
				if (RateLimitTracker.isLockedOut(dat.serverIP)) {
					logger.error(
							"Server {} locked this client out on a previous connection, will not attempt to reconnect",
							dat.serverIP);
					dat.serverMOTD = EnumChatFormatting.RED + "Too Many Requests!\nTry again later";
					dat.pingToServer = -1l;
					dat.hasPing = true;
					dat.field_78841_f = true;
				} else {
					dat.pingToServer = -2l;
					String addr = AddressResolver.resolveURI(dat.serverIP);
					dat.currentQuery = ServerQueryDispatch.sendServerQuery(addr, "MOTD");
					if (dat.currentQuery == null) {
						dat.pingToServer = -1l;
						dat.hasPing = true;
						dat.field_78841_f = true;
					} else {
						++total;
					}
				}
			} else if (dat.currentQuery != null) {
				dat.currentQuery.update();
				if (!dat.hasPing) {
					++total;
					EnumServerRateLimit rateLimit = dat.currentQuery.getRateLimit();
					if (rateLimit != EnumServerRateLimit.OK) {
						if (rateLimit == EnumServerRateLimit.BLOCKED) {
							RateLimitTracker.registerBlock(dat.serverIP);
						} else if (rateLimit == EnumServerRateLimit.LOCKED_OUT) {
							RateLimitTracker.registerLockOut(dat.serverIP);
						}
						dat.serverMOTD = EnumChatFormatting.RED + "Too Many Requests!\nTry again later";
						dat.pingToServer = -1l;
						dat.hasPing = true;
						return;
					}
				}
				if (dat.currentQuery.responsesAvailable() > 0) {
					QueryResponse pkt;
					do {
						pkt = dat.currentQuery.getResponse();
					} while (dat.currentQuery.responsesAvailable() > 0);
					if (pkt.responseType.equalsIgnoreCase("MOTD") && pkt.isResponseJSON()) {
						dat.setMOTDFromQuery(pkt);
						if (!dat.hasPing) {
							dat.pingToServer = pkt.clientTime - dat.pingSentTime;
							dat.hasPing = true;
						}
					}
				}
				if (dat.currentQuery.binaryResponsesAvailable() > 0) {
					byte[] r;
					do {
						r = dat.currentQuery.getBinaryResponse();
					} while (dat.currentQuery.binaryResponsesAvailable() > 0);
					dat.setIconPacket(r);
				}
				if (!dat.currentQuery.isOpen() && dat.pingSentTime > 0l
						&& (EagRuntime.steadyTimeMillis() - dat.pingSentTime) > 2000l && !dat.hasPing) {
					if (RateLimitTracker.isProbablyLockedOut(dat.serverIP)) {
						logger.error("Server {} ratelimited this client out on a previous connection, assuming lockout",
								dat.serverIP);
						dat.serverMOTD = EnumChatFormatting.RED + "Too Many Requests!\nTry again later";
					}
					dat.pingToServer = -1l;
					dat.hasPing = true;
				}
			}
			if (total >= 4) {
				break;
			}
		}

	}

}
