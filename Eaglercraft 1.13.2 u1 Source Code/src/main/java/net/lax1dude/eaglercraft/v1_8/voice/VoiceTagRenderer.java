/*
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.voice;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import java.util.HashSet;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.EaglerUUIDHelper;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class VoiceTagRenderer {

	private static final ResourceLocation voiceGuiIcons = new ResourceLocation("eagler:gui/eagler_gui.png");

	private static final Set<EaglercraftUUID> voiceTagsDrawnThisFrame = new HashSet<>();

	public static void renderVoiceNameTag(Minecraft mc, EntityOtherPlayerMP player, int offset) {
		java.util.UUID playerUUID = player.getUniqueID();
		EaglercraftUUID uuid = new EaglercraftUUID(EaglerUUIDHelper.getMostSignificantBits(playerUUID), EaglerUUIDHelper.getLeastSignificantBits(playerUUID));
		boolean mute = VoiceClientController.getVoiceMuted().contains(uuid);
		if((mute || VoiceClientController.getVoiceSpeaking().contains(uuid)) && voiceTagsDrawnThisFrame.add(uuid)) {
			GlStateManager.disableLighting();
			GlStateManager.disableTexture2D();
			GlStateManager.enableAlpha();
			GlStateManager.depthMask(false);
			GlStateManager.disableDepth();
			GlStateManager.enableBlend();
			GlStateManager.pushMatrix();
			GlStateManager.translate(-8.0F, -18.0F + offset, 0.0F);
			GlStateManager.scale(16.0F, 16.0F, 16.0F);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
			float a = 0.25F;
			bufferbuilder.pos(-0.02D, -0.02D, 0.0D).color(0.0F, 0.0F, 0.0F, a).endVertex();
			bufferbuilder.pos(-0.02D, 1.02D, 0.0D).color(0.0F, 0.0F, 0.0F, a).endVertex();
			bufferbuilder.pos(1.02D, 1.02D, 0.0D).color(0.0F, 0.0F, 0.0F, a).endVertex();
			bufferbuilder.pos(1.02D, -0.02D, 0.0D).color(0.0F, 0.0F, 0.0F, a).endVertex();
			tessellator.draw();

			GlStateManager.enableTexture2D();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL_GREATER, 0.02F);
			mc.getTextureManager().bindTexture(voiceGuiIcons);

			int u = 0;
			int v = mute ? 192 : 160;
			float var7 = 0.00390625F;
			float var8 = 0.00390625F;
			if(mute) {
				GlStateManager.color(0.9F, 0.3F, 0.3F, 0.125F);
			}else {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 0.125F);
			}
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			drawIconQuad(bufferbuilder, u, v, var7, var8);
			tessellator.draw();

			GlStateManager.alphaFunc(GL_GREATER, 0.1F);
			GlStateManager.enableDepth();
			GlStateManager.depthMask(true);
			if(mute) {
				GlStateManager.color(0.9F, 0.3F, 0.3F, 1.0F);
			}else {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
			drawIconQuad(bufferbuilder, u, v, var7, var8);
			tessellator.draw();

			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}

	private static void drawIconQuad(BufferBuilder bufferbuilder, int u, int v, float uScale, float vScale) {
		bufferbuilder.pos(0.0D, 1.0D, 0.0D).tex((double)((float)(u + 0.2F) * uScale), (double)((float)(v + 32 - 0.2F) * vScale)).endVertex();
		bufferbuilder.pos(1.0D, 1.0D, 0.0D).tex((double)((float)(u + 32 - 0.2F) * uScale), (double)((float)(v + 32 - 0.2F) * vScale)).endVertex();
		bufferbuilder.pos(1.0D, 0.0D, 0.0D).tex((double)((float)(u + 32 - 0.2F) * uScale), (double)((float)(v + 0.2F) * vScale)).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex((double)((float)(u + 0.2F) * uScale), (double)((float)(v + 0.2F) * vScale)).endVertex();
	}

	public static void clearTagsDrawnSet() {
		voiceTagsDrawnThisFrame.clear();
	}

}
