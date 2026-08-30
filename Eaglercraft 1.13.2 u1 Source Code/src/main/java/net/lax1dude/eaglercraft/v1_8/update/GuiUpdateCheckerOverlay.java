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

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GuiUpdateCheckerOverlay extends Gui {

	private static final ResourceLocation eaglerIcons = new ResourceLocation("eagler:gui/eagler_gui.png");

	private Minecraft mc;
	private int width;
	private int height;
	private int totalHeightOffset = 0;
	private final boolean isIngame;
	private final GuiScreen backScreen;
	private GuiButton checkForUpdatesButton;
	private GuiButton startDownloadButton;
	private GuiButton viewAllUpdatesButton;
	private GuiButton dismissUpdatesButton;

	public GuiUpdateCheckerOverlay(boolean isIngame, GuiScreen screen) {
		this.isIngame = isIngame;
		this.backScreen = screen;
	}

	public void setResolution(Minecraft mc, int w, int h) {
		if(!UpdateService.supported()) {
			return;
		}
		this.mc = mc;
		this.width = w;
		this.height = h;
		checkForUpdatesButton = new GuiButton(0, 0, 0, 150, 20, updateButtonText()) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateCheckerOverlay.this.onButtonPressed(this);
			}
		};
		startDownloadButton = new GuiButton(1, 1, 0, 115, 20, I18n.format("update.startDownload")) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateCheckerOverlay.this.onButtonPressed(this);
			}
		};
		viewAllUpdatesButton = new GuiButton(2, 1, 0, 115, 20, I18n.format("update.viewAll", 0)) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateCheckerOverlay.this.onButtonPressed(this);
			}
		};
		dismissUpdatesButton = new GuiButton(3, 1, 0, 115, 20, I18n.format("update.dismiss")) {
			public void onClick(double mouseX, double mouseY) {
				GuiUpdateCheckerOverlay.this.onButtonPressed(this);
			}
		};
	}

	public void render(int mouseX, int mouseY, float partialTicks) {
		if(!UpdateService.supported() || checkForUpdatesButton == null) {
			return;
		}
		UpdateProgressStruct progressState = UpdateService.getUpdatingStatus();
		if(progressState.isBusy) {
			drawScreenBusy(mouseX, mouseY, partialTicks, progressState);
			return;
		}

		checkForUpdatesButton.visible = isIngame;
		startDownloadButton.visible = false;
		viewAllUpdatesButton.visible = false;
		dismissUpdatesButton.visible = false;
		totalHeightOffset = 0;

		int updateCount = UpdateService.getAvailableUpdates().size();
		boolean shownSP = updateCount > 0 || !mc.isSingleplayer() || LANServerController.isHostingLAN();
		checkForUpdatesButton.visible &= shownSP;

		if(mc.gameSettings.enableUpdateSvc) {
			UpdateCertificate cert = UpdateService.getLatestUpdateFound();
			if(cert != null) {
				startDownloadButton.visible = true;
				viewAllUpdatesButton.visible = true;
				dismissUpdatesButton.visible = true;
				viewAllUpdatesButton.displayString = I18n.format("update.viewAll", updateCount);
				mc.fontRenderer.drawStringWithShadow(I18n.format("update.found"), 3, 22, 0xFFFFAA);

				int embedY = 35;
				int embedWidth = 115;
				int embedWidth2 = (int)(embedWidth / 0.75F);
				List<String> comment = cert.bundleVersionComment.length() == 0 ? null : mc.fontRenderer.listFormattedStringToWidth(cert.bundleVersionComment, embedWidth2 - 14);
				int embedHeight = 44;
				if(comment != null) {
					embedHeight += 3 + comment.size() * 6;
				}

				GlStateManager.pushMatrix();
				GlStateManager.translate(1.0F, embedY, 0.0F);
				GlStateManager.scale(0.75F, 0.75F, 0.75F);
				int embedHeight2 = (int)(embedHeight / 0.75F);
				drawGradientRect(1, 1, embedWidth2 - 1, embedHeight2 - 1, 0xFFFFFFAA, 0xFFFFFFAA);
				drawGradientRect(0, 1, embedWidth2, 2, 0xFF000000, 0xFF000000);
				drawGradientRect(0, embedHeight2 - 1, embedWidth2, embedHeight2, 0xFF000000, 0xFF000000);
				drawGradientRect(0, 1, 1, embedHeight2 - 1, 0xFF000000, 0xFF000000);
				drawGradientRect(embedWidth2 - 1, 1, embedWidth2, embedHeight2 - 1, 0xFF000000, 0xFF000000);

				mc.getTextureManager().bindTexture(eaglerIcons);
				GlStateManager.pushMatrix();
				GlStateManager.scale(0.3F, 0.3F, 0.3F);
				drawGradientRect(23, 23, 127, 127, 0xFF000000, 0xFF000000);
				EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
				drawTexturedModalRect(25, 25, 156, 0, 100, 100);
				EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
				GlStateManager.popMatrix();

				mc.fontRenderer.drawString(TextFormatting.UNDERLINE + cert.bundleDisplayName, 45, 11, 0x000000);
				mc.fontRenderer.drawString(I18n.format("update.update") + " " + TextFormatting.DARK_RED + cert.bundleDisplayVersion, 45, 25, 0x000000);
				if(comment != null) {
					for(int j = 0, l = comment.size(); j < l; ++j) {
						mc.fontRenderer.drawString(comment.get(j), 5, 42 + j * 8, 0x000000);
					}
				}
				mc.fontRenderer.drawString(I18n.format("update.author") + " " + cert.bundleAuthorName, 5, 44 + (comment == null ? 0 : (3 + comment.size() * 8)), 0x777777);

				startDownloadButton.y = embedHeight + embedY + 5;
				viewAllUpdatesButton.y = startDownloadButton.y + 22;
				dismissUpdatesButton.y = viewAllUpdatesButton.y + 22;
				totalHeightOffset = dismissUpdatesButton.y + 20;
				GlStateManager.popMatrix();
			}else if(isIngame && shownSP) {
				mc.fontRenderer.drawStringWithShadow(I18n.format("update.noneNew"), 3, 22, 0xDDDDDD);
				if(updateCount > 0) {
					viewAllUpdatesButton.y = 40;
					viewAllUpdatesButton.visible = true;
					viewAllUpdatesButton.displayString = I18n.format("update.viewAll", updateCount);
					totalHeightOffset = 60;
				}else {
					totalHeightOffset = 32;
				}
			}
		}

		checkForUpdatesButton.render(mouseX, mouseY, partialTicks);
		startDownloadButton.render(mouseX, mouseY, partialTicks);
		viewAllUpdatesButton.render(mouseX, mouseY, partialTicks);
		dismissUpdatesButton.render(mouseX, mouseY, partialTicks);
	}

	public void drawScreenBusy(int mouseX, int mouseY, float partialTicks, UpdateProgressStruct progressState) {
		if(!UpdateService.supported()) {
			return;
		}
		checkForUpdatesButton.visible = false;
		startDownloadButton.visible = false;
		viewAllUpdatesButton.visible = false;
		dismissUpdatesButton.visible = false;
		GlStateManager.pushMatrix();
		GlStateManager.translate(1.0F, isIngame ? 0.0F : 18.0F, 0.0F);
		mc.fontRenderer.drawStringWithShadow(I18n.format("update.downloading"), 2, 2, 0xFFFFAA);
		GlStateManager.translate(0.0F, 14.0F, 0.0F);
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		if(!StringUtils.isAllBlank(progressState.statusString1)) {
			mc.fontRenderer.drawStringWithShadow(progressState.statusString1, 3, 0, 0xFFFFFF);
		}
		int cc = isIngame ? 0xBBBBBB : 0xFFFFFF;
		if(!StringUtils.isAllBlank(progressState.statusString2)) {
			mc.fontRenderer.drawStringWithShadow(progressState.statusString2, 3, 11, cc);
		}
		int progX1 = 3;
		int progY1 = 22;
		int progX2 = 135;
		int progY2 = 32;
		float prog = progressState.progressBar;
		if(prog >= 0.0F) {
			int split = progX1 + (int)((progX2 - progX1 - 1) * prog);
			drawGradientRect(progX1 + 1, progY1 + 1, split, progY2 - 1, 0xFFDD0000, 0xFFDD0000);
			drawGradientRect(split, progY1 + 1, progX2 - 1, progY2 - 1, 0xFFBBBBBB, 0xFFBBBBBB);
			drawGradientRect(progX1, progY1, progX2, progY1 + 1, 0xFF000000, 0xFF000000);
			drawGradientRect(progX1, progY2 - 1, progX2, progY2, 0xFF000000, 0xFF000000);
			drawGradientRect(progX1, progY1 + 1, progX1 + 1, progY2 - 1, 0xFF000000, 0xFF000000);
			drawGradientRect(progX2 - 1, progY1 + 1, progX2, progY2 - 1, 0xFF000000, 0xFF000000);
		}
		totalHeightOffset = 32;
		if(!StringUtils.isAllBlank(progressState.statusString3)) {
			GlStateManager.translate(0.0F, progY2 + 2, 0.0F);
			GlStateManager.scale(0.66F, 0.66F, 0.66F);
			List<String> wrappedURL = mc.fontRenderer.listFormattedStringToWidth(progressState.statusString3, (int)((progX2 - progX1) * 1.5F));
			for(int i = 0, l = wrappedURL.size(); i < l; ++i) {
				mc.fontRenderer.drawStringWithShadow(wrappedURL.get(i), 5, i * 11, cc);
			}
			totalHeightOffset += (int)(wrappedURL.size() * 5.5F);
		}
		GlStateManager.popMatrix();
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(!UpdateService.supported() || checkForUpdatesButton == null || button != 0) {
			return false;
		}
		return checkForUpdatesButton.mouseClicked(mouseX, mouseY, button) || startDownloadButton.mouseClicked(mouseX, mouseY, button) || viewAllUpdatesButton.mouseClicked(mouseX, mouseY, button) || dismissUpdatesButton.mouseClicked(mouseX, mouseY, button);
	}

	private void onButtonPressed(GuiButton button) {
		if(button == checkForUpdatesButton) {
			mc.gameSettings.enableUpdateSvc = !mc.gameSettings.enableUpdateSvc;
			mc.gameSettings.saveOptions();
			checkForUpdatesButton.displayString = updateButtonText();
		}else if(button == startDownloadButton) {
			if(!UpdateService.getUpdatingStatus().isBusy) {
				UpdateCertificate cert = UpdateService.getLatestUpdateFound();
				if(cert != null) {
					UpdateService.startClientUpdateFrom(cert);
				}
			}
		}else if(button == viewAllUpdatesButton) {
			mc.displayGuiScreen(new GuiUpdateVersionList(backScreen));
		}else if(button == dismissUpdatesButton) {
			UpdateCertificate cert = UpdateService.getLatestUpdateFound();
			if(cert != null) {
				UpdateService.dismiss(cert);
			}
		}
	}

	private String updateButtonText() {
		return I18n.format("update.button") + " " + I18n.format(mc.gameSettings.enableUpdateSvc ? "gui.yes" : "gui.no");
	}

	public int getSharedWorldInfoYOffset() {
		return totalHeightOffset;
	}
}
