/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.update;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiUpdateVersionList extends GuiScreen {

	final GuiScreen back;
	GuiUpdateVersionSlot slots;
	int selected;
	GuiButton downloadButton;
	int mx = 0;
	int my = 0;
	String tooltip = null;

	public GuiUpdateVersionList(GuiScreen back) {
		this.back = back;
	}

	protected void initGui() {
		selected = -1;
		this.buttons.clear();
		this.children.clear();
		this.slots = new GuiUpdateVersionSlot(this);
		this.children.add(slots);
		this.addButton(new GuiButton(0, this.width / 2 + 54, this.height - 28, 100, 20, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateVersionList.this.mc.displayGuiScreen(back);
			}
		});
		this.downloadButton = this.addButton(new GuiButton(1, this.width / 2 - 50, this.height - 28, 100, 20, I18n.format("updateList.download")) {
			public void onClick(double mouseX, double mouseY) {
				if(selected != -1) {
					UpdateService.startClientUpdateFrom(slots.certList.get(selected));
				}
				GuiUpdateVersionList.this.mc.displayGuiScreen(back);
			}
		});
		this.addButton(new GuiButton(2, this.width / 2 - 154, this.height - 28, 100, 20, I18n.format("updateList.refresh")) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateVersionList.this.initGui();
			}
		});
		updateButtons();
	}

	void updateButtons() {
		downloadButton.enabled = selected != -1;
	}

	static Minecraft getMinecraft(GuiUpdateVersionList screen) {
		return screen.mc;
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.mx = mouseX;
		this.my = mouseY;
		this.slots.drawScreen(mouseX, mouseY, partialTick);
		this.drawCenteredString(this.fontRenderer, I18n.format("updateList.title"), this.width / 2, 16, 16777215);
		this.drawCenteredString(this.fontRenderer, I18n.format("updateList.note.0"), this.width / 2, this.height - 55, 0x888888);
		this.drawCenteredString(this.fontRenderer, I18n.format("updateList.note.1"), this.width / 2, this.height - 45, 0x888888);
		super.render(mouseX, mouseY, partialTick);
		if(tooltip != null) {
			this.drawHoveringText(this.fontRenderer.listFormattedStringToWidth(tooltip, 180), mouseX, mouseY);
			GlStateManager.disableLighting();
			tooltip = null;
		}
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.slots.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.slots.mouseReleased(mouseX, mouseY, button) || super.mouseReleased(mouseX, mouseY, button);
	}

	public boolean mouseScrolled(double delta) {
		return this.slots.mouseScrolled(delta) || super.mouseScrolled(delta);
	}
}
