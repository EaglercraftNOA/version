package net.minecraft.profiler;

import com.google.common.collect.Maps;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import java.util.Map.Entry;
import net.minecraft.util.HttpUtil;

public class PlayerUsageSnooper {
	private final Map field_152773_a = Maps.newHashMap();
	private final Map field_152774_b = Maps.newHashMap();
	private final String uniqueID = EaglercraftUUID.randomUUID().toString();

	/** URL of the server to send the report to */
	private final URL serverUrl;
	private final IPlayerUsage playerStatsCollector;

	/** set to fire the snooperThread every 15 mins */
	private final Object syncLock = new Object();
	private final long minecraftStartTimeMilis;
	private boolean isRunning;

	/** incremented on every getSelfCounterFor */
	private int selfCounter;
	private static final String __OBFID = "CL_00001515";

	public PlayerUsageSnooper(String p_i1563_1_, IPlayerUsage p_i1563_2_, long p_i1563_3_) {
		try {
			this.serverUrl = new URL("http://snoop.minecraft.net/" + p_i1563_1_ + "?version=" + 2);
		} catch (MalformedURLException var6) {
			throw new IllegalArgumentException();
		}

		this.playerStatsCollector = p_i1563_2_;
		this.minecraftStartTimeMilis = p_i1563_3_;
	}

	/**
	 * Note issuing start multiple times is not an error.
	 */
	public void startSnooper() {
		if (!this.isRunning) {
			this.isRunning = true;
			this.func_152766_h();
		}
	}

	private void func_152766_h() {
		this.addJvmArgsToSnooper();
		this.func_152768_a("snooper_token", this.uniqueID);
		this.func_152767_b("snooper_token", this.uniqueID);
		this.func_152767_b("os_name", System.getProperty("os.name"));
		this.func_152767_b("os_version", System.getProperty("os.version"));
		this.func_152767_b("os_architecture", System.getProperty("os.arch"));
		this.func_152767_b("java_version", System.getProperty("java.version"));
		this.func_152767_b("version", "1.7.10");
		this.playerStatsCollector.addServerTypeToSnooper(this);
	}

	private void addJvmArgsToSnooper() {

	}

	public void addMemoryStatsToSnooper() {
		this.playerStatsCollector.addServerStatsToSnooper(this);
	}

	public void func_152768_a(String p_152768_1_, Object p_152768_2_) {
		Object var3 = this.syncLock;

		synchronized (this.syncLock) {
			this.field_152774_b.put(p_152768_1_, p_152768_2_);
		}
	}

	public void func_152767_b(String p_152767_1_, Object p_152767_2_) {
		Object var3 = this.syncLock;

		synchronized (this.syncLock) {
			this.field_152773_a.put(p_152767_1_, p_152767_2_);
		}
	}

	public Map getCurrentStats() {
		LinkedHashMap var1 = new LinkedHashMap();
		Object var2 = this.syncLock;

		synchronized (this.syncLock) {
			this.addMemoryStatsToSnooper();
			Iterator var3 = this.field_152773_a.entrySet().iterator();
			Entry var4;

			while (var3.hasNext()) {
				var4 = (Entry) var3.next();
				var1.put(var4.getKey(), var4.getValue().toString());
			}

			var3 = this.field_152774_b.entrySet().iterator();

			while (var3.hasNext()) {
				var4 = (Entry) var3.next();
				var1.put(var4.getKey(), var4.getValue().toString());
			}

			return var1;
		}
	}

	public boolean isSnooperRunning() {
		return this.isRunning;
	}

	public void stopSnooper() {
	}

	public String getUniqueID() {
		return this.uniqueID;
	}

	/**
	 * Returns the saved value of System#currentTimeMillis when the game started
	 */
	public long getMinecraftStartTimeMillis() {
		return this.minecraftStartTimeMilis;
	}

	static int access$308(PlayerUsageSnooper p_access$308_0_) {
		return p_access$308_0_.selfCounter++;
	}
}
