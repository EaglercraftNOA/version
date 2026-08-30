/*
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.profile;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.GL_RGBA;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.GL_RGBA8;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.GL_TEXTURE_2D;
import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.GL_UNSIGNED_BYTE;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.IResourceManager;

public class EaglerSkinTexture extends AbstractTexture {

	private final int[] pixels;
	private final int width;
	private final int height;

	public EaglerSkinTexture(int[] pixels, int width, int height) {
		if(pixels.length != width * height) {
			throw new IllegalArgumentException("Wrong data length " + pixels.length * 4 + "  for " + width + "x" + height + " texture");
		}
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}

	public EaglerSkinTexture(byte[] pixels, int width, int height) {
		if(pixels.length != width * height * 4) {
			throw new IllegalArgumentException("Wrong data length " + pixels.length + "  for " + width + "x" + height + " texture");
		}
		this.pixels = convertToInt(pixels);
		this.width = width;
		this.height = height;
	}

	public static int[] convertToInt(byte[] pixels) {
		int[] p = new int[pixels.length >> 2];
		for(int i = 0, j; i < p.length; ++i) {
			j = i << 2;
			p[i] = (((int) pixels[j] & 0xFF) << 24) | (((int) pixels[j + 1] & 0xFF) << 16)
					| (((int) pixels[j + 2] & 0xFF) << 8) | ((int) pixels[j + 3] & 0xFF);
		}
		return p;
	}

	public void copyPixelsIn(byte[] pixels) {
		copyPixelsIn(convertToInt(pixels));
	}

	public void copyPixelsIn(int[] pixels) {
		if(this.pixels.length != pixels.length) {
			throw new IllegalArgumentException("Tried to copy " + pixels.length + " pixels into a " + this.pixels.length + " pixel texture");
		}
		System.arraycopy(pixels, 0, this.pixels, 0, pixels.length);
		if(this.glTextureId != -1) {
			upload(false);
		}
	}

	@Override
	public void loadTexture(IResourceManager var1) throws IOException {
		upload(true);
	}

	public int getGlTextureId() {
		return super.getGlTextureId();
	}

	public void setBlurMipmap(boolean var1, boolean var2) {
		this.setBlurMipmapDirect(var1, var2);
	}

	public void restoreLastBlurMipmap() {
	}
	
	public void free() {
		this.deleteGlTexture();
	}

	private void upload(boolean allocate) {
		GlStateManager.bindTexture(this.getGlTextureId());
		IntBuffer buffer = EagRuntime.allocateIntBuffer(pixels.length);
		try {
			buffer.put(pixels);
			buffer.flip();
			if(allocate) {
				EaglercraftGPU.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			}else {
				EaglercraftGPU.glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			}
		}finally {
			EagRuntime.freeIntBuffer(buffer);
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getData() {
		return pixels;
	}

}
