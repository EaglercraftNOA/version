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

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.WorkerStartupFailedException;
import net.lax1dude.eaglercraft.v1_8.sp.ipc.IPCPacket15Crashed;
import net.lax1dude.eaglercraft.v1_8.sp.ipc.IPCPacket1CIssueDetected;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.resources.I18n;

public class GuiScreenIntegratedServerStartup extends GuiScreen {

	private final GuiScreen backScreen;
	private final boolean singleThread;
	private static final String[] dotDotDot = new String[] { "", ".", "..", "..." };
	private int counter = 0;
	private GuiButton cancelButton;

	public GuiScreenIntegratedServerStartup(GuiScreen backScreen) {
		this.backScreen = backScreen;
		this.singleThread = false;
	}

	public GuiScreenIntegratedServerStartup(GuiScreen backScreen, boolean singleThread) {
		this.backScreen = backScreen;
		this.singleThread = singleThread;
	}

	@Override
	protected void initGui() {
		super.initGui();
		this.addButton(cancelButton = new GuiButton(0, this.width / 2 - 100, this.height / 3 + 50, 200, 20, I18n.format("singleplayer.busy.killTask")) {
			public void onClick(double mouseX, double mouseY) {
				SingleplayerServerController.killWorker();
				GuiScreenIntegratedServerStartup.this.mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(new GuiMainMenu(), true));
			}
		});
		cancelButton.visible = false;
	}

	@Override
	public void tick() {
		++counter;
		if(counter == 2) {
			try {
				SingleplayerServerController.startIntegratedServerWorker(singleThread);
			}catch(WorkerStartupFailedException ex) {
				mc.displayGuiScreen(new GuiScreenIntegratedServerFailed(ex.getMessage(), new GuiMainMenu()));
				return;
			}
		}else if(counter > 2) {
			if(counter > 100 && SingleplayerServerController.canKillWorker() && !singleThread) {
				cancelButton.visible = true;
			}
			IPCPacket15Crashed[] crashReport = SingleplayerServerController.worldStatusErrors();
			if(crashReport != null) {
				mc.displayGuiScreen(GuiScreenIntegratedServerBusy.createException(new GuiMainMenu(), "singleplayer.failed.notStarted", crashReport));
			}else if(SingleplayerServerController.isIntegratedServerWorkerStarted()) {
				GuiScreen cont = new GuiWorldSelection(backScreen);
				if(SingleplayerServerController.isRunningSingleThreadMode()) {
					cont = new GuiScreenIntegratedServerFailed("singleplayer.failed.singleThreadWarning.1", "singleplayer.failed.singleThreadWarning.2", cont);
				}else if(!EagRuntime.getConfiguration().isRamdiskMode() && SingleplayerServerController.isIssueDetected(IPCPacket1CIssueDetected.ISSUE_RAMDISK_MODE) && SingleplayerServerController.canKillWorker()) {
					cont = new GuiScreenRAMDiskModeDetected(cont);
				}
				mc.displayGuiScreen(cont);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(0);
		String txt = I18n.format("singleplayer.integratedStartup");
		int w = this.fontRenderer.getStringWidth(txt);
		this.drawString(this.fontRenderer, txt + dotDotDot[(int)((EagRuntime.steadyTimeMillis() / 300L) % 4L)], (this.width - w) / 2, this.height / 2 - 50, 16777215);
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean canCloseGui() {
		return false;
	}
}
