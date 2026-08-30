package net.lax1dude.eaglercraft.v1_8.cookie;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import net.lax1dude.eaglercraft.v1_8.cookie.ServerCookieDataStore.ServerCookie;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenInspectSessionToken extends GuiScreen {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yyyy h:mm aa", Locale.US);

	private final GuiScreen parent;
	private final ServerCookie cookie;

	public GuiScreenInspectSessionToken(GuiScreen parent, ServerCookie cookie) {
		this.parent = parent;
		this.cookie = cookie;
	}

	protected void initGui() {
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 106, 200, 20, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenInspectSessionToken.this.mc.displayGuiScreen(GuiScreenInspectSessionToken.this.parent);
			}
		});
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		String server = cookie.server.length() > 32 ? cookie.server.substring(0, 30) + "..." : cookie.server;
		String[][] toDraw = new String[][] {
				{ I18n.format("inspectSessionToken.details.server"), I18n.format("inspectSessionToken.details.expires"), I18n.format("inspectSessionToken.details.length") },
				{ server, DATE_FORMAT.format(new Date(cookie.expires)), Integer.toString(cookie.cookie.length) } };
		int[] maxWidth = new int[2];
		for(int i = 0; i < 2; ++i) {
			String[] strs = toDraw[i];
			int w = 0;
			for(int j = 0; j < strs.length; ++j) {
				int k = this.fontRenderer.getStringWidth(strs[j]);
				if(k > w) {
					w = k;
				}
			}
			maxWidth[i] = w + 10;
		}
		int totalWidth = maxWidth[0] + maxWidth[1];
		drawCenteredString(this.fontRenderer, I18n.format("inspectSessionToken.title"), this.width / 2, 70, 16777215);
		drawString(this.fontRenderer, toDraw[0][0], (this.width - totalWidth) / 2, 90, 11184810);
		drawString(this.fontRenderer, toDraw[0][1], (this.width - totalWidth) / 2, 104, 11184810);
		drawString(this.fontRenderer, toDraw[0][2], (this.width - totalWidth) / 2, 118, 11184810);
		drawString(this.fontRenderer, toDraw[1][0], (this.width - totalWidth) / 2 + maxWidth[0], 90, 11184810);
		drawString(this.fontRenderer, toDraw[1][1], (this.width - totalWidth) / 2 + maxWidth[0], 104, 11184810);
		drawString(this.fontRenderer, toDraw[1][2], (this.width - totalWidth) / 2 + maxWidth[0], 118, 11184810);
		super.render(mouseX, mouseY, partialTick);
	}

	public void close() {
		this.mc.displayGuiScreen(parent);
	}
}
