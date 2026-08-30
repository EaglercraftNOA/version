/*
 * Copyright (c) 2022-2024 lax1dude, ayunami2000. All Rights Reserved.
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

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.GL_GREATER;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.util.ResourceLocation;

public class GuiVoiceOverlay extends Gui {

	public final Minecraft mc;
	public int width;
	public int height;
	private long pttTimer = 0L;
	private static final ResourceLocation voiceGuiIcons = new ResourceLocation("eagler:gui/eagler_gui.png");

	public GuiVoiceOverlay(Minecraft mc) {
		this.mc = mc;
	}

	public void setResolution(int w, int h) {
		this.width = w;
		this.height = h;
	}

	public void drawOverlay() {
		if(mc.world != null && VoiceClientController.getVoiceStatus() == EnumVoiceChannelStatus.CONNECTED && VoiceClientController.getVoiceChannel() != EnumVoiceChannelType.NONE && !(mc.currentScreen instanceof GuiIngameMenu)) {
			if(mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame()) {
				return;
			}

			GlStateManager.disableLighting();
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.alphaFunc(GL_GREATER, 0.1F);
			GlStateManager.pushMatrix();
			if(mc.currentScreen == null || mc.currentScreen instanceof GuiChat) {
				GlStateManager.translate(width / 2 + 77, height - 56, 0.0F);
				if(mc.player == null || mc.player.abilities.isCreativeMode) {
					GlStateManager.translate(0.0F, 16.0F, 0.0F);
				}
			}else {
				GlStateManager.translate(width / 2 + 10, 4, 0.0F);
			}

			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			String prompt = "press '" + Keyboard.getKeyName(mc.gameSettings.voicePTTKey) + "'";
			drawString(mc.fontRenderer, prompt, -3 - mc.fontRenderer.getStringWidth(prompt), 9, 0xDDDDDD);
			GlStateManager.scale(0.66F, 0.66F, 0.66F);
			mc.getTextureManager().bindTexture(voiceGuiIcons);

			if((mc.currentScreen == null || !mc.currentScreen.blockPTTKey()) && Keyboard.isKeyDown(mc.gameSettings.voicePTTKey)) {
				long millis = EagRuntime.steadyTimeMillis();
				if(pttTimer == 0L) {
					pttTimer = millis;
				}
				drawVoiceIcon(0, 64, 0.2F, 0.2F, 0.2F);
				GlStateManager.translate(-1.5F, -1.5F, 0.0F);
				if(millis - pttTimer < 1050L) {
					if((millis - pttTimer) % 300L < 150L) {
						drawVoiceIcon(0, 64, 0.9F, 0.2F, 0.2F);
					}else {
						drawVoiceIcon(0, 64, 0.9F, 0.7F, 0.7F);
					}
				}else {
					drawVoiceIcon(0, 64, 0.9F, 0.3F, 0.3F);
				}
			}else {
				pttTimer = 0L;
				drawVoiceIcon(0, 32, 0.2F, 0.2F, 0.2F);
				GlStateManager.translate(-1.5F, -1.5F, 0.0F);
				drawVoiceIcon(0, 32, 1.0F, 1.0F, 1.0F);
				GlStateManager.translate(-0.5F, -0.5F, 0.0F);
				drawVoiceIcon(0, 32, 1.0F, 1.0F, 1.0F);
			}
			GlStateManager.popMatrix();

			if(VoiceClientController.getVoiceChannel() == EnumVoiceChannelType.PROXIMITY) {
				drawProximityList();
			}else if(VoiceClientController.getVoiceChannel() == EnumVoiceChannelType.GLOBAL) {
				drawGlobalList();
			}
		}
	}

	private void drawVoiceIcon(int u, int v, float r, float g, float b) {
		GlStateManager.color(r, g, b, 1.0F);
		drawTexturedModalRect(0, 0, u, v, 32, 32);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void drawProximityList() {
		Set<EaglercraftUUID> listeners = VoiceClientController.getVoiceListening();
		if(listeners.isEmpty()) {
			return;
		}
		Set<EaglercraftUUID> speakers = VoiceClientController.getVoiceSpeaking();
		Set<EaglercraftUUID> muted = VoiceClientController.getVoiceMuted();
		List<EaglercraftUUID> listenerList = new ArrayList<>(listeners);
		listenerList.removeAll(muted);
		while(listenerList.size() > 5) {
			boolean removed = false;
			for(int i = 0, l = listenerList.size(); i < l; ++i) {
				if(!speakers.contains(listenerList.get(i))) {
					listenerList.remove(i);
					removed = true;
					break;
				}
			}
			if(!removed) {
				break;
			}
		}
		drawListenerList(listenerList, speakers, true);
	}

	private void drawGlobalList() {
		List<EaglercraftUUID> listenerList = new ArrayList<>(VoiceClientController.getVoiceSpeaking());
		listenerList.removeAll(VoiceClientController.getVoiceMuted());
		drawListenerList(listenerList, VoiceClientController.getVoiceSpeaking(), false);
	}

	private void drawListenerList(List<EaglercraftUUID> listenerList, Set<EaglercraftUUID> speakers, boolean dimQuiet) {
		int more = listenerList.size() - 5;
		int ww = width;
		int hh = height;
		if(mc.currentScreen instanceof GuiChat) {
			hh -= 15;
		}

		List<String> listenerListStr = new ArrayList<>(Math.min(5, listenerList.size()));
		int left = 50;
		for(int i = 0, l = listenerList.size(); i < l && i < 5; ++i) {
			String txt = VoiceClientController.getVoiceUsername(listenerList.get(i));
			listenerListStr.add(txt);
			int textWidth = mc.fontRenderer.getStringWidth(txt) + 4;
			if(textWidth > left) {
				left = textWidth;
			}
		}

		if(more > 0) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(ww - left + 3, hh - 10, left);
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			drawString(mc.fontRenderer, "(" + more + " more)", 0, 0, 0xBBBBBB);
			GlStateManager.popMatrix();
			hh -= 9;
		}

		for(int i = 0, l = listenerList.size(); i < l && i < 5; ++i) {
			boolean speaking = speakers.contains(listenerList.get(i));
			float speakf = speaking || !dimQuiet ? 1.0F : 0.75F;
			drawString(mc.fontRenderer, listenerListStr.get(i), ww - left, hh - 13 - i * 11, speaking || !dimQuiet ? 0xEEEEEE : 0xBBBBBB);
			mc.getTextureManager().bindTexture(voiceGuiIcons);
			GlStateManager.pushMatrix();
			GlStateManager.translate(ww - left - 14, hh - 14 - i * 11, 0.0F);
			GlStateManager.scale(0.75F, 0.75F, 0.75F);
			int v = speaking || !dimQuiet ? 176 : 208;
			GlStateManager.color(speakf * 0.2F, speakf * 0.2F, speakf * 0.2F, 1.0F);
			drawTexturedModalRect(0, 0, 64, v, 16, 16);
			GlStateManager.translate(0.25F, 0.25F, 0.0F);
			drawTexturedModalRect(0, 0, 64, v, 16, 16);
			GlStateManager.translate(-1.25F, -1.25F, 0.0F);
			GlStateManager.color(speakf, speakf, speakf, 1.0F);
			drawTexturedModalRect(0, 0, 64, v, 16, 16);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.popMatrix();
		}
	}
}
