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

import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiNetworkSettingsButton extends Gui {

	private final GuiScreen screen;
	private final String text;
	private final Minecraft minecraft;

	public GuiNetworkSettingsButton(GuiScreen screen) {
		this.screen = screen;
		//audrey <3
		this.text = I18n.hasKey("directConnect.lanWorldRelay") ? I18n.format("directConnect.lanWorldRelay") : "Network Settings";
		this.minecraft = Minecraft.getInstance();
	}

	public void drawScreen(int mouseX, int mouseY) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.75f, 0.75f, 0.75f);
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		int w = minecraft.fontRenderer.getStringWidth(text);
		boolean hover = mouseX > 1 && mouseY > 1 && mouseX < (w * 3 / 4) + 7 && mouseY < 12;
		if(hover) {
			Mouse.showCursor(EnumCursorType.HAND);
		}
		drawString(minecraft.fontRenderer, TextFormatting.UNDERLINE + text, 5, 5, hover ? 0xFFEEEE22 : 0xFFCCCCCC);
		GlStateManager.popMatrix();
	}

	public void mouseClicked(int mouseX, int mouseY, int button) {
		int w = minecraft.fontRenderer.getStringWidth(text);
		if(mouseX > 2 && mouseY > 2 && mouseX < (w * 3 / 4) + 5 && mouseY < 12) {
			if(LANServerController.supported()) {
				minecraft.displayGuiScreen(GuiScreenLANInfo.showLANInfoScreen(new GuiScreenRelay(screen)));
			}else {
				minecraft.displayGuiScreen(new GuiScreenLANNotSupported(screen));
			}
		}
	}

}
