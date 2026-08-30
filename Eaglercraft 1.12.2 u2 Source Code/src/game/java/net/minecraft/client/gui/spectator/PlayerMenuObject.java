package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;

import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class PlayerMenuObject implements ISpectatorMenuObject {
	private final GameProfile profile;

	public PlayerMenuObject(GameProfile profileIn) {
		this.profile = profileIn;
	}

	public void selectItem(SpectatorMenu menu) {
		Minecraft.getMinecraft().getConnection().sendPacket(new CPacketSpectate(this.profile.getId()));
	}

	public ITextComponent getSpectatorName() {
		return new TextComponentString(this.profile.getName());
	}

	public void renderIcon(float p_178663_1_, int alpha) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(
				Minecraft.getMinecraft().getNetHandler().getSkinCache().getSkin(profile).getResourceLocation());
		GlStateManager.color(1.0F, 1.0F, 1.0F, (float) alpha / 255.0F);
		Gui.drawScaledCustomSizeModalRect(2, 2, 8.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
		Gui.drawScaledCustomSizeModalRect(2, 2, 40.0F, 8.0F, 8, 8, 12, 12, 64.0F, 64.0F);
	}

	public boolean isEnabled() {
		return true;
	}
}
