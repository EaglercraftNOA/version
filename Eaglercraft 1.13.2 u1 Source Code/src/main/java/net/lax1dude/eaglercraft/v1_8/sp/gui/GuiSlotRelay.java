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

package net.lax1dude.eaglercraft.v1_8.sp.gui;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayQuery;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

class GuiSlotRelay extends GuiSlot {

	private static final ResourceLocation eaglerGuiTex = new ResourceLocation("eagler:gui/eagler_gui.png");

	final GuiScreenRelay screen;
	final RelayManager relayManager;

	public GuiSlotRelay(GuiScreenRelay screen) {
		super(GuiScreenRelay.getMinecraft(screen), screen.width, screen.height, 32, screen.height - 64, 26);
		this.screen = screen;
		this.relayManager = RelayManager.relayManager;
	}

	@Override
	protected int getSize() {
		return relayManager.count();
	}

	@Override
	public int getListWidth() {
		return 240;
	}

	@Override
	protected int getScrollBarX() {
		return this.width / 2 + 124;
	}

	@Override
	protected boolean mouseClicked(int index, int button, double mouseX, double mouseY) {
		if(button == 0) {
			screen.selected = index;
			screen.updateButtons();
			return true;
		}
		return false;
	}

	@Override
	protected boolean isSelected(int slotIndex) {
		return screen.selected == slotIndex;
	}

	@Override
	protected void drawBackground() {
		screen.drawDefaultBackground();
	}

	@Override
	protected void drawSlot(int id, int x, int y, int heightIn, int mouseX, int mouseY, float partialTicks) {
		if(id >= relayManager.count()) {
			return;
		}
		Minecraft mc = Minecraft.getInstance();
		mc.getTextureManager().bindTexture(Gui.ICONS);
		RelayServer srv = relayManager.get(id);
		String comment = srv.comment;
		int var15 = 0;
		int var16 = 0;
		String str = null;
		int h = 12;
		long ping = srv.getPing();
		if(ping == 0l) {
			var16 = 5;
			str = "No Connection";
		}else if(ping < 0l) {
			var15 = 1;
			var16 = (int) (EagRuntime.steadyTimeMillis() / 100L + (long) (id * 2) & 7L);
			if (var16 > 4) {
				var16 = 8 - var16;
			}
			str = "Polling...";
		}else {
			RelayQuery.VersionMismatch vm = srv.getPingCompatible();
			if(!vm.isCompatible()) {
				var16 = 5;
				switch(vm) {
					case CLIENT_OUTDATED:
						str = "Outdated Client!";
						break;
					case RELAY_OUTDATED:
						str = "Outdated Relay!";
						break;
					default:
					case UNKNOWN:
						str = "Incompatible Relay!";
						break;
				}
				GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
				GlStateManager.pushMatrix();
				GlStateManager.translatef(x + 205, y + 11, 0.0f);
				GlStateManager.scalef(0.6f, 0.6f, 0.6f);
				screen.drawTexturedModalRect(0, 0, 0, 144, 16, 16);
				GlStateManager.popMatrix();
				h += 10;
			}else {
				String pingComment = srv.getPingComment().trim();
				if(pingComment.length() > 0) {
					comment = pingComment;
				}
				str = "" + ping + "ms";
				if (ping < 150L) {
					var16 = 0;
				} else if (ping < 300L) {
					var16 = 1;
				} else if (ping < 600L) {
					var16 = 2;
				} else if (ping < 1000L) {
					var16 = 3;
				} else {
					var16 = 4;
				}
			}
		}

		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		screen.drawTexturedModalRect(x + 205, y, 0 + var15 * 10, 176 + var16 * 8, 10, 8);
		if(srv.isPrimary()) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(x + 4, y + 5, 0.0f);
			GlStateManager.scalef(0.8f, 0.8f, 0.8f);
			mc.getTextureManager().bindTexture(eaglerGuiTex);
			screen.drawTexturedModalRect(0, 0, 48, 0, 16, 16);
			GlStateManager.popMatrix();
		}

		screen.drawString(mc.fontRenderer, comment, x + 22, y + 2, 0xFFFFFFFF);
		screen.drawString(mc.fontRenderer, srv.address, x + 22, y + 12, 0xFF999999);

		if(str != null) {
			int rx = x + 202;
			if(mouseX > rx && mouseX < rx + 13 && mouseY > y - 1 && mouseY < y + h) {
				screen.setToolTip(str);
			}
		}
	}
}
