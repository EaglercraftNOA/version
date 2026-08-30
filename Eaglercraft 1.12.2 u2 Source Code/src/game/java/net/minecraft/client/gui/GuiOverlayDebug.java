package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import net.lax1dude.eaglercraft.Display;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;

public class GuiOverlayDebug extends Gui {
	private final Minecraft mc;
	private final FontRenderer fontRenderer;

	public GuiOverlayDebug(Minecraft mc) {
		this.mc = mc;
		this.fontRenderer = mc.fontRendererObj;
	}

	public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
		if (this.mc.gameSettings.showDebugInfo) {
			GlStateManager.pushMatrix();
			this.renderDebugInfoLeft();
			this.renderDebugInfoRight(scaledResolutionIn);
			GlStateManager.popMatrix();

			if (this.mc.gameSettings.showLagometer) {
				this.renderLagometer();
			}
		} else {
			int i = 2;

			if (this.mc.gameSettings.hudFps) {
				drawFPS(2, i);
				drawSingleplayerStats(scaledResolutionIn);
				i += 9;
			}

			if (this.mc.gameSettings.hudCoords) {
				drawXYZ(2, i);
			}
		}
	}
	
	private void drawFPS(int x, int y) {
		this.fontRenderer.drawStringWithShadow(this.mc.renderGlobal.getDebugInfoShort(), x, y, 0xFFFFFF);
	}

	private void drawXYZ(int x, int y) {
		Entity e = mc.getRenderViewEntity();
		BlockPos blockpos = new BlockPos(e.posX, e.getEntityBoundingBox().minY, e.posZ);
		this.fontRenderer.drawStringWithShadow(
				"x: " + blockpos.getX() + ", y: " + blockpos.getY() + ", z: " + blockpos.getZ(), x, y, 0xFFFFFF);
	}
	
	private int drawSingleplayerStats(ScaledResolution parScaledResolution) {
		if (mc.isDemo()) {
			return 13;
		}
		int i = 0;
		if (SingleplayerServerController.isWorldRunning()) {
			long tpsAge = SingleplayerServerController.getTPSAge();
			if (tpsAge < 20000l) {
				int color = tpsAge > 2000l ? 0x777777 : 0xFFFFFF;
				List<String> strs = SingleplayerServerController.getTPS();
				if (SingleplayerServerController.isRunningSingleThreadMode()) {
					strs = Lists.newArrayList(strs);
					strs.add("");
					strs.add(I18n.format("singleplayer.tpscounter.singleThreadMode"));
				}
				int l;
				boolean first = true;
				for (int j = 0, m = strs.size(); j < m; ++j) {
					String str = strs.get(j);
					if (!StringUtils.isAllEmpty(str)) {
						l = (int) (this.fontRenderer.getStringWidth(str) * (!first ? 0.5f : 1.0f));
						GlStateManager.pushMatrix();
						GlStateManager.translate(parScaledResolution.getScaledWidth() - 2 - l, i + 2, 0.0f);
						if (!first) {
							GlStateManager.scale(0.5f, 0.5f, 0.5f);
						}
						this.fontRenderer.drawStringWithShadow(str, 0, 0, color);
						GlStateManager.popMatrix();
						if (color == 0xFFFFFF) {
							color = 14737632;
						}
					}
					i += (int) (this.fontRenderer.FONT_HEIGHT * (!first ? 0.5f : 1.0f));
					first = false;
				}
			}
		}
		return i > 0 ? i + 2 : i;
	}

	protected void renderDebugInfoLeft() {
		List<String> list = this.call();
		list.add("");
		list.add("Debug: Pie [shift]: " + (this.mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden")
				+ " FPS [alt]: " + (this.mc.gameSettings.showLagometer ? "visible" : "hidden"));
		list.add("For help: press F3 + Q");

		for (int i = 0; i < list.size(); ++i) {
			String s = list.get(i);

			if (!Strings.isNullOrEmpty(s)) {
				int j = this.fontRenderer.FONT_HEIGHT;
				int k = this.fontRenderer.getStringWidth(s);
				int l = 2;
				int i1 = 2 + j * i;
				drawRect(1, i1 - 1, 2 + k + 1, i1 + j - 1, -1873784752);
				this.fontRenderer.drawString(s, 2, i1, 14737632);
			}
		}
	}

	protected void renderDebugInfoRight(ScaledResolution scaledRes) {
		List<String> list = this.getDebugInfoRight();

		for (int i = 0; i < list.size(); ++i) {
			String s = list.get(i);

			if (!Strings.isNullOrEmpty(s)) {
				int j = this.fontRenderer.FONT_HEIGHT;
				int k = this.fontRenderer.getStringWidth(s);
				int l = scaledRes.getScaledWidth() - 2 - k;
				int i1 = 2 + j * i;
				drawRect(l - 1, i1 - 1, l + k + 1, i1 + j - 1, -1873784752);
				this.fontRenderer.drawString(s, l, i1, 14737632);
			}
		}
	}

	@SuppressWarnings("incomplete-switch")
	protected List<String> call() {
		BlockPos blockpos = new BlockPos(this.mc.getRenderViewEntity().posX,
				this.mc.getRenderViewEntity().getEntityBoundingBox().minY, this.mc.getRenderViewEntity().posZ);

		if (this.mc.isReducedDebug()) {
			return Lists.newArrayList("Minecraft 1.12.2 (" + this.mc.getVersion() + "/Eaglercraft 1.12.2)",
					this.mc.debug, this.mc.renderGlobal.getDebugInfoRenders(),
					this.mc.renderGlobal.getDebugInfoEntities(),
					"P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(),
					this.mc.world.getProviderName(), "",
					HString.format("Chunk-relative: %d %d %d", new Object[] { Integer.valueOf(blockpos.getX() & 15),
							Integer.valueOf(blockpos.getY() & 15), Integer.valueOf(blockpos.getZ() & 15) }));
		} else {
			Entity entity = this.mc.getRenderViewEntity();
			EnumFacing enumfacing = entity.getHorizontalFacing();
			String s = "Invalid";

			switch (enumfacing) {
			case NORTH:
				s = "Towards negative Z";
				break;

			case SOUTH:
				s = "Towards positive Z";
				break;

			case WEST:
				s = "Towards negative X";
				break;

			case EAST:
				s = "Towards positive X";
			}

			List<String> list = Lists.newArrayList(
					"Minecraft 1.12.2 (" + this.mc.getVersion() + "/ Eaglercraft 1.12.2" + ")", this.mc.debug,
					this.mc.renderGlobal.getDebugInfoRenders(), this.mc.renderGlobal.getDebugInfoEntities(),
					"P: " + this.mc.effectRenderer.getStatistics() + ". T: " + this.mc.world.getDebugLoadedEntities(),
					this.mc.world.getProviderName(), "",
					HString.format("XYZ: %.3f / %.5f / %.3f",
							new Object[] { Double.valueOf(this.mc.getRenderViewEntity().posX),
									Double.valueOf(this.mc.getRenderViewEntity().getEntityBoundingBox().minY),
									Double.valueOf(this.mc.getRenderViewEntity().posZ) }),
					HString.format("Block: %d %d %d",
							new Object[] { Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()),
									Integer.valueOf(blockpos.getZ()) }),
					HString.format("Chunk: %d %d %d in %d %d %d",
							new Object[] { Integer.valueOf(blockpos.getX() & 15), Integer.valueOf(blockpos.getY() & 15),
									Integer.valueOf(blockpos.getZ() & 15), Integer.valueOf(blockpos.getX() >> 4),
									Integer.valueOf(blockpos.getY() >> 4), Integer.valueOf(blockpos.getZ() >> 4) }),
					HString.format("Facing: %s (%s) (%.1f / %.1f)",
							new Object[] { enumfacing, s, Float.valueOf(MathHelper.wrapDegrees(entity.rotationYaw)),
									Float.valueOf(MathHelper.wrapDegrees(entity.rotationPitch)) }));

			if (this.mc.world != null) {
				Chunk chunk = this.mc.world.getChunkFromBlockCoords(blockpos);

				if (this.mc.world.isBlockLoaded(blockpos) && blockpos.getY() >= 0 && blockpos.getY() < 256) {
					if (!chunk.isEmpty()) {
						list.add("Biome: " + chunk.getBiome(blockpos, this.mc.world.getBiomeProvider()).getBiomeName());
						list.add("Light: " + chunk.getLightSubtracted(blockpos, 0) + " ("
								+ chunk.getLightFor(EnumSkyBlock.SKY, blockpos) + " sky, "
								+ chunk.getLightFor(EnumSkyBlock.BLOCK, blockpos) + " block)");
						DifficultyInstance difficultyinstance = this.mc.world.getDifficultyForLocation(blockpos);
						list.add(HString.format("Local Difficulty: %.2f // %.2f (Day %d)",
								new Object[] { Float.valueOf(difficultyinstance.getAdditionalDifficulty()),
										Float.valueOf(difficultyinstance.getClampedAdditionalDifficulty()),
										Long.valueOf(this.mc.world.getWorldTime() / 24000L) }));
					} else {
						list.add("Waiting for chunk...");
					}
				} else {
					list.add("Outside of world...");
				}
			}

			if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK
					&& this.mc.objectMouseOver.getBlockPos() != null) {
				BlockPos blockpos1 = this.mc.objectMouseOver.getBlockPos();
				list.add(HString.format("Looking at: %d %d %d", new Object[] { Integer.valueOf(blockpos1.getX()),
						Integer.valueOf(blockpos1.getY()), Integer.valueOf(blockpos1.getZ()) }));
			}

			return list;
		}
	}

	protected <T extends Comparable<T>> List<String> getDebugInfoRight() {
		long i = EagRuntime.maxMemory();
		long j = EagRuntime.totalMemory();
		long k = EagRuntime.freeMemory();
		long l = j - k;
		List<String> list = Lists.newArrayList(
				HString.format("Java: %s %dbit", new Object[] { "TeaVM", Integer.valueOf(32) }),
				HString.format("Mem: % 2d%% %03d/%03dMB", Long.valueOf(l * 100L / i), Long.valueOf(bytesToMb(l)),
						Long.valueOf(bytesToMb(i))),
				HString.format("Allocated: % 2d%% %03dMB", Long.valueOf(j * 100L / i), Long.valueOf(bytesToMb(j))), "",
				HString.format("CPU: %s", new Object[] { "eaglercraft" }), "",
				HString.format("Display: %dx%d (%s)",
						new Object[] { Integer.valueOf(Display.getWidth()), Integer.valueOf(Display.getHeight()),
								EaglercraftGPU.glGetString(7936) }),
				EaglercraftGPU.glGetString(7937), EaglercraftGPU.glGetString(7938));

		if (this.mc.isReducedDebug()) {
			return list;
		} else {
			if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK
					&& this.mc.objectMouseOver.getBlockPos() != null) {
				BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
				IBlockState iblockstate = this.mc.world.getBlockState(blockpos);

				if (this.mc.world.getWorldType() != WorldType.DEBUG_WORLD) {
					iblockstate = iblockstate.getActualState(this.mc.world, blockpos);
				}

				list.add("");
				list.add(String.valueOf(Block.REGISTRY.getNameForObject(iblockstate.getBlock())));
				IProperty<T> iproperty;
				String s;

				for (UnmodifiableIterator unmodifiableiterator = iblockstate.getProperties().entrySet()
						.iterator(); unmodifiableiterator.hasNext(); list.add(iproperty.getName() + ": " + s)) {
					Entry<IProperty<?>, Comparable<?>> entry = (Entry) unmodifiableiterator.next();
					iproperty = (IProperty) entry.getKey();
					T t = (T) entry.getValue();
					s = iproperty.getName(t);

					if (Boolean.TRUE.equals(t)) {
						s = TextFormatting.GREEN + s;
					} else if (Boolean.FALSE.equals(t)) {
						s = TextFormatting.RED + s;
					}
				}
			}

			return list;
		}
	}

	private void renderLagometer() {
		GlStateManager.disableDepth();
		FrameTimer frametimer = this.mc.getFrameTimer();
		int i = frametimer.getLastIndex();
		int j = frametimer.getIndex();
		long[] along = frametimer.getFrames();
		ScaledResolution scaledresolution = this.mc.scaledResolution;
		int k = i;
		int l = 0;
		drawRect(0, scaledresolution.getScaledHeight() - 60, 240, scaledresolution.getScaledHeight(), -1873784752);

		while (k != j) {
			int i1 = frametimer.getLagometerValue(along[k], 30);
			int j1 = this.getFrameColor(MathHelper.clamp(i1, 0, 60), 0, 30, 60);
			this.drawVerticalLine(l, scaledresolution.getScaledHeight(), scaledresolution.getScaledHeight() - i1, j1);
			++l;
			k = frametimer.parseIndex(k + 1);
		}

		drawRect(1, scaledresolution.getScaledHeight() - 30 + 1, 14, scaledresolution.getScaledHeight() - 30 + 10,
				-1873784752);
		this.fontRenderer.drawString("60", 2, scaledresolution.getScaledHeight() - 30 + 2, 14737632);
		this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 30, -1);
		drawRect(1, scaledresolution.getScaledHeight() - 60 + 1, 14, scaledresolution.getScaledHeight() - 60 + 10,
				-1873784752);
		this.fontRenderer.drawString("30", 2, scaledresolution.getScaledHeight() - 60 + 2, 14737632);
		this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 60, -1);
		this.drawHorizontalLine(0, 239, scaledresolution.getScaledHeight() - 1, -1);
		this.drawVerticalLine(0, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);
		this.drawVerticalLine(239, scaledresolution.getScaledHeight() - 60, scaledresolution.getScaledHeight(), -1);

		if (this.mc.gameSettings.limitFramerate <= 120) {
			this.drawHorizontalLine(0, 239,
					scaledresolution.getScaledHeight() - 60 + this.mc.gameSettings.limitFramerate / 2, -16711681);
		}

		GlStateManager.enableDepth();
	}

	private int getFrameColor(int p_181552_1_, int p_181552_2_, int p_181552_3_, int p_181552_4_) {
		return p_181552_1_ < p_181552_3_ ? this.blendColors(-16711936, -256, (float) p_181552_1_ / (float) p_181552_3_)
				: this.blendColors(-256, -65536,
						(float) (p_181552_1_ - p_181552_3_) / (float) (p_181552_4_ - p_181552_3_));
	}

	private int blendColors(int p_181553_1_, int p_181553_2_, float p_181553_3_) {
		int i = p_181553_1_ >> 24 & 255;
		int j = p_181553_1_ >> 16 & 255;
		int k = p_181553_1_ >> 8 & 255;
		int l = p_181553_1_ & 255;
		int i1 = p_181553_2_ >> 24 & 255;
		int j1 = p_181553_2_ >> 16 & 255;
		int k1 = p_181553_2_ >> 8 & 255;
		int l1 = p_181553_2_ & 255;
		int i2 = MathHelper.clamp((int) ((float) i + (float) (i1 - i) * p_181553_3_), 0, 255);
		int j2 = MathHelper.clamp((int) ((float) j + (float) (j1 - j) * p_181553_3_), 0, 255);
		int k2 = MathHelper.clamp((int) ((float) k + (float) (k1 - k) * p_181553_3_), 0, 255);
		int l2 = MathHelper.clamp((int) ((float) l + (float) (l1 - l) * p_181553_3_), 0, 255);
		return i2 << 24 | j2 << 16 | k2 << 8 | l2;
	}

	private static long bytesToMb(long bytes) {
		return bytes / 1024L / 1024L;
	}
}
