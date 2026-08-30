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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenIntegratedServerCrashed extends GuiScreen {

	private final GuiScreen mainmenu;
	private final String crashReport;

	public GuiScreenIntegratedServerCrashed(GuiScreen mainmenu, String crashReport) {
		this.mainmenu = mainmenu;
		this.crashReport = crashReport;
	}

	@Override
	protected void initGui() {
		super.initGui();
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height - 50, 200, 20, I18n.format("singleplayer.crashed.continue")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenIntegratedServerCrashed.this.mc.displayGuiScreen(mainmenu);
			}
		});
		int scale = (int)this.mc.mainWindow.getGuiScaleFactor();
		CrashScreen.showCrashReportOverlay(crashReport, 90 * scale, 60 * scale, (width - 180) * scale, (height - 130) * scale);
	}

	@Override
	public void onGuiClosed() {
		CrashScreen.hideCrashReportOverlay();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(fontRenderer, I18n.format("singleplayer.crashed.title"), this.width / 2, 25, 0xFFAAAA);
		this.drawCenteredString(fontRenderer, I18n.format("singleplayer.crashed.checkConsole"), this.width / 2, 40, 0xBBBBBB);
		super.render(mouseX, mouseY, partialTicks);
	}
}
