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
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiScreenRelay extends GuiScreen implements GuiYesNoCallback {

	private final GuiScreen screen;
	private GuiSlotRelay slots;
	private boolean hasPinged;
	int selected;

	private GuiButton deleteRelay;
	private GuiButton setPrimary;

	private String tooltipString = null;
	private long lastRefresh = 0l;
	private int deleteRelayId = -1;

	String addNewName;
	String addNewAddr;
	boolean addNewPrimary;

	public GuiScreenRelay(GuiScreen screen) {
		this.screen = screen;
	}

	@Override
	protected void initGui() {
		super.initGui();
		selected = -1;
		this.slots = new GuiSlotRelay(this);
		this.children.add(this.slots);
		this.addButton(new GuiButton(0, this.width / 2 + 54, this.height - 28, 100, 20, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				RelayManager.relayManager.save();
				GuiScreenRelay.this.mc.displayGuiScreen(screen);
			}
		});
		this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("networkSettings.add")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenRelay.this.mc.displayGuiScreen(new GuiScreenAddRelay(GuiScreenRelay.this));
			}
		});
		this.addButton(deleteRelay = new GuiButton(2, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("networkSettings.delete")) {
			public void onClick(double mouseX, double mouseY) {
				deleteSelectedRelay();
			}
		});
		this.addButton(setPrimary = new GuiButton(3, this.width / 2 + 54, this.height - 52, 100, 20, I18n.format("networkSettings.default")) {
			public void onClick(double mouseX, double mouseY) {
				if(selected >= 0) {
					slots.relayManager.setPrimary(selected);
					selected = 0;
					updateButtons();
				}
			}
		});
		this.addButton(new GuiButton(4, this.width / 2 - 50, this.height - 28, 100, 20, I18n.format("networkSettings.refresh")) {
			public void onClick(double mouseX, double mouseY) {
				refreshRelays();
			}
		});
		this.addButton(new GuiButton(5, this.width / 2 - 154, this.height - 28, 100, 20, I18n.format("networkSettings.loadDefaults")) {
			public void onClick(double mouseX, double mouseY) {
				slots.relayManager.loadDefaults();
				refreshRelays();
			}
		});
		this.addButton(new GuiButton(6, this.width - 100, 0, 100, 20, I18n.format("networkSettings.downloadRelay")) {
			public void onClick(double mouseX, double mouseY) {
				EagRuntime.downloadFileWithName("EaglerSPRelay.zip", EagRuntime.getRequiredResourceBytes("relay_download.zip"));
			}
		});
		updateButtons();
		if(!hasPinged) {
			hasPinged = true;
			slots.relayManager.ping();
		}
	}

	void updateButtons() {
		boolean active = selected >= 0;
		if(deleteRelay != null) {
			deleteRelay.enabled = active;
		}
		if(setPrimary != null) {
			setPrimary.enabled = active;
		}
	}

	private void refreshRelays() {
		long millis = EagRuntime.steadyTimeMillis();
		if(millis - lastRefresh > 700l) {
			lastRefresh = millis;
			slots.relayManager.ping();
		}
		lastRefresh += 60l;
	}

	private void deleteSelectedRelay() {
		if(selected >= 0) {
			deleteRelayId = selected;
			RelayServer srv = RelayManager.relayManager.get(deleteRelayId);
			this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("networkSettings.delete"), I18n.format("addRelay.removeText1") + TextFormatting.GRAY + " '" + srv.comment + "' (" + srv.address + ")", deleteRelayId));
		}
	}

	@Override
	public void tick() {
		if(slots != null) {
			slots.relayManager.update();
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		if(slots != null) {
			slots.drawScreen(mouseX, mouseY, partialTicks);
		}

		if(tooltipString != null) {
			int ww = this.fontRenderer.getStringWidth(tooltipString);
			Gui.drawRect(mouseX + 1, mouseY - 14, mouseX + ww + 7, mouseY - 2, 0xC0000000);
			this.drawString(this.fontRenderer, tooltipString, mouseX + 4, mouseY - 12, 0xFF999999);
			tooltipString = null;
		}

		this.drawCenteredString(fontRenderer, I18n.format("networkSettings.title"), this.width / 2, 16, 16777215);

		String str = I18n.format("networkSettings.relayTimeout") + " " + mc.gameSettings.relayTimeout;
		int w = fontRenderer.getStringWidth(str);
		this.drawString(fontRenderer, str, 3, 3, 0xDDDDDD);

		GlStateManager.pushMatrix();
		GlStateManager.translate(w + 7, 4, 0.0f);
		GlStateManager.scale(0.75f, 0.75f, 0.75f);
		String change = TextFormatting.UNDERLINE + I18n.format("networkSettings.relayTimeoutChange");
		int w2 = fontRenderer.getStringWidth(change);
		boolean hoverChange = mouseX > w + 5 && mouseX < w + 7 + w2 * 3 / 4 && mouseY > 3 && mouseY < 11;
		if(hoverChange) {
			Mouse.showCursor(EnumCursorType.HAND);
		}
		this.drawString(fontRenderer, change, 0, 0, hoverChange ? 0xCCCCCC : 0x999999);
		GlStateManager.popMatrix();

		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(button == 0) {
			String str = I18n.format("networkSettings.relayTimeout") + " " + mc.gameSettings.relayTimeout;
			int w = fontRenderer.getStringWidth(str);
			String change = I18n.format("networkSettings.relayTimeoutChange");
			int w2 = fontRenderer.getStringWidth(change);
			if(mouseX > w + 5 && mouseX < w + 7 + w2 * 3 / 4 && mouseY > 3 && mouseY < 11) {
				this.mc.displayGuiScreen(new GuiScreenChangeRelayTimeout(this));
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	void setToolTip(String str) {
		tooltipString = str;
	}

	public void confirmClicked(boolean confirm, int ignored) {
		if(confirm) {
			RelayManager.relayManager.addNew(addNewAddr, addNewName, addNewPrimary);
			addNewAddr = null;
			addNewName = null;
			addNewPrimary = false;
			selected = -1;
			updateButtons();
		}
		this.mc.displayGuiScreen(this);
	}

	@Override
	public void confirmResult(boolean confirm, int id) {
		if(confirm && id == deleteRelayId) {
			RelayManager.relayManager.remove(id);
			selected = -1;
			updateButtons();
		}
		this.mc.displayGuiScreen(this);
	}

	static Minecraft getMinecraft(GuiScreenRelay screen) {
		return screen.mc;
	}
}
