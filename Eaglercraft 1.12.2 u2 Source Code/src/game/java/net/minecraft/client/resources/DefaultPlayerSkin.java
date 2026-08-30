package net.minecraft.client.resources;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.util.ResourceLocation;
import net.lax1dude.eaglercraft.profile.*;

public class DefaultPlayerSkin {
	public static int id = 0;
	private static ResourceLocation Skin = DefaultSkins.defaultSkinsMap[id].location;

	/**
	 * Returns the default skin for versions prior to 1.8, which is always the Steve
	 * texture.
	 */
	public static ResourceLocation getDefaultSkinLegacy() {
		return Skin;
	}

	/**
	 * Retrieves the default skin for this player. Depending on the model used this
	 * will be Alex or Steve.
	 */
	public static ResourceLocation getDefaultSkin(EaglercraftUUID playerUUID) {
		return Skin;
	}

	public static ResourceLocation setDefaultSkin(int skin) {
		id = skin;
		Skin = DefaultSkins.defaultSkinsMap[skin].location;
		return Skin;
	}

	public static int getSkin() {
		return id;
	}

	/**
	 * Retrieves the type of skin that a player is using. The Alex model is slim
	 * while the Steve model is default.
	 */
	public static String getSkinType(EaglercraftUUID playerUUID) {
		return "default";
	}

	/**
	 * Checks if a players skin model is slim or the default. The Alex model is
	 * slime while the Steve model is default.
	 */
	private static boolean isSlimSkin(EaglercraftUUID playerUUID) {
		if (DefaultSkins.defaultSkinsMap[id].model == SkinModel.STEVE) {
			return false;
		} else {
			return true;
		}

	}
}