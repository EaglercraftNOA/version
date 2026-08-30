package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.opengl.WorldVertexBufferUploader;
import net.minecraft.client.shader.TesselatorVertexState;

public class Tessellator {

	public net.lax1dude.eaglercraft.opengl.WorldRenderer worldRenderer;
	public static final Tessellator instance = new Tessellator(524288);
	private VertexFormat format;

	private double textureU = 0;
	private double textureV = 0;

	private int colorR;
	private int colorG;
	private int colorB;
	private int colorA;

	private float normalX;
	private float normalY;
	private float normalZ;

	private double xOffset;
	private double yOffset;
	private double zOffset;

	private boolean renderingChunk;

	private int cachedLightSky;
	private int cachedLightBlock;
	private boolean hasCachedBrightness;

	private Tessellator(int var1) {
		this.renderingChunk = false;
		this.worldRenderer = new net.lax1dude.eaglercraft.opengl.WorldRenderer(var1);
	}

	public int draw() {
		this.worldRenderer.finishDrawing();
		WorldVertexBufferUploader.func_181679_a(this.worldRenderer);
		format = null;
		return 0;	}

	public void setBrightness(int brightness) {
		if (this.format == null || !this.format.attribLightmapEnabled) {
			this.format = DefaultVertexFormats.POSITION_TEX_LMAP_COLOR;
			if (this.worldRenderer != null && this.worldRenderer.isDrawing) {
				this.worldRenderer.setVertexFormat(this.format);
			}
		}
		this.cachedLightSky = (brightness >> 16) & 0xFFFF;
		this.cachedLightBlock = brightness & 0xFFFF;
		this.hasCachedBrightness = true;
	}

	public void startDrawingQuads() {
		if (this.isDrawing()) {
			this.draw();
		}
		this.startDrawing(7);
	}

	public void startDrawing(int var1) {
		if (this.isDrawing()) {
			this.draw();
		}
		this.format = VertexFormat.createVertexFormat(true, true, true);
		this.textureU = 0;
		this.textureV = 0;
		this.colorR = 255;
		this.colorG = 255;
		this.colorB = 255;
		this.colorA = 255;
		this.normalX = 0;
		this.normalY = 0;
		this.normalZ = 0;
		this.xOffset = 0;
		this.yOffset = 0;
		this.zOffset = 0;
		this.worldRenderer.begin(var1, format);
		this.worldRenderer.setVertexFormat(this.format);
		this.hasCachedBrightness = false;
	}

	public void startDrawing(int var1, boolean useTexture, boolean useColor, boolean useNormal) {

		this.format = VertexFormat.createVertexFormat(useTexture, useColor, useNormal);
		this.textureU = 0;
		this.textureV = 0;
		this.colorR = 255;
		this.colorG = 255;
		this.colorB = 255;
		this.colorA = 255;
		this.normalX = 0;
		this.normalY = 0;
		this.normalZ = 0;
		this.xOffset = 0;
		this.yOffset = 0;
		this.zOffset = 0;
		this.worldRenderer.begin(var1, format);
		this.worldRenderer.setVertexFormat(this.format);
	}

	public void setTextureUV(double var1, double var3) {
		this.textureU = var1;
		this.textureV = var3;
	}

	public void setColorOpaque_F(float var1, float var2, float var3) {
		this.setColorOpaque((int) (var1 * 255.0F), (int) (var2 * 255.0F), (int) (var3 * 255.0F));
	}

	public void setColorRGBA_F(float var1, float var2, float var3, float var4) {
		this.setColorRGBA((int) (var1 * 255.0F), (int) (var2 * 255.0F), (int) (var3 * 255.0F), (int) (var4 * 255.0F));
	}

	public void setColorOpaque(int var1, int var2, int var3) {
		this.setColorRGBA(var1, var2, var3, 255);
	}

	public void setColorRGBA(int var1, int var2, int var3, int var4) {
		this.colorR = var1;
		this.colorG = var2;
		this.colorB = var3;
		this.colorA = var4;
	}

	public void addVertexWithUV(double var1, double var3, double var5, double var7, double var9) {
		this.setTextureUV(var7, var9);
		this.addVertex(var1, var3, var5);
	}

	public void addVertex(double var1, double var3, double var5) {
		worldRenderer.setVertexFormat(
				format == null ? (format = VertexFormat.createVertexFormat(false, false, false)) : format);

		worldRenderer.pos(var1 + this.xOffset, var3 + this.yOffset, var5 + this.zOffset);

		if (format.attribTextureEnabled) {
			worldRenderer.tex(this.textureU, this.textureV);
		}

		if (hasCachedBrightness && format.attribLightmapEnabled) {
			worldRenderer.lightmap(cachedLightSky, cachedLightBlock);
		}

		if (format.attribColorEnabled) {
			worldRenderer.setColorRGBA(colorR, colorG, colorB, colorA);
		}

		if (format.attribNormalEnabled) {
			worldRenderer.normal(this.normalX, this.normalY, this.normalZ);
		}

		worldRenderer.endVertex();
	}

	public void setColorOpaque_I(int var1) {
		int var2 = var1 >> 16 & 255;
		int var3 = var1 >> 8 & 255;
		int var4 = var1 & 255;
		this.setColorOpaque(var2, var3, var4);
	}

	public void setColorRGBA_I(int var1, int var2) {
		int var3 = var1 >> 16 & 255;
		int var4 = var1 >> 8 & 255;
		int var5 = var1 & 255;
		this.setColorRGBA(var3, var4, var5, var2);
	}

	public void disableColor() {
		worldRenderer.markDirty();
	}

	public void setNormal(float var1, float var2, float var3) {
		this.normalX = var1;
		this.normalY = var2;
		this.normalZ = var3;
	}

	public void setTranslationD(double var1, double var3, double var5) {
		this.xOffset = var1;
		this.yOffset = var3;
		this.zOffset = var5;
	}

	public void setTranslationF(float var1, float var2, float var3) {
		this.xOffset += (double) var1;
		this.yOffset += (double) var2;
		this.zOffset += (double) var3;
	}

	public boolean isRenderingChunk() {
		return this.renderingChunk;
	}

	public void setRenderingChunk(boolean renderingChunk) {
		this.renderingChunk = renderingChunk;
	}

	/**
	 * Offsets the translation for all vertices in the current draw call.
	 */
	public void addTranslation(float par1, float par2, float par3) {
		this.xOffset += (double) par1;
		this.yOffset += (double) par2;
		this.zOffset += (double) par3;
	}

	public boolean isDrawing() {
		return this.worldRenderer.isDrawing;
	}

	public static Tessellator getInstance() {
		return instance;
	}

	public void setTranslation(double v, double v1, double v2) {
		setTranslationD(v, v1, v2);
	}

	public TesselatorVertexState getVertexState(float x, float y, float z) {
		if (worldRenderer != null) {
			net.lax1dude.eaglercraft.opengl.WorldRenderer.State state = worldRenderer.func_181672_a();
			int[] rawBuffer = new int[state.getRawBuffer().remaining()];
			int oldPos = state.getRawBuffer().position();
			state.getRawBuffer().get(rawBuffer);
			state.getRawBuffer().position(oldPos);			VertexFormat fmt = worldRenderer.getVertexFormat();
			return new TesselatorVertexState(rawBuffer, state.getRawBuffer().position(), state.getVertexCount(),
					fmt.attribTextureEnabled, false,					fmt.attribNormalEnabled, fmt.attribColorEnabled);
		}
		return null;
	}

	public void setVertexState(TesselatorVertexState state) {
		if (worldRenderer != null && state != null) {
			IntBuffer buf = GLAllocation.createDirectIntBuffer(state.getRawBuffer().length);
			buf.put(state.getRawBuffer());
			buf.position(0);
			VertexFormat fmt = worldRenderer.getVertexFormat();
			net.lax1dude.eaglercraft.opengl.WorldRenderer.State wrState = worldRenderer.new State(buf, fmt);
			worldRenderer.setVertexState(wrState);
		}
	}

	public WorldRenderer getWorldRenderer() {
		return worldRenderer;
	}
}
