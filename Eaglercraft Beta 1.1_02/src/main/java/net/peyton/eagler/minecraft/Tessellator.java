package net.peyton.eagler.minecraft;

import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldVertexBufferUploader;

public class Tessellator {
	
	private net.lax1dude.eaglercraft.opengl.WorldRenderer worldRenderer;
	public static final Tessellator instance = new Tessellator(524288);
	private final VertexFormat format = VertexFormat.MODIFIABLE;
	
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

	private Tessellator(int var1) {
		this.renderingChunk = false;
		this.worldRenderer = new net.lax1dude.eaglercraft.opengl.WorldRenderer(var1);
	}

	public void draw() {
		this.worldRenderer.finishDrawing();
		WorldVertexBufferUploader.func_181679_a(this.worldRenderer);
		this.format.reset();
	}

	public void startDrawingQuads() {
		this.startDrawing(7);
	}

	public void startDrawing(int var1) {
		this.worldRenderer.begin(var1, format);
	}

	public void setTextureUV(double var1, double var3) {
		this.format.setTex();
		textureU = var1;
		textureV = var3;
	}
	
	public void setColorOpaque_F(float var1, float var2, float var3) {
		this.setColorOpaque((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F));
	}

	public void setColorRGBA_F(float var1, float var2, float var3, float var4) {
		this.setColorRGBA((int)(var1 * 255.0F), (int)(var2 * 255.0F), (int)(var3 * 255.0F), (int)(var4 * 255.0F));
	}

	public void setColorOpaque(int var1, int var2, int var3) {
		this.setColorRGBA(var1, var2, var3, 255);
	}

	public void setColorRGBA(int var1, int var2, int var3, int var4) {
		if(!this.worldRenderer.needsUpdate) {
			this.format.setColor();
			this.colorR = var1;
			this.colorG = var2;
			this.colorB = var3;
			this.colorA = var4;
		}
	}

	public void addVertexWithUV(double var1, double var3, double var5, double var7, double var9) {
		this.setTextureUV(var7, var9);
		this.addVertex(var1, var3, var5);
	}

	public void addVertex(double var1, double var3, double var5) {
		if(format.needsUpdate()) {
			format.updateVertexFormat();
		}
		
		worldRenderer.vertexFormat = this.format;
		
		worldRenderer.pos(var1 + this.xOffset, var3 + this.yOffset, var5 + this.zOffset);
		
		if(format.attribTextureEnabled) {
			worldRenderer.tex(this.textureU, this.textureV);
		}
		
		if(format.attribColorEnabled ) {
			worldRenderer.setColorRGBA(colorR, colorG, colorB, colorA);
		}
		
		if(format.attribNormalEnabled) {
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
		this.format.setNormal();
		normalX = var1;
		normalY = var2;
		normalZ = var3;
	}

	public void setTranslationD(double var1, double var3, double var5) {
		this.xOffset = var1;
		this.yOffset = var3;
		this.zOffset = var5;
	}

	public void setTranslationF(float var1, float var2, float var3) {
		this.xOffset += (double)var1;
		this.yOffset += (double)var2;
		this.zOffset += (double)var3;
	}
	
	public boolean isRenderingChunk() {
		return this.renderingChunk;
	}
	
	public void setRenderingChunk(boolean renderingChunk) {
		this.renderingChunk = renderingChunk;
	}
}
