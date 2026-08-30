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

package net.lax1dude.eaglercraft.v1_8.sp.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenLANNotSupported extends GuiScreen {

	private final GuiScreen cont;

	public GuiScreenLANNotSupported(GuiScreen cont) {
		this.cont = cont;
	}

	@Override
	protected void initGui() {
		this.buttons.clear();
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 96, I18n.format("singleplayer.crashed.continue")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenLANNotSupported.this.mc.displayGuiScreen(GuiScreenLANNotSupported.this.cont);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.notSupported.title"), this.width / 2, 70, 11184810);
		this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.notSupported.desc"), this.width / 2, 90, 16777215);
		super.render(mouseX, mouseY, partialTicks);
	}

}
