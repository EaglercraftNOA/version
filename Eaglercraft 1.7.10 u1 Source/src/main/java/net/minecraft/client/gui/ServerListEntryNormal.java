package net.minecraft.client.gui;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.imageio.ImageIO;

import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_SRC_ALPHA;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry {
	private static final Logger logger = LogManager.getLogger();
	private final GuiMultiplayer field_148303_c;
	private final Minecraft field_148300_d;
	private final ServerData field_148301_e;
	private long field_148298_f;
	private String field_148299_g;
	private DynamicTexture field_148305_h;
	private ResourceLocation field_148306_i;
	private static final String __OBFID = "CL_00000817";
	protected Minecraft mc = Minecraft.getMinecraft();
	private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
	private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation(
			"textures/gui/server_selection.png");

	protected ServerListEntryNormal(GuiMultiplayer p_i45048_1_, ServerData p_i45048_2_) {
		this.field_148303_c = p_i45048_1_;
		this.field_148301_e = p_i45048_2_;
		this.field_148300_d = Minecraft.getMinecraft();
		this.field_148306_i = new ResourceLocation("servers/" + p_i45048_2_.serverIP + "/icon");
		this.field_148305_h = (DynamicTexture) this.field_148300_d.getTextureManager().getTexture(this.field_148306_i);
	}

	public void func_148279_a(int i, int j, int k, int l, int var5, Tessellator p_148279_6_, int i1, int j1,
			boolean p_148279_9_) {
		if (!this.field_148301_e.field_78841_f) {
			this.field_148301_e.field_78841_f = true;
			this.field_148301_e.pingToServer = -2L;
			this.field_148301_e.serverMOTD = "";
			this.field_148301_e.populationInfo = "";
		}

		boolean flag1 = this.field_148301_e.version > 47;
		boolean flag2 = this.field_148301_e.version < 47;
		boolean flag3 = flag1 || flag2;
		this.mc.fontRenderer.drawString(this.field_148301_e.serverName, j + 32 + 3, k + 1, 16777215);
		List list = this.mc.fontRenderer.listFormattedStringToWidth(this.field_148301_e.serverMOTD, l - 32 - 2);

		for (int k1 = 0; k1 < 2; ++k1) {
			if (k1 < list.size()) {
				this.mc.fontRenderer.drawString((String) list.get(k1), j + 32 + 3,
						k + 12 + this.mc.fontRenderer.FONT_HEIGHT * k1, 8421504);
			} else if (k1 == 1) {
				this.mc.fontRenderer.drawString(
						this.field_148301_e.hideAddress ? I18n.format("selectServer.hiddenAddress", new Object[0])
								: this.field_148301_e.serverIP,
						j + 32 + 3, k + 12 + this.mc.fontRenderer.FONT_HEIGHT * k1 + k1, 0x444444);
			}
		}

		String s2 = flag3 ? EnumChatFormatting.DARK_RED + this.field_148301_e.gameVersion
				: this.field_148301_e.populationInfo;
		int l1 = this.mc.fontRenderer.getStringWidth(s2);
		this.mc.fontRenderer.drawString(s2, j + l - l1 - 15 - 2, k + 1, 8421504);
		byte b0 = 0;
		String s = null;
		int i2;
		String s1;
		if (flag3) {
			i2 = 5;
			s1 = flag1 ? "Client out of date!" : "Server out of date!";
			s = this.field_148301_e.playerList;
		} else if (this.field_148301_e.field_78841_f && this.field_148301_e.pingToServer != -2L) {
			if (this.field_148301_e.pingToServer < 0L) {
				i2 = 5;
			} else if (this.field_148301_e.pingToServer < 150L) {
				i2 = 0;
			} else if (this.field_148301_e.pingToServer < 300L) {
				i2 = 1;
			} else if (this.field_148301_e.pingToServer < 600L) {
				i2 = 2;
			} else if (this.field_148301_e.pingToServer < 1000L) {
				i2 = 3;
			} else {
				i2 = 4;
			}

			if (this.field_148301_e.pingToServer < 0L) {
				s1 = "(no connection)";
			} else {
				s1 = this.field_148301_e.pingToServer + "ms";
				s = this.field_148301_e.playerList;
			}
		} else {
			b0 = 1;
			i2 = (int) (Minecraft.getSystemTime() / 100L + (long) (i * 2) & 7L);
			if (i2 > 4) {
				i2 = 8 - i2;
			}

			s1 = "Pinging...";
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(Gui.icons);
		Gui.func_146110_a(j + l - 15, k, (float) (b0 * 10), (float) (176 + i2 * 8), 10, 8, 256.0F, 256.0F);
		if (this.mc.gameSettings.touchscreen || p_148279_9_) {
			GlStateManager.enableShaderBlendAdd();
			GlStateManager.setShaderBlendSrc(0.6f, 0.6f, 0.6f, 1.0f);
			GlStateManager.setShaderBlendAdd(0.3f, 0.3f, 0.3f, 0.0f);
		}
		if (field_148301_e.iconTextureObject != null) {
			this.func_178012_a(j, k, field_148301_e.iconResourceLocation);
		} else {
			this.func_178012_a(j, k, UNKNOWN_SERVER);
		}
		if (this.mc.gameSettings.touchscreen || p_148279_9_) {
			GlStateManager.disableShaderBlendAdd();
		}

		int j2 = i1 - j;
		int k2 = j1 - k;
		if (j2 >= l - 15 && j2 <= l - 5 && k2 >= 0 && k2 <= 8) {
			this.field_148303_c.setHoveringText(s1);
		} else if (j2 >= l - l1 - 15 - 2 && j2 <= l - 15 - 2 && k2 >= 0 && k2 <= 8) {
			this.field_148303_c.setHoveringText(s);
		}

		if (this.mc.gameSettings.touchscreen || p_148279_9_) {
			this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int l2 = i1 - j;
			int i3 = j1 - k;
			if (this.func_178013_b()) {
				if (l2 < 32 && l2 > 16) {
					Gui.func_146110_a(j, k, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					Gui.func_146110_a(j, k, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.field_148303_c.func_175392_a(this, i)) {
				if (l2 < 16 && i3 < 16) {
					Gui.func_146110_a(j, k, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					Gui.func_146110_a(j, k, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}

			if (this.field_148303_c.func_175394_b(this, i)) {
				if (l2 < 16 && i3 > 16) {
					Gui.func_146110_a(j, k, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
				} else {
					Gui.func_146110_a(j, k, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
				}
			}
		}
	}

	private boolean func_178013_b() {
		return true;
	}

	protected void func_178012_a(int parInt1, int parInt2, ResourceLocation parResourceLocation) {
		this.mc.getTextureManager().bindTexture(parResourceLocation);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		Gui.func_146110_a(parInt1, parInt2, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
	}

	private void func_148297_b() {
		if (this.field_148301_e.func_147409_e() == null) {
			this.field_148300_d.getTextureManager().getTexture(this.field_148306_i);
			this.field_148305_h = null;
		} else {
			ByteBuf var2 = Unpooled.copiedBuffer(this.field_148301_e.func_147409_e(), Charsets.UTF_8);
			ByteBuf var3 = Base64.decode(var2);
			BufferedImage var1;
			label74: {
				try {
					var1 = ImageIO.read(new ByteBufInputStream(var3));
					Validate.validState(var1.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
					Validate.validState(var1.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
					break label74;
				} catch (Exception var8) {
					logger.error("Invalid icon for server " + this.field_148301_e.serverName + " ("
							+ this.field_148301_e.serverIP + ")", var8);
					this.field_148301_e.func_147407_a((String) null);
				}

				return;
			}

			if (this.field_148305_h == null) {
				this.field_148305_h = new DynamicTexture(var1.getWidth(), var1.getHeight());
				this.field_148300_d.getTextureManager().loadTexture(this.field_148306_i, this.field_148305_h);
			}

			var1.getRGB(0, 0, var1.getWidth(), var1.getHeight(), this.field_148305_h.getTextureData(), 0,
					var1.getWidth());
			this.field_148305_h.updateDynamicTexture();
		}
	}

	public boolean func_148278_a(int p_148278_1_, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
			int p_148278_6_) {
		this.field_148303_c.func_146790_a(p_148278_1_);

		if (Minecraft.getSystemTime() - this.field_148298_f < 250L) {
			this.field_148303_c.func_146796_h();
		}

		this.field_148298_f = Minecraft.getSystemTime();
		return false;
	}

	public void func_148277_b(int p_148277_1_, int p_148277_2_, int p_148277_3_, int p_148277_4_, int p_148277_5_,
			int p_148277_6_) {
	}

	public ServerData func_148296_a() {
		return this.field_148301_e;
	}
}
