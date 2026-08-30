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

package net.lax1dude.eaglercraft.v1_8.profanity_filter;

import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenIntegratedServerStartup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiWorldSelection;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiScreenContentWarning extends GuiScreen {

	private final GuiScreen cont;
	private boolean enableState;
	private GuiButton optButton;

	public GuiScreenContentWarning(GuiScreen cont) {
		this.cont = cont;
	}

	public void initGui() {
		this.buttons.clear();
		enableState = mc.gameSettings.enableProfanityFilter;
		optButton = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 108, I18n.format("options.profanityFilterButton") + ": " + I18n.format(enableState ? "gui.yes" : "gui.no")) {
			public void onClick(double mouseX, double mouseY) {
				// audrey <3
				enableState = !enableState;
				optButton.displayString = I18n.format("options.profanityFilterButton") + ": " + I18n.format(enableState ? "gui.yes" : "gui.no");
			}
		});
		this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 6 + 138, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				// audrey <3
				mc.gameSettings.enableProfanityFilter = enableState;
				mc.gameSettings.hasShownProfanityFilter = true;
				mc.gameSettings.saveOptions();
				mc.displayGuiScreen(cont);
			}
		});
	}

	@Override
	public void render(int mx, int my, float pt) {
		this.drawDefaultBackground();
		this.drawCenteredString(fontRenderer, TextFormatting.BOLD + I18n.format("profanityFilterWarning.title"), this.width / 2, 50, 0xFF4444);
		this.drawCenteredString(fontRenderer, I18n.format("profanityFilterWarning.text0"), this.width / 2, 70, 16777215);
		this.drawCenteredString(fontRenderer, I18n.format("profanityFilterWarning.text1"), this.width / 2, 82, 16777215);
		this.drawCenteredString(fontRenderer, I18n.format("profanityFilterWarning.text2"), this.width / 2, 94, 16777215);
		this.drawCenteredString(fontRenderer, I18n.format("profanityFilterWarning.text4"), this.width / 2, 116, 0xCCCCCC);
		super.render(mx, my, pt);
	}

}