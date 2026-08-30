package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import java.io.File;
import net.lax1dude.eaglercraft.profile.EaglerProfile;
import net.lax1dude.eaglercraft.profile.SkinModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public abstract class AbstractClientPlayer extends EntityPlayer implements SkinManager.SkinAvailableCallback {
	public static final ResourceLocation locationStevePng = new ResourceLocation("textures/entity/steve.png");
	private ResourceLocation locationSkin;
	private ResourceLocation locationCape;
	private static final String __OBFID = "CL_00000935";

	public AbstractClientPlayer(World p_i45074_1_, GameProfile p_i45074_2_) {
		super(p_i45074_1_, p_i45074_2_);
		String var3 = this.getCommandSenderName();

		if (!var3.isEmpty()) {
			SkinManager var4 = Minecraft.getMinecraft().func_152342_ad();
			var4.func_152790_a(p_i45074_2_, this, true);
		}
	}

	public boolean func_152122_n() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.thePlayer == this) {
			ResourceLocation activeCape = EaglerProfile.getActiveCapeResourceLocation();
			return activeCape != null;
		}
		return this.locationCape != null;
	}

	public boolean func_152123_o() {
		return this.locationSkin != null;
	}

	public ResourceLocation getLocationSkin() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.thePlayer == this) {
			ResourceLocation rl = EaglerProfile.getActiveSkinResourceLocation();
			return rl != null ? rl : locationStevePng;
		}
		return this.locationSkin == null ? locationStevePng : this.locationSkin;
	}

	public ResourceLocation getLocationCape() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.thePlayer == this) {
			ResourceLocation rl = EaglerProfile.getActiveCapeResourceLocation();
			return rl;
		}
		return this.locationCape;
	}

	public SkinModel getEaglerSkinModel() {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.thePlayer == this) {
			return EaglerProfile.getActiveSkinModel();
		}
		return SkinModel.STEVE;
	}

	

	public static ResourceLocation getLocationSkin(String p_110311_0_) {
		return new ResourceLocation("skins/" + StringUtils.stripControlCodes(p_110311_0_));
	}

	public void func_152121_a(Type p_152121_1_, ResourceLocation p_152121_2_) {
		switch (AbstractClientPlayer.SwitchType.field_152630_a[p_152121_1_.ordinal()]) {
		case 1:
			this.locationSkin = p_152121_2_;
			break;

		case 2:
			this.locationCape = p_152121_2_;
		}
	}

	static final class SwitchType {
		static final int[] field_152630_a = new int[Type.values().length];
		private static final String __OBFID = "CL_00001832";

		static {
			try {
				field_152630_a[Type.SKIN.ordinal()] = 1;
			} catch (NoSuchFieldError var2) {
				;
			}

			try {
				field_152630_a[Type.CAPE.ordinal()] = 2;
			} catch (NoSuchFieldError var1) {
				;
			}
		}
	}

	public enum Type {
		SKIN, CAPE
	}
}
