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

package net.lax1dude.eaglercraft.v1_8.webview;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiScreenRequestDisplay extends GuiScreen {

	private static final ResourceLocation beaconGuiTexture = new ResourceLocation("textures/gui/container/beacon.png");

	private final GuiScreen cont;
	private final GuiScreen back;
	private final NetHandlerPlayClient netHandler;
	private boolean mouseOverCheck;
	private boolean hasCheckedBox;

	public GuiScreenRequestDisplay(GuiScreen cont, GuiScreen back, NetHandlerPlayClient netHandler) {
		this.cont = cont;
		this.back = back;
		this.netHandler = netHandler;
	}

	@Override
	protected void initGui() {
		this.addButton(new GuiButton(0, this.width / 2 + 2, this.height / 6 + 122, 148, 20,
				I18n.format("webviewPhishingWarning.continue")) {
			public void onClick(double mouseX, double mouseY) {
				if (GuiScreenRequestDisplay.this.hasCheckedBox) {
					GuiScreenRequestDisplay.this.netHandler.allowedDisplayWebview = true;
					GuiScreenRequestDisplay.this.netHandler.allowedDisplayWebviewYes = true;
				}
				GuiScreenRequestDisplay.this.mc.displayGuiScreen(GuiScreenRequestDisplay.this.cont);
			}
		});
		this.addButton(new GuiButton(1, this.width / 2 - 150, this.height / 6 + 122, 148, 20,
				I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				if (GuiScreenRequestDisplay.this.hasCheckedBox) {
					GuiScreenRequestDisplay.this.netHandler.allowedDisplayWebview = true;
					GuiScreenRequestDisplay.this.netHandler.allowedDisplayWebviewYes = false;
				}
				GuiScreenRequestDisplay.this.mc.displayGuiScreen(GuiScreenRequestDisplay.this.back);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		drawCenteredString(this.fontRenderer,
				TextFormatting.BOLD + I18n.format("webviewDisplayWarning.title"), this.width / 2, 70, 0xFF4444);
		drawCenteredString(this.fontRenderer, I18n.format("webviewDisplayWarning.text0"), this.width / 2, 90,
				16777215);
		drawCenteredString(this.fontRenderer, I18n.format("webviewDisplayWarning.text1"), this.width / 2, 102,
				16777215);

		String dontShowAgain = I18n.format("webviewPhishingWarning.dontShowAgain");
		int w = this.fontRenderer.getStringWidth(dontShowAgain) + 20;
		int ww = (this.width - w) / 2;
		this.drawString(this.fontRenderer, dontShowAgain, ww + 20, 125, 0xCCCCCC);

		mouseOverCheck = ww < mouseX && ww + 17 > mouseX && 121 < mouseY && 138 > mouseY;

		this.mc.getTextureManager().bindTexture(beaconGuiTexture);
		GlStateManager.color(mouseOverCheck ? 0.7F : 0.6F, mouseOverCheck ? 0.7F : 0.6F,
				mouseOverCheck ? 1.0F : 0.6F, 1.0F);
		GlStateManager.pushMatrix();
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		drawTexturedModalRect(ww * 4 / 3, 121 * 4 / 3, 22, 219, 22, 22);
		GlStateManager.popMatrix();

		if (hasCheckedBox) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.5F, 0.5F, 0.0F);
			drawTexturedModalRect(ww, 121, 90, 222, 16, 16);
			GlStateManager.popMatrix();
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		super.render(mouseX, mouseY, partialTick);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0 && mouseOverCheck) {
			hasCheckedBox = !hasCheckedBox;
			this.mc.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
