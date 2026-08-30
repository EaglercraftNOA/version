package net.minecraft.client;

import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_BACK;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_GREATER;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_LEQUAL;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_MAX_TEXTURE_SIZE;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_MODELVIEW;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_PROJECTION;
import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.GL_SMOOTH;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import net.lax1dude.eaglercraft.sp.gui.GuiScreenRelay;
import net.lax1dude.eaglercraft.sp.relay.RelayManager;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ListenableFuture;

import net.eymenwsmc.GuiAlert;
import net.eymenwsmc.Sys;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.PointerInputAbstraction;
import net.lax1dude.eaglercraft.futures.ListenableFutureTask;
import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.profile.EaglerProfile;
import net.lax1dude.eaglercraft.profile.GuiScreenEditProfile;
import net.lax1dude.eaglercraft.socket.EaglercraftNetworkManager;
import net.lax1dude.eaglercraft.sp.IntegratedServerState;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenIntegratedServerBusy;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenSingleplayerConnecting;
import net.lax1dude.eaglercraft.webview.WebViewOverlayController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.DefaultVertexFormats;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.IStatStringFormat;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;

public class Minecraft implements IPlayerUsage {
	private static final Logger logger = LogManager.getLogger();
	private static final ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
	public static final boolean isRunningOnMac = Util.getOSType() == Util.EnumOS.OSX;

	/** A 10MiB preallocation to ensure the heap is reasonably sized. */
	public static byte[] memoryReserve = new byte[10485760];
	private final Multimap field_152356_J;
	private ServerData currentServerData;

	/** The RenderEngine instance used by Minecraft */
	public TextureManager renderEngine;

	/**
	 * Set to 'this' in Minecraft constructor; used by some settings get methods
	 */
	private static Minecraft theMinecraft;
	public PlayerControllerMP playerController;
	private boolean fullscreen;
	private boolean hasCrashed;

	/** Instance of CrashReport. */
	private CrashReport crashReporter;
	public int displayWidth;
	public int displayHeight;
	private Timer timer = new Timer(20.0F);

	/** Instance of PlayerUsageSnooper. */
	private PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this,
			MinecraftServer.getSystemTimeMillis());
	public WorldClient theWorld;
	public RenderGlobal renderGlobal;
	public EntityClientPlayerMP thePlayer;

	/**
	 * The Entity from which the renderer determines the render viewpoint. Currently
	 * is always the parent Minecraft class's 'thePlayer' instance. Modification of
	 * its location, rotation, or other settings at render time will modify the
	 * camera likewise, with the caveat of triggering chunk rebuilds as it moves,
	 * making it unsuitable for changing the viewpoint mid-render.
	 */
	public EntityLivingBase renderViewEntity;
	public Entity pointedEntity;
	public EffectRenderer effectRenderer;
	public Session session;
	private boolean isGamePaused;

	/** The font renderer used for displaying and measuring text. */
	public FontRenderer fontRenderer;
	public FontRenderer standardGalacticFontRenderer;

	/** The GuiScreen that's being displayed at the moment. */
	public GuiScreen currentScreen;
	public LoadingScreenRenderer loadingScreen;
	public EntityRenderer entityRenderer;

	/** Mouse left click counter */
	private int leftClickCounter;

	/** Display width */
	private int tempDisplayWidth;

	/** Display height */
	private int tempDisplayHeight;

	/** Instance of IntegratedServer. */
	private IntegratedServer theIntegratedServer;

	/** Gui achievement */
	public GuiAchievement guiAchievement;
	public GuiIngame ingameGUI;

	/** Skip render world */
	public boolean skipRenderWorld;

	/** The ray trace hit that the mouse is over. */
	public MovingObjectPosition objectMouseOver;

	/** The game settings that currently hold effect. */
	public GameSettings gameSettings;

	/** Mouse helper instance. */
	public MouseHelper mouseHelper;
	public final VFile2 mcDataDir;
	private final VFile2 fileAssets;
	private final String launchedVersion;

	/**
	 * This is set to fpsCounter every debug screen update, and is shown on the
	 * debug screen. It's also sent as part of the usage snooping.
	 */
	private static int debugFPS;

	/**
	 * When you place a block, it's set to 6, decremented once per tick, when it's
	 * 0, you can place another block.
	 */
	private int rightClickDelayTimer;

	/**
	 * Checked in Minecraft's while(running) loop, if true it's set to false and the
	 * textures refreshed.
	 */
	private boolean refreshTexturePacksScheduled;
	private String serverName;
	private int serverPort;

	/**
	 * Does the actual gameplay have focus. If so then mouse and keys will effect
	 * the player instead of menus.
	 */
	public boolean inGameHasFocus;
	long systemTime = getSystemTime();

	/** Join player counter */
	private int joinPlayerCounter;
	private final boolean isDemo;
	private EaglercraftNetworkManager myNetworkManager;
	private boolean integratedServerIsRunning;

	/** The profiler instance */
	public final Profiler mcProfiler = new Profiler();
	private long field_83002_am = -1L;
	private IReloadableResourceManager mcResourceManager;
	private final IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
	private List defaultResourcePacks = Lists.newArrayList();
	private DefaultResourcePack mcDefaultResourcePack;
	private ResourcePackRepository mcResourcePackRepository;
	private LanguageManager mcLanguageManager;
	private TextureMap textureMapBlocks;
	private SoundHandler mcSoundHandler;
	private MusicTicker mcMusicTicker;
	private ResourceLocation field_152354_ay;
	private SkinManager field_152350_aA;
	private final Queue field_152351_aB = Queues.newArrayDeque();
	private final Thread field_152352_aC = Thread.currentThread();

	/**
	 * Set to true to keep the game loop running. Set to false by shutdown() to
	 * allow the game loop to exit cleanly.
	 */
	volatile boolean running = true;

	/** String that shows the debug information */
	public String debug = "";

	/** Approximate time (in ms) of last update to debug string */
	long debugUpdateTime = getSystemTime();

	/** holds the current fps */
	int fpsCounter;
	long prevFrameTime = -1L;

	/** Profiler currently displayed in the debug screen pie chart */
	private String debugProfilerName = "root";
	private static final String __OBFID = "CL_00000631";
	public ScaledResolution scaledResolution = null;
	private float displayDPI;

	private ResourceLocation mojangLogo;

	private RelayManager relayManager;

	public Minecraft() {
		theMinecraft = this;
		this.mcDataDir = new VFile2("mc");
		this.fileAssets = new VFile2("resources/assets/minecraft");
		this.launchedVersion = "1.7.10";
		this.field_152356_J = null;
		this.mcDefaultResourcePack = new DefaultResourcePack();
		this.addDefaultResourcePack();
		this.startTimerHackThread();
		this.session = new Session(EaglerProfile.username, "-", "-", "legacy");
		logger.info("Setting user: " + session.getUsername());
		logger.info("(Session ID is " + session.getSessionID() + ")");
		this.isDemo = false;
		this.fullscreen = false;
		this.relayManager = RelayManager.relayManager;
	}



	private void startTimerHackThread() {

	}

	public void crashed(CrashReport p_71404_1_) {
		this.hasCrashed = true;
		this.crashReporter = p_71404_1_;
	}

	/**
	 * Wrapper around displayCrashReportInternal
	 */
	public void displayCrashReport(CrashReport p_71377_1_) {
		this.hasCrashed = true;
		String report = p_71377_1_.getCompleteReport();
		PlatformRuntime.writeCrashReport(report);
		if (PlatformRuntime.getPlatformType() == EnumPlatformType.JAVASCRIPT) {
			System.err.println(
					"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			System.err.println("NATIVE BROWSER EXCEPTION:");
			if (!PlatformRuntime.printJSExceptionIfBrowser(p_71377_1_.getCrashCause())) {
				System.err.println("<undefined>");
			}
			System.err.println(
					"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}
	}

	public void setServer(String p_71367_1_, int p_71367_2_) {
		this.serverName = p_71367_1_;
		this.serverPort = p_71367_2_;
	}

	/**
	 * Starts the game: initializes the canvas, the title, the settings, etcetera.
	 */
	private void startGame() throws LWJGLException {
		this.gameSettings = new GameSettings(this, this.mcDataDir);

		if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
			this.displayWidth = this.gameSettings.overrideWidth;
			this.displayHeight = this.gameSettings.overrideHeight;
		}
		Bootstrap.func_151354_b();

		Display.create();
		this.displayWidth = Display.getWidth();
		this.displayHeight = Display.getHeight();
		this.tempDisplayWidth = Display.getWidth();
		this.tempDisplayHeight = Display.getHeight();
		Display.setTitle("Minecraft 1.7.10");
		logger.info("LWJGL Version: " + Sys.getVersion());
		Util.EnumOS var1 = Util.getOSType();

		if (var1 != Util.EnumOS.OSX) {
			try {
				InputStream var2 = this.mcDefaultResourcePack
						.getInputStream(new ResourceLocation("icons/icon_16x16.png"));
				InputStream var3 = this.mcDefaultResourcePack
						.getInputStream(new ResourceLocation("icons/icon_32x32.png"));

			} catch (IOException var8) {
				logger.error("Couldn\'t set icon", var8);
			}
		}

		this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(),
				TextureMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(),
				FontMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(),
				AnimationMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(),
				PackMetadataSection.class);
		this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(),
				LanguageMetadataSection.class);
		this.guiAchievement = new GuiAchievement(this);

		this.mcResourcePackRepository = new ResourcePackRepository(mcDefaultResourcePack, metadataSerializer_,
				gameSettings);
		this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
		this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
		this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
		this.refreshResources();
		this.renderEngine = new TextureManager(this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.renderEngine);
		this.field_152350_aA = new SkinManager(this.renderEngine, new VFile2(this.fileAssets, "skins"));

		EaglerProfile.read();
		this.session.reset();

		this.renderGlobal = new RenderGlobal(this);

		this.textureMapBlocks = new TextureMap(0, "textures/blocks");
		this.textureMapBlocks.func_147632_b(this.gameSettings.anisotropicFiltering);
		int __af = this.gameSettings.anisotropicFiltering;
		int __mip = this.gameSettings.mipmapLevels;
		if (__af > 1 && __mip == 0) {
			__mip = 1;			logger.debug("AF={} but mipmapLevels was 0, forcing mipmapLevels=1 for crisp textures",
					Integer.valueOf(__af));
		}
		this.textureMapBlocks.func_147633_a(__mip);
		this.renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, this.textureMapBlocks);

		TextureMap __itemsTexMap = new TextureMap(1, "textures/items");
		__itemsTexMap.func_147632_b(this.gameSettings.anisotropicFiltering);
		int __af_items = this.gameSettings.anisotropicFiltering;
		int __mip_items = this.gameSettings.mipmapLevels;
		if (__af_items > 1 && __mip_items == 0) {
			__mip_items = 1;
			logger.debug("AF={} but mipmapLevels was 0, forcing mipmapLevels=1 for items atlas",
					Integer.valueOf(__af_items));
		}
		__itemsTexMap.func_147633_a(__mip_items);
		this.renderEngine.loadTextureMap(TextureMap.locationItemsTexture, __itemsTexMap);

		this.drawSplashScreen(this.renderEngine);
		this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
		this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
		this.mcMusicTicker = new MusicTicker(this);
		this.fontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"),
				this.renderEngine, false);

		if (this.gameSettings.language != null) {
			this.fontRenderer.setUnicodeFlag(this.func_152349_b());
			this.fontRenderer.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
		}

		this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings,
				new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
		this.mcResourceManager.registerReloadListener(this.fontRenderer);
		this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
		this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
		this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
		RenderManager.instance.itemRenderer = new ItemRenderer(this);
		this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.entityRenderer);
		AchievementList.openInventory.setStatStringFormatter(new IStatStringFormat() {
			private static final String __OBFID = "CL_00000639";

			public String formatString(String p_74535_1_) {
				try {
					return String.format(p_74535_1_, new Object[] { GameSettings
							.getKeyDisplayString(Minecraft.this.gameSettings.keyBindInventory.getKeyCode()) });
				} catch (Exception var3) {
					return "Error: " + var3.getLocalizedMessage();
				}
			}
		});
		this.mouseHelper = new MouseHelper();
		this.checkGLError("Pre startup");
		GlStateManager.enableTexture2D();
		GlStateManager.shadeModel(GL_SMOOTH);
		GlStateManager.clearDepth(1.0f);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(GL_LEQUAL);
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL_GREATER, 0.1F);
		GlStateManager.cullFace(GL_BACK);
		GlStateManager.matrixMode(5889);		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);		GlStateManager.loadIdentity();
		this.checkGLError("Startup");
		this.renderGlobal = new RenderGlobal(this);
		this.textureMapBlocks = new TextureMap(0, "textures/blocks");
		this.textureMapBlocks.func_147632_b(this.gameSettings.anisotropicFiltering);
		int __af2 = this.gameSettings.anisotropicFiltering;
		int __mip2 = this.gameSettings.mipmapLevels;
		if (__af2 > 1 && __mip2 == 0) {
			__mip2 = 1;			logger.debug("AF={} but mipmapLevels was 0, forcing mipmapLevels=1 for crisp textures (post-startup)",
					Integer.valueOf(__af2));
		}
		this.textureMapBlocks.func_147633_a(__mip2);
		this.renderEngine.loadTexture(TextureMap.locationBlocksTexture, this.textureMapBlocks);

		TextureMap __itemsTexMap2 = new TextureMap(1, "textures/items");
		__itemsTexMap2.func_147632_b(this.gameSettings.anisotropicFiltering);
		int __af_items2 = this.gameSettings.anisotropicFiltering;
		int __mip_items2 = this.gameSettings.mipmapLevels;
		if (__af_items2 > 1 && __mip_items2 == 0) {
			__mip_items2 = 1;
			logger.debug("AF={} but mipmapLevels was 0, forcing mipmapLevels=1 for items atlas (post-startup)",
					Integer.valueOf(__af_items2));
		}
		__itemsTexMap2.func_147633_a(__mip_items2);
		this.renderEngine.loadTexture(TextureMap.locationItemsTexture, __itemsTexMap2);
		GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
		this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
		this.checkGLError("Post startup");
		this.ingameGUI = new GuiIngame(this);

this.displayGuiScreen(new GuiMainMenu());
		this.relayManager.loadDefaults();
		this.field_152354_ay = null;
		this.loadingScreen = new LoadingScreenRenderer(this);

		if (this.gameSettings.fullScreen && !this.fullscreen) {
			this.toggleFullscreen();
		}

		try {
			Display.setVSync(this.gameSettings.enableVsync);
		} catch (Exception var4) {
			this.gameSettings.enableVsync = false;
			this.gameSettings.saveOptions();
		}
	}

	public boolean func_152349_b() {
		return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
	}

	public void refreshResources() {
		GlStateManager.recompileShaders();

		ArrayList arraylist = Lists.newArrayList(this.defaultResourcePacks);

		for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository
				.getRepositoryEntries()) {
			arraylist.add(resourcepackrepository$entry.getResourcePack());
		}

		if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
			arraylist.add(this.mcResourcePackRepository.getResourcePackInstance());
		}

		try {
			this.mcResourceManager.reloadResources(arraylist);
		} catch (RuntimeException runtimeexception) {
			logger.info("Caught error stitching, removing all assigned resourcepacks");
			logger.info(runtimeexception);
			arraylist.clear();
			arraylist.addAll(this.defaultResourcePacks);
			this.mcResourcePackRepository.setRepositories(Collections.emptyList());
			this.mcResourceManager.reloadResources(arraylist);
			this.gameSettings.resourcePacks.clear();
			this.gameSettings.resourcePacks.clear();
			this.gameSettings.saveOptions();
		}

		this.mcLanguageManager.parseLanguageMetadata(arraylist);
		if (this.renderGlobal != null) {
			this.renderGlobal.loadRenderers();
		}

	}

	private void addDefaultResourcePack() {
		this.defaultResourcePacks.add(this.mcDefaultResourcePack);
	}

	private ByteBuffer func_152340_a(InputStream p_152340_1_) throws IOException {
		BufferedImage var2 = ImageIO.read(p_152340_1_);
		int[] var3 = var2.getRGB(0, 0, var2.getWidth(), var2.getHeight(), (int[]) null, 0, var2.getWidth());
		ByteBuffer var4 = ByteBuffer.allocate(4 * var3.length);
		int[] var5 = var3;
		int var6 = var3.length;

		for (int var7 = 0; var7 < var6; ++var7) {
			int var8 = var5[var7];
			var4.putInt(var8 << 8 | var8 >> 24 & 255);
		}

		var4.flip();
		return var4;
	}

	private void updateDisplayMode() {
		this.displayWidth = Display.getWidth();
		this.displayHeight = Display.getHeight();
		this.displayDPI = Display.getDPI();
		this.scaledResolution = new ScaledResolution(this, Display.getWidth(), Display.getHeight());
	}

	public void scaledTessellator(int p_71392_1_, int p_71392_2_, int p_71392_3_, int p_71392_4_, int p_71392_5_,
			int p_71392_6_) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double) (p_71392_1_ + 0), (double) (p_71392_2_ + p_71392_6_), 0.0D,
				(double) ((float) (p_71392_3_ + 0) * var7), (double) ((float) (p_71392_4_ + p_71392_6_) * var8));
		var9.addVertexWithUV((double) (p_71392_1_ + p_71392_5_), (double) (p_71392_2_ + p_71392_6_), 0.0D,
				(double) ((float) (p_71392_3_ + p_71392_5_) * var7),
				(double) ((float) (p_71392_4_ + p_71392_6_) * var8));
		var9.addVertexWithUV((double) (p_71392_1_ + p_71392_5_), (double) (p_71392_2_ + 0), 0.0D,
				(double) ((float) (p_71392_3_ + p_71392_5_) * var7), (double) ((float) (p_71392_4_ + 0) * var8));
		var9.addVertexWithUV((double) (p_71392_1_ + 0), (double) (p_71392_2_ + 0), 0.0D,
				(double) ((float) (p_71392_3_ + 0) * var7), (double) ((float) (p_71392_4_ + 0) * var8));
		var9.draw();
	}

	/**
	 * Returns the save loader that is currently being used
	 */
	public ISaveFormat getSaveLoader() {
		return SingleplayerServerController.instance;
	}

	/**
	 * Displays a new GUI screen or closes the current one if null is passed.
	 */
	public void displayGuiScreen(GuiScreen p_147108_1_) {

		try {
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			String caller = (st != null && st.length > 3) ? (st[3].getClassName() + "#" + st[3].getMethodName())
					: "unknown";
			String target = (p_147108_1_ == null ? "null" : p_147108_1_.getClass().getSimpleName());
		} catch (Throwable t) {
		}

		if (this.currentScreen != null) {
			this.currentScreen.onGuiClosed();
		}

		if (p_147108_1_ == null) {
			if (this.theWorld == null) {
				p_147108_1_ = new GuiMainMenu();
			} else if (this.thePlayer != null && this.thePlayer.getHealth() <= 0.0F) {
				p_147108_1_ = new GuiGameOver();
			}
		}

		if (p_147108_1_ instanceof GuiMainMenu) {
			this.gameSettings.showDebugInfo = false;
			this.ingameGUI.getChatGUI().func_146231_a();
		}

		this.currentScreen = (GuiScreen) p_147108_1_;
		if (p_147108_1_ != null) {
			this.setIngameNotInFocus();
			ScaledResolution var2 = new ScaledResolution(this, this.displayWidth, this.displayHeight);
			int var3 = var2.getScaledWidth();
			int var4 = var2.getScaledHeight();
			((GuiScreen) p_147108_1_).setWorldAndResolution(this, var3, var4);
			this.skipRenderWorld = false;
		} else {
			this.mcSoundHandler.resumeSounds();
			this.setIngameFocus();
		}
	}

	/**
	 * Checks for an OpenGL error. If there is one, prints the error ID and error
	 * string.
	 */
	private void checkGLError(String p_71361_1_) {
		int var2 = GL11.glGetError();

		if (var2 != 0) {
			String var3 = GLU.gluErrorString(var2);
			logger.error("########## GL ERROR ##########");
			logger.error("@ " + p_71361_1_);
			logger.error(var2 + ": " + var3);
		}
	}

	/**
	 * Shuts down the minecraft applet by stopping the resource downloads, and
	 * clearing up GL stuff; called when the application (or web page) is exited.
	 */
	public void shutdownMinecraftApplet() {
		try {
			logger.info("Stopping!");

			try {
				this.loadWorld((WorldClient) null);
			} catch (Throwable var7) {
				;
			}

			this.mcSoundHandler.stopSounds();
		} finally {

			if (!this.hasCrashed) {
				EagRuntime.exit();
			}
		}

		EagRuntime.requestGarbageCollection();
	}

	public void run() {
		this.running = true;
		CrashReport var2;

		try {
			this.startGame();
		} catch (Throwable var11) {
			var2 = CrashReport.makeCrashReport(var11, "Initializing game");
			var2.makeCategory("Initialization");
			this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(var2));
			return;
		}

		while (true) {
			try {
				while (this.running) {
					if (!this.hasCrashed || this.crashReporter == null) {
						try {
							this.runGameLoop();
						} catch (OutOfMemoryError var10) {
							this.freeMemory();
							this.displayGuiScreen(new GuiMemoryErrorScreen());
							EagRuntime.requestGarbageCollection();
						}

						continue;
					}

					this.displayCrashReport(this.crashReporter);
					return;
				}
			} catch (MinecraftError var12) {
				;
			} catch (ReportedException var13) {
				this.addGraphicsAndWorldToCrashReport(var13.getCrashReport());
				this.freeMemory();
				logger.fatal("Reported exception thrown!", var13);
				this.displayCrashReport(var13.getCrashReport());
			} catch (Throwable var14) {
				var2 = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", var14));
				this.freeMemory();
				logger.fatal("Unreported exception thrown!", var14);
				this.displayCrashReport(var2);
			} finally {
				this.shutdownMinecraftApplet();
			}

			return;
		}
	}

	/**
	 * Called repeatedly from run()
	 */
	private void runGameLoop() {
		this.mcProfiler.startSection("root");

		if (Display.isCloseRequested()) {
			this.shutdown();
		}

		if (this.isGamePaused && this.theWorld != null) {
			float var1 = this.timer.renderPartialTicks;
			this.timer.updateTimer();
			this.timer.renderPartialTicks = var1;
		} else {
			this.timer.updateTimer();
		}

		if ((this.theWorld == null || this.currentScreen == null) && this.refreshTexturePacksScheduled) {
			this.refreshTexturePacksScheduled = false;
			this.refreshResources();
		}

		long var5 = System.nanoTime();
		this.mcProfiler.startSection("tick");

		for (int var3 = 0; var3 < this.timer.elapsedTicks; ++var3) {
			this.runTick();
		}

		this.mcProfiler.endStartSection("preRenderErrors");
		long var6 = System.nanoTime() - var5;
		this.checkGLError("Pre render");
		RenderBlocks.fancyGrass = this.gameSettings.fancyGraphics;
		this.mcProfiler.endStartSection("sound");
		this.mcSoundHandler.setListener(thePlayer, this.timer.renderPartialTicks);
		this.mcProfiler.endSection();
		this.mcProfiler.startSection("render");
		GL11.glPushMatrix();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		this.mcProfiler.startSection("display");
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
			this.gameSettings.thirdPersonView = 0;
		}

		this.mcProfiler.endSection();

		if (!this.skipRenderWorld) {
			this.mcProfiler.endStartSection("gameRenderer");
			this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
			this.mcProfiler.endSection();
		}

		this.mcProfiler.endSection();

		if (!Display.isActive() && this.fullscreen) {
			this.toggleFullscreen();
		}

		
		this.guiAchievement.func_146254_a();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		this.entityRenderer.func_152430_c(this.timer.renderPartialTicks);
		GL11.glPopMatrix();
		this.mcProfiler.startSection("root");
		this.func_147120_f();
		this.mcProfiler.startSection("stream");
		this.mcProfiler.startSection("update");
		this.mcProfiler.endStartSection("submit");
		this.mcProfiler.endSection();
		this.mcProfiler.endSection();
		this.checkGLError("Post render");
		++this.fpsCounter;
		this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame();

		while (getSystemTime() >= this.debugUpdateTime + 1000L) {
			debugFPS = this.fpsCounter;
			this.debug = debugFPS + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
			WorldRenderer.chunksUpdated = 0;
			this.debugUpdateTime += 1000L;
			this.fpsCounter = 0;
			this.usageSnooper.addMemoryStatsToSnooper();

			if (!this.usageSnooper.isSnooperRunning()) {
				this.usageSnooper.startSnooper();
			}
		}

		this.mcProfiler.endSection();

		if (this.isFramerateLimitBelowMax()) {
			Display.sync(this.getLimitFramerate());
		}
	}

	public void func_147120_f() {
		Display.update();

		if (!this.fullscreen && Display.wasResized()) {
			int var1 = this.displayWidth;
			int var2 = this.displayHeight;
			this.displayWidth = Display.getWidth();
			this.displayHeight = Display.getHeight();

			if (this.displayWidth != var1 || this.displayHeight != var2) {
				if (this.displayWidth <= 0) {
					this.displayWidth = 1;
				}

				if (this.displayHeight <= 0) {
					this.displayHeight = 1;
				}

				this.resize(this.displayWidth, this.displayHeight);
			}
		}
	}

	public int getLimitFramerate() {
		return this.theWorld == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
	}

	public boolean isFramerateLimitBelowMax() {
		return (float) this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
	}

	public void freeMemory() {
		try {
			memoryReserve = new byte[0];
			this.renderGlobal.deleteAllDisplayLists();
		} catch (Throwable var4) {
			;
		}

		try {
			EagRuntime.requestGarbageCollection();
		} catch (Throwable var3) {
			;
		}

		try {
			EagRuntime.requestGarbageCollection();
			this.loadWorld((WorldClient) null);
		} catch (Throwable var2) {
			;
		}

		EagRuntime.requestGarbageCollection();
	}

	/**
	 * Update debugProfilerName in response to number keys in debug screen
	 */
	private void updateDebugProfilerName(int p_71383_1_) {
		List var2 = this.mcProfiler.getProfilingData(this.debugProfilerName);

		if (var2 != null && !var2.isEmpty()) {
			Profiler.Result var3 = (Profiler.Result) var2.remove(0);

			if (p_71383_1_ == 0) {
				if (var3.field_76331_c.length() > 0) {
					int var4 = this.debugProfilerName.lastIndexOf(".");

					if (var4 >= 0) {
						this.debugProfilerName = this.debugProfilerName.substring(0, var4);
					}
				}
			} else {
				--p_71383_1_;

				if (p_71383_1_ < var2.size()
						&& !((Profiler.Result) var2.get(p_71383_1_)).field_76331_c.equals("unspecified")) {
					if (this.debugProfilerName.length() > 0) {
						this.debugProfilerName = this.debugProfilerName + ".";
					}

					this.debugProfilerName = this.debugProfilerName
							+ ((Profiler.Result) var2.get(p_71383_1_)).field_76331_c;
				}
			}
		}
	}

	private void displayDebugInfo(long p_71366_1_) {
		if (this.mcProfiler.profilingEnabled) {
			List var3 = this.mcProfiler.getProfilingData(this.debugProfilerName);
			Profiler.Result var4 = (Profiler.Result) var3.remove(0);
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glMatrixMode(5889);			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glLoadIdentity();
			GL11.glOrtho(0.0D, (double) this.displayWidth, (double) this.displayHeight, 0.0D, 1000.0D, 3000.0D);
			GL11.glMatrixMode(5888);			GL11.glLoadIdentity();
			GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
			GL11.glLineWidth(1.0F);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			Tessellator var5 = Tessellator.instance;
			short var6 = 160;
			int var7 = this.displayWidth - var6 - 10;
			int var8 = this.displayHeight - var6 * 2;
			GL11.glEnable(GL11.GL_BLEND);
			var5.startDrawingQuads();
			var5.setColorRGBA_I(0, 200);
			var5.addVertex((double) ((float) var7 - (float) var6 * 1.1F),
					(double) ((float) var8 - (float) var6 * 0.6F - 16.0F), 0.0D);
			var5.addVertex((double) ((float) var7 - (float) var6 * 1.1F), (double) (var8 + var6 * 2), 0.0D);
			var5.addVertex((double) ((float) var7 + (float) var6 * 1.1F), (double) (var8 + var6 * 2), 0.0D);
			var5.addVertex((double) ((float) var7 + (float) var6 * 1.1F),
					(double) ((float) var8 - (float) var6 * 0.6F - 16.0F), 0.0D);
			var5.draw();
			GL11.glDisable(GL11.GL_BLEND);
			double var9 = 0.0D;
			int var13;

			for (int var11 = 0; var11 < var3.size(); ++var11) {
				Profiler.Result var12 = (Profiler.Result) var3.get(var11);
				var13 = MathHelper.floor_double(var12.field_76332_a / 4.0D) + 1;
				var5.startDrawing(6);
				var5.setColorOpaque_I(var12.func_76329_a());
				var5.addVertex((double) var7, (double) var8, 0.0D);
				int var14;
				float var15;
				float var16;
				float var17;

				for (var14 = var13; var14 >= 0; --var14) {
					var15 = (float) ((var9 + var12.field_76332_a * (double) var14 / (double) var13) * Math.PI * 2.0D
							/ 100.0D);
					var16 = MathHelper.sin(var15) * (float) var6;
					var17 = MathHelper.cos(var15) * (float) var6 * 0.5F;
					var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17), 0.0D);
				}

				var5.draw();
				var5.startDrawing(5);
				var5.setColorOpaque_I((var12.func_76329_a() & 16711422) >> 1);

				for (var14 = var13; var14 >= 0; --var14) {
					var15 = (float) ((var9 + var12.field_76332_a * (double) var14 / (double) var13) * Math.PI * 2.0D
							/ 100.0D);
					var16 = MathHelper.sin(var15) * (float) var6;
					var17 = MathHelper.cos(var15) * (float) var6 * 0.5F;
					var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17), 0.0D);
					var5.addVertex((double) ((float) var7 + var16), (double) ((float) var8 - var17 + 10.0F), 0.0D);
				}

				var5.draw();
				var9 += var12.field_76332_a;
			}

			DecimalFormat var18 = new DecimalFormat("##0.00");
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			String var19 = "";

			if (!var4.field_76331_c.equals("unspecified")) {
				var19 = var19 + "[0] ";
			}

			if (var4.field_76331_c.length() == 0) {
				var19 = var19 + "ROOT ";
			} else {
				var19 = var19 + var4.field_76331_c + " ";
			}

			var13 = 16777215;
			this.fontRenderer.drawStringWithShadow(var19, var7 - var6, var8 - var6 / 2 - 16, var13);
			this.fontRenderer.drawStringWithShadow(var19 = var18.format(var4.field_76330_b) + "%",
					var7 + var6 - this.fontRenderer.getStringWidth(var19), var8 - var6 / 2 - 16, var13);

			for (int var20 = 0; var20 < var3.size(); ++var20) {
				Profiler.Result var21 = (Profiler.Result) var3.get(var20);
				String var22 = "";

				if (var21.field_76331_c.equals("unspecified")) {
					var22 = var22 + "[?] ";
				} else {
					var22 = var22 + "[" + (var20 + 1) + "] ";
				}

				var22 = var22 + var21.field_76331_c;
				this.fontRenderer.drawStringWithShadow(var22, var7 - var6, var8 + var6 / 2 + var20 * 8 + 20,
						var21.func_76329_a());
				this.fontRenderer.drawStringWithShadow(var22 = var18.format(var21.field_76332_a) + "%",
						var7 + var6 - 50 - this.fontRenderer.getStringWidth(var22), var8 + var6 / 2 + var20 * 8 + 20,
						var21.func_76329_a());
				this.fontRenderer.drawStringWithShadow(var22 = var18.format(var21.field_76330_b) + "%",
						var7 + var6 - this.fontRenderer.getStringWidth(var22), var8 + var6 / 2 + var20 * 8 + 20,
						var21.func_76329_a());
			}
		}
	}

	/**
	 * Called when the window is closing. Sets 'running' to false which allows the
	 * game loop to exit cleanly.
	 */
	public void shutdown() {
		this.running = false;
	}

	/**
	 * Will set the focus to ingame if the Minecraft window is the active with
	 * focus. Also clears any GUI screen currently displayed
	 */
	public void setIngameFocus() {
		if (Display.isActive() && this.currentScreen == null) {
			if (!this.inGameHasFocus) {
				this.inGameHasFocus = true;
				this.mouseHelper.grabMouseCursor();
				if (this.currentScreen == null) {
					this.displayGuiScreen((GuiScreen) null);
				}
				this.leftClickCounter = 10000;
			}
		}
	}

	/**
	 * Resets the player keystate, disables the ingame focus, and ungrabs the mouse
	 * cursor.
	 */
	public void setIngameNotInFocus() {
		if (this.inGameHasFocus) {
			KeyBinding.unPressAllKeys();
			this.inGameHasFocus = false;
			if (!PointerInputAbstraction.isTouchMode()) {
				this.mouseHelper.ungrabMouseCursor();
			}
		}
	}


	/**
	 * Displays the ingame menu
	 */
	public void displayInGameMenu() {
		if (this.currentScreen == null) {
			this.displayGuiScreen(new GuiIngameMenu());

			if (this.isSingleplayer()) {
				this.mcSoundHandler.resumeSounds();
			}
		}
	}

	private void func_147115_a(boolean p_147115_1_) {
		if (!p_147115_1_) {
			this.leftClickCounter = 0;
		}

		if (this.leftClickCounter <= 0) {
			if (p_147115_1_ && this.objectMouseOver != null
					&& this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int var2 = this.objectMouseOver.blockX;
				int var3 = this.objectMouseOver.blockY;
				int var4 = this.objectMouseOver.blockZ;

				if (this.theWorld.getBlock(var2, var3, var4).getMaterial() != Material.air) {
					this.playerController.onPlayerDamageBlock(var2, var3, var4, this.objectMouseOver.sideHit);

					if (this.thePlayer.isCurrentToolAdventureModeExempt(var2, var3, var4)) {
						this.effectRenderer.addBlockHitEffects(var2, var3, var4, this.objectMouseOver.sideHit);
						this.thePlayer.swingItem();
					}
				}
			} else {
				this.playerController.resetBlockRemoving();
			}
		}
	}

	private void func_147116_af() {
		if (this.leftClickCounter <= 0) {
			this.thePlayer.swingItem();

			if (this.objectMouseOver == null) {
				logger.error("Null returned as \'hitResult\', this shouldn\'t happen!");

				if (this.playerController.isNotCreative()) {
					this.leftClickCounter = 10;
				}
			} else {
				switch (Minecraft.SwitchMovingObjectType.field_152390_a[this.objectMouseOver.typeOfHit.ordinal()]) {
				case 1:
					this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
					break;

				case 2:
					int var1 = this.objectMouseOver.blockX;
					int var2 = this.objectMouseOver.blockY;
					int var3 = this.objectMouseOver.blockZ;

					if (this.theWorld.getBlock(var1, var2, var3).getMaterial() == Material.air) {
						if (this.playerController.isNotCreative()) {
							this.leftClickCounter = 10;
						}
					} else {
						this.playerController.clickBlock(var1, var2, var3, this.objectMouseOver.sideHit);
					}
				}
			}
		}
	}

	private void func_147121_ag() {
		this.rightClickDelayTimer = 4;
		boolean var1 = true;
		ItemStack var2 = this.thePlayer.inventory.getCurrentItem();

		if (this.objectMouseOver == null) {
			logger.warn("Null returned as \'hitResult\', this shouldn\'t happen!");
		} else {
			switch (Minecraft.SwitchMovingObjectType.field_152390_a[this.objectMouseOver.typeOfHit.ordinal()]) {
			case 1:
				if (this.playerController.interactWithEntitySendPacket(this.thePlayer,
						this.objectMouseOver.entityHit)) {
					var1 = false;
				}

				break;

			case 2:
				int var3 = this.objectMouseOver.blockX;
				int var4 = this.objectMouseOver.blockY;
				int var5 = this.objectMouseOver.blockZ;

				if (this.theWorld.getBlock(var3, var4, var5).getMaterial() != Material.air) {
					int var6 = var2 != null ? var2.stackSize : 0;

					if (this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, var2, var3, var4, var5,
							this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
						var1 = false;
						this.thePlayer.swingItem();
					}

					if (var2 == null) {
						return;
					}

					if (var2.stackSize == 0) {
						this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
					} else if (var2.stackSize != var6 || this.playerController.isInCreativeMode()) {
						this.entityRenderer.itemRenderer.resetEquippedProgress();
					}
				}
			}
		}

		if (var1) {
			ItemStack var7 = this.thePlayer.inventory.getCurrentItem();

			if (var7 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, var7)) {
				this.entityRenderer.itemRenderer.resetEquippedProgress2();
			}
		}
	}

	/**
	 * Toggles fullscreen mode.
	 */
	public void toggleFullscreen() {
		try {
			this.fullscreen = !this.fullscreen;

			if (this.fullscreen) {
				this.updateDisplayMode();
				this.displayWidth = Display.getWidth();
				this.displayHeight = Display.getHeight();

				if (this.displayWidth <= 0) {
					this.displayWidth = 1;
				}

				if (this.displayHeight <= 0) {
					this.displayHeight = 1;
				}
			} else {
				this.displayWidth = this.tempDisplayWidth;
				this.displayHeight = this.tempDisplayHeight;

				if (this.displayWidth <= 0) {
					this.displayWidth = 1;
				}

				if (this.displayHeight <= 0) {
					this.displayHeight = 1;
				}
			}

			if (this.currentScreen != null) {
				this.resize(this.displayWidth, this.displayHeight);
			} else {
				this.updateFramebufferSize();
			}

			Display.setVSync(this.gameSettings.enableVsync);
			this.func_147120_f();
		} catch (Exception var2) {
			logger.error("Couldn\'t toggle fullscreen", var2);
		}
	}

	/**
	 * Called to resize the current screen.
	 */
	private void resize(int p_71370_1_, int p_71370_2_) {
		this.displayWidth = p_71370_1_ <= 0 ? 1 : p_71370_1_;
		this.displayHeight = p_71370_2_ <= 0 ? 1 : p_71370_2_;

		if (this.currentScreen != null) {
			ScaledResolution var3 = new ScaledResolution(this, p_71370_1_, p_71370_2_);
			int var4 = var3.getScaledWidth();
			int var5 = var3.getScaledHeight();
			this.currentScreen.setWorldAndResolution(this, var4, var5);
		}

		this.loadingScreen = new LoadingScreenRenderer(this);
		this.updateFramebufferSize();
	}

	private void updateFramebufferSize() {

	}

	/**
	 * Runs the current tick.
	 */
	public void runTick() {

		net.lax1dude.eaglercraft.sp.SingleplayerServerController.runTick();
		
		this.mcProfiler.startSection("scheduledExecutables");
		this.mcProfiler.endSection();

		if (this.rightClickDelayTimer > 0) {
			--this.rightClickDelayTimer;
		}

		this.mcProfiler.startSection("gui");

		if (!this.isGamePaused) {
			this.ingameGUI.updateTick();
		}

		this.mcProfiler.endStartSection("pick");
		this.entityRenderer.getMouseOver(1.0F);
		this.mcProfiler.endStartSection("gameMode");

		if (!this.isGamePaused && this.theWorld != null) {
			this.playerController.updateController();
		}

		this.mcProfiler.endStartSection("textures");

		if (!this.isGamePaused) {
			this.renderEngine.tick();
		}

		if (this.currentScreen == null && this.thePlayer != null) {
			if (!Mouse.isMouseGrabbed()) {
				this.setIngameNotInFocus();
				this.displayInGameMenu();
			}
			if (this.thePlayer.getHealth() <= 0.0F) {
				this.displayGuiScreen((GuiScreen) null);
			} else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
				this.displayGuiScreen(new GuiSleepMP());
			}
		} else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP
				&& !this.thePlayer.isPlayerSleeping()) {
			this.displayGuiScreen((GuiScreen) null);
		}

		if (this.currentScreen != null) {
			this.leftClickCounter = 10000;
		}

		CrashReport var2;
		CrashReportCategory var3;

		if (this.currentScreen != null) {
			try {
				this.currentScreen.handleInput();
			} catch (Throwable var6) {
				var2 = CrashReport.makeCrashReport(var6, "Updating screen events");
				var3 = var2.makeCategory("Affected screen");
				var3.addCrashSectionCallable("Screen name", new Callable() {
					private static final String __OBFID = "CL_00000640";

					public String call() {
						return Minecraft.this.currentScreen.getClass().getCanonicalName();
					}
				});
				throw new ReportedException(var2);
			}

			if (this.currentScreen != null) {
				try {
					this.currentScreen.updateScreen();
				} catch (Throwable var5) {
					var2 = CrashReport.makeCrashReport(var5, "Ticking screen");
					var3 = var2.makeCategory("Affected screen");
					var3.addCrashSectionCallable("Screen name", new Callable() {
						private static final String __OBFID = "CL_00000642";

						public String call() {
							return Minecraft.this.currentScreen.getClass().getCanonicalName();
						}
					});
					throw new ReportedException(var2);
				}
			}
		}

		if (this.currentScreen == null || this.currentScreen.field_146291_p) {
			this.mcProfiler.endStartSection("mouse");
			int var9;

			while (Mouse.next()) {
				var9 = Mouse.getEventButton();
				KeyBinding.setKeyBindState(var9 - 100, Mouse.getEventButtonState());

				if (Mouse.getEventButtonState()) {
					KeyBinding.onTick(var9 - 100);
				}

				long var11 = getSystemTime() - this.systemTime;

				if (var11 <= 200L) {
					int var4 = Mouse.getEventDWheel();

					if (var4 != 0) {
						this.thePlayer.inventory.changeCurrentItem(var4);

						if (this.gameSettings.noclip) {
							if (var4 > 0) {
								var4 = 1;
							}

							if (var4 < 0) {
								var4 = -1;
							}

							this.gameSettings.noclipRate += (float) var4 * 0.25F;
						}
					}

					if (this.currentScreen == null) {
						if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
							this.setIngameFocus();
						}
					} else if (this.currentScreen != null) {
						this.currentScreen.handleMouseInput();
					}
				}
			}

			if (this.leftClickCounter > 0) {
				--this.leftClickCounter;
			}

			this.mcProfiler.endStartSection("keyboard");
			boolean var10;

			while (Keyboard.next()) {
				KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());

				if (Keyboard.getEventKeyState()) {
					KeyBinding.onTick(Keyboard.getEventKey());
				}

				if (this.field_83002_am > 0L) {
					if (getSystemTime() - this.field_83002_am >= 6000L) {
						throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
					}

					if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
						this.field_83002_am = -1L;
					}
				} else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
					this.field_83002_am = getSystemTime();
				}

				this.func_152348_aa();

				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == 62 && this.entityRenderer != null) {
						this.entityRenderer.deactivateShader();
					}

					if (this.currentScreen != null) {
						this.currentScreen.handleKeyboardInput();
					} else {
						if (Keyboard.getEventKey() == 1) {
							this.displayInGameMenu();
						}

						if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
							this.refreshResources();
						}

						if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61)) {
							this.refreshResources();
						}

						if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61)) {
							var10 = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
							this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, var10 ? -1 : 1);
						}

						if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
							this.renderGlobal.loadRenderers();
						}

						if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
							this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
							this.gameSettings.saveOptions();
						}

						if (Keyboard.getEventKey() == 48 && Keyboard.isKeyDown(61)) {
							RenderManager.field_85095_o = !RenderManager.field_85095_o;
						}

						if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
							this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
							this.gameSettings.saveOptions();
						}

						if (Keyboard.getEventKey() == 59) {
							this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
						}

						if (Keyboard.getEventKey() == 61) {
							this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
						}

						if (this.gameSettings.keyBindTogglePerspective.isPressed()) {
							++this.gameSettings.thirdPersonView;

							if (this.gameSettings.thirdPersonView > 2) {
								this.gameSettings.thirdPersonView = 0;
							}
						}

						if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
							this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
						}
					}

					if (this.gameSettings.showDebugInfo) {
						if (Keyboard.getEventKey() == 11) {
							this.updateDebugProfilerName(0);
						}

						for (var9 = 0; var9 < 9; ++var9) {
							if (Keyboard.getEventKey() == 2 + var9) {
								this.updateDebugProfilerName(var9 + 1);
							}
						}
					}
				}
			}

			for (var9 = 0; var9 < 9; ++var9) {
				if (this.gameSettings.keyBindsHotbar[var9].isPressed()) {
					this.thePlayer.inventory.currentItem = var9;
				}
			}

			var10 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

			while (this.gameSettings.keyBindInventory.isPressed()) {
				if (this.playerController.func_110738_j()) {
					this.thePlayer.func_110322_i();
				} else {
					this.getNetHandler().addToSendQueue(
							new C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT));
					this.displayGuiScreen(new GuiInventory(this.thePlayer));
				}
			}

			while (this.gameSettings.keyBindDrop.isPressed()) {
				this.thePlayer.dropOneItem(GuiScreen.isCtrlKeyDown());
			}

			while (this.gameSettings.keyBindChat.isPressed() && var10) {
				this.displayGuiScreen(new GuiChat());
			}

			if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && var10) {
				this.displayGuiScreen(new GuiChat("/"));
			}

			if (this.thePlayer.isUsingItem()) {
				if (!this.gameSettings.keyBindUseItem.getIsKeyPressed()) {
					this.playerController.onStoppedUsingItem(this.thePlayer);
				}

				label391:

				while (true) {
					if (!this.gameSettings.keyBindAttack.isPressed()) {
						while (this.gameSettings.keyBindUseItem.isPressed()) {
							;
						}

						while (true) {
							if (this.gameSettings.keyBindPickBlock.isPressed()) {
								continue;
							}

							break label391;
						}
					}
				}
			} else {
				while (this.gameSettings.keyBindAttack.isPressed()) {
					this.func_147116_af();
				}

				while (this.gameSettings.keyBindUseItem.isPressed()) {
					this.func_147121_ag();
				}

				while (this.gameSettings.keyBindPickBlock.isPressed()) {
					this.func_147112_ai();
				}
			}

			if (this.gameSettings.keyBindUseItem.getIsKeyPressed() && this.rightClickDelayTimer == 0
					&& !this.thePlayer.isUsingItem()) {
				this.func_147121_ag();
			}

			this.func_147115_a(this.currentScreen == null && this.gameSettings.keyBindAttack.getIsKeyPressed()
					&& this.inGameHasFocus);
		}

		if (this.theWorld != null) {
			if (this.thePlayer != null) {
				++this.joinPlayerCounter;

				if (this.joinPlayerCounter == 30) {
					this.joinPlayerCounter = 0;
					this.theWorld.joinEntityInSurroundings(this.thePlayer);
				}
			}

			this.mcProfiler.endStartSection("gameRenderer");

			if (!this.isGamePaused) {
				this.entityRenderer.updateRenderer();
			}

			this.mcProfiler.endStartSection("levelRenderer");

			if (!this.isGamePaused) {
				this.renderGlobal.updateClouds();
			}

			this.mcProfiler.endStartSection("level");

			if (!this.isGamePaused) {
				if (this.theWorld.lastLightningBolt > 0) {
					--this.theWorld.lastLightningBolt;
				}

				this.theWorld.updateEntities();
			}
		}

		if (!this.isGamePaused) {
			this.mcMusicTicker.update();
			this.mcSoundHandler.update();
		}

		if (this.theWorld != null) {
			if (!this.isGamePaused) {
				this.theWorld.setAllowedSpawnTypes(this.theWorld.difficultySetting != EnumDifficulty.PEACEFUL, true);

				try {
					this.theWorld.tick();
				} catch (Throwable var7) {
					var2 = CrashReport.makeCrashReport(var7, "Exception in world tick");

					if (this.theWorld == null) {
						var3 = var2.makeCategory("Affected level");
						var3.addCrashSection("Problem", "Level is null!");
					} else {
						this.theWorld.addWorldInfoToCrashReport(var2);
					}

					throw new ReportedException(var2);
				}
			}

			this.mcProfiler.endStartSection("animateTick");

			if (!this.isGamePaused && this.theWorld != null && this.thePlayer != null) {
				this.theWorld.doVoidFogParticles(MathHelper.floor_double(this.thePlayer.posX),
						MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
			}

			this.mcProfiler.endStartSection("particles");

			if (!this.isGamePaused) {
				this.effectRenderer.updateEffects();
			}
		} else if (this.myNetworkManager != null) {
			this.mcProfiler.endStartSection("pendingConnection");
			try {
				this.myNetworkManager.processReceivedPackets();

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		this.mcProfiler.endSection();
		this.systemTime = getSystemTime();
	}

	/**
	 * Arguments: World foldername, World ingame name, WorldSettings
	 */
	public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
		this.loadWorld((WorldClient) null);

		SingleplayerServerController.launchEaglercraftServer(folderName, gameSettings.difficulty.getDifficultyId(),
				Math.max(gameSettings.renderDistanceChunks, 2), worldSettingsIn);
		EagRuntime.setMCServerWindowGlobal("singleplayer");
		this.displayGuiScreen(new GuiScreenIntegratedServerBusy(
				new GuiScreenSingleplayerConnecting(new GuiMainMenu(), "Connecting to " + folderName),
				"singleplayer.busy.startingIntegratedServer", "singleplayer.failed.startingIntegratedServer",
				() -> SingleplayerServerController.isWorldReady(), (t, u) -> {
					Minecraft.this.displayGuiScreen(GuiScreenIntegratedServerBusy.createException(new GuiMainMenu(),
							((GuiScreenIntegratedServerBusy) t).failMessage, u));
				}));
	}
	
	public void shutdownIntegratedServer(GuiScreen cont) {
		if (SingleplayerServerController.shutdownEaglercraftServer()
				|| SingleplayerServerController.getStatusState() == IntegratedServerState.WORLD_UNLOADING) {
			displayGuiScreen(new GuiScreenIntegratedServerBusy(cont, "singleplayer.busy.stoppingIntegratedServer",
					"singleplayer.failed.stoppingIntegratedServer", SingleplayerServerController::isReady));
		} else {
			displayGuiScreen(cont);
		}
	}

	/**
	 * unloads the current world first
	 */
	public void loadWorld(WorldClient p_71403_1_) {
		this.loadWorld(p_71403_1_, "");
	}

	/**
	 * par2Str is displayed on the loading screen to the user unloads the current
	 * world first
	 */
	public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
		if (worldClientIn == null) {
			NetHandlerPlayClient nethandlerplayclient = this.getNetHandler();
			if (nethandlerplayclient != null) {
				nethandlerplayclient.cleanup();
			}
			session.reset();
			WebViewOverlayController.setPacketSendCallback(null);

			this.guiAchievement.func_146257_b();
			this.entityRenderer.getMapItemRenderer().func_148249_a();
		}

		this.renderViewEntity = null;
		this.myNetworkManager = null;
		if (this.loadingScreen != null) {
			this.loadingScreen.resetProgressAndMessage(loadingMessage);
			this.loadingScreen.displaySavingString("");
		}

		if (worldClientIn == null && this.theWorld != null) {
			this.mcResourcePackRepository.func_148529_f();
			this.setServerData((ServerData) null);
			this.integratedServerIsRunning = false;
		}

		this.theWorld = worldClientIn;
		if (worldClientIn != null) {
			if (this.renderGlobal != null) {
				this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
			}

			if (this.effectRenderer != null) {
				this.effectRenderer.clearEffects(worldClientIn);
			}

			NetHandlerPlayClient handler = null;
			if (worldClientIn instanceof WorldClient) {
				handler = ((WorldClient) worldClientIn).sendQueue;
			}
			if (handler == null && this.thePlayer != null) {
				handler = this.thePlayer.sendQueue;
			}
			if (handler == null) {
				handler = new NetHandlerPlayClient(this, null, this.myNetworkManager);
			}

			this.playerController = new PlayerControllerMP(this, handler);

			if (this.thePlayer == null) {
				this.thePlayer = this.playerController.func_147493_a(worldClientIn, new StatFileWriter());
				this.thePlayer.sendQueue = handler;
				this.playerController.flipPlayer(this.thePlayer);
			}

			this.thePlayer.preparePlayerToSpawn();
			worldClientIn.spawnEntityInWorld(this.thePlayer);
			this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
			this.playerController.setPlayerCapabilities(this.thePlayer);
			this.renderViewEntity = this.thePlayer;
		} else {
			this.thePlayer = null;
			this.renderViewEntity = null;
		}

		EagRuntime.requestGarbageCollection();
		this.systemTime = 0L;
	}

	/**
	 * A String of renderGlobal.getDebugInfoRenders
	 */
	public String debugInfoRenders() {
		return this.renderGlobal.getDebugInfoRenders();
	}

	/**
	 * Gets the information in the F3 menu about how many entities are
	 * infront/around you
	 */
	public String getEntityDebug() {
		return this.renderGlobal.getDebugInfoEntities();
	}

	/**
	 * Gets the name of the world's current chunk provider
	 */
	public String getWorldProviderName() {
		return this.theWorld.getProviderName();
	}

	/**
	 * A String of how many entities are in the world
	 */
	public String debugInfoEntities() {
		return "P: " + this.effectRenderer.getStatistics() + ". T: " + this.theWorld.getDebugLoadedEntities();
	}

	public void setDimensionAndSpawnPlayer(int p_71354_1_) {
		this.theWorld.setSpawnLocation();
		this.theWorld.removeAllEntities();
		int var2 = 0;
		String var3 = null;

		if (this.thePlayer != null) {
			var2 = this.thePlayer.getEntityId();
			this.theWorld.removeEntity(this.thePlayer);
			var3 = this.thePlayer.func_142021_k();
		}

		this.renderViewEntity = null;
		this.thePlayer = this.playerController.func_147493_a(this.theWorld,
				this.thePlayer == null ? new StatFileWriter() : this.thePlayer.func_146107_m());
		this.thePlayer.dimension = p_71354_1_;
		this.renderViewEntity = this.thePlayer;
		this.thePlayer.preparePlayerToSpawn();
		this.thePlayer.func_142020_c(var3);
		this.theWorld.spawnEntityInWorld(this.thePlayer);
		this.playerController.flipPlayer(this.thePlayer);
		this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
		this.thePlayer.setEntityId(var2);
		this.playerController.setPlayerCapabilities(this.thePlayer);

		if (this.currentScreen instanceof GuiGameOver) {
			this.displayGuiScreen((GuiScreen) null);
		}
	}

	/**
	 * Gets whether this is a demo or not.
	 */
	public final boolean isDemo() {
		return this.isDemo;
	}

	public NetHandlerPlayClient getNetHandler() {
		return this.thePlayer != null ? this.thePlayer.sendQueue : null;
	}

	public static boolean isGuiEnabled() {
		return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
	}

	public static boolean isFancyGraphicsEnabled() {
		return false;	}

	/**
	 * Returns if ambient occlusion is enabled
	 */
	public static boolean isAmbientOcclusionEnabled() {
		return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
	}

	private void func_147112_ai() {
		if (this.objectMouseOver != null) {
			boolean var1 = this.thePlayer.capabilities.isCreativeMode;
			int var3 = 0;
			boolean var4 = false;
			Item var2;
			int var5;

			if (this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				var5 = this.objectMouseOver.blockX;
				int var6 = this.objectMouseOver.blockY;
				int var7 = this.objectMouseOver.blockZ;
				Block var8 = this.theWorld.getBlock(var5, var6, var7);

				if (var8.getMaterial() == Material.air) {
					return;
				}

				var2 = var8.getItem(this.theWorld, var5, var6, var7);

				if (var2 == null) {
					return;
				}

				var4 = var2.getHasSubtypes();
				Block var9 = var2 instanceof ItemBlock && !var8.isFlowerPot() ? Block.getBlockFromItem(var2) : var8;
				var3 = var9.getDamageValue(this.theWorld, var5, var6, var7);
			} else {
				if (this.objectMouseOver.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY
						|| this.objectMouseOver.entityHit == null || !var1) {
					return;
				}

				if (this.objectMouseOver.entityHit instanceof EntityPainting) {
					var2 = Items.painting;
				} else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
					var2 = Items.lead;
				} else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
					EntityItemFrame var10 = (EntityItemFrame) this.objectMouseOver.entityHit;
					ItemStack var12 = var10.getDisplayedItem();

					if (var12 == null) {
						var2 = Items.item_frame;
					} else {
						var2 = var12.getItem();
						var3 = var12.getItemDamage();
						var4 = true;
					}
				} else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
					EntityMinecart var11 = (EntityMinecart) this.objectMouseOver.entityHit;

					if (var11.getMinecartType() == 2) {
						var2 = Items.furnace_minecart;
					} else if (var11.getMinecartType() == 1) {
						var2 = Items.chest_minecart;
					} else if (var11.getMinecartType() == 3) {
						var2 = Items.tnt_minecart;
					} else if (var11.getMinecartType() == 5) {
						var2 = Items.hopper_minecart;
					} else if (var11.getMinecartType() == 6) {
						var2 = Items.command_block_minecart;
					} else {
						var2 = Items.minecart;
					}
				} else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
					var2 = Items.boat;
				} else {
					var2 = Items.spawn_egg;
					var3 = EntityList.getEntityID(this.objectMouseOver.entityHit);
					var4 = true;

					if (var3 <= 0 || !EntityList.entityEggs.containsKey(Integer.valueOf(var3))) {
						return;
					}
				}
			}

			this.thePlayer.inventory.func_146030_a(var2, var3, var4, var1);

			if (var1) {
				var5 = this.thePlayer.inventoryContainer.inventorySlots.size() - 9
						+ this.thePlayer.inventory.currentItem;
				this.playerController.sendSlotPacket(
						this.thePlayer.inventory.getStackInSlot(this.thePlayer.inventory.currentItem), var5);
			}
		}
	}

	/**
	 * adds core server Info (GL version , Texture pack, isModded, type), and the
	 * worldInfo to the crash report
	 */
	public CrashReport addGraphicsAndWorldToCrashReport(CrashReport p_71396_1_) {
		p_71396_1_.getCategory().addCrashSectionCallable("Launched Version", new Callable() {
			private static final String __OBFID = "CL_00000643";

			public String call() {
				return Minecraft.this.launchedVersion;
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("LWJGL", new Callable() {
			private static final String __OBFID = "CL_00000644";

			public String call() {
				return Sys.getVersion();
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("OpenGL", new Callable() {
			private static final String __OBFID = "CL_00000645";

			public String call() {
				return GL11.glGetString(GL11.GL_RENDERER) + " GL version " + GL11.glGetString(GL11.GL_VERSION) + ", "
						+ GL11.glGetString(GL11.GL_VENDOR);
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("Is Modded", new Callable() {
			private static final String __OBFID = "CL_00000647";

			public String call() {
				String var1 = ClientBrandRetriever.getClientModName();
				return var1;
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("Type", new Callable() {
			private static final String __OBFID = "CL_00000633";

			public String call() {
				return "Client (map_client.txt)";
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("Resource Packs", new Callable() {
			private static final String __OBFID = "CL_00000634";

			public String call() {
				return Minecraft.this.gameSettings.resourcePacks.toString();
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("Current Language", new Callable() {
			private static final String __OBFID = "CL_00000635";

			public String call() {
				return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("Profiler Position", new Callable() {
			private static final String __OBFID = "CL_00000636";

			public String call() {
				return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection()
						: "N/A (disabled)";
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("Vec3 Pool Size", new Callable() {
			private static final String __OBFID = "CL_00000637";

			public String call() {
				byte var1 = 0;
				int var2 = 56 * var1;
				int var3 = var2 / 1024 / 1024;
				byte var4 = 0;
				int var5 = 56 * var4;
				int var6 = var5 / 1024 / 1024;
				return var1 + " (" + var2 + " bytes; " + var3 + " MB) allocated, " + var4 + " (" + var5 + " bytes; "
						+ var6 + " MB) used";
			}
		});
		p_71396_1_.getCategory().addCrashSectionCallable("Anisotropic Filtering", new Callable() {
			private static final String __OBFID = "CL_00001853";

			public String func_152388_a() {
				return Minecraft.this.gameSettings.anisotropicFiltering == 1 ? "Off (1)"
						: "On (" + Minecraft.this.gameSettings.anisotropicFiltering + ")";
			}

			public Object call() {
				return this.func_152388_a();
			}
		});

		if (this.theWorld != null) {
			this.theWorld.addWorldInfoToCrashReport(p_71396_1_);
		}

		return p_71396_1_;
	}

	/**
	 * Return the singleton Minecraft instance for the game
	 */
	public static Minecraft getMinecraft() {
		return theMinecraft;
	}

	public void scheduleResourcesRefresh() {
		this.refreshTexturePacksScheduled = true;
	}

	public void addServerStatsToSnooper(PlayerUsageSnooper p_70000_1_) {
		p_70000_1_.func_152768_a("fps", Integer.valueOf(debugFPS));
		p_70000_1_.func_152768_a("vsync_enabled", Boolean.valueOf(this.gameSettings.enableVsync));
		p_70000_1_.func_152768_a("display_type", this.fullscreen ? "fullscreen" : "windowed");
		p_70000_1_.func_152768_a("run_time", Long.valueOf(
				(MinecraftServer.getSystemTimeMillis() - p_70000_1_.getMinecraftStartTimeMillis()) / 60L * 1000L));
		p_70000_1_.func_152768_a("resource_packs",
				Integer.valueOf(this.mcResourcePackRepository.getRepositoryEntries().size()));
		int var2 = 0;
		Iterator var3 = this.mcResourcePackRepository.getRepositoryEntries().iterator();

		while (var3.hasNext()) {
			ResourcePackRepository.Entry var4 = (ResourcePackRepository.Entry) var3.next();
			p_70000_1_.func_152768_a("resource_pack[" + var2++ + "]", var4.getResourcePackName());
		}

		if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
			p_70000_1_.func_152768_a("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
		}
	}

	public void addServerTypeToSnooper(PlayerUsageSnooper p_70001_1_) {

	}

	/**
	 * Used in the usage snooper.
	 */
	public static int getGLMaximumTextureSize() {
		return EaglercraftGPU.glGetInteger(GL_MAX_TEXTURE_SIZE);
	}

	/**
	 * Returns whether snooping is enabled or not.
	 */
	public boolean isSnooperEnabled() {
		return this.gameSettings.snooperEnabled;
	}

	/**
	 * Set the current ServerData instance.
	 */
	public void setServerData(ServerData p_71351_1_) {
		this.currentServerData = p_71351_1_;
	}

	public ServerData func_147104_D() {
		return this.currentServerData;
	}

	public boolean isIntegratedServerRunning() {
		return this.integratedServerIsRunning;
	}

	/**
	 * Returns true if there is only one player playing, and the current server is
	 * the integrated one.
	 */
	public boolean isSingleplayer() {
		return SingleplayerServerController.isWorldRunning();
	}

	/**
	 * Returns the currently running integrated server
	 */
	public IntegratedServer getIntegratedServer() {
		return this.theIntegratedServer;
	}

	public static void stopIntegratedServer() {
		if (theMinecraft != null) {
			IntegratedServer var0 = theMinecraft.getIntegratedServer();

			if (var0 != null) {
				var0.stopServer();
			}
		}
	}

	/**
	 * Returns the PlayerUsageSnooper instance.
	 */
	public PlayerUsageSnooper getPlayerUsageSnooper() {
		return this.usageSnooper;
	}

	/**
	 * Gets the system time in milliseconds.
	 */
	public static long getSystemTime() {
		return Sys.getTime() * 1000L / Sys.getTimerResolution();
	}

	/**
	 * Returns whether we're in full screen or not.
	 */
	public boolean isFullScreen() {
		return this.fullscreen;
	}

	public Session getSession() {
		return this.session;
	}

	public TextureManager getTextureManager() {
		return this.renderEngine;
	}

	public IResourceManager getResourceManager() {
		return this.mcResourceManager;
	}

	public ResourcePackRepository getResourcePackRepository() {
		return this.mcResourcePackRepository;
	}

	public LanguageManager getLanguageManager() {
		return this.mcLanguageManager;
	}

	public TextureMap getTextureMapBlocks() {
		return this.textureMapBlocks;
	}


	public boolean func_147113_T() {
		return this.isGamePaused;
	}

	public SoundHandler getSoundHandler() {
		return this.mcSoundHandler;
	}

	public MusicTicker.MusicType func_147109_W() {
		return this.currentScreen instanceof GuiWinGame ? MusicTicker.MusicType.CREDITS
				: (this.thePlayer != null ? (this.thePlayer.worldObj.provider instanceof WorldProviderHell
						? MusicTicker.MusicType.NETHER
						: (this.thePlayer.worldObj.provider instanceof WorldProviderEnd
								? (BossStatus.bossName != null && BossStatus.statusBarTime > 0
										? MusicTicker.MusicType.END_BOSS
										: MusicTicker.MusicType.END)
								: (this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying
										? MusicTicker.MusicType.CREATIVE
										: MusicTicker.MusicType.GAME)))
						: MusicTicker.MusicType.MENU);
	}

	public void func_152348_aa() {

		int var1 = Keyboard.getEventKey();

		if (var1 != 0 && !Keyboard.isRepeatEvent()) {
			if (!(this.currentScreen instanceof GuiControls)
					|| ((GuiControls) this.currentScreen).field_152177_g <= getSystemTime() - 20L) {
				if (Keyboard.getEventKeyState()) {
						if (var1 == this.gameSettings.field_152395_am.getKeyCode()) {
							this.toggleFullscreen();
						} else if (var1 == this.gameSettings.keyBindScreenshot.getKeyCode()) {
							this.ingameGUI.getChatGUI().func_146227_a(ScreenShotHelper.saveScreenshot());
						}
				}
			}
		}
	}

	public <V> ListenableFuture<V> func_152343_a(Callable<V> callableToSchedule) {
		Validate.notNull(callableToSchedule);
		ListenableFutureTask listenablefuturetask = ListenableFutureTask.create(callableToSchedule);
		synchronized (this.field_152351_aB) {
			this.field_152351_aB.add(listenablefuturetask);
			return (ListenableFuture<V>) listenablefuturetask;
		}
	}

	public ListenableFuture func_152344_a(Runnable p_152344_1_) {
		Validate.notNull(p_152344_1_);
		return this.func_152343_a(Executors.callable(p_152344_1_));
	}

	public SkinManager func_152342_ad() {
		return this.field_152350_aA;
	}

	public void addScheduledTask(Runnable runnable) {
	}

	static final class SwitchMovingObjectType {
		static final int[] field_152390_a = new int[MovingObjectPosition.MovingObjectType.values().length];
		private static final String __OBFID = "CL_00000638";

		static {
			try {
				field_152390_a[MovingObjectPosition.MovingObjectType.ENTITY.ordinal()] = 1;
			} catch (NoSuchFieldError var2) {
				;
			}

			try {
				field_152390_a[MovingObjectPosition.MovingObjectType.BLOCK.ordinal()] = 2;
			} catch (NoSuchFieldError var1) {
				;
			}
		}
	}

	private void drawSplashScreen(TextureManager textureManagerInstance) {
		Display.update();
		updateDisplayMode();
		GlStateManager.viewport(0, 0, displayWidth, displayHeight);
		GlStateManager.matrixMode(GL_PROJECTION);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, (double) scaledResolution.getScaledWidth(),
				(double) scaledResolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepth();
		GlStateManager.enableTexture2D();
		InputStream inputstream = null;

		try {
			inputstream = this.mcDefaultResourcePack.getInputStream(locationMojangPng);
			this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo",
					new DynamicTexture(ImageData.loadImageFile(inputstream)));
			textureManagerInstance.bindTexture(this.mojangLogo);
		} catch (IOException ioexception) {
			logger.error("Unable to load logo: " + locationMojangPng, ioexception);
		} finally {
			IOUtils.closeQuietly(inputstream);
		}

		Tessellator tessellator = Tessellator.getInstance();
		net.lax1dude.eaglercraft.opengl.WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos(0.0D, (double) this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255)
				.endVertex();
		worldrenderer.pos((double) this.displayWidth, (double) this.displayHeight, 0.0D).tex(1.0D, 0.0D)
				.color(255, 255, 255, 255).endVertex();
		worldrenderer.pos((double) this.displayWidth, 0.0D, 0.0D).tex(1.0D, 1.0D).color(255, 255, 255, 255).endVertex();
		worldrenderer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 1.0D).color(255, 255, 255, 255).endVertex();
		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		short short1 = 256;
		short short2 = 256;
		this.func_181536_a((scaledResolution.getScaledWidth() - short1) / 2,
				(scaledResolution.getScaledHeight() - short2) / 2, 0, 0, short1, short2, 255, 255, 255, 255);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(GL_GREATER, 0.1F);
		this.func_147120_f();
	}

	public void func_181536_a(int parInt1, int parInt2, int parInt3, int parInt4, int parInt5, int parInt6, int parInt7,
			int parInt8, int parInt9, int parInt10) {
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		net.lax1dude.eaglercraft.opengl.WorldRenderer worldrenderer = Tessellator.getInstance().getWorldRenderer();
		worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		worldrenderer.pos((double) parInt1, (double) (parInt2 + parInt6), 0.0D)
				.tex((double) ((float) parInt3 * f), (double) ((float) (parInt4 + parInt6) * f1))
				.color(parInt7, parInt8, parInt9, parInt10).endVertex();
		worldrenderer.pos((double) (parInt1 + parInt5), (double) (parInt2 + parInt6), 0.0D)
				.tex((double) ((float) (parInt3 + parInt5) * f), (double) ((float) (parInt4 + parInt6) * f1))
				.color(parInt7, parInt8, parInt9, parInt10).endVertex();
		worldrenderer.pos((double) (parInt1 + parInt5), (double) parInt2, 0.0D)
				.tex((double) ((float) (parInt3 + parInt5) * f), (double) ((float) parInt4 * f1))
				.color(parInt7, parInt8, parInt9, parInt10).endVertex();
		worldrenderer.pos((double) parInt1, (double) parInt2, 0.0D)
				.tex((double) ((float) parInt3 * f), (double) ((float) parInt4 * f1))
				.color(parInt7, parInt8, parInt9, parInt10).endVertex();
		Tessellator.getInstance().draw();
	}
}
