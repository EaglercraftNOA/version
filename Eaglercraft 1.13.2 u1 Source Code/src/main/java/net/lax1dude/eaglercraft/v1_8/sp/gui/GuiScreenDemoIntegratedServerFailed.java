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

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenDemoIntegratedServerFailed extends GuiScreen {

	private final String titleText;
	private final String descText;

	public GuiScreenDemoIntegratedServerFailed() {
		this.titleText = I18n.format("singleplayer.failed.demo.title");
		this.descText = I18n.format("singleplayer.failed.demo.desc");
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(fontRenderer, titleText, this.width / 2, 70, 11184810);
		this.drawCenteredString(fontRenderer, descText, this.width / 2, 90, 16777215);
		super.render(mouseX, mouseY, partialTicks);
	}
}
