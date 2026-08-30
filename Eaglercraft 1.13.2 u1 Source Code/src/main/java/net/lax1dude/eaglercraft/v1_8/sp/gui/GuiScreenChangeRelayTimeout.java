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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenChangeRelayTimeout extends GuiScreen {

	private final GuiScreen parent;
	private GuiSlider2 slider;
	private String title;

	public GuiScreenChangeRelayTimeout(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	protected void initGui() {
		super.initGui();
		title = I18n.format("networkSettings.relayTimeoutTitle");
		this.addButton(new GuiButton(0, width / 2 - 100, height / 3 + 55, 200, 20, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				mc.gameSettings.relayTimeout = (int)((slider.sliderValue * 14.0f) + 1.0f);
				mc.gameSettings.saveOptions();
				mc.displayGuiScreen(parent);
			}
		});
		this.addButton(new GuiButton(1, width / 2 - 100, height / 3 + 85, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				mc.displayGuiScreen(parent);
			}
		});
		slider = this.addButton(new GuiSlider2(0, width / 2 - 100, height / 3 + 10, 200, 20, (mc.gameSettings.relayTimeout - 1) / 14.0f, 1.0f) {
			@Override
			protected String updateDisplayString() {
				return "" + (int)((sliderValue * 14.0f) + 1.0f) + "s";
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(0);
		drawCenteredString(fontRenderer, title, width / 2, height / 3 - 20, 0xFFFFFF);
		super.render(mouseX, mouseY, partialTicks);
	}

}
