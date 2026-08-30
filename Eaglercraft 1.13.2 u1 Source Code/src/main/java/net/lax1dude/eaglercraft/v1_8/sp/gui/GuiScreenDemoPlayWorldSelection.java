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

package net.lax1dude.eaglercraft.v1_8.sp.gui;

import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.WorldServerDemo;

public class GuiScreenDemoPlayWorldSelection extends GuiScreen {

	private final GuiScreen mainmenu;
	private GuiButton playWorld;
	private GuiButton joinWorld;

	public GuiScreenDemoPlayWorldSelection(GuiScreen mainmenu) {
		this.mainmenu = mainmenu;
	}

	@Override
	protected void initGui() {
		this.buttons.clear();
		this.addButton(this.playWorld = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 40, I18n.format("singleplayer.demo.create.create")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenDemoPlayWorldSelection.this.playDemoWorld();
			}
		});
		this.addButton(this.joinWorld = new GuiButton(2, this.width / 2 - 100, this.height / 4 + 65, I18n.format("singleplayer.demo.create.join")) {
			public void onClick(double mouseX, double mouseY) {
				if(LANServerController.supported()) {
					GuiScreenDemoPlayWorldSelection.this.mc.displayGuiScreen(GuiScreenLANInfo.showLANInfoScreen(new GuiScreenLANConnect(GuiScreenDemoPlayWorldSelection.this.mainmenu)));
				}else {
					GuiScreenDemoPlayWorldSelection.this.mc.displayGuiScreen(new GuiScreenLANNotSupported(GuiScreenDemoPlayWorldSelection.this.mainmenu));
				}
			}
		});
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 130, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenDemoPlayWorldSelection.this.mc.displayGuiScreen(GuiScreenDemoPlayWorldSelection.this.mainmenu);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.demo.create.title"), this.width / 2, this.height / 4, 16777215);
		int toolTipColor = 0xDDDDAA;
		if(this.playWorld.isMouseOver()) {
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.demo.create.create.tooltip"), this.width / 2, this.height / 4 + 20, toolTipColor);
		}else if(this.joinWorld.isMouseOver()) {
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.demo.create.join.tooltip"), this.width / 2, this.height / 4 + 20, toolTipColor);
		}
		super.render(mouseX, mouseY, partialTicks);
	}

	private void playDemoWorld() {
		this.mc.gameSettings.hasCreatedDemoWorld = true;
		this.mc.gameSettings.saveOptions();
		this.mc.launchIntegratedServer("Demo World", "Demo World", WorldServerDemo.DEMO_WORLD_SETTINGS);
	}

}
