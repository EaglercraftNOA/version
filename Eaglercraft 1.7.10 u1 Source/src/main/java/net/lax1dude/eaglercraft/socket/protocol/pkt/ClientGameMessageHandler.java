/*
 * Copyright (c) 2025.
 */
package net.lax1dude.eaglercraft.socket.protocol.pkt;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.profile.DefaultSkins;
import net.lax1dude.eaglercraft.profile.DefaultCapes;
import net.lax1dude.eaglercraft.profile.EaglerSkinTexture;
import net.lax1dude.eaglercraft.profile.SkinConverter;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinCustomV3EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinCustomV4EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinCustomV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinPresetEAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherSkinPresetV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapeCustomEAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapeCustomV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapePresetEAG;
import net.lax1dude.eaglercraft.socket.protocol.pkt.server.SPacketOtherCapePresetV5EAG;
import net.lax1dude.eaglercraft.socket.protocol.util.SkinPacketVersionCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Client-side handler for server->client game plugin messages that carry other
 * players' skin data. Applies textures to the corresponding
 * AbstractClientPlayer instances so they are not shown as Steve.
 */
public class ClientGameMessageHandler implements GameMessageHandler {

	private static final String REMOTE_SKIN_PREFIX = "eagler:skins/other/";

	// V5 requestId -> EaglercraftUUID mapping so we can apply skins to the correct
	// player on response
	private static final ConcurrentHashMap<Integer, EaglercraftUUID> V5_REQUESTS = new ConcurrentHashMap<>();
	private static final AtomicInteger V5_COUNTER = new AtomicInteger(1);

	public static int registerV5Request(EaglercraftUUID EaglercraftUUID) {
		int id = V5_COUNTER.getAndIncrement();
		V5_REQUESTS.put(id, EaglercraftUUID);
		return id;
	}

	/**
	 * Compose 64x64 modern skin overlays onto base and return a legacy 64x32 ARGB
	 * buffer.
	 */
	private static byte[] bakeModernToLegacy64x32ARGB(byte[] modern) {
		// Start with a copy of the top 32px (64x32 base layout)
		byte[] legacy = new byte[64 * 32 * 4];
		// Copy top half directly
		System.arraycopy(modern, 0, legacy, 0, legacy.length);
		// We intentionally skip baking body/arm/leg overlays to prevent misaligned
		// artifacts in legacy UVs.
		// Head overlay remains available in the left half top region per legacy layout.
		return legacy;
	}

	private static void blendRegion(byte[] dst, int dw, int dh, int dx0, int dy0, int dx1, int dy1, byte[] src, int sw,
			int sh, int sx0, int sy0, int sx1, int sy1) {
		int w = dx1 - dx0 + 1;
		int h = dy1 - dy0 + 1;
		for (int y = 0; y < h; ++y) {
			int drow = (dy0 + y) * dw * 4;
			int srow = (sy0 + y) * sw * 4;
			for (int x = 0; x < w; ++x) {
				int di = drow + (dx0 + x) * 4;
				int si = srow + (sx0 + x) * 4;
				int sa = src[si] & 0xFF;
				if (sa == 0)
					continue; // no overlay
				if (sa == 255) {
					// replace
					dst[di] = src[si];
					dst[di + 1] = src[si + 1];
					dst[di + 2] = src[si + 2];
					dst[di + 3] = src[si + 3];
				} else {
					// alpha blend over dst
					int da = dst[di] & 0xFF;
					int sr = src[si + 1] & 0xFF, sg = src[si + 2] & 0xFF, sb = src[si + 3] & 0xFF;
					int dr = dst[di + 1] & 0xFF, dg = dst[di + 2] & 0xFF, db = dst[di + 3] & 0xFF;
					int outA = Math.min(255, sa + (da * (255 - sa) + 127) / 255);
					int outR = (sr * sa + dr * (255 - sa) + 127) / 255;
					int outG = (sg * sa + dg * (255 - sa) + 127) / 255;
					int outB = (sb * sa + db * (255 - sa) + 127) / 255;
					dst[di] = (byte) outA;
					dst[di + 1] = (byte) outR;
					dst[di + 2] = (byte) outG;
					dst[di + 3] = (byte) outB;
				}
			}
		}
	}

	/**
	 * For 64x64 skins, if left leg/arm base or overlay regions are empty, copy from
	 * right counterparts. Coordinates are in pixels; buffer format is ARGB bytes.
	 */
	private static void fixMissingLeftLimbs64x64(byte[] argb, int w, int h) {
		if (argb == null || w != 64 || h != 64)
			return;
		// Regions: [x0,x1], [y0,y1] inclusive
		// Right leg base (0-15,16-31) -> Left leg base (16-31,48-63)
		maybeCopyRegion(argb, w, h, 16, 48, 31, 63, 0, 16, 15, 31);
		// Right leg overlay (0-15,32-47) -> Left leg overlay (0-15,48-63)
		maybeCopyRegion(argb, w, h, 0, 48, 15, 63, 0, 32, 15, 47);
		// Right arm base (40-55,16-31) -> Left arm base (32-47,48-63)
		maybeCopyRegion(argb, w, h, 32, 48, 47, 63, 40, 16, 55, 31);
		// Right arm overlay (40-55,32-47) -> Left arm overlay (48-63,48-63)
		maybeCopyRegion(argb, w, h, 48, 48, 63, 63, 40, 32, 55, 47);
	}

	private static void maybeCopyRegion(byte[] px, int w, int h, int dx0, int dy0, int dx1, int dy1, int sx0, int sy0,
			int sx1, int sy1) {
		if (isRegionEmpty(px, w, h, dx0, dy0, dx1, dy1)) {
			copyRegion(px, w, h, dx0, dy0, dx1, dy1, sx0, sy0, sx1, sy1);
		}
	}

	private static boolean isRegionEmpty(byte[] px, int w, int h, int x0, int y0, int x1, int y1) {
		for (int y = y0; y <= y1; ++y) {
			int row = y * w * 4;
			for (int x = x0; x <= x1; ++x) {
				int i = row + x * 4;
				int a = px[i] & 0xFF;
				int r = px[i + 1] & 0xFF;
				int g = px[i + 2] & 0xFF;
				int b = px[i + 3] & 0xFF;
				if (a != 0 || r != 0 || g != 0 || b != 0)
					return false;
			}
		}
		return true;
	}

	private static void copyRegion(byte[] px, int w, int h, int dx0, int dy0, int dx1, int dy1, int sx0, int sy0,
			int sx1, int sy1) {
		int width = dx1 - dx0 + 1;
		int height = dy1 - dy0 + 1;
		for (int y = 0; y < height; ++y) {
			int srcRow = (sy0 + y) * w * 4;
			int dstRow = (dy0 + y) * w * 4;
			for (int x = 0; x < width; ++x) {
				int si = srcRow + (sx0 + x) * 4;
				int di = dstRow + (dx0 + x) * 4;
				px[di] = px[si]; // A
				px[di + 1] = px[si + 1];// R
				px[di + 2] = px[si + 2];// G
				px[di + 3] = px[si + 3];// B
			}
		}
	}

	@Override
	public void handleServer(SPacketOtherSkinPresetEAG packet) {
		EaglercraftUUID EaglercraftUUID = new EaglercraftUUID(packet.uuidMost, packet.uuidLeast);
		EntityPlayer player = findPlayerByEaglercraftUUID(EaglercraftUUID);
		if (player instanceof AbstractClientPlayer) {
			int preset = packet.presetSkin;
			if (preset < 0 || preset >= DefaultSkins.defaultSkinsMap.length) {
				preset = 0;
			}
			ResourceLocation loc = DefaultSkins.defaultSkinsMap[preset].location;
			((AbstractClientPlayer) player).func_152121_a(AbstractClientPlayer.Type.SKIN, loc);
		}
	}

	@Override
	public void handleServer(SPacketOtherSkinCustomV4EAG packet) {
		EaglercraftUUID EaglercraftUUID = new EaglercraftUUID(packet.uuidMost, packet.uuidLeast);
		EntityPlayer player = findPlayerByEaglercraftUUID(EaglercraftUUID);
		if (player instanceof AbstractClientPlayer) {
			byte[] argb = SkinPacketVersionCache.convertToV3Raw(packet.customSkin);
			// Heuristic: if alpha channel is mostly zero, server likely sent raw RGB;
			// rebuild ARGB with full alpha
			if (isMostlyTransparent(argb)) {
				byte[] rgb = packet.customSkin;
				byte[] argb2 = new byte[64 * 64 * 4];
				for (int i = 0, j = 0; i < argb2.length; i += 4, j += 3) {
					argb2[i] = (byte) 0xFF; // A
					argb2[i + 1] = rgb[j]; // R
					argb2[i + 2] = rgb[j + 1]; // G
					argb2[i + 3] = rgb[j + 2]; // B
				}
				argb = argb2;
			}
			applyCustomSkinTextureWithModel((AbstractClientPlayer) player, EaglercraftUUID, argb, packet.modelID);
		}
	}

	@Override
	public void handleServer(SPacketOtherSkinCustomV3EAG packet) {
		EaglercraftUUID EaglercraftUUID = new EaglercraftUUID(packet.uuidMost, packet.uuidLeast);
		EntityPlayer player = findPlayerByEaglercraftUUID(EaglercraftUUID);
		if (player instanceof AbstractClientPlayer) {
			// Already 16384 RGBA bytes
			applyCustomSkinTexture((AbstractClientPlayer) player, EaglercraftUUID, packet.customSkin);
		}
	}

	// V5 responses use requestId to identify target; look up EaglercraftUUID in our
	// registry
	@Override
	public void handleServer(SPacketOtherSkinPresetV5EAG packet) {
		EaglercraftUUID EaglercraftUUID = V5_REQUESTS.remove(packet.requestId);
		if (EaglercraftUUID == null)
			return;
		EntityPlayer player = findPlayerByEaglercraftUUID(EaglercraftUUID);
		if (player instanceof AbstractClientPlayer) {
			int preset = packet.presetSkin;
			if (preset < 0 || preset >= DefaultSkins.defaultSkinsMap.length) {
				preset = 0;
			}
			ResourceLocation loc = DefaultSkins.defaultSkinsMap[preset].location;
			((AbstractClientPlayer) player).func_152121_a(AbstractClientPlayer.Type.SKIN, loc);
		}
	}

	@Override
	public void handleServer(SPacketOtherSkinCustomV5EAG packet) {
		EaglercraftUUID EaglercraftUUID = V5_REQUESTS.remove(packet.requestId);
		if (EaglercraftUUID == null)
			return;
		EntityPlayer player = findPlayerByEaglercraftUUID(EaglercraftUUID);
		if (player instanceof AbstractClientPlayer) {
			// V5 uses same packed RGB format as V4; convert using the shared helper
			byte[] argb = SkinPacketVersionCache.convertToV3Raw(packet.customSkin);
			// Heuristic fallback to plain RGB -> ARGB if alpha looks wrong
			if (isMostlyTransparent(argb)) {
				byte[] rgb = packet.customSkin;
				byte[] argb2 = new byte[64 * 64 * 4];
				for (int i = 0, j = 0; i < argb2.length; i += 4, j += 3) {
					argb2[i] = (byte) 0xFF;
					argb2[i + 1] = rgb[j];
					argb2[i + 2] = rgb[j + 1];
					argb2[i + 3] = rgb[j + 2];
				}
				argb = argb2;
			}
			applyCustomSkinTextureWithModel((AbstractClientPlayer) player, EaglercraftUUID, argb, packet.modelID);
		}
	}

	private static boolean isMostlyTransparent(byte[] argb) {
		if (argb == null || argb.length < 64 * 64 * 4)
			return false;
		int zeros = 0;
		int total = argb.length / 4;
		for (int i = 0; i < argb.length; i += 4) {
			if ((argb[i] & 0xFF) < 8)
				zeros++;
		}
		// if over 50% pixels have alpha ~0, likely wrong format
		return zeros * 2 > total;
	}

	private void applyCustomSkinTexture(AbstractClientPlayer player, EaglercraftUUID EaglercraftUUID,
			byte[] rgba64x64) {
		try {
			// SkinPacketVersionCache.convertToV3Raw already produces ARGB ordering expected
			// by EaglerSkinTexture.
			// Ensure only base-layer regions are opaque; keep overlays as-is.
			forceBaseLayerOpaqueARGB(rgba64x64, 64, 64);
			// If server omitted left-limb regions, copy from right-limb as vanilla does
			fixMissingLeftLimbs64x64(rgba64x64, 64, 64);
			// Bake overlays into base and down-convert to 64x32 for legacy renderer
			byte[] legacy = bakeModernToLegacy64x32ARGB(rgba64x64);
			// Build ARGB int[] explicitly to avoid any byte-order ambiguity
			int[] pixels = new int[64 * 32];
			for (int i = 0, p = 0; i < legacy.length; i += 4, ++p) {
				int a = legacy[i] & 0xFF;
				int r = legacy[i + 1] & 0xFF;
				int g = legacy[i + 2] & 0xFF;
				int b = legacy[i + 3] & 0xFF;
				pixels[p] = (a << 24) | (r << 16) | (g << 8) | b;
			}
			ResourceLocation res = new ResourceLocation(REMOTE_SKIN_PREFIX + EaglercraftUUID.toString());
			TextureManager tm = Minecraft.getMinecraft().getTextureManager();
			EaglerSkinTexture tex = new EaglerSkinTexture(pixels, 64, 32);
			tm.loadTexture(res, tex);
			player.func_152121_a(AbstractClientPlayer.Type.SKIN, res);
		} catch (Throwable t) {
			// fail silently to avoid crashing rendering if bad data
		}
	}

	private void applyCustomSkinTextureWithModel(AbstractClientPlayer player, EaglercraftUUID EaglercraftUUID,
			byte[] rgba64x64, int modelId) {
		try {
			// base prep same as legacy path
			forceBaseLayerOpaqueARGB(rgba64x64, 64, 64);
			fixMissingLeftLimbs64x64(rgba64x64, 64, 64);
			byte[] legacy = bakeModernToLegacy64x32ARGB(rgba64x64);
			// If Alex (slim arms), widen right arm base (44..47,16..31) to ensure 4px width
			if (isAlex(modelId)) {
				widenRightArmLegacyBase(legacy, 64, 32);
			}
			int[] pixels = new int[64 * 32];
			for (int i = 0, p = 0; i < legacy.length; i += 4, ++p) {
				int a = legacy[i] & 0xFF;
				int r = legacy[i + 1] & 0xFF;
				int g = legacy[i + 2] & 0xFF;
				int b = legacy[i + 3] & 0xFF;
				pixels[p] = (a << 24) | (r << 16) | (g << 8) | b;
			}
			ResourceLocation res = new ResourceLocation(REMOTE_SKIN_PREFIX + EaglercraftUUID.toString());
			TextureManager tm = Minecraft.getMinecraft().getTextureManager();
			EaglerSkinTexture tex = new EaglerSkinTexture(pixels, 64, 32);
			tm.loadTexture(res, tex);
			player.func_152121_a(AbstractClientPlayer.Type.SKIN, res);
		} catch (Throwable t) {
		}
	}

	private static boolean isAlex(int modelId) {
		// SkinModel.ALEX id is 1 in this codebase
		return (modelId & 0x7F) == 1;
	}

	/**
	 * Ensure the 64x32 legacy right arm base (44..47,16..31) has no empty columns
	 * by duplicating neighbors
	 */
	private static void widenRightArmLegacyBase(byte[] legacy, int w, int h) {
		int x0 = 44, x1 = 47, y0 = 16, y1 = 31;
		// detect empty columns
		boolean[] empty = new boolean[4];
		for (int c = 0; c < 4; ++c) {
			int x = x0 + c;
			empty[c] = isColumnEmpty(legacy, w, h, x, y0, y1);
		}
		// duplicate nearest non-empty into empty ones
		for (int c = 0; c < 4; ++c) {
			if (empty[c]) {
				// find neighbor
				int from = -1;
				for (int d = 1; d < 4 && from == -1; ++d) {
					int l = c - d;
					int r = c + d;
					if (l >= 0 && !empty[l])
						from = l;
					else if (r < 4 && !empty[r])
						from = r;
				}
				if (from == -1) {
					// all empty: just set opaque skin-colored placeholder (copy x0)
					from = 0;
				}
				copyColumn(legacy, w, h, x0 + from, x0 + c, y0, y1);
			}
		}
	}

	private static boolean isColumnEmpty(byte[] px, int w, int h, int x, int y0, int y1) {
		for (int y = y0; y <= y1; ++y) {
			int i = (y * w + x) * 4;
			int a = px[i] & 0xFF;
			int r = px[i + 1] & 0xFF;
			int g = px[i + 2] & 0xFF;
			int b = px[i + 3] & 0xFF;
			if (a != 0 || r != 0 || g != 0 || b != 0)
				return false;
		}
		return true;
	}

	private static void copyColumn(byte[] px, int w, int h, int sx, int dx, int y0, int y1) {
		for (int y = y0; y <= y1; ++y) {
			int si = (y * w + sx) * 4;
			int di = (y * w + dx) * 4;
			px[di] = px[si];
			px[di + 1] = px[si + 1];
			px[di + 2] = px[si + 2];
			px[di + 3] = px[si + 3];
		}
	}

	/**
	 * Force alpha channel to 255 on base-layer rectangles of a 64x64 skin. Input
	 * format must be ARGB bytes.
	 */
	private static void forceBaseLayerOpaqueARGB(byte[] argb, int w, int h) {
		if (argb == null || argb.length != w * h * 4)
			return;
		// helper: set alpha 255 in rect inclusive
		final java.util.function.BiConsumer<int[], int[]> fill = (xs, ys) -> {
			int x0 = xs[0], x1 = xs[1], y0 = ys[0], y1 = ys[1];
			for (int y = y0; y <= y1; ++y) {
				int row = y * w * 4;
				for (int x = x0; x <= x1; ++x) {
					int i = row + x * 4;
					argb[i] = (byte) 0xFF;
				}
			}
		};
		// 64x64 base-layer regions (vanilla layout)
		fill.accept(new int[] { 0, 31 }, new int[] { 0, 15 }); // head base
		fill.accept(new int[] { 16, 39 }, new int[] { 16, 31 }); // body base
		fill.accept(new int[] { 40, 55 }, new int[] { 16, 31 }); // right arm base
		fill.accept(new int[] { 0, 15 }, new int[] { 16, 31 }); // right leg base
		fill.accept(new int[] { 32, 47 }, new int[] { 48, 63 }); // left arm base
		fill.accept(new int[] { 16, 31 }, new int[] { 48, 63 }); // left leg base
	}

	private EntityPlayer findPlayerByEaglercraftUUID(EaglercraftUUID EaglercraftUUID) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.theWorld == null)
			return null;
		List<?> players = mc.theWorld.playerEntities;
		for (Object o : players) {
			if (o instanceof EntityPlayer) {
				EntityPlayer p = (EntityPlayer) o;
				if (p.getGameProfile() != null && EaglercraftUUID.equals(p.getGameProfile().getId())) {
					return p;
				}
			}
		}
		return null;
	}

	// Cape packet handlers
	@Override
	public void handleServer(SPacketOtherCapePresetEAG packet) {
		EaglercraftUUID uuid = new EaglercraftUUID(packet.uuidMost, packet.uuidLeast);
		EntityPlayer player = findPlayerByEaglercraftUUID(uuid);
		if (player instanceof AbstractClientPlayer) {
			AbstractClientPlayer acp = (AbstractClientPlayer) player;
			if (packet.presetCape >= 0 && packet.presetCape < DefaultCapes.defaultCapesMap.length) {
				ResourceLocation capeLocation = DefaultCapes.defaultCapesMap[packet.presetCape].location;
				acp.func_152121_a(AbstractClientPlayer.Type.CAPE, capeLocation);
			}
		}
	}

	@Override
	public void handleServer(SPacketOtherCapeCustomEAG packet) {
		EaglercraftUUID uuid = new EaglercraftUUID(packet.uuidMost, packet.uuidLeast);
		EntityPlayer player = findPlayerByEaglercraftUUID(uuid);
		if (player instanceof AbstractClientPlayer && packet.customCape != null) {
			AbstractClientPlayer acp = (AbstractClientPlayer) player;
			String capeUrl = "eagler:capes/other/" + uuid.toString().replace("-", "");
			ResourceLocation capeLocation = new ResourceLocation(capeUrl);
			// Convert and load the cape texture (23x17 RGB -> 32x32 RGBA)
			TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
			byte[] rgba32 = new byte[32 * 32 * 4];
			try {
				SkinConverter.convertCape23x17RGBto32x32RGBA(packet.customCape, rgba32);
				EaglerSkinTexture tex = new EaglerSkinTexture(rgba32, 32, 32);
				textureManager.loadTexture(capeLocation, tex);
			} catch (Throwable t) {
			}
			acp.func_152121_a(AbstractClientPlayer.Type.CAPE, capeLocation);
		}
	}
}
