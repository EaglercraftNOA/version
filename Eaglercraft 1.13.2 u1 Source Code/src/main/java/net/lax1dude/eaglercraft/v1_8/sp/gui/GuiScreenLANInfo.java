/*
 * Copyright (c) 2024 lax1dude, ayunami2000. All Rights Reserved.
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

public class GuiScreenLANInfo extends GuiScreen {

	private final GuiScreen parent;
	private static boolean hasShown = false;

	public GuiScreenLANInfo(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	protected void initGui() {
		this.buttons.clear();
		this.addButton(new GuiButton(0, this.width / 2 - 100, height / 6 + 168, I18n.format("gui.continue")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenLANInfo.this.mc.displayGuiScreen(GuiScreenLANInfo.this.parent);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("lanInfo.title"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
		this.fontRenderer.drawSplitString(I18n.format("lanInfo.desc.0") + "\n\n\n" + I18n.format("lanInfo.desc.1", I18n.format("menu.multiplayer"), I18n.format("menu.openToLan")), this.width / 2 - 100, this.height / 4 - 60 + 60, 200, -6250336);
		super.render(mouseX, mouseY, partialTicks);
	}

	public static GuiScreen showLANInfoScreen(GuiScreen cont) {
		if(!hasShown) {
			hasShown = true;
			return new GuiScreenLANInfo(cont);
		}else {
			return cont;
		}
	}

}
