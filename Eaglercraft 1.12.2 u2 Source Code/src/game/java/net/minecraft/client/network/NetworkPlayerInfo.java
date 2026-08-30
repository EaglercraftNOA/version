package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.profile.SkinModel;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;

public class NetworkPlayerInfo {
	/**
	 * The GameProfile for the player represented by this NetworkPlayerInfo instance
	 */
	private final GameProfile gameProfile;
	private GameType gameType;

	/** Player response time to server in milliseconds */
	private int responseTime;

	/**
	 * When this is non-null, it is displayed instead of the player's real name
	 */
	private ITextComponent displayName;
	private int lastHealth;
	private int displayHealth;
	private long lastHealthTime;
	private long healthBlinkTime;
	private long renderVisibilityId;

	public NetworkPlayerInfo(GameProfile profile) {
		this.gameProfile = profile;
	}

	public NetworkPlayerInfo(SPacketPlayerListItem.AddPlayerData entry) {
		this.gameProfile = entry.getProfile();
		this.gameType = entry.getGameMode();
		this.responseTime = entry.getPing();
		this.displayName = entry.getDisplayName();
	}

	/**
	 * Returns the GameProfile for the player represented by this NetworkPlayerInfo
	 * instance
	 */
	public GameProfile getGameProfile() {
		return this.gameProfile;
	}

	public GameType getGameType() {
		return this.gameType;
	}

	protected void setGameType(GameType gameMode) {
		this.gameType = gameMode;
	}

	public int getResponseTime() {
		return this.responseTime;
	}

	protected void setResponseTime(int latency) {
		this.responseTime = latency;
	}

	public boolean hasLocationSkin() {
		return true;
	}


	public String getSkinType() {
		return Minecraft.getMinecraft().getNetHandler().getSkinCache().getSkin(this.gameProfile)
				.getSkinModel().profileSkinType;
	}

	public SkinModel getEaglerSkinModel() {
		return Minecraft.getMinecraft().getNetHandler().getSkinCache().getSkin(this.gameProfile).getSkinModel();
	}

	public ResourceLocation getLocationSkin() {
		return Minecraft.getMinecraft().getNetHandler().getSkinCache().getSkin(this.gameProfile).getResourceLocation();
	}

	public ResourceLocation getLocationCape() {
		return Minecraft.getMinecraft().getNetHandler().getCapeCache().getCape(this.gameProfile.getId())
				.getResourceLocation();
	}

	@Nullable

	/**
	 * Gets the special Elytra texture for the player.
	 */
	public ResourceLocation getLocationElytra() {
		return null;
	}

	@Nullable
	public ScorePlayerTeam getPlayerTeam() {
		return Minecraft.getMinecraft().world.getScoreboard().getPlayersTeam(this.getGameProfile().getName());
	}

//    protected void loadPlayerTextures()
//    {
//        synchronized (this)
//        {
//            if (!this.playerTexturesLoaded)
//            {
//                this.playerTexturesLoaded = true;
//                Minecraft.getMinecraft().getSkinManager().loadProfileTextures(this.gameProfile, new SkinManager.SkinAvailableCallback()
//                {
//                    public void skinAvailable(Type typeIn, ResourceLocation location, MinecraftProfileTexture profileTexture)
//                    {
//                        switch (typeIn)
//                        {
//                            case SKIN:
//                                NetworkPlayerInfo.this.playerTextures.put(Type.SKIN, location);
//                                NetworkPlayerInfo.this.skinType = profileTexture.getMetadata("model");
//
//                                if (NetworkPlayerInfo.this.skinType == null)
//                                {
//                                    NetworkPlayerInfo.this.skinType = "default";
//                                }
//
//                                break;
//
//                            case CAPE:
//                                NetworkPlayerInfo.this.playerTextures.put(Type.CAPE, location);
//                                break;
//
//                            case ELYTRA:
//                                NetworkPlayerInfo.this.playerTextures.put(Type.ELYTRA, location);
//                        }
//                    }
//                }, true);
//            }
//        }
//    }

	public void setDisplayName(@Nullable ITextComponent displayNameIn) {
		this.displayName = displayNameIn;
	}

	@Nullable
	public ITextComponent getDisplayName() {
		return this.displayName;
	}

	public int getLastHealth() {
		return this.lastHealth;
	}

	public void setLastHealth(int p_178836_1_) {
		this.lastHealth = p_178836_1_;
	}

	public int getDisplayHealth() {
		return this.displayHealth;
	}

	public void setDisplayHealth(int p_178857_1_) {
		this.displayHealth = p_178857_1_;
	}

	public long getLastHealthTime() {
		return this.lastHealthTime;
	}

	public void setLastHealthTime(long p_178846_1_) {
		this.lastHealthTime = p_178846_1_;
	}

	public long getHealthBlinkTime() {
		return this.healthBlinkTime;
	}

	public void setHealthBlinkTime(long p_178844_1_) {
		this.healthBlinkTime = p_178844_1_;
	}

	public long getRenderVisibilityId() {
		return this.renderVisibilityId;
	}

	public void setRenderVisibilityId(long p_178843_1_) {
		this.renderVisibilityId = p_178843_1_;
	}
}
