package net.minecraft.client.shader;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

public class Framebuffer {
	public int framebufferTextureWidth;
	public int framebufferTextureHeight;
	public int framebufferWidth;
	public int framebufferHeight;
	public boolean useDepth;
	public int framebufferObject;
	public int framebufferTexture;
	public int depthBuffer;
	public float[] framebufferColor;
	public int framebufferFilter;
	private static final String __OBFID = "CL_00000959";

	public Framebuffer(int p_i45078_1_, int p_i45078_2_, boolean p_i45078_3_) {

	}

	public void createBindFramebuffer(int p_147613_1_, int p_147613_2_) {

	}

	public void deleteFramebuffer() {

	}

	public void createFramebuffer(int p_147605_1_, int p_147605_2_) {

	}

	public void setFramebufferFilter(int p_147607_1_) {

	}

	public void checkFramebufferComplete() {

	}

	public void bindFramebufferTexture() {

	}

	public void unbindFramebufferTexture() {

	}

	public void bindFramebuffer(boolean p_147610_1_) {

	}

	public void unbindFramebuffer() {

	}

	public void setFramebufferColor(float p_147604_1_, float p_147604_2_, float p_147604_3_, float p_147604_4_) {

	}

	public void framebufferRender(int p_147615_1_, int p_147615_2_) {

	}

	public void framebufferClear() {

	}
}
