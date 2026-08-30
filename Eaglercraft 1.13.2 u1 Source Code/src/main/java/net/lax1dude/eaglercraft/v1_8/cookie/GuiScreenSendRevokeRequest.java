package net.lax1dude.eaglercraft.v1_8.cookie;

import org.json.JSONObject;
import net.lax1dude.eaglercraft.v1_8.cookie.ServerCookieDataStore.ServerCookie;
import net.lax1dude.eaglercraft.v1_8.internal.IServerQuery;
import net.lax1dude.eaglercraft.v1_8.internal.QueryResponse;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.minecraft.GuiScreenGenericErrorMessage;
import net.lax1dude.eaglercraft.v1_8.socket.ServerQueryDispatch;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenSendRevokeRequest extends GuiScreen {

	private static final Logger logger = LogManager.getLogger("SessionRevokeRequest");

	private final GuiScreen parent;
	private final ServerCookie cookie;
	private String message;
	private int timer = 0;
	private boolean cancelRequested = false;
	private IServerQuery query = null;
	private boolean hasSentPacket = false;
	private GuiButton cancelButton;

	public GuiScreenSendRevokeRequest(GuiScreen parent, ServerCookie cookie) {
		this.parent = parent;
		this.cookie = cookie;
		this.message = I18n.format("revokeSendingScreen.message.opening", cookie.server);
	}

	protected void initGui() {
		this.cancelButton = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 96, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenSendRevokeRequest.this.cancelRequested = true;
				this.enabled = false;
			}
		});
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		drawCenteredString(this.fontRenderer, I18n.format("revokeSendingScreen.title"), this.width / 2, 70, 11184810);
		drawCenteredString(this.fontRenderer, message, this.width / 2, 90, 16777215);
		super.render(mouseX, mouseY, partialTick);
	}

	public void tick() {
		++timer;
		if(timer > 1) {
			if(query == null) {
				logger.info("Attempting to revoke session tokens for: {}", cookie.server);
				query = ServerQueryDispatch.sendServerQuery(cookie.server, "revoke_session_token");
				if(query == null) {
					this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", "revokeFailure.desc.connectionError", parent));
					return;
				}
			}else {
				query.update();
				QueryResponse resp = query.getResponse();
				if(resp != null) {
					if(resp.responseType.equalsIgnoreCase("revoke_session_token") && (hasSentPacket ? resp.isResponseJSON() : resp.isResponseString())) {
						if(!hasSentPacket) {
							String str = resp.getResponseString();
							if("ready".equalsIgnoreCase(str)) {
								hasSentPacket = true;
								message = I18n.format("revokeSendingScreen.message.sending");
								query.send(cookie.cookie);
								return;
							}else {
								this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", "revokeFailure.desc.clientError", parent));
								return;
							}
						}else {
							JSONObject json = resp.getResponseJSON();
							String stat = json.optString("status");
							if("ok".equalsIgnoreCase(stat)) {
								query.close();
								this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeSuccess.title", "revokeSuccess.desc", parent));
								ServerCookieDataStore.clearCookie(cookie.server);
								return;
							}else if("error".equalsIgnoreCase(stat)) {
								int code = json.optInt("code", -1);
								if(code == -1) {
									query.close();
									this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", "revokeFailure.desc.clientError", parent));
									return;
								}else {
									String key;
									switch(code) {
									case 1:
										key = "revokeFailure.desc.notSupported";
										break;
									case 2:
										key = "revokeFailure.desc.notAllowed";
										break;
									case 3:
										key = "revokeFailure.desc.notFound";
										break;
									case 4:
										key = "revokeFailure.desc.serverError";
										break;
									default:
										key = "revokeFailure.desc.genericCode";
										break;
									}
									logger.error("Recieved error code {}! ({})", code, key);
									query.close();
									this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", key, parent));
									if(json.optBoolean("delete", false)) {
										ServerCookieDataStore.clearCookie(cookie.server);
									}
									return;
								}
							}else {
								logger.error("Recieved unknown status \"{}\"!", stat);
								query.close();
								this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", "revokeFailure.desc.clientError", parent));
								return;
							}
						}
					}else {
						query.close();
						this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", "revokeFailure.desc.clientError", parent));
						return;
					}
				}
				if(query.isClosed()) {
					if(!hasSentPacket || query.responsesAvailable() == 0) {
						this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", "revokeFailure.desc.connectionError", parent));
						return;
					}
				}else {
					if(timer > 400) {
						query.close();
						this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", "revokeFailure.desc.connectionError", parent));
						return;
					}
				}
				if(cancelRequested) {
					query.close();
					this.mc.displayGuiScreen(new GuiScreenGenericErrorMessage("revokeFailure.title", "revokeFailure.desc.cancelled", parent));
				}
			}
		}
	}

	public void onGuiClosed() {
		if(query != null && !query.isClosed()) {
			query.close();
		}
	}
}
