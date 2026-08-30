/*
 * Copyright (c) 2023-2024 lax1dude. All Rights Reserved.
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

import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.internal.ClientPlatformSingleplayer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenIntegratedServerFailed extends GuiScreen {

	private final String str1;
	private final String str2;
	private final GuiScreen cont;

	public GuiScreenIntegratedServerFailed(String str1, String str2, GuiScreen cont) {
		this.str1 = I18n.format(str1);
		this.str2 = I18n.format(str2);
		this.cont = cont;
	}

	public GuiScreenIntegratedServerFailed(String str2, GuiScreen cont) {
		this.str1 = I18n.format("singleplayer.failed.title");
		this.str2 = I18n.format(str2);
		this.cont = cont;
	}

	@Override
	protected void initGui() {
		super.initGui();
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 96, 200, 20, I18n.format("singleplayer.crashed.continue")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenIntegratedServerFailed.this.mc.displayGuiScreen(cont);
			}
		});
		if(!ClientPlatformSingleplayer.isRunningSingleThreadMode() && ClientPlatformSingleplayer.isSingleThreadModeSupported()) {
			this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 6 + 126, 200, 20, I18n.format("singleplayer.crashed.singleThreadCont")) {
				public void onClick(double mouseX, double mouseY) {
					if(SingleplayerServerController.canKillWorker()) {
						SingleplayerServerController.killWorker();
					}
					GuiScreenIntegratedServerFailed.this.mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(new GuiMainMenu(), true));
				}
			});
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(fontRenderer, str1, this.width / 2, 70, 11184810);
		this.drawCenteredString(fontRenderer, str2, this.width / 2, 90, 16777215);
		super.render(mouseX, mouseY, partialTicks);
	}
}
