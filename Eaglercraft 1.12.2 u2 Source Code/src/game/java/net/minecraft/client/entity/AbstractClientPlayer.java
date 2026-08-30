package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.profile.SkinModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public abstract class AbstractClientPlayer extends EntityPlayer {
	private NetworkPlayerInfo playerInfo;
	
	public long eaglerHighPolyAnimationTick = EagRuntime.steadyTimeMillis();
	public float eaglerHighPolyAnimationFloat1 = 0.0f;
	public float eaglerHighPolyAnimationFloat2 = 0.0f;
	public float eaglerHighPolyAnimationFloat3 = 0.0f;
	public float eaglerHighPolyAnimationFloat4 = 0.0f;
	public float eaglerHighPolyAnimationFloat5 = 0.0f;
	public float eaglerHighPolyAnimationFloat6 = 0.0f;
	
	public float rotateElytraX;
	public float rotateElytraY;
	public float rotateElytraZ;
	
	public EaglercraftUUID clientBrandUUIDCache = null;

	public AbstractClientPlayer(World worldIn, GameProfile playerProfile) {
		super(worldIn, playerProfile);
	}

	/**
	 * Returns true if the player is in spectator mode.
	 */
	public boolean isSpectator() {
		NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection()
				.getPlayerInfo(this.getGameProfile().getId());
		return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.SPECTATOR;
	}

	public boolean isCreative() {
		NetworkPlayerInfo networkplayerinfo = Minecraft.getMinecraft().getConnection()
				.getPlayerInfo(this.getGameProfile().getId());
		return networkplayerinfo != null && networkplayerinfo.getGameType() == GameType.CREATIVE;
	}

	/**
	 * Checks if this instance of AbstractClientPlayer has any associated player
	 * data.
	 */
	public boolean hasPlayerInfo() {
		return this.getPlayerInfo() != null;
	}

	@Nullable
	protected NetworkPlayerInfo getPlayerInfo() {
		if (this.playerInfo == null) {
			this.playerInfo = Minecraft.getMinecraft().getConnection().getPlayerInfo(this.getUniqueID());
		}

		return this.playerInfo;
	}

	/**
	 * Returns true if the player has an associated skin.
	 */
	public boolean hasSkin() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo != null && networkplayerinfo.hasLocationSkin();
	}

	/**
	 * Returns true if the player instance has an associated skin.
	 */
	public ResourceLocation getLocationSkin() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID())
				: networkplayerinfo.getLocationSkin();
	}

	@Nullable
	public ResourceLocation getLocationCape() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? null : networkplayerinfo.getLocationCape();
	}

	public boolean isPlayerInfoSet() {
		return this.getPlayerInfo() != null;
	}

	@Nullable

	/**
	 * Gets the special Elytra texture for the player.
	 */
	public ResourceLocation getLocationElytra() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? null : networkplayerinfo.getLocationElytra();
	}

//    /**
//     * Returns true if the username has an associated skin.
//     */
//    public static ResourceLocation getLocationSkin(String username)
//    {
//        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
//    }

	public String getSkinType() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID())
				: networkplayerinfo.getSkinType();
	}

	public SkinModel getEaglerSkinModel() {
		NetworkPlayerInfo networkplayerinfo = this.getPlayerInfo();
		return networkplayerinfo == null ? SkinModel.STEVE : networkplayerinfo.getEaglerSkinModel();
	}

	public float getFovModifier() {
		float f = 1.0F;

		if (this.capabilities.isFlying) {
			f *= 1.1F;
		}

		IAttributeInstance iattributeinstance = this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
		f = (float) ((double) f
				* ((iattributeinstance.getAttributeValue() / (double) this.capabilities.getWalkSpeed() + 1.0D) / 2.0D));

		if (this.capabilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
			f = 1.0F;
		}

		if (this.isHandActive() && this.getActiveItemStack().getItem() == Items.BOW) {
			int i = this.getItemInUseMaxCount();
			float f1 = (float) i / 20.0F;

			if (f1 > 1.0F) {
				f1 = 1.0F;
			} else {
				f1 = f1 * f1;
			}

			f *= 1.0F - f1 * 0.15F;
		}

		return f;
	}
}
