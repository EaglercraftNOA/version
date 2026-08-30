package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.lax1dude.eaglercraft.IOUtils;
import java.nio.file.Files;

public class PlayerProfileCache {
	public static final SimpleDateFormat field_152659_a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
	private final Map field_152661_c = Maps.newHashMap();
	private final Map field_152662_d = Maps.newHashMap();
	private final LinkedList field_152663_e = Lists.newLinkedList();
	private final MinecraftServer field_152664_f;
	private final File field_152665_g;

	private static final String __OBFID = "CL_00001888";

	public PlayerProfileCache(MinecraftServer p_i1171_1_, File p_i1171_2_) {
		this.field_152664_f = p_i1171_1_;
		this.field_152665_g = p_i1171_2_;
		this.func_152657_b();
	}

	private static GameProfile func_152650_a(MinecraftServer p_152650_0_, String p_152650_1_) {
		final GameProfile[] var2 = new GameProfile[1];

		return var2[0];
	}

	public void func_152649_a(GameProfile p_152649_1_) {
		this.func_152651_a(p_152649_1_, (Date) null);
	}

	private void func_152651_a(GameProfile p_152651_1_, Date p_152651_2_) {
		EaglercraftUUID var3 = p_152651_1_.getId();

		if (p_152651_2_ == null) {
			Calendar var4 = Calendar.getInstance();
			var4.setTime(new Date());
			var4.add(2, 1);
			p_152651_2_ = var4.getTime();
		}

		String var10 = p_152651_1_.getName().toLowerCase(Locale.ROOT);
		PlayerProfileCache.ProfileEntry var5 = new PlayerProfileCache.ProfileEntry(p_152651_1_, p_152651_2_, null);
		LinkedList var6 = this.field_152663_e;

		synchronized (this.field_152663_e) {
			if (this.field_152662_d.containsKey(var3)) {
				PlayerProfileCache.ProfileEntry var7 = (PlayerProfileCache.ProfileEntry) this.field_152662_d.get(var3);
				this.field_152661_c.remove(var7.func_152668_a().getName().toLowerCase(Locale.ROOT));
				this.field_152661_c.put(p_152651_1_.getName().toLowerCase(Locale.ROOT), var5);
				this.field_152663_e.remove(p_152651_1_);
			} else {
				this.field_152662_d.put(var3, var5);
				this.field_152661_c.put(var10, var5);
			}

			this.field_152663_e.addFirst(p_152651_1_);
		}
	}

	public GameProfile func_152655_a(String p_152655_1_) {
		String var2 = p_152655_1_.toLowerCase(Locale.ROOT);
		PlayerProfileCache.ProfileEntry var3 = (PlayerProfileCache.ProfileEntry) this.field_152661_c.get(var2);

		if (var3 != null && (new Date()).getTime() >= var3.field_152673_c.getTime()) {
			this.field_152662_d.remove(var3.func_152668_a().getId());
			this.field_152661_c.remove(var3.func_152668_a().getName().toLowerCase(Locale.ROOT));
			LinkedList var4 = this.field_152663_e;

			synchronized (this.field_152663_e) {
				this.field_152663_e.remove(var3.func_152668_a());
			}

			var3 = null;
		}

		GameProfile var9;

		if (var3 != null) {
			var9 = var3.func_152668_a();
			LinkedList var5 = this.field_152663_e;

			synchronized (this.field_152663_e) {
				this.field_152663_e.remove(var9);
				this.field_152663_e.addFirst(var9);
			}
		} else {
			var9 = func_152650_a(this.field_152664_f, var2);

			if (var9 != null) {
				this.func_152649_a(var9);
				var3 = (PlayerProfileCache.ProfileEntry) this.field_152661_c.get(var2);
			}
		}

		this.func_152658_c();
		return var3 == null ? null : var3.func_152668_a();
	}

	public String[] func_152654_a() {
		ArrayList var1 = Lists.newArrayList(this.field_152661_c.keySet());
		return (String[]) var1.toArray(new String[var1.size()]);
	}

	public GameProfile func_152652_a(EaglercraftUUID p_152652_1_) {
		PlayerProfileCache.ProfileEntry var2 = (PlayerProfileCache.ProfileEntry) this.field_152662_d.get(p_152652_1_);
		return var2 == null ? null : var2.func_152668_a();
	}

	private PlayerProfileCache.ProfileEntry func_152653_b(EaglercraftUUID p_152653_1_) {
		PlayerProfileCache.ProfileEntry var2 = (PlayerProfileCache.ProfileEntry) this.field_152662_d.get(p_152653_1_);

		if (var2 != null) {
			GameProfile var3 = var2.func_152668_a();
			LinkedList var4 = this.field_152663_e;

			synchronized (this.field_152663_e) {
				this.field_152663_e.remove(var3);
				this.field_152663_e.addFirst(var3);
			}
		}

		return var2;
	}

	public void func_152657_b() {
		List<PlayerProfileCache.ProfileEntry> var1 = null;
		BufferedReader var2 = null;
		try {
			var2 = new BufferedReader(
					new java.io.InputStreamReader(new java.io.FileInputStream(this.field_152665_g), Charsets.UTF_8));
			StringBuilder jsonStr = new StringBuilder();
			String line;
			while ((line = var2.readLine()) != null) {
				jsonStr.append(line);
			}
			JSONArray arr = new JSONArray(jsonStr.toString());
			var1 = new ArrayList<>();
			for (int i = 0; i < arr.length(); ++i) {
				JSONObject obj = arr.getJSONObject(i);
				var1.add(ProfileEntry.Serializer.fromJson(obj));
			}
		} catch (FileNotFoundException e) {

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(var2);
		}
		if (var1 != null) {
			this.field_152661_c.clear();
			this.field_152662_d.clear();
			LinkedList var3 = this.field_152663_e;
			synchronized (this.field_152663_e) {
				this.field_152663_e.clear();
			}
			var1 = Lists.reverse(var1);
			Iterator var12 = var1.iterator();
			while (var12.hasNext()) {
				PlayerProfileCache.ProfileEntry var4 = (PlayerProfileCache.ProfileEntry) var12.next();
				if (var4 != null) {
					this.func_152651_a(var4.func_152668_a(), var4.func_152670_b());
				}
			}
		}
	}

	public void func_152658_c() {
		List<PlayerProfileCache.ProfileEntry> entries = this.func_152656_a(1000);
		JSONArray arr = new JSONArray();
		for (PlayerProfileCache.ProfileEntry entry : entries) {
			arr.put(ProfileEntry.Serializer.toJson(entry));
		}
		BufferedWriter var2 = null;
		try {
			var2 = new BufferedWriter(
					new java.io.OutputStreamWriter(new java.io.FileOutputStream(this.field_152665_g), Charsets.UTF_8));
			var2.write(arr.toString());
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		} finally {
			IOUtils.closeQuietly(var2);
		}
	}

	private List func_152656_a(int p_152656_1_) {
		ArrayList var2 = Lists.newArrayList();
		LinkedList var4 = this.field_152663_e;
		ArrayList var3;

		synchronized (this.field_152663_e) {
			var3 = Lists.newArrayList(Iterators.limit(this.field_152663_e.iterator(), p_152656_1_));
		}

		Iterator var8 = var3.iterator();

		while (var8.hasNext()) {
			GameProfile var5 = (GameProfile) var8.next();
			PlayerProfileCache.ProfileEntry var6 = this.func_152653_b(var5.getId());

			if (var6 != null) {
				var2.add(var6);
			}
		}

		return var2;
	}

	static class ProfileEntry {
		private final GameProfile field_152672_b;
		private final Date field_152673_c;
		private static final String __OBFID = "CL_00001885";

		private ProfileEntry(GameProfile p_i46333_2_, Date p_i46333_3_) {
			this.field_152672_b = p_i46333_2_;
			this.field_152673_c = p_i46333_3_;
		}

		public GameProfile func_152668_a() {
			return this.field_152672_b;
		}

		public Date func_152670_b() {
			return this.field_152673_c;
		}

		ProfileEntry(GameProfile p_i1166_2_, Date p_i1166_3_, Object p_i1166_4_) {
			this(p_i1166_2_, p_i1166_3_);
		}

		public static class Serializer {
			public static JSONObject toJson(PlayerProfileCache.ProfileEntry entry) {
				JSONObject obj = new JSONObject();
				EaglercraftUUID uuid = (EaglercraftUUID) entry.func_152668_a().getId();
				obj.put("uuid", uuid == null ? "" : uuid.toString());
				obj.put("name", entry.func_152668_a().getName());
				obj.put("expiresOn", PlayerProfileCache.field_152659_a.format(entry.func_152670_b()));
				return obj;
			}

			public static PlayerProfileCache.ProfileEntry fromJson(JSONObject obj) {
				EaglercraftUUID uuid = null;
				String uuidStr = obj.has("uuid") ? obj.getString("uuid") : "";
				if (!uuidStr.isEmpty()) {
					try {
						uuid = EaglercraftUUID.fromString(uuidStr);
					} catch (Throwable t) {
						uuid = null;
					}
				}
				String name = obj.has("name") ? obj.getString("name") : null;
				Date expires = null;
				if (obj.has("expiresOn")) {
					try {
						expires = PlayerProfileCache.field_152659_a.parse(obj.getString("expiresOn"));
					} catch (ParseException e) {
						expires = null;
					}
				}
				return new PlayerProfileCache.ProfileEntry(new GameProfile(uuid, name), expires, null);
			}
		}
	}
}
