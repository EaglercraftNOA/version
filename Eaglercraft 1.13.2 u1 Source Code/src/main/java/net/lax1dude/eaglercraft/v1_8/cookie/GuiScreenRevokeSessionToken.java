package net.lax1dude.eaglercraft.v1_8.cookie;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenRevokeSessionToken extends GuiScreen {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("M/d/yyyy h:mm aa", Locale.US);

	private final GuiScreen parent;
	private final List<ServerCookieDataStore.ServerCookie> cookies = new ArrayList<>();
	private int selected = -1;
	private GuiButton inspectButton;
	private GuiButton revokeButton;
	private GuiButton prevButton;
	private GuiButton nextButton;

	public GuiScreenRevokeSessionToken(GuiScreen parent) {
		this.parent = parent;
	}

	protected void initGui() {
		reloadCookieList();
		prevButton = this.addButton(new GuiButton(1, this.width / 2 - 100, 86, 20, 20, "<") {
			public void onClick(double mouseX, double mouseY) {
				if(!GuiScreenRevokeSessionToken.this.cookies.isEmpty()) {
					GuiScreenRevokeSessionToken.this.selected = (GuiScreenRevokeSessionToken.this.selected - 1 + GuiScreenRevokeSessionToken.this.cookies.size()) % GuiScreenRevokeSessionToken.this.cookies.size();
					GuiScreenRevokeSessionToken.this.updateButtons();
				}
			}
		});
		nextButton = this.addButton(new GuiButton(2, this.width / 2 + 80, 86, 20, 20, ">") {
			public void onClick(double mouseX, double mouseY) {
				if(!GuiScreenRevokeSessionToken.this.cookies.isEmpty()) {
					GuiScreenRevokeSessionToken.this.selected = (GuiScreenRevokeSessionToken.this.selected + 1) % GuiScreenRevokeSessionToken.this.cookies.size();
					GuiScreenRevokeSessionToken.this.updateButtons();
				}
			}
		});
		//audrey <3
		inspectButton = this.addButton(new GuiButton(10, this.width / 2 - 154, this.height - 38, 100, 20, eaglerFormat("revokeSessionToken.inspect", "Inspect")) {
			public void onClick(double mouseX, double mouseY) {
				ServerCookieDataStore.ServerCookie cookie = GuiScreenRevokeSessionToken.this.getSelectedCookie();
				if(cookie != null) {
					GuiScreenRevokeSessionToken.this.mc.displayGuiScreen(new GuiScreenInspectSessionToken(GuiScreenRevokeSessionToken.this, cookie));
				}
			}
		});
		//audrey <3
		revokeButton = this.addButton(new GuiButton(9, this.width / 2 - 50, this.height - 38, 100, 20, eaglerFormat("revokeSessionToken.revoke", "Revoke")) {
			public void onClick(double mouseX, double mouseY) {
				ServerCookieDataStore.ServerCookie cookie = GuiScreenRevokeSessionToken.this.getSelectedCookie();
				if(cookie != null) {
					GuiScreenRevokeSessionToken.this.mc.displayGuiScreen(new GuiScreenSendRevokeRequest(GuiScreenRevokeSessionToken.this, cookie));
				}
			}
		});
		this.addButton(new GuiButton(6, this.width / 2 + 54, this.height - 38, 100, 20, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenRevokeSessionToken.this.mc.displayGuiScreen(GuiScreenRevokeSessionToken.this.parent);
			}
		});
		updateButtons();
	}

	//audrey <3
	private static String eaglerFormat(String key, String fallback) {
		return I18n.hasKey(key) ? I18n.format(key) : fallback;
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		//audrey <3
		drawCenteredString(this.fontRenderer, eaglerFormat("revokeSessionToken.title", "Revoke Session Token"), this.width / 2, 16, 16777215);
		ServerCookieDataStore.ServerCookie cookie = getSelectedCookie();
		if(cookie == null) {
			//audrey <3
			drawCenteredString(this.fontRenderer, eaglerFormat("revokeFailure.desc.notFound", "No revokable session tokens were found"), this.width / 2, 92, 8421504);
		}else {
			drawCenteredString(this.fontRenderer, cookie.server, this.width / 2, 92, 16777215);
			//audrey <3
			drawCenteredString(this.fontRenderer, eaglerFormat("inspectSessionToken.details.expires", "Expires:") + " " + DATE_FORMAT.format(new Date(cookie.expires)), this.width / 2, 114, 8421504);
			//audrey <3
			drawCenteredString(this.fontRenderer, eaglerFormat("inspectSessionToken.details.length", "Length:") + " " + Integer.toString(cookie.cookie.length), this.width / 2, 126, 8421504);
		}
		//audrey <3
		drawCenteredString(this.fontRenderer, eaglerFormat("revokeSessionToken.note.0", "Only tokens saved by this client can be revoked."), this.width / 2, this.height - 66, 8421504);
		//audrey <3
		drawCenteredString(this.fontRenderer, eaglerFormat("revokeSessionToken.note.1", "Revoking may log you out of that server."), this.width / 2, this.height - 56, 8421504);
		super.render(mouseX, mouseY, partialTick);
	}

	public void close() {
		this.mc.displayGuiScreen(parent);
	}

	private void reloadCookieList() {
		cookies.clear();
		ServerCookieDataStore.flush();
		for(String server : ServerCookieDataStore.getRevokableServers()) {
			ServerCookieDataStore.ServerCookie cookie = ServerCookieDataStore.loadCookie(server);
			if(cookie != null) {
				cookies.add(cookie);
			}
		}
		cookies.sort(Comparator.comparing((cookie) -> cookie.server));
		if(cookies.isEmpty()) {
			selected = -1;
		}else if(selected < 0 || selected >= cookies.size()) {
			selected = 0;
		}
	}

	private void updateButtons() {
		boolean hasCookie = getSelectedCookie() != null;
		prevButton.enabled = hasCookie && cookies.size() > 1;
		nextButton.enabled = hasCookie && cookies.size() > 1;
		inspectButton.enabled = hasCookie;
		revokeButton.enabled = hasCookie;
	}

	private ServerCookieDataStore.ServerCookie getSelectedCookie() {
		return selected >= 0 && selected < cookies.size() ? cookies.get(selected) : null;
	}
}
