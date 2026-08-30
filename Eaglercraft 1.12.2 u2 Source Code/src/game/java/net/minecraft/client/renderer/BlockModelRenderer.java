package net.minecraft.client.renderer;

import java.util.BitSet;
import java.util.List;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;

public class BlockModelRenderer {
	private final BlockColors blockColors;

	public BlockModelRenderer(BlockColors blockColorsIn) {
		this.blockColors = blockColorsIn;
	}

	public boolean renderModel(IBlockAccess blockAccessIn, IBakedModel modelIn, IBlockState blockStateIn,
			BlockPos blockPosIn, WorldRenderer buffer, boolean checkSides) {
		return this.renderModel(blockAccessIn, modelIn, blockStateIn, blockPosIn, buffer, checkSides,
				MathHelper.getPositionRandom(blockPosIn));
	}

	public boolean renderModel(IBlockAccess worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn,
			WorldRenderer buffer, boolean checkSides, long rand) {
		boolean flag = Minecraft.isAmbientOcclusionEnabled() && stateIn.getLightValue() == 0
				&& modelIn.isAmbientOcclusion();

		try {
			return flag ? this.renderModelSmooth(worldIn, modelIn, stateIn, posIn, buffer, checkSides, rand)
					: this.renderModelFlat(worldIn, modelIn, stateIn, posIn, buffer, checkSides, rand);
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block model");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Block model being tesselated");
			CrashReportCategory.addBlockInfo(crashreportcategory, posIn, stateIn);
			crashreportcategory.addCrashSection("Using AO", Boolean.valueOf(flag));
			throw new ReportedException(crashreport);
		}
	}

	public boolean renderModelSmooth(IBlockAccess worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn,
			WorldRenderer buffer, boolean checkSides, long rand) {
		boolean flag = false;
		float[] afloat = new float[EnumFacing._VALUES.length * 2];
		BitSet bitset = new BitSet(3);
		BlockModelRenderer.AmbientOcclusionFace blockmodelrenderer$ambientocclusionface = new BlockModelRenderer.AmbientOcclusionFace();

		EnumFacing[] values = EnumFacing._VALUES;
		for (int i = 0; i < values.length; i++) {
			EnumFacing enumfacing = values[i];
			List<BakedQuad> list = modelIn.getQuads(stateIn, enumfacing, rand);

			if (!list.isEmpty() && (!checkSides || stateIn.shouldSideBeRendered(worldIn, posIn, enumfacing))) {
				this.renderQuadsSmooth(worldIn, stateIn, posIn, buffer, list, afloat, bitset,
						blockmodelrenderer$ambientocclusionface);
				flag = true;
			}
		}

		List<BakedQuad> list1 = modelIn.getQuads(stateIn, (EnumFacing) null, rand);

		if (!list1.isEmpty()) {
			this.renderQuadsSmooth(worldIn, stateIn, posIn, buffer, list1, afloat, bitset,
					blockmodelrenderer$ambientocclusionface);
			flag = true;
		}

		return flag;
	}

	public boolean renderModelFlat(IBlockAccess worldIn, IBakedModel modelIn, IBlockState stateIn, BlockPos posIn,
			WorldRenderer buffer, boolean checkSides, long rand) {
		boolean flag = false;
		BitSet bitset = new BitSet(3);

		EnumFacing[] values = EnumFacing._VALUES;
		for (int j = 0; j < values.length; j++) {
			EnumFacing enumfacing = values[j];
			List<BakedQuad> list = modelIn.getQuads(stateIn, enumfacing, rand);

			if (!list.isEmpty() && (!checkSides || stateIn.shouldSideBeRendered(worldIn, posIn, enumfacing))) {
				int i = stateIn.getPackedLightmapCoords(worldIn, posIn.offset(enumfacing));
				this.renderQuadsFlat(worldIn, stateIn, posIn, i, false, buffer, list, bitset);
				flag = true;
			}
		}

		List<BakedQuad> list1 = modelIn.getQuads(stateIn, (EnumFacing) null, rand);

		if (!list1.isEmpty()) {
			this.renderQuadsFlat(worldIn, stateIn, posIn, -1, true, buffer, list1, bitset);
			flag = true;
		}

		return flag;
	}

	private void renderQuadsSmooth(IBlockAccess blockAccessIn, IBlockState stateIn, BlockPos posIn,
			WorldRenderer buffer, List<BakedQuad> list, float[] quadBounds, BitSet bitSet,
			BlockModelRenderer.AmbientOcclusionFace aoFace) {
		Vec3d vec3d = stateIn.func_191059_e(blockAccessIn, posIn);
		double d0 = (double) posIn.getX() + vec3d.xCoord;
		double d1 = (double) posIn.getY() + vec3d.yCoord;
		double d2 = (double) posIn.getZ() + vec3d.zCoord;
		int i = 0;

		for (int j = list.size(); i < j; ++i) {
			BakedQuad bakedquad = list.get(i);
			this.fillQuadBounds(stateIn, bakedquad.getVertexData(), bakedquad.getFace(), quadBounds, bitSet);
			aoFace.updateVertexBrightness(blockAccessIn, stateIn.getBlock(), posIn, bakedquad.getFace(), quadBounds, bitSet);
			buffer.addVertexData(bakedquad.getVertexData());
			buffer.putBrightness4(aoFace.vertexBrightness[0], aoFace.vertexBrightness[1], aoFace.vertexBrightness[2],
					aoFace.vertexBrightness[3]);

			if (bakedquad.hasTintIndex()) {
				int k = this.blockColors.colorMultiplier(stateIn, blockAccessIn, posIn, bakedquad.getTintIndex());

				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}

				float f = (float) (k >> 16 & 255) / 255.0F;
				float f1 = (float) (k >> 8 & 255) / 255.0F;
				float f2 = (float) (k & 255) / 255.0F;
				buffer.putColorMultiplier(aoFace.vertexColorMultiplier[0] * f, aoFace.vertexColorMultiplier[0] * f1,
						aoFace.vertexColorMultiplier[0] * f2, 4);
				buffer.putColorMultiplier(aoFace.vertexColorMultiplier[1] * f, aoFace.vertexColorMultiplier[1] * f1,
						aoFace.vertexColorMultiplier[1] * f2, 3);
				buffer.putColorMultiplier(aoFace.vertexColorMultiplier[2] * f, aoFace.vertexColorMultiplier[2] * f1,
						aoFace.vertexColorMultiplier[2] * f2, 2);
				buffer.putColorMultiplier(aoFace.vertexColorMultiplier[3] * f, aoFace.vertexColorMultiplier[3] * f1,
						aoFace.vertexColorMultiplier[3] * f2, 1);
			} else {
				buffer.putColorMultiplier(aoFace.vertexColorMultiplier[0], aoFace.vertexColorMultiplier[0],
						aoFace.vertexColorMultiplier[0], 4);
				buffer.putColorMultiplier(aoFace.vertexColorMultiplier[1], aoFace.vertexColorMultiplier[1],
						aoFace.vertexColorMultiplier[1], 3);
				buffer.putColorMultiplier(aoFace.vertexColorMultiplier[2], aoFace.vertexColorMultiplier[2],
						aoFace.vertexColorMultiplier[2], 2);
				buffer.putColorMultiplier(aoFace.vertexColorMultiplier[3], aoFace.vertexColorMultiplier[3],
						aoFace.vertexColorMultiplier[3], 1);
			}

			buffer.putPosition(d0, d1, d2);
		}
	}

	private void fillQuadBounds(IBlockState stateIn, int[] vertexData, EnumFacing face, @Nullable float[] quadBounds,
			BitSet boundsFlags) {
		float f = 32.0F;
		float f1 = 32.0F;
		float f2 = 32.0F;
		float f3 = -32.0F;
		float f4 = -32.0F;
		float f5 = -32.0F;

		for (int i = 0; i < 4; ++i) {
			float f6 = Float.intBitsToFloat(vertexData[i * 7]);
			float f7 = Float.intBitsToFloat(vertexData[i * 7 + 1]);
			float f8 = Float.intBitsToFloat(vertexData[i * 7 + 2]);
			f = Math.min(f, f6);
			f1 = Math.min(f1, f7);
			f2 = Math.min(f2, f8);
			f3 = Math.max(f3, f6);
			f4 = Math.max(f4, f7);
			f5 = Math.max(f5, f8);
		}

		if (quadBounds != null) {
			quadBounds[EnumFacing.WEST.getIndex()] = f;
			quadBounds[EnumFacing.EAST.getIndex()] = f3;
			quadBounds[EnumFacing.DOWN.getIndex()] = f1;
			quadBounds[EnumFacing.UP.getIndex()] = f4;
			quadBounds[EnumFacing.NORTH.getIndex()] = f2;
			quadBounds[EnumFacing.SOUTH.getIndex()] = f5;
			int j = EnumFacing._VALUES.length;
			quadBounds[EnumFacing.WEST.getIndex() + j] = 1.0F - f;
			quadBounds[EnumFacing.EAST.getIndex() + j] = 1.0F - f3;
			quadBounds[EnumFacing.DOWN.getIndex() + j] = 1.0F - f1;
			quadBounds[EnumFacing.UP.getIndex() + j] = 1.0F - f4;
			quadBounds[EnumFacing.NORTH.getIndex() + j] = 1.0F - f2;
			quadBounds[EnumFacing.SOUTH.getIndex() + j] = 1.0F - f5;
		}

		float f9 = 1.0E-4F;
		float f10 = 0.9999F;

		switch (face) {
		case DOWN:
			boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, (f1 < 1.0E-4F || stateIn.isFullCube()) && f1 == f4);
			break;

		case UP:
			boundsFlags.set(1, f >= 1.0E-4F || f2 >= 1.0E-4F || f3 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, (f4 > 0.9999F || stateIn.isFullCube()) && f1 == f4);
			break;

		case NORTH:
			boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
			boundsFlags.set(0, (f2 < 1.0E-4F || stateIn.isFullCube()) && f2 == f5);
			break;

		case SOUTH:
			boundsFlags.set(1, f >= 1.0E-4F || f1 >= 1.0E-4F || f3 <= 0.9999F || f4 <= 0.9999F);
			boundsFlags.set(0, (f5 > 0.9999F || stateIn.isFullCube()) && f2 == f5);
			break;

		case WEST:
			boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, (f < 1.0E-4F || stateIn.isFullCube()) && f == f3);
			break;

		case EAST:
			boundsFlags.set(1, f1 >= 1.0E-4F || f2 >= 1.0E-4F || f4 <= 0.9999F || f5 <= 0.9999F);
			boundsFlags.set(0, (f3 > 0.9999F || stateIn.isFullCube()) && f == f3);
		}
	}

	private void renderQuadsFlat(IBlockAccess blockAccessIn, IBlockState stateIn, BlockPos posIn, int brightnessIn,
			boolean ownBrightness, WorldRenderer buffer, List<BakedQuad> list, BitSet bitSet) {
		Vec3d vec3d = stateIn.func_191059_e(blockAccessIn, posIn);
		double d0 = (double) posIn.getX() + vec3d.xCoord;
		double d1 = (double) posIn.getY() + vec3d.yCoord;
		double d2 = (double) posIn.getZ() + vec3d.zCoord;
		int i = 0;

		for (int j = list.size(); i < j; ++i) {
			BakedQuad bakedquad = list.get(i);

			if (ownBrightness) {
				this.fillQuadBounds(stateIn, bakedquad.getVertexData(), bakedquad.getFace(), (float[]) null, bitSet);
				BlockPos blockpos = bitSet.get(0) ? posIn.offset(bakedquad.getFace()) : posIn;
				brightnessIn = stateIn.getPackedLightmapCoords(blockAccessIn, blockpos);
			}

			buffer.addVertexData(bakedquad.getVertexData());
			buffer.putBrightness4(brightnessIn, brightnessIn, brightnessIn, brightnessIn);

			if (bakedquad.hasTintIndex()) {
				int k = this.blockColors.colorMultiplier(stateIn, blockAccessIn, posIn, bakedquad.getTintIndex());

				if (EntityRenderer.anaglyphEnable) {
					k = TextureUtil.anaglyphColor(k);
				}

				float f = (float) (k >> 16 & 255) / 255.0F;
				float f1 = (float) (k >> 8 & 255) / 255.0F;
				float f2 = (float) (k & 255) / 255.0F;
				buffer.putColorMultiplier(f, f1, f2, 4);
				buffer.putColorMultiplier(f, f1, f2, 3);
				buffer.putColorMultiplier(f, f1, f2, 2);
				buffer.putColorMultiplier(f, f1, f2, 1);
			}

			buffer.putPosition(d0, d1, d2);
		}
	}

	public void renderModelBrightnessColor(IBakedModel bakedModel, float p_178262_2_, float red, float green,
			float blue) {
		this.renderModelBrightnessColor((IBlockState) null, bakedModel, p_178262_2_, red, green, blue);
	}

	public void renderModelBrightnessColor(IBlockState state, IBakedModel p_187495_2_, float p_187495_3_,
			float p_187495_4_, float p_187495_5_, float p_187495_6_) {
		EnumFacing[] values = EnumFacing._VALUES;
		for (int i = 0; i < values.length; i++) {
			EnumFacing enumfacing = values[i];
			this.renderModelBrightnessColorQuads(p_187495_3_, p_187495_4_, p_187495_5_, p_187495_6_,
					p_187495_2_.getQuads(state, enumfacing, 0L));
		}

		this.renderModelBrightnessColorQuads(p_187495_3_, p_187495_4_, p_187495_5_, p_187495_6_,
				p_187495_2_.getQuads(state, (EnumFacing) null, 0L));
	}

	public void renderModelBrightness(IBakedModel model, IBlockState state, float brightness, boolean p_178266_4_) {
		Block block = state.getBlock();
		GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
		int i = this.blockColors.colorMultiplier(state, (IBlockAccess) null, (BlockPos) null, 0);

		if (EntityRenderer.anaglyphEnable) {
			i = TextureUtil.anaglyphColor(i);
		}

		float f = (float) (i >> 16 & 255) / 255.0F;
		float f1 = (float) (i >> 8 & 255) / 255.0F;
		float f2 = (float) (i & 255) / 255.0F;

		if (!p_178266_4_) {
			GlStateManager.color(brightness, brightness, brightness, 1.0F);
		}

		this.renderModelBrightnessColor(state, model, brightness, f, f1, f2);
	}

	private void renderModelBrightnessColorQuads(float brightness, float red, float green, float blue,
			List<BakedQuad> listQuads) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer bufferbuilder = tessellator.getBuffer();
		int i = 0;

		for (int j = listQuads.size(); i < j; ++i) {
			BakedQuad bakedquad = listQuads.get(i);
			bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
			bufferbuilder.addVertexData(bakedquad.getVertexData());

			if (bakedquad.hasTintIndex()) {
				bufferbuilder.putColorRGB_F4(red * brightness, green * brightness, blue * brightness);
			} else {
				bufferbuilder.putColorRGB_F4(brightness, brightness, brightness);
			}

			Vec3i vec3i = bakedquad.getFace().getDirectionVec();
			bufferbuilder.putNormal((float) vec3i.getX(), (float) vec3i.getY(), (float) vec3i.getZ());
			tessellator.draw();
		}
	}

	public static class AmbientOcclusionFace {
		private final float[] vertexColorMultiplier = new float[4];
		private final int[] vertexBrightness = new int[4];

		private final BlockPos blockpos0 = new BlockPos(0, 0, 0);
		private final BlockPos blockpos1 = new BlockPos(0, 0, 0);
		private final BlockPos blockpos2 = new BlockPos(0, 0, 0);
		private final BlockPos blockpos3 = new BlockPos(0, 0, 0);
		private final BlockPos blockpos4 = new BlockPos(0, 0, 0);
		private final BlockPos blockpos5 = new BlockPos(0, 0, 0);

		public void updateVertexBrightness(IBlockAccess blockAccessIn, Block blockIn, BlockPos blockPosIn,
				EnumFacing facingIn, float[] quadBounds, BitSet boundsFlags) {
			BlockPos blockpos = boundsFlags.get(0) ? blockPosIn.offsetEvenFaster(facingIn, blockpos0) : blockPosIn;
			BlockModelRenderer.EnumNeighborInfo blockmodelrenderer$enumneighborinfo = BlockModelRenderer.EnumNeighborInfo
					.getNeighbourInfo(facingIn);
			IBlockState blockState = blockIn.getBlockState().getBaseState();
			blockpos.offsetEvenFaster(blockmodelrenderer$enumneighborinfo.corners[0], blockpos1);
			int i = blockState.getPackedLightmapCoords(blockAccessIn, blockpos1);
			float f = blockAccessIn.getBlockState(blockpos1).getAmbientOcclusionLightValue();
			blockpos1.offsetEvenFaster(facingIn, blockpos5);
			boolean flag = blockAccessIn.getBlockState(blockpos5).isTranslucent();

			blockpos.offsetEvenFaster(blockmodelrenderer$enumneighborinfo.corners[1], blockpos2);
			int j = blockState.getPackedLightmapCoords(blockAccessIn, blockpos2);
			float f1 = blockAccessIn.getBlockState(blockpos2).getAmbientOcclusionLightValue();
			blockpos2.offsetEvenFaster(facingIn, blockpos5);
			boolean flag1 = blockAccessIn.getBlockState(blockpos5).isTranslucent();

			blockpos.offsetEvenFaster(blockmodelrenderer$enumneighborinfo.corners[2], blockpos3);
			int k = blockState.getPackedLightmapCoords(blockAccessIn, blockpos3);
			float f2 = blockAccessIn.getBlockState(blockpos3).getAmbientOcclusionLightValue();
			blockpos3.offsetEvenFaster(facingIn, blockpos5);
			boolean flag2 = blockAccessIn.getBlockState(blockpos5).isTranslucent();

			blockpos.offsetEvenFaster(blockmodelrenderer$enumneighborinfo.corners[3], blockpos4);
			int l = blockState.getPackedLightmapCoords(blockAccessIn, blockpos4);
			float f3 = blockAccessIn.getBlockState(blockpos4).getAmbientOcclusionLightValue();
			blockpos4.offsetEvenFaster(facingIn, blockpos5);
			boolean flag3 = blockAccessIn.getBlockState(blockpos5).isTranslucent();

			float f4;
			int i1;
			if (!flag2 && !flag) {
				f4 = f;
				i1 = i;
			} else {
				blockpos1.offsetEvenFaster(blockmodelrenderer$enumneighborinfo.corners[2], blockpos5);
				f4 = blockAccessIn.getBlockState(blockpos5).getAmbientOcclusionLightValue();
				i1 = blockState.getPackedLightmapCoords(blockAccessIn, blockpos5);
			}

			float f5;
			int j1;
			if (!flag3 && !flag) {
				f5 = f;
				j1 = i;
			} else {
				blockpos1.offsetEvenFaster(blockmodelrenderer$enumneighborinfo.corners[3], blockpos5);
				f5 = blockAccessIn.getBlockState(blockpos5).getAmbientOcclusionLightValue();
				j1 = blockState.getPackedLightmapCoords(blockAccessIn, blockpos5);
			}

			float f6;
			int k1;
			if (!flag2 && !flag1) {
				f6 = f1;
				k1 = j;
			} else {
				blockpos2.offsetEvenFaster(blockmodelrenderer$enumneighborinfo.corners[2], blockpos5);
				f6 = blockAccessIn.getBlockState(blockpos5).getAmbientOcclusionLightValue();
				k1 = blockState.getPackedLightmapCoords(blockAccessIn, blockpos5);
			}

			float f7;
			int l1;
			if (!flag3 && !flag1) {
				f7 = f1;
				l1 = j;
			} else {
				blockpos2.offsetEvenFaster(blockmodelrenderer$enumneighborinfo.corners[3], blockpos5);
				f7 = blockAccessIn.getBlockState(blockpos5).getAmbientOcclusionLightValue();
				l1 = blockState.getPackedLightmapCoords(blockAccessIn, blockpos5);
			}

			blockPosIn.offsetEvenFaster(facingIn, blockpos5);
			int i3 = blockState.getPackedLightmapCoords(blockAccessIn, blockPosIn);
			if (boundsFlags.get(0) || !blockAccessIn.getBlockState(blockpos5).isOpaqueCube()) {
				i3 = blockState.getPackedLightmapCoords(blockAccessIn, blockpos5);
			}

			float f8 = boundsFlags.get(0)
					? blockAccessIn.getBlockState(blockpos).getAmbientOcclusionLightValue()
					: blockAccessIn.getBlockState(blockPosIn).getAmbientOcclusionLightValue();
			BlockModelRenderer.VertexTranslations blockmodelrenderer$vertextranslations = BlockModelRenderer.VertexTranslations
					.getVertexTranslations(facingIn);
			if (boundsFlags.get(1) && blockmodelrenderer$enumneighborinfo.doNonCubicWeight) {
				float f29 = (f3 + f + f5 + f8) * 0.25F;
				float f30 = (f2 + f + f4 + f8) * 0.25F;
				float f31 = (f2 + f1 + f6 + f8) * 0.25F;
				float f32 = (f3 + f1 + f7 + f8) * 0.25F;
				float f13 = quadBounds[blockmodelrenderer$enumneighborinfo.vert0Weights[0].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert0Weights[1].shape];
				float f14 = quadBounds[blockmodelrenderer$enumneighborinfo.vert0Weights[2].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert0Weights[3].shape];
				float f15 = quadBounds[blockmodelrenderer$enumneighborinfo.vert0Weights[4].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert0Weights[5].shape];
				float f16 = quadBounds[blockmodelrenderer$enumneighborinfo.vert0Weights[6].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert0Weights[7].shape];
				float f17 = quadBounds[blockmodelrenderer$enumneighborinfo.vert1Weights[0].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert1Weights[1].shape];
				float f18 = quadBounds[blockmodelrenderer$enumneighborinfo.vert1Weights[2].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert1Weights[3].shape];
				float f19 = quadBounds[blockmodelrenderer$enumneighborinfo.vert1Weights[4].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert1Weights[5].shape];
				float f20 = quadBounds[blockmodelrenderer$enumneighborinfo.vert1Weights[6].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert1Weights[7].shape];
				float f21 = quadBounds[blockmodelrenderer$enumneighborinfo.vert2Weights[0].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert2Weights[1].shape];
				float f22 = quadBounds[blockmodelrenderer$enumneighborinfo.vert2Weights[2].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert2Weights[3].shape];
				float f23 = quadBounds[blockmodelrenderer$enumneighborinfo.vert2Weights[4].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert2Weights[5].shape];
				float f24 = quadBounds[blockmodelrenderer$enumneighborinfo.vert2Weights[6].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert2Weights[7].shape];
				float f25 = quadBounds[blockmodelrenderer$enumneighborinfo.vert3Weights[0].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert3Weights[1].shape];
				float f26 = quadBounds[blockmodelrenderer$enumneighborinfo.vert3Weights[2].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert3Weights[3].shape];
				float f27 = quadBounds[blockmodelrenderer$enumneighborinfo.vert3Weights[4].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert3Weights[5].shape];
				float f28 = quadBounds[blockmodelrenderer$enumneighborinfo.vert3Weights[6].shape]
						* quadBounds[blockmodelrenderer$enumneighborinfo.vert3Weights[7].shape];
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f29 * f13 + f30 * f14
						+ f31 * f15 + f32 * f16;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f29 * f17 + f30 * f18
						+ f31 * f19 + f32 * f20;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f29 * f21 + f30 * f22
						+ f31 * f23 + f32 * f24;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f29 * f25 + f30 * f26
						+ f31 * f27 + f32 * f28;
				int i2 = getAoBrightness(l, i, j1, i3);
				int j2 = getAoBrightness(k, i, i1, i3);
				int k2 = getAoBrightness(k, j, k1, i3);
				int l2 = getAoBrightness(l, j, l1, i3);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = getVertexBrightness(i2,
						j2, k2, l2, f13, f14, f15, f16);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = getVertexBrightness(i2,
						j2, k2, l2, f17, f18, f19, f20);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = getVertexBrightness(i2,
						j2, k2, l2, f21, f22, f23, f24);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = getVertexBrightness(i2,
						j2, k2, l2, f25, f26, f27, f28);
			} else {
				float f9 = (f3 + f + f5 + f8) * 0.25F;
				float f10 = (f2 + f + f4 + f8) * 0.25F;
				float f11 = (f2 + f1 + f6 + f8) * 0.25F;
				float f12 = (f3 + f1 + f7 + f8) * 0.25F;
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert0] = getAoBrightness(l, i, j1,
						i3);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert1] = getAoBrightness(k, i, i1,
						i3);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert2] = getAoBrightness(k, j, k1,
						i3);
				this.vertexBrightness[blockmodelrenderer$vertextranslations.vert3] = getAoBrightness(l, j, l1,
						i3);
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert0] = f9;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert1] = f10;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert2] = f11;
				this.vertexColorMultiplier[blockmodelrenderer$vertextranslations.vert3] = f12;
			}

		}
		
		private static int getVertexBrightness(int parInt1, int parInt2, int parInt3, int parInt4, float parFloat1,
				float parFloat2, float parFloat3, float parFloat4) {
			int i = (int) ((float) (parInt1 >> 16 & 255) * parFloat1 + (float) (parInt2 >> 16 & 255) * parFloat2
					+ (float) (parInt3 >> 16 & 255) * parFloat3 + (float) (parInt4 >> 16 & 255) * parFloat4) & 255;
			int j = (int) ((float) (parInt1 & 255) * parFloat1 + (float) (parInt2 & 255) * parFloat2
					+ (float) (parInt3 & 255) * parFloat3 + (float) (parInt4 & 255) * parFloat4) & 255;
			return i << 16 | j;
		}
		
		private static int getAoBrightness(int parInt1, int parInt2, int parInt3, int parInt4) {
			if (parInt1 == 0) {
				parInt1 = parInt4;
			}

			if (parInt2 == 0) {
				parInt2 = parInt4;
			}

			if (parInt3 == 0) {
				parInt3 = parInt4;
			}

			return parInt1 + parInt2 + parInt3 + parInt4 >> 2 & 16711935;
		}
	}

	public static enum EnumNeighborInfo {
		DOWN(new EnumFacing[] { EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH }, 0.5F, true,
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.FLIP_WEST,
						BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_WEST,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.WEST,
						BlockModelRenderer.Orientation.SOUTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.FLIP_WEST,
						BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_WEST,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.WEST,
						BlockModelRenderer.Orientation.NORTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.FLIP_EAST,
						BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_EAST,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.EAST,
						BlockModelRenderer.Orientation.NORTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.FLIP_EAST,
						BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_EAST,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.EAST,
						BlockModelRenderer.Orientation.SOUTH }),
		UP(new EnumFacing[] { EnumFacing.EAST, EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.SOUTH }, 1.0F, true,
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.EAST,
						BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.EAST,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_EAST,
						BlockModelRenderer.Orientation.SOUTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.EAST,
						BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.EAST,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_EAST,
						BlockModelRenderer.Orientation.NORTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.WEST,
						BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.WEST,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_WEST,
						BlockModelRenderer.Orientation.NORTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.WEST,
						BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.WEST,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_WEST,
						BlockModelRenderer.Orientation.SOUTH }),
		NORTH(new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST }, 0.8F, true,
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.FLIP_WEST },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.FLIP_EAST },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.FLIP_EAST },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.FLIP_WEST }),
		SOUTH(new EnumFacing[] { EnumFacing.WEST, EnumFacing.EAST, EnumFacing.DOWN, EnumFacing.UP }, 0.8F, true,
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.WEST },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.FLIP_WEST, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.WEST, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.WEST },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.EAST },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.FLIP_EAST, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.EAST, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.EAST }),
		WEST(new EnumFacing[] { EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH }, 0.6F, true,
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.SOUTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.NORTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.NORTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.SOUTH }),
		EAST(new EnumFacing[] { EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH }, 0.6F, true,
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.SOUTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_DOWN,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.DOWN,
						BlockModelRenderer.Orientation.NORTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.NORTH, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.FLIP_NORTH, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.NORTH },
				new BlockModelRenderer.Orientation[] { BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.SOUTH, BlockModelRenderer.Orientation.FLIP_UP,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.FLIP_SOUTH, BlockModelRenderer.Orientation.UP,
						BlockModelRenderer.Orientation.SOUTH });

		private final EnumFacing[] corners;
		private final float shadeWeight;
		private final boolean doNonCubicWeight;
		private final BlockModelRenderer.Orientation[] vert0Weights;
		private final BlockModelRenderer.Orientation[] vert1Weights;
		private final BlockModelRenderer.Orientation[] vert2Weights;
		private final BlockModelRenderer.Orientation[] vert3Weights;
		private static final BlockModelRenderer.EnumNeighborInfo[] VALUES = new BlockModelRenderer.EnumNeighborInfo[6];

		private EnumNeighborInfo(EnumFacing[] p_i46236_3_, float p_i46236_4_, boolean p_i46236_5_,
				BlockModelRenderer.Orientation[] p_i46236_6_, BlockModelRenderer.Orientation[] p_i46236_7_,
				BlockModelRenderer.Orientation[] p_i46236_8_, BlockModelRenderer.Orientation[] p_i46236_9_) {
			this.corners = p_i46236_3_;
			this.shadeWeight = p_i46236_4_;
			this.doNonCubicWeight = p_i46236_5_;
			this.vert0Weights = p_i46236_6_;
			this.vert1Weights = p_i46236_7_;
			this.vert2Weights = p_i46236_8_;
			this.vert3Weights = p_i46236_9_;
		}

		public static BlockModelRenderer.EnumNeighborInfo getNeighbourInfo(EnumFacing p_178273_0_) {
			return VALUES[p_178273_0_.getIndex()];
		}

		static {
			VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
			VALUES[EnumFacing.UP.getIndex()] = UP;
			VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
			VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
			VALUES[EnumFacing.WEST.getIndex()] = WEST;
			VALUES[EnumFacing.EAST.getIndex()] = EAST;
		}
	}

	public static enum Orientation {
		DOWN(EnumFacing.DOWN, false), UP(EnumFacing.UP, false), NORTH(EnumFacing.NORTH, false),
		SOUTH(EnumFacing.SOUTH, false), WEST(EnumFacing.WEST, false), EAST(EnumFacing.EAST, false),
		FLIP_DOWN(EnumFacing.DOWN, true), FLIP_UP(EnumFacing.UP, true), FLIP_NORTH(EnumFacing.NORTH, true),
		FLIP_SOUTH(EnumFacing.SOUTH, true), FLIP_WEST(EnumFacing.WEST, true), FLIP_EAST(EnumFacing.EAST, true);

		private final int shape;

		private Orientation(EnumFacing p_i46233_3_, boolean p_i46233_4_) {
			this.shape = p_i46233_3_.getIndex() + (p_i46233_4_ ? EnumFacing._VALUES.length : 0);
		}
	}

	static enum VertexTranslations {
		DOWN(0, 1, 2, 3), UP(2, 3, 0, 1), NORTH(3, 0, 1, 2), SOUTH(0, 1, 2, 3), WEST(3, 0, 1, 2), EAST(1, 2, 3, 0);

		private final int vert0;
		private final int vert1;
		private final int vert2;
		private final int vert3;
		private static final BlockModelRenderer.VertexTranslations[] VALUES = new BlockModelRenderer.VertexTranslations[6];

		private VertexTranslations(int p_i46234_3_, int p_i46234_4_, int p_i46234_5_, int p_i46234_6_) {
			this.vert0 = p_i46234_3_;
			this.vert1 = p_i46234_4_;
			this.vert2 = p_i46234_5_;
			this.vert3 = p_i46234_6_;
		}

		public static BlockModelRenderer.VertexTranslations getVertexTranslations(EnumFacing p_178184_0_) {
			return VALUES[p_178184_0_.getIndex()];
		}

		static {
			VALUES[EnumFacing.DOWN.getIndex()] = DOWN;
			VALUES[EnumFacing.UP.getIndex()] = UP;
			VALUES[EnumFacing.NORTH.getIndex()] = NORTH;
			VALUES[EnumFacing.SOUTH.getIndex()] = SOUTH;
			VALUES[EnumFacing.WEST.getIndex()] = WEST;
			VALUES[EnumFacing.EAST.getIndex()] = EAST;
		}
	}
}
