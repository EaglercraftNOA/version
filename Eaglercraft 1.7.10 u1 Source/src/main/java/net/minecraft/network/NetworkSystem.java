package net.minecraft.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
	private static final Logger logger = LogManager.getLogger();

	/** Reference to the MinecraftServer object. */
	private final MinecraftServer mcServer;

	/** True if this NetworkSystem has never had his endpoints terminated */
	public volatile boolean isAlive;

	/** Contains all endpoints added to this NetworkSystem */
	private final List endpoints = Collections.synchronizedList(new ArrayList());

	/** A list containing all NetworkManager instances of all endpoints */
	private final List networkManagers = Collections.synchronizedList(new ArrayList());
	private static final String __OBFID = "CL_00001447";

	public NetworkSystem(MinecraftServer p_i45292_1_) {
		this.mcServer = p_i45292_1_;
		this.isAlive = true;
	}

	/**
	 * Adds a channel that listens on publicly accessible network ports
	 */
	public void addLanEndpoint(InetAddress p_151265_1_, int p_151265_2_) throws IOException {

	}

	/**
	 * Shuts down all open endpoints (with immediate effect?)
	 */
	public void terminateEndpoints() {

	}

	/**
	 * Will try to process the packets received by each NetworkManager, gracefully
	 * manage processing failures and cleans up dead connections
	 */
	public void networkTick() {

	}

	public MinecraftServer func_151267_d() {
		return this.mcServer;
	}
}
