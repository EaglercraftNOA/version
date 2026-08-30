/*
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.socket;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiHandshakeApprove extends GuiScreen {

	protected final String message;
	protected final GuiScreen no;
	protected final GuiScreen yes;

	protected String titleString;
	protected List<String> bodyLines;

	protected int bodyY;

	public GuiHandshakeApprove(String message, GuiScreen no, GuiScreen yes) {
		this.message = message;
		this.no = no;
		this.yes = yes;
	}

	public GuiHandshakeApprove(String message, GuiScreen back) {
		this(message, back, null);
	}

	@Override
	protected void initGui() {
		titleString = I18n.format("handshakeApprove." + message + ".title");
		bodyLines = new ArrayList<>();
		int i = 0;
		boolean wasNull = true;
		while(true) {
			String line = getI18nOrNull("handshakeApprove." + message + ".body." + (i++));
			if(line == null) {
				if(wasNull) {
					break;
				}else {
					bodyLines.add("");
					wasNull = true;
				}
			}else {
				bodyLines.add(line);
				wasNull = false;
			}
		}
		int totalHeight = 10 + 10 + bodyLines.size() * 10 + 10 + 20;
		bodyY = (height - totalHeight) / 2 - 15;
		int buttonY = bodyY + totalHeight - 20;
		if(yes != null) {
			this.addButton(new GuiButton(0, width / 2 + 3, buttonY, 100, 20, I18n.format("gui.no")) {
				public void onClick(double mouseX, double mouseY) {
					GuiHandshakeApprove.this.mc.displayGuiScreen(GuiHandshakeApprove.this.no);
				}
			});
			this.addButton(new GuiButton(1, width / 2 - 103, buttonY, 100, 20, I18n.format("gui.yes")) {
				public void onClick(double mouseX, double mouseY) {
					GuiHandshakeApprove.this.mc.displayGuiScreen(GuiHandshakeApprove.this.yes);
				}
			});
		}else {
			this.addButton(new GuiButton(0, width / 2 - 100, buttonY, 200, 20, I18n.format("gui.back")) {
				public void onClick(double mouseX, double mouseY) {
					GuiHandshakeApprove.this.mc.displayGuiScreen(GuiHandshakeApprove.this.no);
				}
			});
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		drawCenteredString(fontRenderer, titleString, width / 2, bodyY, 16777215);
		for(int i = 0, l = bodyLines.size(); i < l; ++i) {
			String s = bodyLines.get(i);
			if(s.length() > 0) {
				drawCenteredString(fontRenderer, s, width / 2, bodyY + 20 + i * 10, 16777215);
			}
		}
		super.render(mouseX, mouseY, partialTicks);
	}

	private String getI18nOrNull(String key) {
		String ret = I18n.format(key);
		return key.equals(ret) ? null : ret;
	}

	public static GuiScreen displayAuthProtocolConfirm(int protocol, GuiScreen no, GuiScreen yes) {
		if(protocol == HandshakePacketTypes.AUTH_METHOD_PLAINTEXT) {
			return new GuiHandshakeApprove("plaintext", no, yes);
		}else if(protocol != HandshakePacketTypes.AUTH_METHOD_EAGLER_SHA256 && protocol != HandshakePacketTypes.AUTH_METHOD_AUTHME_SHA256) {
			return new GuiHandshakeApprove("unsupportedAuth", no);
		}else {
			return null;
		}
	}

}
