package net.minecraft.client.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.resources.MinecraftProfileTexture;

public class SkinManager {
	public static final ResourceLocation field_152793_a = new ResourceLocation("textures/entity/steve.png");
	private final TextureManager field_152795_c;
	private final VFile2 field_152796_d;
	private final Map<GameProfile, Map<MinecraftProfileTexture.Type, MinecraftProfileTexture>> field_152798_f = new HashMap<>();
	private static final String __OBFID = "CL_00001830";

	public SkinManager(TextureManager p_i1044_1_, VFile2 p_i1044_2_) {
		this.field_152795_c = p_i1044_1_;
		this.field_152796_d = p_i1044_2_;
	}

	public ResourceLocation func_152792_a(MinecraftProfileTexture p_152792_1_,
			MinecraftProfileTexture.Type p_152792_2_) {
		return this.func_152789_a(p_152792_1_, p_152792_2_, (SkinManager.SkinAvailableCallback) null);
	}

	public ResourceLocation func_152789_a(MinecraftProfileTexture p_152789_1_,
			final MinecraftProfileTexture.Type p_152789_2_, final SkinManager.SkinAvailableCallback p_152789_3_) {
		final ResourceLocation var4 = new ResourceLocation("skins/" + p_152789_1_.getHash());
		ITextureObject var5 = this.field_152795_c.getTexture(var4);

		if (var5 != null) {
			if (p_152789_3_ != null) {
				p_152789_3_.func_152121_a(p_152789_2_, var4);
			}
		} else {
			VFile2 var6 = new VFile2(this.field_152796_d, p_152789_1_.getHash().substring(0, 2));
			VFile2 var7 = new VFile2(var6, p_152789_1_.getHash());
			final ImageBufferDownload var8 = p_152789_2_ == MinecraftProfileTexture.Type.SKIN
					? new ImageBufferDownload()
					: null;

		}

		return var4;
	}

	public void func_152790_a(final GameProfile p_152790_1_, final SkinManager.SkinAvailableCallback p_152790_2_,
			final boolean p_152790_3_) {

	}

	public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> func_152788_a(GameProfile p_152788_1_) {
		return field_152798_f.get(p_152788_1_);
	}

	public interface SkinAvailableCallback {
		void func_152121_a(MinecraftProfileTexture.Type p_152121_1_, ResourceLocation p_152121_2_);
	}
}
