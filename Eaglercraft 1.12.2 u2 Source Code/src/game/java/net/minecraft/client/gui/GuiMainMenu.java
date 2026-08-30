package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.EaglercraftVersion;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.internal.PlatformApplication;
import net.lax1dude.eaglercraft.internal.PlatformOpenGL;
import net.lax1dude.eaglercraft.minecraft.MainMenuSkyboxTexture;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenIntegratedServerBusy;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenIntegratedServerStartup;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResource;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import net.peyton.eagler.gui.GuiCredits;
import net.lax1dude.eaglercraft.profile.*;

public class GuiMainMenu extends GuiScreen {
	private static final EaglercraftRandom RANDOM = new EaglercraftRandom();

	/** Counts the number of screen updates. */
	private final float updateCounter;

	/** The splash message. */
	private String splashText;
	private GuiButton buttonResetDemo;

	/** Timer used to rotate the panorama, increases every tick. */
	private float panoramaTimer;

	/**
	 * Texture allocated for the current viewport of the main menu's panorama
	 * background.
	 */
	private static MainMenuSkyboxTexture viewportTexture = null;
	private static MainMenuSkyboxTexture viewportTexture2 = null;

	/**
	 * The Object object utilized as a thread lock when performing non thread-safe
	 * operations
	 */
	// private final Object threadLock = new Object();
	public static final String MORE_INFO_TEXT = "Please click " + TextFormatting.UNDERLINE + "here"
			+ TextFormatting.RESET + " for more information.";

	private static final ResourceLocation SPLASH_TEXTS = new ResourceLocation("texts/splashes.txt");
	private static final ResourceLocation MINECRAFT_TITLE_TEXTURES = new ResourceLocation(
			"textures/gui/title/minecraft.png");
	private static final ResourceLocation field_194400_H = new ResourceLocation("textures/gui/title/edition.png");

	/** An array of all the paths to the panorama pictures. */
	private static final ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[] {
			new ResourceLocation("textures/gui/title/background/panorama_0.png"),
			new ResourceLocation("textures/gui/title/background/panorama_1.png"),
			new ResourceLocation("textures/gui/title/background/panorama_2.png"),
			new ResourceLocation("textures/gui/title/background/panorama_3.png"),
			new ResourceLocation("textures/gui/title/background/panorama_4.png"),
			new ResourceLocation("textures/gui/title/background/panorama_5.png") };
	private ResourceLocation backgroundTexture = null;
	private static ResourceLocation backgroundTexture2 = null;

	private int field_193978_M;
	private int field_193979_N;

	public GuiMainMenu() {
		this.splashText = "missingno";
		IResource iresource = null;

		try {
			List<String> list = Lists.<String>newArrayList();
			iresource = Minecraft.getMinecraft().getResourceManager().getResource(SPLASH_TEXTS);
			BufferedReader bufferedreader = new BufferedReader(
					new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8));
			String s;

			while ((s = bufferedreader.readLine()) != null) {
				s = s.trim();

				if (!s.isEmpty()) {
					list.add(s);
				}
			}

			if (!list.isEmpty()) {
				while (true) {
					this.splashText = list.get(RANDOM.nextInt(list.size()));

					if (this.splashText.hashCode() != 125780783) {
						break;
					}
				}
			}
		} catch (IOException var8) {
			;
		} finally {
			IOUtils.closeQuietly((Closeable) iresource);
		}

		this.updateCounter = RANDOM.nextFloat();
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		if (!this.mc.gameSettings.hasSeenFirstLoad) {
			this.mc.displayGuiScreen(new GuiScreenFirstLoad(this.mc.gameSettings));
			return;
		}

		viewportTexture = new MainMenuSkyboxTexture(256, 256);
		this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
		viewportTexture2 = new MainMenuSkyboxTexture(256, 256);
		backgroundTexture2 = this.mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture2);
		this.field_193978_M = this.fontRendererObj.getStringWidth("Resources copyright Mojang AB");
		this.field_193979_N = this.width - this.field_193978_M - 2;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		if (calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
			this.splashText = I18n.format("eaglercraft.splashes.xmas");
		} else if (calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
			this.splashText = I18n.format("eaglercraft.splashes.newyear");
		} else if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
			this.splashText = I18n.format("eaglercraft.splashes.halloween");
		}

		int i = 24;
		int j = this.height / 4 + 48;

		if (this.mc.isDemo()) {
			this.addDemoButtons(j, 24);
		} else {
			this.addSingleplayerMultiplayerButtons(j, 24);
		}

		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, j + 72 + 12, 98, 20, I18n.format("menu.options")));
		this.buttonList.add(new GuiButton(4, this.width / 2 + 2, j + 72 + 12, 98, 20, I18n.format("Edit Profile")));
		this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, j + 72 + 12));
	}

	/**
	 * Adds Singleplayer and Multiplayer buttons on Main Menu for players who have
	 * bought the game.
	 */
	private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
		this.buttonList.add(new GuiButton(1, this.width / 2 - 100, p_73969_1_, I18n.format("menu.singleplayer")));
		this.buttonList.add(
				new GuiButton(2, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 1, I18n.format("menu.multiplayer")));
		this.buttonList
				.add(new GuiButton(14, this.width / 2 - 100, p_73969_1_ + p_73969_2_ * 2, I18n.format("menu.credits")));
	}

	/**
	 * Adds Demo buttons on Main Menu for players who are playing Demo.
	 */
	private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
		this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo")));
		this.buttonResetDemo = this.addButton(
				new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo")));
		ISaveFormat isaveformat = this.mc.getSaveLoader();
		WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

		if (worldinfo == null) {
			this.buttonResetDemo.enabled = false;
		}
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiScreenIntegratedServerStartup(this));
		}

		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (button.id == 4) {
			this.mc.displayGuiScreen(new GuiScreenEditProfile(this));
		}

		if (button.id == 11) {
			this.mc.launchIntegratedServer("Demo_World", "Demo_World", WorldServerDemo.DEMO_WORLD_SETTINGS);
		}

		if (button.id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");

			if (worldinfo != null) {
				this.mc.displayGuiScreen(new GuiYesNo(this, I18n.format("selectWorld.deleteQuestion"),
						"'" + worldinfo.getWorldName() + "' " + I18n.format("selectWorld.deleteWarning"),
						I18n.format("selectWorld.deleteButton"), I18n.format("gui.cancel"), 12));
			}
		}

		if (button.id == 14) {
			this.mc.displayGuiScreen(new GuiCredits(this, "/assets/eagler/credits.txt"));
		}
	}

	public void confirmClicked(boolean result, int id) {
		if (result && id == 12) {
			ISaveFormat isaveformat = this.mc.getSaveLoader();
			isaveformat.flushCache();
			isaveformat.deleteWorldDirectory("Demo_World");
			this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(this, "singleplayer.busy.deleting",
					"singleplayer.failed.deleting", SingleplayerServerController::isReady));
		} else if (id == 12) {
			this.mc.displayGuiScreen(this);
		}
	}

	/**
	 * Draws the main menu panorama
	 */
	private void drawPanorama(int mouseX, int mouseY, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer bufferbuilder = tessellator.getBuffer();
		GlStateManager.matrixMode(5889);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		GlStateManager.disableCull();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA,
				RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
		byte b0 = 4;

		for (int i = 0; i < b0 * b0; ++i) {
			GlStateManager.pushMatrix();
			float f = ((float) (i % b0) / (float) b0 - 0.5F) / 64.0F;
			float f1 = ((float) (i / b0) / (float) b0 - 0.5F) / 64.0F;
			float f2 = 0.0F;
			GlStateManager.translate(f, f1, f2);
			GlStateManager.rotate(MathHelper.sin(((float) this.panoramaTimer + partialTicks) / 400.0F) * 25.0F + 20.0F,
					1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-((float) this.panoramaTimer + partialTicks) * 0.1F, 0.0F, 1.0F, 0.0F);

			for (int j = 0; j < 6; ++j) {
				GlStateManager.pushMatrix();
				if (j == 1) {
					GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (j == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (j == 3) {
					GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (j == 4) {
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (j == 5) {
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				this.mc.getTextureManager().bindTexture(TITLE_PANORAMA_PATHS[j]);

				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				int k = 255 / (i + 1);
				float f3 = 0.0F;
				bufferbuilder.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, k).endVertex();
				bufferbuilder.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, k).endVertex();
				bufferbuilder.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, k).endVertex();
				bufferbuilder.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, k).endVertex();
				tessellator.draw();
				GlStateManager.popMatrix();
			}

			GlStateManager.popMatrix();
			GlStateManager.colorMask(true, true, true, false);
		}

		bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
		GlStateManager.colorMask(true, true, true, true);
		GlStateManager.matrixMode(5889);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
	}

	/**
	 * Rotate and blurs the skybox view in the main menu
	 */
	private void rotateAndBlurSkybox() {
		EaglercraftGPU.glTexParameteri(3553, 10241, 9729);
		EaglercraftGPU.glTexParameteri(3553, 10240, 9729);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA,
				RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
		GlStateManager.colorMask(true, true, true, false);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		GlStateManager.disableAlpha();
		int i = 3;

		for (int j = 0; j < 3; ++j) {
			float f = 1.0F / (float) (j + 1);
			int k = this.width;
			int l = this.height;
			float f1 = (float) (j - 1) / 256.0F;
			bufferbuilder.pos((double) k, (double) l, (double) this.zLevel).tex((double) (0.0F + f1), 1.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			bufferbuilder.pos((double) k, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 1.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			bufferbuilder.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (1.0F + f1), 0.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
			bufferbuilder.pos(0.0D, (double) l, (double) this.zLevel).tex((double) (0.0F + f1), 0.0D)
					.color(1.0F, 1.0F, 1.0F, f).endVertex();
		}

		tessellator.draw();
		GlStateManager.enableAlpha();
		GlStateManager.colorMask(true, true, true, true);
	}

	/**
	 * Renders the skybox in the main menu
	 */
	private void renderSkybox(int mouseX, int mouseY, float partialTicks) {
		viewportTexture.bindFramebuffer();
		GlStateManager.viewport(0, 0, 256, 256);
		GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GlStateManager.clear(RealOpenGLEnums.GL_COLOR_BUFFER_BIT);
		this.drawPanorama(mouseX, mouseY, partialTicks);
		viewportTexture2.bindFramebuffer();
		GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GlStateManager.clear(RealOpenGLEnums.GL_COLOR_BUFFER_BIT);
		this.mc.getTextureManager().bindTexture(backgroundTexture);
		this.rotateAndBlurSkybox();
		viewportTexture.bindFramebuffer();
		this.mc.getTextureManager().bindTexture(backgroundTexture2);
		this.rotateAndBlurSkybox();
		viewportTexture2.bindFramebuffer();
		this.mc.getTextureManager().bindTexture(backgroundTexture);
		this.rotateAndBlurSkybox();
		viewportTexture.bindFramebuffer();
		this.mc.getTextureManager().bindTexture(backgroundTexture2);
		this.rotateAndBlurSkybox();
		viewportTexture2.bindFramebuffer();
		this.mc.getTextureManager().bindTexture(backgroundTexture);
		this.rotateAndBlurSkybox();
		viewportTexture.bindFramebuffer();
		this.mc.getTextureManager().bindTexture(backgroundTexture2);
		this.rotateAndBlurSkybox();

		PlatformOpenGL._wglBindFramebuffer(0x8D40, null);
		this.mc.getTextureManager().bindTexture(backgroundTexture);
		
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		float f = 120.0F / (float) (this.width > this.height ? this.width : this.height);
		float f1 = (float) this.height * f / 256.0F;
		float f2 = (float) this.width * f / 256.0F;
		int i = this.width;
		int j = this.height;
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.0D, (double) j, (double) this.zLevel).tex((double) (0.5F - f1), (double) (0.5F + f2))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		bufferbuilder.pos((double) i, (double) j, (double) this.zLevel).tex((double) (0.5F - f1), (double) (0.5F - f2))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		bufferbuilder.pos((double) i, 0.0D, (double) this.zLevel).tex((double) (0.5F + f1), (double) (0.5F - f2))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, (double) this.zLevel).tex((double) (0.5F + f1), (double) (0.5F + f2))
				.color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
		tessellator.draw();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.panoramaTimer += partialTicks;
		GlStateManager.disableAlpha();
		this.renderSkybox(mouseX, mouseY, partialTicks);
		GlStateManager.enableAlpha();
		int i = 274;
		int j = this.width / 2 - 137;
		int k = 30;
		this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
		this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);
		this.mc.getTextureManager().bindTexture(MINECRAFT_TITLE_TEXTURES);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		if ((double) this.updateCounter < 1.0E-4D) {
			this.drawTexturedModalRect(j + 0, 30, 0, 0, 99, 44);
			this.drawTexturedModalRect(j + 99, 30, 129, 0, 27, 44);
			this.drawTexturedModalRect(j + 99 + 26, 30, 126, 0, 3, 44);
			this.drawTexturedModalRect(j + 99 + 26 + 3, 30, 99, 0, 26, 44);
			this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
		} else {
			this.drawTexturedModalRect(j + 0, 30, 0, 0, 155, 44);
			this.drawTexturedModalRect(j + 155, 30, 0, 45, 155, 44);
		}

		this.mc.getTextureManager().bindTexture(field_194400_H);

		drawModalRectWithCustomSizedTexture(j + 88, 67, 0.0F, 0.0F, 98, 14, 128.0F, 16.0F);

		GlStateManager.pushMatrix();
		GlStateManager.translate((float) (this.width / 2 + 90), 70.0F, 0.0F);
		GlStateManager.rotate(-20.0F, 0.0F, 0.0F, 1.0F);
		float f = 1.8F - MathHelper.abs(
				MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * ((float) Math.PI * 2F)) * 0.1F);
		f = f * 100.0F / (float) (this.fontRendererObj.getStringWidth(this.splashText) + 32);
		GlStateManager.scale(f, f, f);
		this.drawCenteredString(this.fontRendererObj, this.splashText, 0, -8, -256);
		GlStateManager.popMatrix();
		String s = "Minecraft 1.12.2";

		if (this.mc.isDemo()) {
			s = s + " Demo";
		}

		this.drawString(this.fontRendererObj, s, 2, this.height - 20, -1);
		this.drawString(this.fontRendererObj, "Eaglercraft 1.12.2 (" + EaglercraftVersion.projectOriginVersion + ")", 2,
				this.height - 10, -1);
		this.drawString(this.fontRendererObj, "Resources copyright Mojang AB", this.field_193979_N, this.height - 10,
				-1);

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if (mouseX > this.field_193979_N && mouseX < this.field_193979_N + this.field_193978_M
				&& mouseY > this.height - 10 && mouseY < this.height) {
			this.mc.displayGuiScreen(new GuiWinGame(false, null));
		}

	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
	}
}
