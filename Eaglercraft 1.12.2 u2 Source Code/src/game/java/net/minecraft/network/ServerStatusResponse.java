package net.minecraft.network;

import com.mojang.authlib.GameProfile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.json.JSONTypeCodec;
import net.lax1dude.eaglercraft.json.JSONTypeDeserializer;
import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.util.text.ITextComponent;

public class ServerStatusResponse {
	private ITextComponent description;
	private ServerStatusResponse.Players players;
	private ServerStatusResponse.Version version;
	private String favicon;

	public ITextComponent getServerDescription() {
		return this.description;
	}

	public void setServerDescription(ITextComponent descriptionIn) {
		this.description = descriptionIn;
	}

	public ServerStatusResponse.Players getPlayers() {
		return this.players;
	}

	public void setPlayers(ServerStatusResponse.Players playersIn) {
		this.players = playersIn;
	}

	public ServerStatusResponse.Version getVersion() {
		return this.version;
	}

	public void setVersion(ServerStatusResponse.Version versionIn) {
		this.version = versionIn;
	}

	public void setFavicon(String faviconBlob) {
		this.favicon = faviconBlob;
	}

	public String getFavicon() {
		return this.favicon;
	}

	public static class Players {
		private final int maxPlayers;
		private final int onlinePlayerCount;
		private GameProfile[] players;

		public Players(int maxOnlinePlayers, int onlinePlayers) {
			this.maxPlayers = maxOnlinePlayers;
			this.onlinePlayerCount = onlinePlayers;
		}

		public int getMaxPlayers() {
			return this.maxPlayers;
		}

		public int getOnlinePlayerCount() {
			return this.onlinePlayerCount;
		}

		public GameProfile[] getPlayers() {
			return this.players;
		}

		public void setPlayers(GameProfile[] playersIn) {
			this.players = playersIn;
		}

		public static class Serializer implements JSONTypeDeserializer<JSONObject, ServerStatusResponse.Players>,
				JSONTypeCodec<ServerStatusResponse.Players, JSONObject> {
			public ServerStatusResponse.Players deserialize(JSONObject jsonobject) throws JSONException {
				ServerStatusResponse.Players serverstatusresponse$players = new ServerStatusResponse.Players(
						jsonobject.getInt("max"), jsonobject.getInt("online"));

				if (jsonobject.has("sample") && jsonobject.get("sample") instanceof JSONArray) {
					JSONArray jsonarray = jsonobject.getJSONArray("sample");

					if (jsonarray.length() > 0) {
						GameProfile[] agameprofile = new GameProfile[jsonarray.length()];

						for (int i = 0; i < agameprofile.length; ++i) {
							JSONObject jsonobject1 = jsonarray.getJSONObject(i);
							String s = jsonobject1.getString("id");
							agameprofile[i] = new GameProfile(EaglercraftUUID.fromString(s),
									jsonobject1.getString("name"));
						}

						serverstatusresponse$players.setPlayers(agameprofile);
					}
				}

				return serverstatusresponse$players;
			}

			public JSONObject serialize(ServerStatusResponse.Players p_serialize_1_) {
				JSONObject jsonobject = new JSONObject();
				jsonobject.put("max", Integer.valueOf(p_serialize_1_.getMaxPlayers()));
				jsonobject.put("online", Integer.valueOf(p_serialize_1_.getOnlinePlayerCount()));

				if (p_serialize_1_.getPlayers() != null && p_serialize_1_.getPlayers().length > 0) {
					JSONArray jsonarray = new JSONArray();

					for (int i = 0; i < p_serialize_1_.getPlayers().length; ++i) {
						JSONObject jsonobject1 = new JSONObject();
						EaglercraftUUID uuid = p_serialize_1_.getPlayers()[i].getId();
						jsonobject1.put("id", uuid == null ? "" : uuid.toString());
						jsonobject1.put("name", p_serialize_1_.getPlayers()[i].getName());
						jsonarray.put(jsonobject1);
					}

					jsonobject.put("sample", jsonarray);
				}

				return jsonobject;
			}
		}
	}

	public static class Serializer implements JSONTypeDeserializer<JSONObject, ServerStatusResponse>,
			JSONTypeCodec<ServerStatusResponse, JSONObject> {
		public ServerStatusResponse deserialize(JSONObject jsonobject) throws JSONException {
			ServerStatusResponse serverstatusresponse = new ServerStatusResponse();

			if (jsonobject.has("description")) {
				serverstatusresponse.setServerDescription((ITextComponent) JSONTypeProvider
						.deserialize(jsonobject.get("description"), ITextComponent.class));
			}

			if (jsonobject.has("players")) {
				serverstatusresponse.setPlayers((ServerStatusResponse.Players) JSONTypeProvider
						.deserialize(jsonobject.get("players"), ServerStatusResponse.Players.class));
			}

			if (jsonobject.has("version")) {
				serverstatusresponse.setVersion((ServerStatusResponse.Version) JSONTypeProvider
						.deserialize(jsonobject.get("version"), ServerStatusResponse.Version.class));
			}

			if (jsonobject.has("favicon")) {
				serverstatusresponse.setFavicon(jsonobject.getString("favicon"));
			}

			return serverstatusresponse;
		}

		public JSONObject serialize(ServerStatusResponse p_serialize_1_) {
			JSONObject jsonobject = new JSONObject();

			if (p_serialize_1_.getServerDescription() != null) {
				jsonobject.put("description",
						(Object) JSONTypeProvider.serialize(p_serialize_1_.getServerDescription()));
			}

			if (p_serialize_1_.getPlayers() != null) {
				jsonobject.put("players", (Object) JSONTypeProvider.serialize(p_serialize_1_.getPlayers()));
			}

			if (p_serialize_1_.getVersion() != null) {
				jsonobject.put("version", (Object) JSONTypeProvider.serialize(p_serialize_1_.getVersion()));
			}

			if (p_serialize_1_.getFavicon() != null) {
				jsonobject.put("favicon", p_serialize_1_.getFavicon());
			}

			return jsonobject;
		}
	}

	public static class Version {
		private final String name;
		private final int protocol;

		public Version(String nameIn, int protocolIn) {
			this.name = nameIn;
			this.protocol = protocolIn;
		}

		public String getName() {
			return this.name;
		}

		public int getProtocol() {
			return this.protocol;
		}

		public static class Serializer implements JSONTypeDeserializer<JSONObject, ServerStatusResponse.Version>,
				JSONTypeCodec<ServerStatusResponse.Version, JSONObject> {
			public ServerStatusResponse.Version deserialize(JSONObject jsonobject) throws JSONException {
				return new ServerStatusResponse.Version(jsonobject.getString("name"), jsonobject.getInt("protocol"));
			}

			public JSONObject serialize(ServerStatusResponse.Version p_serialize_1_) {
				JSONObject jsonobject = new JSONObject();
				jsonobject.put("name", p_serialize_1_.getName());
				jsonobject.put("protocol", Integer.valueOf(p_serialize_1_.getProtocol()));
				return jsonobject;
			}
		}
	}
}
