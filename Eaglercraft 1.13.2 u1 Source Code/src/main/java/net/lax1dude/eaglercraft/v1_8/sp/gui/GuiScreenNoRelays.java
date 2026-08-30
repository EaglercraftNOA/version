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

public class GuiScreenNoRelays extends GuiScreen {

	private final GuiScreen parent;
	private final String title1;
	private final String title2;
	private final String title3;

	public GuiScreenNoRelays(GuiScreen parent, String title) {
		this(parent, title, null, null);
	}

	public GuiScreenNoRelays(GuiScreen parent, String title1, String title2, String title3) {
		this.parent = parent;
		this.title1 = title1;
		this.title2 = title2;
		this.title3 = title3;
	}

	@Override
	protected void initGui() {
		this.buttons.clear();
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 - 60 + 145, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenNoRelays.this.mc.displayGuiScreen(GuiScreenNoRelays.this.parent);
			}
		});
		this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 - 60 + 115, I18n.format("directConnect.lanWorldRelay")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenNoRelays.this.mc.displayGuiScreen(GuiScreenLANInfo.showLANInfoScreen(new GuiScreenRelay(GuiScreenNoRelays.this.parent)));
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format(title1), this.width / 2, this.height / 4 - 60 + 70, 16777215);
		if(title2 != null) {
			this.drawCenteredString(this.fontRenderer, I18n.format(title2), this.width / 2, this.height / 4 - 60 + 80, 0xCCCCCC);
		}
		if(title3 != null) {
			this.drawCenteredString(this.fontRenderer, I18n.format(title3), this.width / 2, this.height / 4 - 60 + 90, 0xCCCCCC);
		}
		super.render(mouseX, mouseY, partialTicks);
	}

}
