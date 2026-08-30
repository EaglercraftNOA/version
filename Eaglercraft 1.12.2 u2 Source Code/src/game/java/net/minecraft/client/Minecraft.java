package net.minecraft.client;

import static net.lax1dude.eaglercraft.internal.PlatformOpenGL._wglBindFramebuffer;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import net.lax1dude.eaglercraft.ClientUUIDLoadingCache;
import net.lax1dude.eaglercraft.Display;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EagUtils;
import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.Keyboard;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.PauseMenuCustomizeState;
import net.lax1dude.eaglercraft.futures.Executors;
import net.lax1dude.eaglercraft.futures.FutureTask;
import net.lax1dude.eaglercraft.futures.ListenableFuture;
import net.lax1dude.eaglercraft.futures.ListenableFutureTask;
import net.lax1dude.eaglercraft.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.minecraft.EaglerFontRenderer;
import net.lax1dude.eaglercraft.notifications.ServerNotificationRenderer;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.profile.EaglerProfile;
import net.lax1dude.eaglercraft.socket.AddressResolver;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;

import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.ScreenChatOptions;
import net.minecraft.client.gui.advancements.GuiScreenAdvancements;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.RecipeList;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.MetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.tutorial.Tutorial;
import net.minecraft.client.util.ISearchTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentKeybind;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.LanguageMap;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.peyton.eagler.fs.FileUtils;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.profile.*;

import net.lax1dude.eaglercraft.sp.IntegratedServerState;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.SkullCommand;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenIntegratedServerBusy;
import net.lax1dude.eaglercraft.sp.gui.GuiScreenSingleplayerConnecting;
import net.lax1dude.eaglercraft.sp.server.EaglerSaveFormat;
import net.lax1dude.eaglercraft.webview.WebViewOverlayController;
import net.lax1dude.eaglercraft.socket.RateLimitTracker;
import net.lax1dude.eaglercraft.cookie.ServerCookieDataStore;

public class Minecraft implements IThreadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final ResourceLocation LOCATION_MOJANG_PNG = new ResourceLocation("textures/gui/title/mojang.png");
	public static final boolean IS_RUNNING_ON_MAC = Util.getOSType() == Util.EnumOS.OSX;

	/** The player's GameProfile properties */
	private ServerData currentServerData;

	/** The RenderEngine instance used by Minecraft */
	private TextureManager renderEngine;

	/**
	 * Set to 'this' in Minecraft constructor; used by some settings get methods
	 */
	private static Minecraft theMinecraft;
	private DataFixer dataFixer;
	public PlayerControllerMP playerController;
	private boolean fullscreen;
	private boolean enableGLErrorChecking = false;
	private boolean hasCrashed;

	/** Instance of CrashReport. */
	private CrashReport crashReporter;
	public int displayWidth;
	public int displayHeight;
	public float displayDPI;

	/** True if the player is connected to a realms server */
	private final Timer timer = new Timer(20.0F);

	public WorldClient world;
	public RenderGlobal renderGlobal;
	private RenderManager renderManager;
	private RenderItem renderItem;
	private ItemRenderer itemRenderer;
	public EntityPlayerSP player;
	private Entity renderViewEntity;
	public Entity pointedEntity;
	public ParticleManager effectRenderer;
	private SearchTreeManager field_193995_ae = new SearchTreeManager();
	private final Session session;
	private boolean isGamePaused;
	private boolean wasPaused;
	private float field_193996_ah;
	private int dontPauseTimer = 0;

	/** The font renderer used for displaying and measuring text */
	public FontRenderer fontRendererObj;
	public FontRenderer standardGalacticFontRenderer;

	/** The GuiScreen that's being displayed at the moment. */
	public GuiScreen currentScreen;
	public LoadingScreenRenderer loadingScreen;
	public EntityRenderer entityRenderer;
	public DebugRenderer debugRenderer;

	/** Mouse left click counter */
	private int leftClickCounter;

	/** Display width */
	private final int tempDisplayWidth;

	/** Display height */
	private final int tempDisplayHeight;

	/** Instance of IntegratedServer. */
	public GuiIngame ingameGUI;

	/** Skip render world */
	public boolean skipRenderWorld;

	/** The ray trace hit that the mouse is over. */
	public RayTraceResult objectMouseOver;

	/** The game settings that currently hold effect. */
	public GameSettings gameSettings;
	public CreativeSettings field_191950_u;

	/** Mouse helper instance. */
	public MouseHelper mouseHelper;
	private final String launchedVersion;
	private ISaveFormat saveLoader;

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

	/** The FrameTimer's instance */
	public final FrameTimer frameTimer = new FrameTimer();

	/** Time in nanoseconds of when the class is loaded */
	long startNanoTime = EagRuntime.nanoTime();
	private final boolean isDemo;
	private NetworkManager myNetworkManager;
	private boolean integratedServerIsRunning;

	/**
	 * Keeps track of how long the debug crash keycombo (F3+C) has been pressed for,
	 * in order to crash after 10 seconds.
	 */
	private long debugCrashKeyPressTime = -1L;
	private IReloadableResourceManager mcResourceManager;
	private final MetadataSerializer metadataSerializer_ = new MetadataSerializer();
	private final List<IResourcePack> defaultResourcePacks = Lists.<IResourcePack>newArrayList();
	public final DefaultResourcePack mcDefaultResourcePack;
	private ResourcePackRepository mcResourcePackRepository;
	private LanguageManager mcLanguageManager;
	private BlockColors blockColors;
	private ItemColors itemColors;
	private TextureMap textureMapBlocks;
	private SoundHandler mcSoundHandler;
	private MusicTicker mcMusicTicker;
	private ResourceLocation mojangLogo;
	private final List<FutureTask<?>> scheduledTasks = new LinkedList();
	private ModelManager modelManager;

	/**
	 * The BlockRenderDispatcher instance that will be used based off gamesettings
	 */
	private BlockRendererDispatcher blockRenderDispatcher;
	private final GuiToast field_193034_aS;

	/**
	 * Set to true to keep the game loop running. Set to false by shutdown() to
	 * allow the game loop to exit cleanly.
	 */
	volatile boolean running = true;

	/** String that shows the debug information */
	public String debug = "";
	public boolean renderChunksMany = true;

	/** Approximate time (in ms) of last update to debug string */
	private long debugUpdateTime = getSystemTime();

	/** holds the current fps */
	private int fpsCounter;
	private boolean actionKeyF3;
	private final Tutorial field_193035_aW;

	/** Profiler currently displayed in the debug screen pie chart */
	private String debugProfilerName = "root";

	public static final String mcDataDir = FileUtils.dataDir;
	
	public SkullCommand eagskullCommand;
	public ServerNotificationRenderer notifRenderer;
	public ScaledResolution scaledResolution = null;
	private String reconnectURI = null;

	public Minecraft(GameConfiguration gameConfig) {
		theMinecraft = this;
		LanguageMap.initClient();
		this.launchedVersion = gameConfig.gameInfo.version;
		this.mcDefaultResourcePack = new DefaultResourcePack();
		this.session = gameConfig.userInfo.session;
		LOGGER.info("Setting user: " + this.session.getProfile().getName());
		this.isDemo = gameConfig.gameInfo.isDemo;
		this.displayWidth = gameConfig.displayInfo.width > 0 ? gameConfig.displayInfo.width : 1;
		this.displayHeight = gameConfig.displayInfo.height > 0 ? gameConfig.displayInfo.height : 1;
		this.displayDPI = 1.0f;
		this.tempDisplayWidth = gameConfig.displayInfo.width;
		this.tempDisplayHeight = gameConfig.displayInfo.height;
		this.fullscreen = gameConfig.displayInfo.fullscreen;

		this.enableGLErrorChecking = EagRuntime.getConfiguration().isCheckGLErrors();

		String serverToJoin = EagRuntime.getConfiguration().getServerToJoin();
		if (serverToJoin != null) {
			ServerAddress addr = AddressResolver.resolveAddressFromURI(serverToJoin);
			this.serverName = addr.getIP();
			this.serverPort = addr.getPort();
		}

		TextComponentKeybind.field_193637_b = KeyBinding::func_193626_b;
		this.field_193034_aS = new GuiToast(this);
		this.field_193035_aW = new Tutorial(this);
	}

	public void run() {
		this.running = true;

		try {
			this.startGame();
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Initializing game");
			crashreport.makeCategory("Initialization");
			this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(crashreport));
			return;
		}

		try {
			while (true) {
				if (!this.running) {
					break;
				}

				if (!this.hasCrashed || this.crashReporter == null) {
					this.runGameLoop();
					continue;
				}

				this.displayCrashReport(this.crashReporter);
			}
		} catch (MinecraftError var12) {
			/* sneeze */
		} catch (ReportedException reportedexception) {
			this.addGraphicsAndWorldToCrashReport(reportedexception.getCrashReport());
			LOGGER.fatal("Reported exception thrown!", (Throwable) reportedexception);
			this.displayCrashReport(reportedexception.getCrashReport());
		} catch (Throwable throwable1) {
			CrashReport crashreport1 = this
					.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", throwable1));
			LOGGER.fatal("Unreported exception thrown!", throwable1);
			this.displayCrashReport(crashreport1);
		} finally {
			this.shutdownMinecraftApplet();
		}
	}

	/**
	 * Starts the game: initializes the canvas, the title, the settings, etcetera.
	 */
	private void startGame() throws IOException {
		Bootstrap.register();
		this.dataFixer = DataFixesManager.createFixer();
		this.gameSettings = new GameSettings(this);
		this.field_191950_u = new CreativeSettings(this, this.mcDataDir);
		this.defaultResourcePacks.add(this.mcDefaultResourcePack);

		if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
			this.displayWidth = this.gameSettings.overrideWidth;
			this.displayHeight = this.gameSettings.overrideHeight;
		}

		LOGGER.info("EagRuntime Version: " + EagRuntime.getVersion());
		this.createDisplay();
		this.registerMetadataSerializers();
		this.mcResourcePackRepository = new ResourcePackRepository(this.mcDefaultResourcePack, this.metadataSerializer_,
				this.gameSettings);
		this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
		this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
		this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
		this.refreshResources();
		Bootstrap.register2();
		this.renderEngine = new TextureManager(this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.renderEngine);
		this.drawSplashScreen(this.renderEngine);
		this.saveLoader = new EaglerSaveFormat(new VFile2(this.mcDataDir, "worlds"), this.dataFixer);
		this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
		this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
		this.scaledResolution = new ScaledResolution(this);
		this.mcMusicTicker = new MusicTicker(this);
		this.fontRendererObj = EaglerFontRenderer.createSupportedFontRenderer(this.gameSettings,
				new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);

		if (this.gameSettings.language != null) {
			this.fontRendererObj.setUnicodeFlag(this.isUnicode());
			this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
		}

		this.standardGalacticFontRenderer = EaglerFontRenderer.createSupportedFontRenderer(this.gameSettings,
				new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
		this.mcResourceManager.registerReloadListener(this.fontRendererObj);
		this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
		this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
		this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
		this.mouseHelper = new MouseHelper();
		this.checkGLError("Pre startup");
		GlStateManager.enableTexture2D();
		GlStateManager.shadeModel(7425);
		GlStateManager.clearDepth(1.0F);
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.cullFace(RealOpenGLEnums.GL_BACK);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.matrixMode(5888);
		this.checkGLError("Startup");
		this.textureMapBlocks = new TextureMap("textures");
		this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
		this.renderEngine.loadTickableTexture(TextureMap.LOCATION_BLOCKS_TEXTURE, this.textureMapBlocks);
		this.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		this.textureMapBlocks.setBlurMipmapDirect(false, this.gameSettings.mipmapLevels > 0);
		this.modelManager = new ModelManager(this.textureMapBlocks);
		this.mcResourceManager.registerReloadListener(this.modelManager);
		this.blockColors = BlockColors.init();
		this.itemColors = ItemColors.init(this.blockColors);
		this.renderItem = new RenderItem(this.renderEngine, this.modelManager, this.itemColors);
		this.renderManager = new RenderManager(this.renderEngine, this.renderItem);
		this.itemRenderer = new ItemRenderer(this);
		this.mcResourceManager.registerReloadListener(this.renderItem);
		this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
		this.mcResourceManager.registerReloadListener(this.entityRenderer);
		this.blockRenderDispatcher = new BlockRendererDispatcher(this.modelManager.getBlockModelShapes(),
				this.blockColors);
		this.mcResourceManager.registerReloadListener(this.blockRenderDispatcher);
		this.renderGlobal = new RenderGlobal(this);
		this.mcResourceManager.registerReloadListener(this.renderGlobal);
		this.func_193986_ar();
		this.mcResourceManager.registerReloadListener(this.field_193995_ae);
		GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
		this.effectRenderer = new ParticleManager(this.world, this.renderEngine);
		SkinPreviewRenderer.initialize();
		this.checkGLError("Post startup");
		this.ingameGUI = new GuiIngame(this);
		
		this.eagskullCommand = new SkullCommand(this);
		
		this.notifRenderer = new ServerNotificationRenderer();
		this.notifRenderer.init();
		this.notifRenderer.setResolution(this, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(),
				scaledResolution.getScaleFactor());

		ServerList.initServerList(this);
		EaglerProfile.read();
		ServerCookieDataStore.load();

		if (this.serverName != null) {
			this.displayGuiScreen(new GuiConnecting(new GuiScreenEditProfile(new GuiMainMenu()), this, this.serverName,
					this.serverPort));
		} else {
			this.displayGuiScreen(new GuiScreenEditProfile(new GuiMainMenu()));
		}

		this.renderEngine.deleteTexture(this.mojangLogo);
		this.mojangLogo = null;
		this.loadingScreen = new LoadingScreenRenderer(this);
		this.debugRenderer = new DebugRenderer(this);

		this.renderGlobal.makeEntityOutlineShader();
	}

	private void func_193986_ar() {
		SearchTree<ItemStack> searchtree = new SearchTree<ItemStack>((p_193988_0_) -> {
			return (List) p_193988_0_.getTooltip((EntityPlayer) null, ITooltipFlag.TooltipFlags.NORMAL).stream()
					.map(TextFormatting::getTextWithoutFormattingCodes).map(String::trim).filter((p_193984_0_) -> {
						return !p_193984_0_.isEmpty();
					}).collect(Collectors.toList());
		}, (p_193985_0_) -> {
			return Collections.singleton(Item.REGISTRY.getNameForObject(p_193985_0_.getItem()));
		});
		NonNullList<ItemStack> nonnulllist = NonNullList.<ItemStack>func_191196_a();

		for (Item item : Item.REGISTRY) {
			item.getSubItems(CreativeTabs.SEARCH, nonnulllist);
		}

		nonnulllist.forEach(searchtree::func_194043_a);
		SearchTree<RecipeList> searchtree1 = new SearchTree<RecipeList>((p_193990_0_) -> {
			return (List) p_193990_0_.func_192711_b().stream().flatMap((p_193993_0_) -> {
				return p_193993_0_.getRecipeOutput().getTooltip((EntityPlayer) null, ITooltipFlag.TooltipFlags.NORMAL)
						.stream();
			}).map(TextFormatting::getTextWithoutFormattingCodes).map(String::trim).filter((p_193994_0_) -> {
				return !p_193994_0_.isEmpty();
			}).collect(Collectors.toList());
		}, (p_193991_0_) -> {
			return (List) p_193991_0_.func_192711_b().stream().map((p_193992_0_) -> {
				return Item.REGISTRY.getNameForObject(p_193992_0_.getRecipeOutput().getItem());
			}).collect(Collectors.toList());
		});
		RecipeBookClient.field_194087_f.forEach(searchtree1::func_194043_a);
		this.field_193995_ae.func_194009_a(SearchTreeManager.field_194011_a, searchtree);
		this.field_193995_ae.func_194009_a(SearchTreeManager.field_194012_b, searchtree1);
	}

	private void registerMetadataSerializers() {
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
	}

	private void createDisplay() {
		Display.create();
		Display.setTitle("Eaglercraft 1.12.2");
	}

	public String getVersion() {
		return this.launchedVersion;
	}

	public void crashed(CrashReport crash) {
		this.hasCrashed = true;
		this.crashReporter = crash;
	}

	/**
	 * Wrapper around displayCrashReportInternal
	 */
	public void displayCrashReport(CrashReport crashReportIn) {
		String report = crashReportIn.getCompleteReport();
		Bootstrap.printToSYSOUT(report);
		PlatformRuntime.writeCrashReport(report);
		if (PlatformRuntime.getPlatformType() == EnumPlatformType.JAVASCRIPT) {
			System.err.println(
					"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			System.err.println("NATIVE BROWSER EXCEPTION:");
			if (!PlatformRuntime.printJSExceptionIfBrowser(crashReportIn.getCrashCause())) {
				System.err.println("<undefined>");
			}
			System.err.println(
					"%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		}
	}

	public boolean isUnicode() {
		return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
	}

	public void refreshResources() {

		GlStateManager.recompileShaders();

		List<IResourcePack> list = Lists.newArrayList(this.defaultResourcePacks);

		for (ResourcePackRepository.Entry resourcepackrepository$entry : this.mcResourcePackRepository
				.getRepositoryEntries()) {
			list.add(resourcepackrepository$entry.getResourcePack());
		}

		if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
			list.add(this.mcResourcePackRepository.getResourcePackInstance());
		}

		try {
			this.mcResourceManager.reloadResources(list);
		} catch (RuntimeException runtimeexception) {
			LOGGER.info("Caught error stitching, removing all assigned resourcepacks", (Throwable) runtimeexception);
			list.clear();
			list.addAll(this.defaultResourcePacks);
			this.mcResourcePackRepository.setRepositories(Collections.emptyList());
			this.mcResourceManager.reloadResources(list);
			this.gameSettings.resourcePacks.clear();
			this.gameSettings.incompatibleResourcePacks.clear();
			this.gameSettings.saveOptions();
		}

		this.mcLanguageManager.parseLanguageMetadata(list);

		if (this.renderGlobal != null) {
			this.renderGlobal.loadRenderers();
		}
	}

	private void updateDisplayMode() {
		this.displayWidth = Display.getWidth();
		this.displayHeight = Display.getHeight();
		this.displayDPI = Display.getDPI();
		this.scaledResolution = new ScaledResolution(this);
	}

	private void drawSplashScreen(TextureManager textureManagerInstance) {
		Display.update();
		updateDisplayMode();
		GlStateManager.viewport(0, 0, displayWidth, displayHeight);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, (double) scaledResolution.getScaledWidth(),
				(double) scaledResolution.getScaledHeight(), 0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.disableDepth();
		GlStateManager.enableTexture2D();
		InputStream inputstream = null;

		try {
			inputstream = this.mcDefaultResourcePack.getInputStream(LOCATION_MOJANG_PNG);
			this.mojangLogo = textureManagerInstance.getDynamicTextureLocation("logo",
					new DynamicTexture(ImageData.loadImageFile(inputstream)));
			textureManagerInstance.bindTexture(this.mojangLogo);
		} catch (IOException ioexception) {
			LOGGER.error("Unable to load logo: {}", LOCATION_MOJANG_PNG, ioexception);
		} finally {
			IOUtils.closeQuietly(inputstream);
		}

		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferbuilder.pos(0.0D, (double) this.displayHeight, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255)
				.endVertex();
		bufferbuilder.pos((double) this.displayWidth, (double) this.displayHeight, 0.0D).tex(0.0D, 0.0D)
				.color(255, 255, 255, 255).endVertex();
		bufferbuilder.pos((double) this.displayWidth, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 0.0D).color(255, 255, 255, 255).endVertex();
		tessellator.draw();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		short short1 = 256;
		short short2 = 256;
		this.draw((scaledResolution.getScaledWidth() - short1) / 2,
				(scaledResolution.getScaledHeight() - short2) / 2, 0, 0, short1, short2, 255, 255, 255, 255);
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.1F);
		this.updateDisplay();
	}

	/**
	 * Draw with the WorldRenderer
	 */
	public void draw(int posX, int posY, int texU, int texV, int width, int height, int red, int green, int blue,
			int alpha) {
		WorldRenderer bufferbuilder = Tessellator.getInstance().getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		bufferbuilder.pos((double) posX, (double) (posY + height), 0.0D)
				.tex((double) ((float) texU * 0.00390625F), (double) ((float) (texV + height) * 0.00390625F))
				.color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos((double) (posX + width), (double) (posY + height), 0.0D)
				.tex((double) ((float) (texU + width) * 0.00390625F), (double) ((float) (texV + height) * 0.00390625F))
				.color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos((double) (posX + width), (double) posY, 0.0D)
				.tex((double) ((float) (texU + width) * 0.00390625F), (double) ((float) texV * 0.00390625F))
				.color(red, green, blue, alpha).endVertex();
		bufferbuilder.pos((double) posX, (double) posY, 0.0D)
				.tex((double) ((float) texU * 0.00390625F), (double) ((float) texV * 0.00390625F))
				.color(red, green, blue, alpha).endVertex();
		Tessellator.getInstance().draw();
	}

	/**
	 * Returns the save loader that is currently being used
	 */
	public ISaveFormat getSaveLoader() {
		return this.saveLoader;
	}

	/**
	 * Sets the argument GuiScreen as the main (topmost visible) screen.
	 */
	public void displayGuiScreen(GuiScreen guiScreenIn) {
		if (this.currentScreen != null) {
			this.currentScreen.onGuiClosed();
		}

		if (guiScreenIn == null && this.world == null) {
			guiScreenIn = new GuiMainMenu();
		} else if (guiScreenIn == null && this.player.getHealth() <= 0.0F) {
			guiScreenIn = new GuiGameOver((ITextComponent) null);
		}

		if (guiScreenIn instanceof GuiMainMenu || guiScreenIn instanceof GuiMultiplayer) {
			this.gameSettings.showDebugInfo = false;
			this.ingameGUI.getChatGUI().clearChatMessages(true);
		}

		this.currentScreen = guiScreenIn;
		this.scaledResolution = new ScaledResolution(this);

		if (guiScreenIn != null) {
			this.setIngameNotInFocus();
			((GuiScreen) guiScreenIn).setWorldAndResolution(this, scaledResolution.getScaledWidth(),
					scaledResolution.getScaledHeight());
			this.skipRenderWorld = false;
		} else {
			this.mcSoundHandler.resumeSounds();
			this.setIngameFocus();
		}
		EagRuntime.getConfiguration().getHooks().callScreenChangedHook(
				currentScreen != null ? currentScreen.getClass().getName() : null, scaledResolution.getScaledWidth(),
				scaledResolution.getScaledHeight(), displayWidth, displayHeight, scaledResolution.getScaleFactor());
	}

	/**
	 * Checks for an OpenGL error. If there is one, prints the error ID and error
	 * string.
	 */
	private void checkGLError(String message) {
		if (this.enableGLErrorChecking) {
			int i = EaglercraftGPU.glGetError();

			if (i != 0) {
				String s = EaglercraftGPU.gluErrorString(i);
				LOGGER.error("########## GL ERROR ##########");
				LOGGER.error("@ {}", (Object) message);
				LOGGER.error("{}: {}", Integer.valueOf(i), s);
			}
		}
	}

	/**
	 * Shuts down the minecraft applet by stopping the resource downloads, and
	 * clearing up GL stuff; called when the application (or web page) is exited.
	 */
	public void shutdownMinecraftApplet() {
		try {
			LOGGER.info("Stopping!");

			try {
				this.loadWorld((WorldClient) null);
			} catch (Throwable var5) {
				;
			}

			this.mcSoundHandler.unloadSounds();

			if (SingleplayerServerController.isWorldRunning()) {
				SingleplayerServerController.shutdownEaglercraftServer();
				while (SingleplayerServerController.getStatusState() == IntegratedServerState.WORLD_UNLOADING) {
					EagUtils.sleep(50l);
					SingleplayerServerController.runTick();
				}
			}
			if (SingleplayerServerController.isIntegratedServerWorkerAlive()
					&& SingleplayerServerController.canKillWorker()) {
				SingleplayerServerController.killWorker();
				EagUtils.sleep(50l);
			}
		} finally {
			EagRuntime.destroy();
			if (!this.hasCrashed) {
				EagRuntime.exit();
			}
		}
	}

	/**
	 * Called repeatedly from run()
	 */
	private void runGameLoop() throws IOException {
		if (Display.isCloseRequested()) {
			this.shutdown();
		}

		this.timer.updateTimer();

		synchronized (this.scheduledTasks) {
			while (!this.scheduledTasks.isEmpty()) {
				Util.runTask((FutureTask) this.scheduledTasks.remove(0), LOGGER);
			}
		}

		long l = EagRuntime.nanoTime();

		for (int j = 0; j < this.timer.elapsedTicks; ++j) {
			this.runTick();
		}

		long i1 = EagRuntime.nanoTime() - l;
		this.checkGLError("Pre render");
		this.mcSoundHandler.setListener(this.player, this.timer.field_194147_b);
		if (!Display.contextLost()) {
			EaglercraftGPU.optimize();
			_wglBindFramebuffer(0x8D40, null);
			GlStateManager.viewport(0, 0, this.displayWidth, this.displayHeight);
			GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
			GlStateManager.clear(RealOpenGLEnums.GL_COLOR_BUFFER_BIT | RealOpenGLEnums.GL_DEPTH_BUFFER_BIT);
			GlStateManager.enableTexture2D();
			
			if (!this.skipRenderWorld) {
				this.entityRenderer.updateCameraAndRender(this.isGamePaused ? this.field_193996_ah : this.timer.field_194147_b, i1);
				this.field_193034_aS.func_191783_a(this.scaledResolution);
			}
		}

		this.updateDisplay();
		this.checkGLError("Post render");
		++this.fpsCounter;

		boolean flag = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame();
		if (this.isGamePaused != flag) {
			if (this.isGamePaused) {
				this.field_193996_ah = this.timer.field_194147_b;
			} else {
				this.timer.field_194147_b = this.field_193996_ah;
			}

			this.isGamePaused = flag;
		}
		
		RateLimitTracker.tick();

		if (wasPaused != flag) {
			SingleplayerServerController.setPaused(flag);
			if (isGamePaused) {
				mcSoundHandler.pauseSounds();
			} else {
				mcSoundHandler.resumeSounds();
			}
			wasPaused = flag;
		}
		
		WebViewOverlayController.runTick();
		SingleplayerServerController.runTick();

		long k = EagRuntime.nanoTime();
		this.frameTimer.addFrame(k - this.startNanoTime);
		this.startNanoTime = k;

		while (getSystemTime() >= this.debugUpdateTime + 1000L) {
			debugFPS = this.fpsCounter;
			this.debug = HString.format("%d fps (%d chunk update%s) T: %s%s%s%s%s",
					new Object[] { Integer.valueOf(debugFPS), Integer.valueOf(RenderChunk.renderChunksUpdated),
							RenderChunk.renderChunksUpdated == 1 ? "" : "s",
							(float) this.gameSettings.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT
									.getValueMax() ? "inf" : Integer.valueOf(this.gameSettings.limitFramerate),
							this.gameSettings.enableVsync ? " vsync" : "",
							this.gameSettings.fancyGraphics ? "" : " fast", this.gameSettings.clouds == 0 ? ""
									: (this.gameSettings.clouds == 1 ? " fast-clouds" : " fancy-clouds"),
							"" });
			RenderChunk.renderChunksUpdated = 0;
			this.debugUpdateTime += 1000L;
			this.fpsCounter = 0;
		}
	}

	public void updateDisplay() {
		if (Display.isVSyncSupported()) {
			Display.setVSync(this.gameSettings.enableVsync);
		} else {
			this.gameSettings.enableVsync = false;
		}
		if (!this.gameSettings.enableVsync && this.isFramerateLimitBelowMax()) {
			Display.update(this.getLimitFramerate());
		} else {
			Display.update(0);
		}
		this.checkWindowResize();
	}

	protected void checkWindowResize() {
		float dpiFetch = -1.0f;
		if (!this.fullscreen
				&& (Display.wasResized() || (dpiFetch = Math.max(Display.getDPI(), 1.0f)) != this.displayDPI)) {
			int i = this.displayWidth;
			int j = this.displayHeight;
			float f = this.displayDPI;
			this.displayWidth = Display.getWidth();
			this.displayHeight = Display.getHeight();
			this.displayDPI = dpiFetch == -1.0f ? Math.max(Display.getDPI(), 1.0f) : dpiFetch;
			if (this.displayWidth != i || this.displayHeight != j || this.displayDPI != f) {
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
		return this.world == null && this.currentScreen != null ? 30 : this.gameSettings.limitFramerate;
	}

	public boolean isFramerateLimitBelowMax() {
		return (float) this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
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
		if (Display.isActive()) {
			if (!this.inGameHasFocus) {
				if (!IS_RUNNING_ON_MAC) {
					KeyBinding.updateKeyBindState();
				}

				this.inGameHasFocus = true;
				this.mouseHelper.grabMouseCursor();
				this.displayGuiScreen((GuiScreen) null);
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
			this.mouseHelper.ungrabMouseCursor();
		}
	}

	/**
	 * Displays the ingame menu
	 */
	public void displayInGameMenu() {
		if (this.currentScreen == null) {
			this.displayGuiScreen(new GuiIngameMenu());

			if (this.isSingleplayer()) {
				this.mcSoundHandler.pauseSounds();
			}
		}
	}

	private void sendClickBlockToController(boolean leftClick) {
		if (!leftClick) {
			this.leftClickCounter = 0;
		}

		if (this.leftClickCounter <= 0 && !this.player.isHandActive()) {
			if (leftClick && this.objectMouseOver != null
					&& this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos blockpos = this.objectMouseOver.getBlockPos();

				if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR
						&& this.playerController.onPlayerDamageBlock(blockpos, this.objectMouseOver.sideHit)) {
					this.effectRenderer.addBlockHitEffects(blockpos, this.objectMouseOver.sideHit);
					this.player.swingArm(EnumHand.MAIN_HAND);
				}
			} else {
				this.playerController.resetBlockRemoving();
			}
		}
	}

	private void clickMouse() {
		if (this.leftClickCounter <= 0) {
			if (this.objectMouseOver == null) {
				LOGGER.error("Null returned as 'hitResult', this shouldn't happen!");

				if (this.playerController.isNotCreative()) {
					this.leftClickCounter = 10;
				}
			} else if (!this.player.isRowingBoat()) {
				switch (this.objectMouseOver.typeOfHit) {
				case ENTITY:
					this.playerController.attackEntity(this.player, this.objectMouseOver.entityHit);
					break;

				case BLOCK:
					BlockPos blockpos = this.objectMouseOver.getBlockPos();

					if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR) {
						this.playerController.clickBlock(blockpos, this.objectMouseOver.sideHit);
						break;
					}

				case MISS:
					if (this.playerController.isNotCreative()) {
						this.leftClickCounter = 10;
					}

					this.player.resetCooldown();
				}

				this.player.swingArm(EnumHand.MAIN_HAND);
			}
		}
	}

	@SuppressWarnings("incomplete-switch")

	/**
	 * Called when user clicked he's mouse right button (place)
	 */
	private void rightClickMouse() {
		if (!this.playerController.getIsHittingBlock()) {
			this.rightClickDelayTimer = 4;

			if (!this.player.isRowingBoat()) {
				if (this.objectMouseOver == null) {
					LOGGER.warn("Null returned as 'hitResult', this shouldn't happen!");
				}

				EnumHand[] hands = EnumHand._VALUES;
				for (int ii = 0; ii < hands.length; ++ii) {
					EnumHand enumhand = hands[ii];
					ItemStack itemstack = this.player.getHeldItem(enumhand);

					if (this.objectMouseOver != null) {
						switch (this.objectMouseOver.typeOfHit) {
						case ENTITY:
							if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entityHit,
									this.objectMouseOver, enumhand) == EnumActionResult.SUCCESS) {
								return;
							}

							if (this.playerController.interactWithEntity(this.player, this.objectMouseOver.entityHit,
									enumhand) == EnumActionResult.SUCCESS) {
								return;
							}

							break;

						case BLOCK:
							BlockPos blockpos = this.objectMouseOver.getBlockPos();

							if (this.world.getBlockState(blockpos).getMaterial() != Material.AIR) {
								int i = itemstack.func_190916_E();
								EnumActionResult enumactionresult = this.playerController.processRightClickBlock(
										this.player, this.world, blockpos, this.objectMouseOver.sideHit,
										this.objectMouseOver.hitVec, enumhand);

								if (enumactionresult == EnumActionResult.SUCCESS) {
									this.player.swingArm(enumhand);

									if (!itemstack.func_190926_b() && (itemstack.func_190916_E() != i
											|| this.playerController.isInCreativeMode())) {
										this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
									}

									return;
								}
							}
						}
					}

					if (!itemstack.func_190926_b() && this.playerController.processRightClick(this.player, this.world,
							enumhand) == EnumActionResult.SUCCESS) {
						this.entityRenderer.itemRenderer.resetEquippedProgress(enumhand);
						return;
					}
				}
			}
		}
	}

	/**
	 * Toggles fullscreen mode.
	 */
	public void toggleFullscreen() {
		Display.toggleFullscreen();
	}

	/**
	 * Called to resize the current screen.
	 */
	private void resize(int width, int height) {
		this.displayWidth = Math.max(1, width);
		this.displayHeight = Math.max(1, height);
		this.scaledResolution = new ScaledResolution(this);
		if (this.currentScreen != null) {
			this.currentScreen.onResize(this, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
		}

		this.loadingScreen = new LoadingScreenRenderer(this);
		
		if (notifRenderer != null) {
			notifRenderer.setResolution(this, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(),
					scaledResolution.getScaleFactor());
		}

		EagRuntime.getConfiguration().getHooks().callScreenChangedHook(
				currentScreen != null ? currentScreen.getClass().getName() : null, scaledResolution.getScaledWidth(),
				scaledResolution.getScaledHeight(), displayWidth, displayHeight, scaledResolution.getScaleFactor());
	}

	/**
	 * Return the musicTicker's instance
	 */
	public MusicTicker getMusicTicker() {
		return this.mcMusicTicker;
	}

	/**
	 * Runs the current tick.
	 */
	public void runTick() throws IOException {
		if (this.rightClickDelayTimer > 0) {
			--this.rightClickDelayTimer;
		}

		if (!this.isGamePaused) {
			this.ingameGUI.updateTick();
		}

		this.entityRenderer.getMouseOver(1.0F);
		this.field_193035_aW.func_193297_a(this.world, this.objectMouseOver);

		if (!this.isGamePaused && this.world != null) {
			this.playerController.updateController();
		}

		if (!this.isGamePaused) {
			this.renderEngine.tick();
			GlStateManager.viewport(0, 0, displayWidth, displayHeight); // to be safe
		}
		
		if (this.player != null && this.player.connection != null) {
			this.player.connection.getEaglerMessageController().flush();
		}

		if (this.currentScreen == null && this.player != null) {
			if (this.player.getHealth() <= 0.0F && !(this.currentScreen instanceof GuiGameOver)) {
				this.displayGuiScreen((GuiScreen) null);
			} else if (this.player.isPlayerSleeping() && this.world != null) {
				this.displayGuiScreen(new GuiSleepMP());
			}
			if (this.currentScreen == null && this.dontPauseTimer <= 0) {
				if (!Mouse.isMouseGrabbed()) {
					this.setIngameNotInFocus();
					this.displayInGameMenu();
				}
			}
		} else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP
				&& !this.player.isPlayerSleeping()) {
			this.displayGuiScreen((GuiScreen) null);
		}

		if (this.currentScreen != null) {
			this.leftClickCounter = 10000;
			this.dontPauseTimer = 6;
		} else {
			if (this.dontPauseTimer > 0) {
				--this.dontPauseTimer;
			}
		}

		if (this.currentScreen != null) {
			try {
				this.currentScreen.handleInput();
			} catch (Throwable throwable1) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Updating screen events");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Affected screen");
				crashreportcategory.setDetail("Screen name", new ICrashReportDetail<String>() {
					public String call() throws Exception {
						return Minecraft.this.currentScreen.getClass().getCanonicalName();
					}
				});
				throw new ReportedException(crashreport);
			}

			if (this.currentScreen != null) {
				try {
					this.currentScreen.updateScreen();
				} catch (Throwable throwable) {
					CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Ticking screen");
					CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Affected screen");
					crashreportcategory1.setDetail("Screen name", new ICrashReportDetail<String>() {
						public String call() throws Exception {
							return Minecraft.this.currentScreen.getClass().getCanonicalName();
						}
					});
					throw new ReportedException(crashreport1);
				}
			}
		}

		if (this.currentScreen == null || this.currentScreen.allowUserInput) {
			this.runTickMouse();

			if (this.leftClickCounter > 0) {
				--this.leftClickCounter;
			}

			this.runTickKeyboard();
		}

		if (this.world != null) {
			if (this.player != null) {
				++this.joinPlayerCounter;

				if (this.joinPlayerCounter == 30) {
					this.joinPlayerCounter = 0;
					this.world.joinEntityInSurroundings(this.player);
				}
			}

			if (!this.isGamePaused) {
				this.entityRenderer.updateRenderer();
			}

			if (!this.isGamePaused) {
				this.renderGlobal.updateClouds();
			}

			if (!this.isGamePaused) {
				if (this.world.getLastLightningBolt() > 0) {
					this.world.setLastLightningBolt(this.world.getLastLightningBolt() - 1);
				}

				this.world.updateEntities();
			}
			this.eagskullCommand.tick();
		}

		if (!this.isGamePaused) {
			this.mcMusicTicker.update();
			this.mcSoundHandler.update();
		}

		if (this.world != null) {
			if (!this.isGamePaused) {
				this.world.setAllowedSpawnTypes(this.world.getDifficulty() != EnumDifficulty.PEACEFUL, true);
				this.field_193035_aW.func_193303_d();

				try {
					this.world.tick();
				} catch (Throwable throwable2) {
					CrashReport crashreport2 = CrashReport.makeCrashReport(throwable2, "Exception in world tick");

					if (this.world == null) {
						CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Affected level");
						crashreportcategory2.addCrashSection("Problem", "Level is null!");
					} else {
						this.world.addWorldInfoToCrashReport(crashreport2);
					}

					throw new ReportedException(crashreport2);
				}
			}

			if (!this.isGamePaused && this.world != null) {
				this.world.doVoidFogParticles(MathHelper.floor(this.player.posX), MathHelper.floor(this.player.posY),
						MathHelper.floor(this.player.posZ));
			}

			if (!this.isGamePaused) {
				this.effectRenderer.updateEffects();
			}
		} else if (this.myNetworkManager != null) {
			this.myNetworkManager.processReceivedPackets();
		}

		if (this.world == null) {
			if (currentScreen != null && currentScreen.shouldHangupIntegratedServer()) {
				if (SingleplayerServerController.hangupEaglercraftServer()) {
					this.displayGuiScreen(new GuiScreenIntegratedServerBusy(currentScreen,
							"singleplayer.busy.stoppingIntegratedServer",
							"singleplayer.failed.stoppingIntegratedServer", SingleplayerServerController::isReady));
				}
			}
		}
		
		if (reconnectURI != null) {
			String reconURI = reconnectURI;
			reconnectURI = null;
			if (EagRuntime.getConfiguration().isAllowServerRedirects()) {
				boolean enableCookies;
				boolean msg;
				if (this.currentServerData != null) {
					enableCookies = this.currentServerData.enableCookies;
					msg = false;
				} else {
					enableCookies = EagRuntime.getConfiguration().isEnableServerCookies();
					msg = true;
				}
				if (world != null) {
					world.sendQuittingDisconnectingPacket();
					loadWorld(null);
				}
				LOGGER.info("Recieved SPacketRedirectClientV4EAG, reconnecting to: {}", reconURI);
				if (msg) {
					LOGGER.warn("No existing server connection, cookies will default to {}!",
							enableCookies ? "enabled" : "disabled");
				}
				ServerAddress addr = AddressResolver.resolveAddressFromURI(reconURI);
				this.displayGuiScreen(
						new GuiConnecting(new GuiMainMenu(), this, addr.getIP(), addr.getPort(), enableCookies));
			} else {
				LOGGER.warn("Server redirect blocked: {}", reconURI);
			}
		}

		this.systemTime = getSystemTime();
	}

	private void runTickKeyboard() throws IOException {
		while (Keyboard.next()) {
			int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

			if (i == 0x1D && (Keyboard.areKeysLocked() || isFullScreen())) {
				KeyBinding.setKeyBindState(gameSettings.keyBindSprint.getKeyCode(), Keyboard.getEventKeyState());
			}

			if (this.debugCrashKeyPressTime > 0L) {
				if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
					throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
				}

				if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
					this.debugCrashKeyPressTime = -1L;
				}
			} else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
				this.actionKeyF3 = true;
				this.debugCrashKeyPressTime = getSystemTime();
			}

			this.dispatchKeypresses();

			if (this.currentScreen != null) {
				this.currentScreen.handleKeyboardInput();
			}

			boolean flag = Keyboard.getEventKeyState();

			if (flag) {
				if (i == 62 && this.entityRenderer != null) {
					this.entityRenderer.switchUseShader();
				}

				boolean flag1 = false;

				if (this.currentScreen == null) {
					if (i == 1 /* || (i > -1 && i == this.gameSettings.keyBindClose.getKeyCode()) */) {
						this.displayInGameMenu();
					}

					flag1 = Keyboard.isKeyDown(61) && this.processKeyF3(i);
					this.actionKeyF3 |= flag1;

					if (i == 59) {
						this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
					}
				}

				if (flag1) {
					KeyBinding.setKeyBindState(i, false);
				} else {
					KeyBinding.setKeyBindState(i, true);
					KeyBinding.onTick(i);
				}
			} else {
				KeyBinding.setKeyBindState(i, false);

				if (i == 61) {
					if (this.actionKeyF3) {
						this.actionKeyF3 = false;
					} else {
						this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
						this.gameSettings.showDebugProfilerChart = this.gameSettings.showDebugInfo
								&& GuiScreen.isShiftKeyDown();
						this.gameSettings.showLagometer = this.gameSettings.showDebugInfo && GuiScreen.isAltKeyDown();
					}
				}
			}
		}

		this.processKeyBinds();
	}

	private boolean processKeyF3(int p_184122_1_) {
		if (p_184122_1_ == 30) {
			this.renderGlobal.loadRenderers();
			this.func_190521_a("debug.reload_chunks.message");
			return true;
		} else if (p_184122_1_ == 48) {
			boolean flag1 = !this.renderManager.isDebugBoundingBox();
			this.renderManager.setDebugBoundingBox(flag1);
			this.func_190521_a(flag1 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
			return true;
		} else if (p_184122_1_ == 32) {
			if (this.ingameGUI != null) {
				this.ingameGUI.getChatGUI().clearChatMessages(false);
			}

			return true;
		} else if (p_184122_1_ == 33) {
			this.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, GuiScreen.isShiftKeyDown() ? -1 : 1);
			this.func_190521_a("debug.cycle_renderdistance.message", this.gameSettings.renderDistanceChunks);
			return true;
		} else if (p_184122_1_ == 34) {
			boolean flag = this.debugRenderer.toggleDebugScreen();
			this.func_190521_a(flag ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
			return true;
		} else if (p_184122_1_ == 35) {
			this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
			this.func_190521_a(this.gameSettings.advancedItemTooltips ? "debug.advanced_tooltips.on"
					: "debug.advanced_tooltips.off");
			this.gameSettings.saveOptions();
			return true;
		} else if (p_184122_1_ == 49) {
			if (!this.player.canCommandSenderUseCommand(2, "")) {
				this.func_190521_a("debug.creative_spectator.error");
			} else if (this.player.isCreative()) {
				this.player.sendChatMessage("/gamemode spectator");
			} else if (this.player.isSpectator()) {
				this.player.sendChatMessage("/gamemode creative");
			}

			return true;
		} else if (p_184122_1_ == 25) {
			this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
			this.gameSettings.saveOptions();
			this.func_190521_a(this.gameSettings.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
			return true;
		} else if (p_184122_1_ == 16) {
			this.func_190521_a("debug.help.message");
			GuiNewChat guinewchat = this.ingameGUI.getChatGUI();
			guinewchat.printChatMessage(new TextComponentTranslation("debug.reload_chunks.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.show_hitboxes.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.clear_chat.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.cycle_renderdistance.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.chunk_boundaries.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.advanced_tooltips.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.creative_spectator.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.pause_focus.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.help.help", new Object[0]));
			guinewchat.printChatMessage(new TextComponentTranslation("debug.reload_resourcepacks.help", new Object[0]));
			return true;
		} else if (p_184122_1_ == 20) {
			this.func_190521_a("debug.reload_resourcepacks.message");
			this.refreshResources();
			return true;
		} else {
			return false;
		}
	}

	private void processKeyBinds() {
		for (; this.gameSettings.keyBindTogglePerspective.isPressed(); this.renderGlobal
				.setDisplayListEntitiesDirty()) {
			++this.gameSettings.thirdPersonView;

			if (this.gameSettings.thirdPersonView > 2) {
				this.gameSettings.thirdPersonView = 0;
			}

			if (this.gameSettings.thirdPersonView == 0) {
				this.entityRenderer.loadEntityShader(this.getRenderViewEntity());
			} else if (this.gameSettings.thirdPersonView == 1) {
				this.entityRenderer.loadEntityShader((Entity) null);
			}
		}

		while (this.gameSettings.keyBindSmoothCamera.isPressed()) {
			this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
		}

		for (int i = 0; i < 9; ++i) {
			boolean flag = this.gameSettings.field_193629_ap.isKeyDown();
			boolean flag1 = this.gameSettings.field_193630_aq.isKeyDown();

			if (this.gameSettings.keyBindsHotbar[i].isPressed()) {
				if (this.player.isSpectator()) {
					this.ingameGUI.getSpectatorGui().onHotbarSelected(i);
				} else if (!this.player.isCreative() || this.currentScreen != null || !flag1 && !flag) {
					this.player.inventory.currentItem = i;
				} else {
					GuiContainerCreative.func_192044_a(this, i, flag1, flag);
				}
			}
		}

		while (this.gameSettings.keyBindInventory.isPressed()) {
			if (this.playerController.isRidingHorse()) {
				this.player.sendHorseInventory();
			} else {
				this.field_193035_aW.func_193296_a();
				this.displayGuiScreen(new GuiInventory(this.player));
			}
		}

		while (this.gameSettings.field_194146_ao.isPressed()) {
			this.displayGuiScreen(new GuiScreenAdvancements(this.player.connection.func_191982_f()));
		}

		while (this.gameSettings.keyBindSwapHands.isPressed()) {
			if (!this.player.isSpectator()) {
				this.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.SWAP_HELD_ITEMS,
						BlockPos.ORIGIN, EnumFacing.DOWN));
			}
		}

		while (this.gameSettings.keyBindDrop.isPressed()) {
			if (!this.player.isSpectator()) {
				this.player.dropItem(GuiScreen.isCtrlKeyDown());
			}
		}

		boolean flag2 = this.gameSettings.chatVisibility != EntityPlayer.EnumChatVisibility.HIDDEN;

		if (flag2) {
			while (this.gameSettings.keyBindChat.isPressed()) {
				this.displayGuiScreen(new GuiChat());
			}

			if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed()) {
				this.displayGuiScreen(new GuiChat("/"));
			}
		}

		if (this.player.isHandActive()) {
			if (!this.gameSettings.keyBindUseItem.isKeyDown()) {
				this.playerController.onStoppedUsingItem(this.player);
			}

			while (this.gameSettings.keyBindAttack.isPressed()) {
				;
			}

			while (this.gameSettings.keyBindUseItem.isPressed()) {
				;
			}

			while (this.gameSettings.keyBindPickBlock.isPressed()) {
				;
			}
		} else {
			while (this.gameSettings.keyBindAttack.isPressed()) {
				this.clickMouse();
			}

			while (this.gameSettings.keyBindUseItem.isPressed()) {
				this.rightClickMouse();
			}

			while (this.gameSettings.keyBindPickBlock.isPressed()) {
				this.middleClickMouse();
			}
		}

		if (this.gameSettings.keyBindUseItem.isKeyDown() && this.rightClickDelayTimer == 0
				&& !this.player.isHandActive()) {
			this.rightClickMouse();
		}

		this.sendClickBlockToController(
				this.currentScreen == null && this.gameSettings.keyBindAttack.isKeyDown() && this.inGameHasFocus);
	}

	private void runTickMouse() throws IOException {
		while (Mouse.next()) {
			int i = Mouse.getEventButton();
			KeyBinding.setKeyBindState(i - 100, Mouse.getEventButtonState());

			if (Mouse.getEventButtonState()) {
				if (this.player.isSpectator() && i == 2) {
					this.ingameGUI.getSpectatorGui().onMiddleClick();
				} else {
					KeyBinding.onTick(i - 100);
				}
			}

			long j = getSystemTime() - this.systemTime;

			if (j <= 200L) {
				int k = Mouse.getEventDWheel();

				if (k != 0) {
					if (this.player.isSpectator()) {
						k = k < 0 ? -1 : 1;

						if (this.ingameGUI.getSpectatorGui().isMenuActive()) {
							this.ingameGUI.getSpectatorGui().onMouseScroll(-k);
						} else {
							float f = MathHelper.clamp(this.player.capabilities.getFlySpeed() + (float) k * 0.005F,
									0.0F, 0.2F);
							this.player.capabilities.setFlySpeed(f);
						}
					} else {
						this.player.inventory.changeCurrentItem(k);
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
	}

	private void func_190521_a(String p_190521_1_, Object... p_190521_2_) {
		this.ingameGUI.getChatGUI()
				.printChatMessage((new TextComponentString(""))
						.appendSibling((new TextComponentTranslation("debug.prefix", new Object[0]))
								.setStyle((new Style()).setColor(TextFormatting.YELLOW).setBold(Boolean.valueOf(true))))
						.appendText(" ").appendSibling(new TextComponentTranslation(p_190521_1_, p_190521_2_)));
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
	 * Arguments: World foldername, World ingame name, WorldSettings
	 */
	public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) {
		this.loadWorld((WorldClient) null);
		SingleplayerServerController.launchEaglercraftServer(this.mcDataDir, folderName, worldName,
				Math.max(gameSettings.renderDistanceChunks, 2), worldSettingsIn);
		this.displayGuiScreen(new GuiScreenIntegratedServerBusy(
				new GuiScreenSingleplayerConnecting(new GuiMainMenu(), "Connecting to " + folderName),
				"singleplayer.busy.startingIntegratedServer", "singleplayer.failed.startingIntegratedServer",
				() -> SingleplayerServerController.isWorldReady(), (t, u) -> {
					Minecraft.this.displayGuiScreen(GuiScreenIntegratedServerBusy.createException(new GuiMainMenu(),
							((GuiScreenIntegratedServerBusy) t).failMessage, u));
				}));
	}

	/**
	 * unloads the current world first
	 */
	public void loadWorld(WorldClient worldClientIn) {
		this.loadWorld(worldClientIn, "");
	}

	/**
	 * par2Str is displayed on the loading screen to the user unloads the current
	 * world first
	 */
	public void loadWorld(WorldClient worldClientIn, String loadingMessage) {
		if (worldClientIn == null) {
			NetHandlerPlayClient nethandlerplayclient = this.getConnection();

			if (nethandlerplayclient != null) {
				nethandlerplayclient.cleanup();
			}
			session.reset();
			EaglerProfile.clearServerSkinOverride();
			PauseMenuCustomizeState.reset();
			ClientUUIDLoadingCache.flushRequestCache();
			ClientUUIDLoadingCache.resetFlags();
			WebViewOverlayController.setPacketSendCallback(null);

			this.entityRenderer.func_190564_k();
			this.playerController = null;
			NarratorChatListener.field_193643_a.func_193642_b();
		}

		this.renderViewEntity = null;
		this.myNetworkManager = null;

		if (this.loadingScreen != null) {
			this.loadingScreen.resetProgressAndMessage(loadingMessage);
			this.loadingScreen.displayLoadingString("");
		}

		if (worldClientIn == null && this.world != null) {
			this.mcResourcePackRepository.clearResourcePack();
			this.ingameGUI.resetPlayersOverlayFooterHeader();
			this.setServerData((ServerData) null);
			this.integratedServerIsRunning = false;
		}

		this.mcSoundHandler.stopSounds();
		this.world = worldClientIn;

		if (this.renderGlobal != null) {
			this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
		}

		if (this.effectRenderer != null) {
			this.effectRenderer.clearEffects(worldClientIn);
		}

		TileEntityRendererDispatcher.instance.setWorld(worldClientIn);

		if (worldClientIn != null) {
			if (this.player == null) {
				this.player = this.playerController.func_192830_a(worldClientIn, new StatisticsManager(),
						new RecipeBookClient());
				this.playerController.flipPlayer(this.player);
			}

			this.player.preparePlayerToSpawn();
			worldClientIn.spawnEntityInWorld(this.player);
			this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
			this.playerController.setPlayerCapabilities(this.player);
			this.renderViewEntity = this.player;
		} else {
			this.saveLoader.flushCache();
			this.player = null;
		}

		System.gc();
		this.systemTime = 0L;
	}

	public void setDimensionAndSpawnPlayer(int dimension) {
		this.world.setInitialSpawnLocation();
		this.world.removeAllEntities();
		int i = 0;
		String s = null;

		if (this.player != null) {
			i = this.player.getEntityId();
			this.world.removeEntity(this.player);
			s = this.player.getServerBrand();
		}

		this.renderViewEntity = null;
		EntityPlayerSP entityplayersp = this.player;
		this.player = this.playerController.func_192830_a(this.world,
				this.player == null ? new StatisticsManager() : this.player.getStatFileWriter(),
				this.player == null ? new RecipeBook() : this.player.func_192035_E());
		this.player.getDataManager().setEntryValues(entityplayersp.getDataManager().getAll());
		this.player.dimension = dimension;
		this.renderViewEntity = this.player;
		this.player.preparePlayerToSpawn();
		this.player.setServerBrand(s);
		this.world.spawnEntityInWorld(this.player);
		this.playerController.flipPlayer(this.player);
		this.player.movementInput = new MovementInputFromOptions(this.gameSettings);
		this.player.setEntityId(i);
		this.playerController.setPlayerCapabilities(this.player);
		this.player.setReducedDebug(entityplayersp.hasReducedDebug());

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

	public NetHandlerPlayClient getConnection() {
		return this.player == null ? null : this.player.connection;
	}

	public static boolean isGuiEnabled() {
		return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
	}

	public static boolean isFancyGraphicsEnabled() {
		return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
	}

	/**
	 * Returns if ambient occlusion is enabled
	 */
	public static boolean isAmbientOcclusionEnabled() {
		return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
	}

	/**
	 * Called when user clicked he's mouse middle button (pick block)
	 */
	private void middleClickMouse() {
		if (this.objectMouseOver != null && this.objectMouseOver.typeOfHit != RayTraceResult.Type.MISS) {
			boolean flag = this.player.capabilities.isCreativeMode;
			TileEntity tileentity = null;
			ItemStack itemstack;

			if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
				BlockPos blockpos = this.objectMouseOver.getBlockPos();
				IBlockState iblockstate = this.world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();

				if (iblockstate.getMaterial() == Material.AIR) {
					return;
				}

				itemstack = block.getItem(this.world, blockpos, iblockstate);

				if (itemstack.func_190926_b()) {
					return;
				}

				if (flag && GuiScreen.isCtrlKeyDown() && block.hasTileEntity()) {
					tileentity = this.world.getTileEntity(blockpos);
				}
			} else {
				if (this.objectMouseOver.typeOfHit != RayTraceResult.Type.ENTITY
						|| this.objectMouseOver.entityHit == null || !flag) {
					return;
				}

				if (this.objectMouseOver.entityHit instanceof EntityPainting) {
					itemstack = new ItemStack(Items.PAINTING);
				} else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
					itemstack = new ItemStack(Items.LEAD);
				} else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
					EntityItemFrame entityitemframe = (EntityItemFrame) this.objectMouseOver.entityHit;
					ItemStack itemstack1 = entityitemframe.getDisplayedItem();

					if (itemstack1.func_190926_b()) {
						itemstack = new ItemStack(Items.ITEM_FRAME);
					} else {
						itemstack = itemstack1.copy();
					}
				} else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
					EntityMinecart entityminecart = (EntityMinecart) this.objectMouseOver.entityHit;
					Item item1;

					switch (entityminecart.getType()) {
					case FURNACE:
						item1 = Items.FURNACE_MINECART;
						break;

					case CHEST:
						item1 = Items.CHEST_MINECART;
						break;

					case TNT:
						item1 = Items.TNT_MINECART;
						break;

					case HOPPER:
						item1 = Items.HOPPER_MINECART;
						break;

					case COMMAND_BLOCK:
						item1 = Items.COMMAND_BLOCK_MINECART;
						break;

					default:
						item1 = Items.MINECART;
					}

					itemstack = new ItemStack(item1);
				} else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
					itemstack = new ItemStack(((EntityBoat) this.objectMouseOver.entityHit).getItemBoat());
				} else if (this.objectMouseOver.entityHit instanceof EntityArmorStand) {
					itemstack = new ItemStack(Items.ARMOR_STAND);
				} else if (this.objectMouseOver.entityHit instanceof EntityEnderCrystal) {
					itemstack = new ItemStack(Items.END_CRYSTAL);
				} else {
					ResourceLocation resourcelocation = EntityList.func_191301_a(this.objectMouseOver.entityHit);

					if (resourcelocation == null || !EntityList.ENTITY_EGGS.containsKey(resourcelocation)) {
						return;
					}

					itemstack = new ItemStack(Items.SPAWN_EGG);
					ItemMonsterPlacer.applyEntityIdToItemStack(itemstack, resourcelocation);
				}
			}

			if (itemstack.func_190926_b()) {
				String s = "";

				if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
					s = ((ResourceLocation) Block.REGISTRY
							.getNameForObject(this.world.getBlockState(this.objectMouseOver.getBlockPos()).getBlock()))
							.toString();
				} else if (this.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
					s = EntityList.func_191301_a(this.objectMouseOver.entityHit).toString();
				}

				LOGGER.warn("Picking on: [{}] {} gave null item", this.objectMouseOver.typeOfHit, s);
			} else {
				InventoryPlayer inventoryplayer = this.player.inventory;

				if (tileentity != null) {
					this.storeTEInStack(itemstack, tileentity);
				}

				int i = inventoryplayer.getSlotFor(itemstack);

				if (flag) {
					inventoryplayer.setPickedItemStack(itemstack);
					this.playerController.sendSlotPacket(this.player.getHeldItem(EnumHand.MAIN_HAND),
							36 + inventoryplayer.currentItem);
				} else if (i != -1) {
					if (InventoryPlayer.isHotbar(i)) {
						inventoryplayer.currentItem = i;
					} else {
						this.playerController.pickItem(i);
					}
				}
			}
		}
	}

	private ItemStack storeTEInStack(ItemStack stack, TileEntity te) {
		NBTTagCompound nbttagcompound = te.writeToNBT(new NBTTagCompound());

		if (stack.getItem() == Items.SKULL && nbttagcompound.hasKey("Owner")) {
			NBTTagCompound nbttagcompound2 = nbttagcompound.getCompoundTag("Owner");
			NBTTagCompound nbttagcompound3 = new NBTTagCompound();
			nbttagcompound3.setTag("SkullOwner", nbttagcompound2);
			stack.setTagCompound(nbttagcompound3);
			return stack;
		} else {
			stack.setTagInfo("BlockEntityTag", nbttagcompound);
			NBTTagCompound nbttagcompound1 = new NBTTagCompound();
			NBTTagList nbttaglist = new NBTTagList();
			nbttaglist.appendTag(new NBTTagString("(+NBT)"));
			nbttagcompound1.setTag("Lore", nbttaglist);
			stack.setTagInfo("display", nbttagcompound1);
			return stack;
		}
	}

	/**
	 * adds core server Info (GL version , Texture pack, isModded, type), and the
	 * worldInfo to the crash report
	 */
	public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
		theCrash.getCategory().setDetail("Launched Version", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				return Minecraft.this.launchedVersion;
			}
		});
		theCrash.getCategory().setDetail("Is Modded", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				return "Yes, Eaglercraft 1.12.2";
			}
		});
		theCrash.getCategory().setDetail("Type", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				return "Client (map_client.txt)";
			}
		});
		theCrash.getCategory().setDetail("Resource Packs", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				StringBuilder stringbuilder = new StringBuilder();

				for (String s : Minecraft.this.gameSettings.resourcePacks) {
					if (stringbuilder.length() > 0) {
						stringbuilder.append(", ");
					}

					stringbuilder.append(s);

					if (Minecraft.this.gameSettings.incompatibleResourcePacks.contains(s)) {
						stringbuilder.append(" (incompatible)");
					}
				}

				return stringbuilder.toString();
			}
		});
		theCrash.getCategory().setDetail("Current Language", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
			}
		});
		theCrash.getCategory().setDetail("Profiler Position", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				return "N/A (disabled)";
			}
		});

		if (this.world != null) {
			this.world.addWorldInfoToCrashReport(theCrash);
		}

		return theCrash;
	}

	/**
	 * Return the singleton Minecraft instance for the game
	 */
	public static Minecraft getMinecraft() {
		return theMinecraft;
	}

	public ListenableFuture<Object> scheduleResourcesRefresh() {
		return this.addScheduledTaskFuture(new Runnable() {
			public void run() {
				Minecraft.this.loadingScreen.eaglerShow(I18n.format("resourcePack.load.refreshing"),
						I18n.format("resourcePack.load.pleaseWait"));
				Minecraft.this.refreshResources();
			}
		});
	}

	public static int getGLMaximumTextureSize() {
		return EaglercraftGPU.glGetInteger(RealOpenGLEnums.GL_MAX_TEXTURE_SIZE);
	}

	/**
	 * Set the current ServerData instance.
	 */
	public void setServerData(ServerData serverDataIn) {
		this.currentServerData = serverDataIn;
	}

	public ServerData getCurrentServerData() {
		return this.currentServerData;
	}

	public boolean isIntegratedServerRunning() {
		return SingleplayerServerController.isWorldRunning();
	}

	/**
	 * + Returns true if there is only one player playing, and the current server is
	 * the integrated one.
	 */
	public boolean isSingleplayer() {
		return SingleplayerServerController.isWorldRunning();
	}

	public static void stopIntegratedServer() {

	}

	/**
	 * Gets the system time in milliseconds.
	 */
	public static long getSystemTime() {
		return EagRuntime.steadyTimeMillis();
	}

	/**
	 * Returns whether we're in full screen or not.
	 */
	public boolean isFullScreen() {
		return Display.isFullscreen();
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

	public boolean isGamePaused() {
		return this.isGamePaused;
	}

	public SoundHandler getSoundHandler() {
		return this.mcSoundHandler;
	}

	public MusicTicker.MusicType getAmbientMusicType() {
		if (this.currentScreen instanceof GuiWinGame) {
			return MusicTicker.MusicType.CREDITS;
		} else if (this.player != null) {
			if (this.player.world.provider instanceof WorldProviderHell) {
				return MusicTicker.MusicType.NETHER;
			} else if (this.player.world.provider instanceof WorldProviderEnd) {
				return this.ingameGUI.getBossOverlay().shouldPlayEndBossMusic() ? MusicTicker.MusicType.END_BOSS
						: MusicTicker.MusicType.END;
			} else {
				return this.player.capabilities.isCreativeMode && this.player.capabilities.allowFlying
						? MusicTicker.MusicType.CREATIVE
						: MusicTicker.MusicType.GAME;
			}
		} else {
			return MusicTicker.MusicType.MENU;
		}
	}

	public void dispatchKeypresses() {
		int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();

		if (i != 0 && !Keyboard.isRepeatEvent()) {
			if (!(this.currentScreen instanceof GuiControls)
					|| ((GuiControls) this.currentScreen).time <= getSystemTime() - 20L) {
				if (Keyboard.getEventKeyState()) {
					if (i == this.gameSettings.keyBindScreenshot.getKeyCode()) {
						this.ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot());
					} else if (i == 48 && GuiScreen.isCtrlKeyDown() && (this.currentScreen == null
							|| this.currentScreen != null && !this.currentScreen.func_193976_p())) {
						// this.gameSettings.setOptionValue(GameSettings.Options.NARRATOR, 1);

						if (this.currentScreen instanceof ScreenChatOptions) {
							((ScreenChatOptions) this.currentScreen).func_193024_a();
						}
					}
				}
			}
		}
	}

	public Entity getRenderViewEntity() {
		return this.renderViewEntity;
	}

	public void setRenderViewEntity(Entity viewingEntity) {
		this.renderViewEntity = viewingEntity;
		this.entityRenderer.loadEntityShader(viewingEntity);
	}

	public <V> ListenableFuture<V> addScheduledTaskFuture(Callable<V> callableToSchedule) {
		Validate.notNull(callableToSchedule);
		ListenableFutureTask listenablefuturetask = ListenableFutureTask.create(callableToSchedule);
		synchronized (this.scheduledTasks) {
			this.scheduledTasks.add(listenablefuturetask);
			return listenablefuturetask;
		}
	}

	public ListenableFuture<Object> addScheduledTaskFuture(Runnable runnableToSchedule) {
		Validate.notNull(runnableToSchedule);
		return this.addScheduledTaskFuture(Executors.callable(runnableToSchedule));
	}

	public void addScheduledTask(Runnable runnableToSchedule) {
		this.addScheduledTaskFuture(Executors.callable(runnableToSchedule));
	}

	public BlockRendererDispatcher getBlockRendererDispatcher() {
		return this.blockRenderDispatcher;
	}

	public RenderManager getRenderManager() {
		return this.renderManager;
	}

	public RenderItem getRenderItem() {
		return this.renderItem;
	}

	public ItemRenderer getItemRenderer() {
		return this.itemRenderer;
	}

	public <T> ISearchTree<T> func_193987_a(SearchTreeManager.Key<T> p_193987_1_) {
		return this.field_193995_ae.<T>func_194010_a(p_193987_1_);
	}

	public static int getDebugFPS() {
		return debugFPS;
	}

	/**
	 * Return the FrameTimer's instance
	 */
	public FrameTimer getFrameTimer() {
		return this.frameTimer;
	}

	public DataFixer getDataFixer() {
		return this.dataFixer;
	}

	public float getRenderPartialTicks() {
		return this.timer.field_194147_b;
	}

	public float func_193989_ak() {
		return this.timer.field_194148_c;
	}

	public BlockColors getBlockColors() {
		return this.blockColors;
	}

	/**
	 * Whether to use reduced debug info
	 */
	public boolean isReducedDebug() {
		return this.player != null && this.player.hasReducedDebug() || this.gameSettings.reducedDebugInfo;
	}

	public GuiToast func_193033_an() {
		return this.field_193034_aS;
	}

	public Tutorial func_193032_ao() {
		return this.field_193035_aW;
	}

	public NetHandlerPlayClient getNetHandler() {
		return this.player != null ? this.player.connection : null;
	}
	
	public void clearTitles() {
		ingameGUI.displayTitle(null, null, -1, -1, -1);
	}
	
	public boolean getEnableFNAWSkins() {
		boolean ret = this.gameSettings.enableFNAWSkins;
		if (this.player != null) {
			ret &= this.player.connection.currentFNAWSkinAllowedState;
		}
		return ret;
	}
	
	public void handleReconnectPacket(String redirectURI) {
		this.reconnectURI = redirectURI;
	}
}
