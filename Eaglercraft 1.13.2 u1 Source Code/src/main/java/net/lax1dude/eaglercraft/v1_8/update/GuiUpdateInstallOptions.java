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

import net.lax1dude.eaglercraft.v1_8.EaglercraftVersion;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenGenericErrorMessage;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.resources.I18n;

public class GuiUpdateInstallOptions extends GuiScreen {

	protected final GuiScreen parent;
	protected final GuiScreen onDone;
	protected final UpdateDataObj updateData;
	protected boolean makeDefault;
	protected boolean enableCountdown;
	protected GuiButton makeDefaultBtn;
	protected GuiButton enableCountdownBtn;

	public GuiUpdateInstallOptions(GuiScreen parent, GuiScreen onDone, UpdateDataObj updateData) {
		this.parent = parent;
		this.onDone = onDone;
		this.updateData = updateData;
		makeDefault = updateData.clientSignature.bundleVersionInteger > EaglercraftVersion.updateBundlePackageVersionInt;
		enableCountdown = makeDefault;
	}

	protected void initGui() {
		this.buttons.clear();
		this.children.clear();
		this.addButton(makeDefaultBtn = new GuiButton(0, this.width / 2 - 100, this.height / 6 + 46, 200, 20, makeDefaultText()) {
			public void onClick(double mouseX, double mouseY) {
				makeDefault = !makeDefault;
				makeDefaultBtn.displayString = makeDefaultText();
			}
		});
		this.addButton(enableCountdownBtn = new GuiButton(1, this.width / 2 - 100, this.height / 6 + 76, 200, 20, countdownText()) {
			public void onClick(double mouseX, double mouseY) {
				enableCountdown = !enableCountdown;
				enableCountdownBtn.displayString = countdownText();
			}
		});
		this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 6 + 110, 200, 20, I18n.format("updateInstall.install")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenWorking ls = GuiUpdateInstallOptions.this.mc.loadingScreen;
				ls.displayLoadingString(I18n.format("updateSuccess.installing"));
				GuiUpdateInstallOptions.this.mc.displayGuiScreen(ls);
				try {
					UpdateService.installSignedClient(updateData.clientSignature, updateData.clientBundle, makeDefault, enableCountdown);
				}catch(Throwable t) {
					GuiUpdateInstallOptions.this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("installFailed.title", t.toString(), onDone));
					return;
				}
				GuiUpdateInstallOptions.this.mc.displayGuiScreen(onDone);
			}
		});
		this.addButton(new GuiButton(3, this.width / 2 - 100, this.height / 6 + 140, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateInstallOptions.this.mc.displayGuiScreen(parent);
			}
		});
	}

	private String makeDefaultText() {
		return I18n.format("updateInstall.setDefault") + ": " + I18n.format(makeDefault ? "gui.yes" : "gui.no");
	}

	private String countdownText() {
		return I18n.format("updateInstall.setCountdown") + ": " + I18n.format(enableCountdown ? "gui.yes" : "gui.no");
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("updateInstall.title"), this.width / 2, 40, 11184810);
		this.drawCenteredString(this.fontRenderer, updateData.clientSignature.bundleDisplayName + " " + updateData.clientSignature.bundleDisplayVersion, this.width / 2, 60, 0xFFFFAA);
		super.render(mouseX, mouseY, partialTick);
	}
}
