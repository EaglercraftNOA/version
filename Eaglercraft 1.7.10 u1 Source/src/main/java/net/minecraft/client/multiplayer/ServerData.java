package net.minecraft.client.multiplayer;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.IServerQuery;
import net.lax1dude.eaglercraft.internal.QueryResponse;
import net.lax1dude.eaglercraft.profile.EaglerSkinTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class ServerData {
	final boolean enableCookies;
	public String serverName;
	public String serverIP;

	/**
	 * the string indicating number of players on and capacity of the server that is
	 * shown on the server browser (i.e. "5/20" meaning 5 slots used out of 20 slots
	 * total)
	 */
	public String populationInfo;

	/**
	 * (better variable name would be 'hostname') server name as displayed in the
	 * server browser's second line (grey text)
	 */
	public boolean hasPing = false;
	public long pingSentTime = -1l;
	public String serverMOTD;
	public String playerList;

	/** last server ping that showed up in the server browser */
	public long pingToServer;
	public int field_82821_f;
	public IServerQuery currentQuery = null;
	/** Game version for this server. */
	public EaglerSkinTexture iconTextureObject = null;
	public final ResourceLocation iconResourceLocation;
	public String gameVersion;
	public boolean field_78841_f;
	public String field_147412_i;
	public int version = 47;
	public boolean hideAddress = false;
	private ServerData.ServerResourceMode field_152587_j;
	private String field_147411_m;
	private boolean field_152588_l;
	public boolean serverIconEnabled = false;
	public static Logger logger = LogManager.getLogger("ServerData");
	private static int serverTextureId = 0;

	public ServerData(String p_i1193_1_, String p_i1193_2_) {
		this.field_82821_f = 5;
		this.gameVersion = "1.7.10";
		this.field_152587_j = ServerData.ServerResourceMode.PROMPT;
		this.serverName = p_i1193_1_;
		this.serverIP = p_i1193_2_;
		this.iconResourceLocation = new ResourceLocation("eagler:servers/icons/tex_" + serverTextureId++);
		this.enableCookies = EagRuntime.getConfiguration().isEnableServerCookies();

	}

	public ServerData(String p_i46395_1_, String p_i46395_2_, boolean p_i46395_3_) {
		this(p_i46395_1_, p_i46395_2_);
		this.field_152588_l = p_i46395_3_;
	}

	/**
	 * Returns an NBTTagCompound with the server's name, IP and maybe
	 * acceptTextures.
	 */
	public NBTTagCompound getNBTCompound() {
		NBTTagCompound var1 = new NBTTagCompound();
		var1.setString("name", this.serverName);
		var1.setString("ip", this.serverIP);

		if (this.field_147411_m != null) {
			var1.setString("icon", this.field_147411_m);
		}

		if (this.field_152587_j == ServerData.ServerResourceMode.ENABLED) {
			var1.setBoolean("acceptTextures", true);
		} else if (this.field_152587_j == ServerData.ServerResourceMode.DISABLED) {
			var1.setBoolean("acceptTextures", false);
		}

		return var1;
	}

	public ServerData.ServerResourceMode func_152586_b() {
		return this.field_152587_j;
	}

	public void func_152584_a(ServerData.ServerResourceMode p_152584_1_) {
		this.field_152587_j = p_152584_1_;
	}

	/**
	 * Takes an NBTTagCompound with 'name' and 'ip' keys, returns a ServerData
	 * instance.
	 */
	public static ServerData getServerDataFromNBTCompound(NBTTagCompound p_78837_0_) {
		ServerData var1 = new ServerData(p_78837_0_.getString("name"), p_78837_0_.getString("ip"));

		if (p_78837_0_.func_150297_b("icon", 8)) {
			var1.func_147407_a(p_78837_0_.getString("icon"));
		}

		if (p_78837_0_.func_150297_b("acceptTextures", 1)) {
			if (p_78837_0_.getBoolean("acceptTextures")) {
				var1.func_152584_a(ServerData.ServerResourceMode.ENABLED);
			} else {
				var1.func_152584_a(ServerData.ServerResourceMode.DISABLED);
			}
		} else {
			var1.func_152584_a(ServerData.ServerResourceMode.PROMPT);
		}

		return var1;
	}

	public String func_147409_e() {
		return this.field_147411_m;
	}

	public void func_147407_a(String p_147407_1_) {
		this.field_147411_m = p_147407_1_;
	}

	public void func_152583_a(ServerData p_152583_1_) {
		this.serverIP = p_152583_1_.serverIP;
		this.serverName = p_152583_1_.serverName;
		this.func_152584_a(p_152583_1_.func_152586_b());
		this.field_147411_m = p_152583_1_.field_147411_m;
	}

	public boolean func_152585_d() {
		return this.field_152588_l;
	}

	public static enum ServerResourceMode {
		ENABLED("ENABLED", 0, "enabled"), DISABLED("DISABLED", 1, "disabled"), PROMPT("PROMPT", 2, "prompt");

		private final IChatComponent field_152594_d;

		private static final ServerData.ServerResourceMode[] $VALUES = new ServerData.ServerResourceMode[] { ENABLED,
				DISABLED, PROMPT };
		private static final String __OBFID = "CL_00001833";

		private ServerResourceMode(String p_i1053_1_, int p_i1053_2_, String p_i1053_3_) {
			this.field_152594_d = new ChatComponentTranslation("addServer.resourcePack." + p_i1053_3_, new Object[0]);
		}

		public IChatComponent func_152589_a() {
			return this.field_152594_d;
		}
	}

	public void setMOTDFromQuery(QueryResponse pkt) {
		try {
			if (pkt.isResponseJSON()) {
				JSONObject motdData = pkt.getResponseJSON();
				JSONArray motd = motdData.getJSONArray("motd");
				this.serverMOTD = motd.length() > 0
						? (motd.length() > 1 ? motd.getString(0) + "\n" + motd.getString(1) : motd.getString(0))
						: "";
				int max = motdData.getInt("max");
				if (max > 0) {
					this.populationInfo = "" + motdData.getInt("online") + "/" + max;
				} else {
					this.populationInfo = "" + motdData.getInt("online");
				}
				this.playerList = null;
				JSONArray players = motdData.optJSONArray("players");
				if (players.length() > 0) {
					StringBuilder builder = new StringBuilder();
					for (int i = 0, l = players.length(); i < l; ++i) {
						if (i > 0) {
							builder.append('\n');
						}
						builder.append(players.getString(i));
					}
					this.playerList = builder.toString();
				}
				serverIconEnabled = motdData.getBoolean("icon");
				if (!serverIconEnabled) {
					if (iconTextureObject != null) {
						Minecraft.getMinecraft().getTextureManager().func_147645_c(iconResourceLocation);
						iconTextureObject = null;
					}
				}
			} else {
				throw new IOException("Response was not JSON!");
			}
		} catch (Throwable t) {
			pingToServer = -1l;
			logger.error("Could not decode QueryResponse from: {}", serverIP);
			logger.error(t);
		}
	}

	public void setIconPacket(byte[] pkt) {
		try {
			if (!serverIconEnabled) {
				throw new IOException("Unexpected icon packet on text-only MOTD");
			}
			if (pkt.length != 16384) {
				throw new IOException("MOTD icon packet is the wrong size!");
			}
			int[] pixels = new int[4096];
			for (int i = 0, j; i < 4096; ++i) {
				j = i << 2;
				pixels[i] = ((int) pkt[j] & 0xFF) | (((int) pkt[j + 1] & 0xFF) << 8) | (((int) pkt[j + 2] & 0xFF) << 16)
						| (((int) pkt[j + 3] & 0xFF) << 24);
			}
			if (iconTextureObject != null) {
				iconTextureObject.copyPixelsIn(pixels);
			} else {
				iconTextureObject = new EaglerSkinTexture(pixels, 64, 64);
				Minecraft.getMinecraft().getTextureManager().loadTexture(iconResourceLocation, iconTextureObject);
			}
		} catch (Throwable t) {
			pingToServer = -1l;
			logger.error("Could not decode MOTD icon from: {}", serverIP);
			logger.error(t);
		}
	}

}
