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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.resources.I18n;

public class GuiUpdateDownloadSuccess extends GuiScreen {

	protected final GuiScreen parent;
	protected final UpdateDataObj updateData;

	public GuiUpdateDownloadSuccess(GuiScreen parent, UpdateDataObj updateData) {
		this.parent = parent;
		this.updateData = updateData;
	}

	protected void initGui() {
		this.buttons.clear();
		this.children.clear();
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 56, 200, 20, I18n.format("updateSuccess.downloadOffline")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenWorking ls = GuiUpdateDownloadSuccess.this.mc.loadingScreen;
				ls.displayLoadingString(I18n.format("updateSuccess.downloading"));
				GuiUpdateDownloadSuccess.this.mc.displayGuiScreen(ls);
				UpdateService.quine(updateData.clientSignature, updateData.clientBundle);
				GuiUpdateDownloadSuccess.this.mc.displayGuiScreen(parent);
			}
		});
		this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 6 + 86, 200, 20, I18n.format("updateSuccess.installToBootMenu")) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateDownloadSuccess.this.mc.displayGuiScreen(new GuiUpdateInstallOptions(GuiUpdateDownloadSuccess.this, parent, updateData));
			}
		});
		this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 6 + 130, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateDownloadSuccess.this.mc.displayGuiScreen(parent);
			}
		});
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("updateSuccess.title"), this.width / 2, 50, 11184810);
		this.drawCenteredString(this.fontRenderer, updateData.clientSignature.bundleDisplayName + " " + updateData.clientSignature.bundleDisplayVersion, this.width / 2, 70, 0xFFFFAA);
		super.render(mouseX, mouseY, partialTick);
	}
}
