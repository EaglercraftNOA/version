package net.lax1dude.eaglercraft.minecraft;

import org.lwjgl.opengl.GL11;

import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.InstancedFontRenderer;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.lax1dude.eaglercraft.opengl.Tessellator;
import net.lax1dude.eaglercraft.opengl.VertexFormat;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.minecraft.src.GameSettings;
import net.minecraft.src.RenderEngine;
import net.peyton.eagler.minecraft.FontRenderer;

/**
 * Copyright (c) 2022 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class EaglerFontRenderer extends FontRenderer {

	private final int[] temporaryCodepointArray = new int[6553];

	public static FontRenderer createSupportedFontRenderer(GameSettings gameSettingsIn, String location, RenderEngine textureManagerIn) {
		if (EaglercraftGPU.checkInstancingCapable()) {
			return new EaglerFontRenderer(gameSettingsIn, location, textureManagerIn);
		} else {
			return new FontRenderer(gameSettingsIn, location, textureManagerIn);
		}
	}

	public EaglerFontRenderer(GameSettings gameSettingsIn, String location, RenderEngine textureManagerIn) {
		super(gameSettingsIn, location, textureManagerIn);
	}

	@Override
	public int drawString(String text, float x, float y, int color, boolean dropShadow) {
		GlStateManager.enableAlpha();
		if (text == null || text.length() == 0) {
			this.posX = x + (dropShadow ? 1 : 0);
			this.posY = y;
		} else {
			if (!decodeASCIICodepointsAndValidate(text)) {
				return super.drawString(text, x, y, color, dropShadow);
			}
			if ((color & 0xFC000000) == 0) {
				color |= 0xFF000000;
			}
			this.red = (float) (color >>> 16 & 255) / 255.0F;
			this.blue = (float) (color >>> 8 & 255) / 255.0F;
			this.green = (float) (color & 255) / 255.0F;
			this.alpha = (float) (color >>> 24 & 255) / 255.0F;
			this.posX = x;
			this.posY = y;
			this.textColor = color;
			this.renderStringAtPos0(text, dropShadow);
		}
		return (int) this.posX;
	}

	@Override
	protected void renderStringAtPos(String parString1, boolean parFlag) {
		if (parString1 == null)
			return;
		if (!decodeASCIICodepointsAndValidate(parString1)) {
			super.renderStringAtPos(parString1, parFlag);
		} else {
			renderStringAtPos0(parString1, false);
		}
	}

	private void renderStringAtPos0(String parString1, boolean parFlag) {
		GL11.glBindTexture(RealOpenGLEnums.GL_TEXTURE_2D, this.renderEngine.getTexture(this.locationFontTexture));
		InstancedFontRenderer.begin();

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, VertexFormat.POSITION_TEX);

		for (int i = 0; i < parString1.length(); ++i) {
			char c0 = parString1.charAt(i);
			if (c0 == 167 && i + 1 < parString1.length()) {
				int i1 = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(parString1.charAt(i + 1)));
				if (i1 < 16) {
					if (i1 < 0 || i1 > 15) {
						i1 = 15;
					}
					int j1 = this.colorCode[i1];
					this.textColor = j1 | (this.textColor & 0xFF000000);
				} else if (i1 == 21) {
					this.textColor = ((int) (this.alpha * 255.0f) << 24) | ((int) (this.red * 255.0f) << 16)
							| ((int) (this.green * 255.0f) << 8) | (int) (this.blue * 255.0f);
				}

				++i;
			} else {
				int j = temporaryCodepointArray[i];
				if (j > 255)
					continue;

				float f = this.appendCharToBuffer(j, this.textColor);
				this.posX += (float) ((int) f);
			}
		}

		float texScale = 0.0625f;

		worldrenderer.finishDrawing();

		if (parFlag) {
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			InstancedFontRenderer.render(8, 8, texScale, texScale, true);
		} else {
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			InstancedFontRenderer.render(8, 8, texScale, texScale, false);
		}

		if (parFlag) {
			this.posX += 1.0f;
		}
	}

	private float appendCharToBuffer(int parInt1, int color) {
		if (parInt1 == 32) {
			return 4.0f;
		} else {
			int i = parInt1 % 16;
			int j = parInt1 / 16;
			float w = this.charWidth[parInt1];
			InstancedFontRenderer.appendQuad((int) this.posX, (int) this.posY, i, j, color, false);
			return w;
		}
	}

	private boolean decodeASCIICodepointsAndValidate(String str) {
		for (int i = 0, l = str.length(); i < l; ++i) {
			int j = FontMappingHelper.lookupChar(str.charAt(i), true);
			if (j != -1) {
				temporaryCodepointArray[i] = j;
			} else {
				return false;
			}
		}
		return true;
	}
}
