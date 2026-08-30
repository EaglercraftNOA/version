package net.minecraft.network;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import com.mojang.authlib.GameProfile;
import java.lang.reflect.Type;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonUtils;

public class ServerStatusResponse {
	private IChatComponent field_151326_a;
	private ServerStatusResponse.PlayerCountData field_151324_b;
	private ServerStatusResponse.MinecraftProtocolVersionIdentifier field_151325_c;
	private String field_151323_d;
	private static final String __OBFID = "CL_00001385";

	public IChatComponent func_151317_a() {
		return this.field_151326_a;
	}

	public void func_151315_a(IChatComponent p_151315_1_) {
		this.field_151326_a = p_151315_1_;
	}

	public ServerStatusResponse.PlayerCountData func_151318_b() {
		return this.field_151324_b;
	}

	public void func_151319_a(ServerStatusResponse.PlayerCountData p_151319_1_) {
		this.field_151324_b = p_151319_1_;
	}

	public ServerStatusResponse.MinecraftProtocolVersionIdentifier func_151322_c() {
		return this.field_151325_c;
	}

	public void func_151321_a(ServerStatusResponse.MinecraftProtocolVersionIdentifier p_151321_1_) {
		this.field_151325_c = p_151321_1_;
	}

	public void func_151320_a(String p_151320_1_) {
		this.field_151323_d = p_151320_1_;
	}

	public String func_151316_d() {
		return this.field_151323_d;
	}

	public static class MinecraftProtocolVersionIdentifier {
		private final String field_151306_a;
		private final int field_151305_b;
		private static final String __OBFID = "CL_00001389";

		public MinecraftProtocolVersionIdentifier(String p_i45275_1_, int p_i45275_2_) {
			this.field_151306_a = p_i45275_1_;
			this.field_151305_b = p_i45275_2_;
		}

		public String func_151303_a() {
			return this.field_151306_a;
		}

		public int func_151304_b() {
			return this.field_151305_b;
		}

		public static class Serializer {
			private static final String __OBFID = "CL_00001390";

			public static ServerStatusResponse.MinecraftProtocolVersionIdentifier fromJson(JSONObject obj)
					throws JSONException {
				return new ServerStatusResponse.MinecraftProtocolVersionIdentifier(obj.getString("name"),
						obj.getInt("protocol"));
			}

			public static JSONObject toJson(ServerStatusResponse.MinecraftProtocolVersionIdentifier ver)
					throws JSONException {
				JSONObject obj = new JSONObject();
				obj.put("name", ver.func_151303_a());
				obj.put("protocol", ver.func_151304_b());
				return obj;
			}
		}
	}

	public static class PlayerCountData {
		private final int field_151336_a;
		private final int field_151334_b;
		private GameProfile[] field_151335_c;
		private static final String __OBFID = "CL_00001386";

		public PlayerCountData(int p_i45274_1_, int p_i45274_2_) {
			this.field_151336_a = p_i45274_1_;
			this.field_151334_b = p_i45274_2_;
		}

		public int func_151332_a() {
			return this.field_151336_a;
		}

		public int func_151333_b() {
			return this.field_151334_b;
		}

		public GameProfile[] func_151331_c() {
			return this.field_151335_c;
		}

		public void func_151330_a(GameProfile[] p_151330_1_) {
			this.field_151335_c = p_151330_1_;
		}

		public static class Serializer {
			private static final String __OBFID = "CL_00001387";

			public static ServerStatusResponse.PlayerCountData fromJson(JSONObject obj) throws JSONException {
				ServerStatusResponse.PlayerCountData data = new ServerStatusResponse.PlayerCountData(obj.getInt("max"),
						obj.getInt("online"));
				if (obj.has("sample")) {
					JSONArray arr = obj.getJSONArray("sample");
					if (arr.length() > 0) {
						GameProfile[] profiles = new GameProfile[arr.length()];
						for (int i = 0; i < arr.length(); ++i) {
							JSONObject p = arr.getJSONObject(i);
							profiles[i] = new GameProfile(EaglercraftUUID.fromString(p.getString("id")),
									p.getString("name"));
						}
						data.func_151330_a(profiles);
					}
				}
				return data;
			}

			public static JSONObject toJson(ServerStatusResponse.PlayerCountData data) throws JSONException {
				JSONObject obj = new JSONObject();
				obj.put("max", data.func_151332_a());
				obj.put("online", data.func_151333_b());
				if (data.func_151331_c() != null && data.func_151331_c().length > 0) {
					JSONArray arr = new JSONArray();
					for (GameProfile gp : data.func_151331_c()) {
						JSONObject p = new JSONObject();
						EaglercraftUUID uuid = (EaglercraftUUID) gp.getId();
						p.put("id", uuid == null ? "" : uuid.toString());
						p.put("name", gp.getName());
						arr.put(p);
					}
					obj.put("sample", arr);
				}
				return obj;
			}
		}
	}

	public static class Serializer
			implements net.lax1dude.eaglercraft.json.JSONTypeDeserializer<org.json.JSONObject, ServerStatusResponse> {
		private static final String __OBFID = "CL_00001388";

		@Override
		public ServerStatusResponse deserialize(org.json.JSONObject obj) throws org.json.JSONException {
			return fromJson(obj);
		}

		public static ServerStatusResponse fromJson(org.json.JSONObject obj) throws org.json.JSONException {
			ServerStatusResponse resp = new ServerStatusResponse();
			if (obj.has("description")) {
				resp.func_151315_a(IChatComponent.Serializer.fromJsonObject(obj.getJSONObject("description")));
			}
			if (obj.has("players")) {
				resp.func_151319_a(PlayerCountData.Serializer.fromJson(obj.getJSONObject("players")));
			}
			if (obj.has("version")) {
				resp.func_151321_a(
						MinecraftProtocolVersionIdentifier.Serializer.fromJson(obj.getJSONObject("version")));
			}
			if (obj.has("favicon")) {
				resp.func_151320_a(obj.getString("favicon"));
			}
			return resp;
		}

		public static org.json.JSONObject toJson(ServerStatusResponse resp) throws org.json.JSONException {
			org.json.JSONObject obj = new org.json.JSONObject();
			if (resp.func_151317_a() != null) {
				obj.put("description", IChatComponent.Serializer.toJsonObject(resp.func_151317_a()));
			}
			if (resp.func_151318_b() != null) {
				obj.put("players", PlayerCountData.Serializer.toJson(resp.func_151318_b()));
			}
			if (resp.func_151322_c() != null) {
				obj.put("version", MinecraftProtocolVersionIdentifier.Serializer.toJson(resp.func_151322_c()));
			}
			if (resp.func_151316_d() != null) {
				obj.put("favicon", resp.func_151316_d());
			}
			return obj;
		}
	}
}
