package net.lax1dude.eaglercraft.minecraft;

import java.io.IOException;

import net.lax1dude.eaglercraft.internal.IFramebufferGL;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

import static net.lax1dude.eaglercraft.internal.PlatformOpenGL.*;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.*;

public class MainMenuSkyboxTexture extends AbstractTexture {

	public static final int _GL_FRAMEBUFFER = 0x8D40;
	public static final int _GL_COLOR_ATTACHMENT0 = 0x8CE0;

	private IFramebufferGL framebuffer = null;

	public MainMenuSkyboxTexture(int width, int height) {
		TextureUtil.allocateTexture(this.getGlTextureId(), width, height);
		EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
	}

	@Override
	public void loadTexture(IResourceManager var1) throws IOException {
	}

	public void bindFramebuffer() {
		if(framebuffer == null) {
			framebuffer = _wglCreateFramebuffer();
			_wglBindFramebuffer(_GL_FRAMEBUFFER, framebuffer);
			int tex = getGlTextureId();
			GlStateManager.bindTexture(tex);
			_wglFramebufferTexture2D(_GL_FRAMEBUFFER, _GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D,
					EaglercraftGPU.getNativeTexture(tex), 0);
		}else {
			_wglBindFramebuffer(_GL_FRAMEBUFFER, framebuffer);
		}
		_wglDrawBuffers(new int[] { _GL_COLOR_ATTACHMENT0 });
	}

	public void deleteGlTexture() {
		super.deleteGlTexture();
		if(framebuffer != null) {
			_wglDeleteFramebuffer(framebuffer);
			framebuffer = null;
		}
	}

}