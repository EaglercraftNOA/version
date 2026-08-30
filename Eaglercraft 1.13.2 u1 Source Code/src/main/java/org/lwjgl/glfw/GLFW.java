/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
*/
package org.lwjgl.glfw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryUtil;

import net.lax1dude.eaglercraft.v1_8.Display;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.KeyboardConstants;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.internal.EnumPlatformOS;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformInput;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer;

public class GLFW {

	// ======================================================================
	// GLFW Version Constants
	// ======================================================================

	public static final int GLFW_VERSION_MAJOR    = 3;
	public static final int GLFW_VERSION_MINOR    = 3;
	public static final int GLFW_VERSION_REVISION = 0;

	// ======================================================================
	// Boolean Constants
	// ======================================================================

	public static final int GLFW_TRUE  = 1;
	public static final int GLFW_FALSE = 0;

	// ======================================================================
	// Key Constants (matching LWJGL3/GLFW specification exactly)
	// ======================================================================

	public static final int GLFW_KEY_UNKNOWN       = -1;
	public static final int GLFW_KEY_SPACE          = 32;
	public static final int GLFW_KEY_APOSTROPHE     = 39;
	public static final int GLFW_KEY_COMMA          = 44;
	public static final int GLFW_KEY_MINUS          = 45;
	public static final int GLFW_KEY_PERIOD         = 46;
	public static final int GLFW_KEY_SLASH          = 47;
	public static final int GLFW_KEY_0              = 48;
	public static final int GLFW_KEY_1              = 49;
	public static final int GLFW_KEY_2              = 50;
	public static final int GLFW_KEY_3              = 51;
	public static final int GLFW_KEY_4              = 52;
	public static final int GLFW_KEY_5              = 53;
	public static final int GLFW_KEY_6              = 54;
	public static final int GLFW_KEY_7              = 55;
	public static final int GLFW_KEY_8              = 56;
	public static final int GLFW_KEY_9              = 57;
	public static final int GLFW_KEY_SEMICOLON      = 59;
	public static final int GLFW_KEY_EQUAL          = 61;
	public static final int GLFW_KEY_A              = 65;
	public static final int GLFW_KEY_B              = 66;
	public static final int GLFW_KEY_C              = 67;
	public static final int GLFW_KEY_D              = 68;
	public static final int GLFW_KEY_E              = 69;
	public static final int GLFW_KEY_F              = 70;
	public static final int GLFW_KEY_G              = 71;
	public static final int GLFW_KEY_H              = 72;
	public static final int GLFW_KEY_I              = 73;
	public static final int GLFW_KEY_J              = 74;
	public static final int GLFW_KEY_K              = 75;
	public static final int GLFW_KEY_L              = 76;
	public static final int GLFW_KEY_M              = 77;
	public static final int GLFW_KEY_N              = 78;
	public static final int GLFW_KEY_O              = 79;
	public static final int GLFW_KEY_P              = 80;
	public static final int GLFW_KEY_Q              = 81;
	public static final int GLFW_KEY_R              = 82;
	public static final int GLFW_KEY_S              = 83;
	public static final int GLFW_KEY_T              = 84;
	public static final int GLFW_KEY_U              = 85;
	public static final int GLFW_KEY_V              = 86;
	public static final int GLFW_KEY_W              = 87;
	public static final int GLFW_KEY_X              = 88;
	public static final int GLFW_KEY_Y              = 89;
	public static final int GLFW_KEY_Z              = 90;
	public static final int GLFW_KEY_LEFT_BRACKET   = 91;
	public static final int GLFW_KEY_BACKSLASH      = 92;
	public static final int GLFW_KEY_RIGHT_BRACKET  = 93;
	public static final int GLFW_KEY_GRAVE_ACCENT   = 96;
	public static final int GLFW_KEY_WORLD_1        = 161;
	public static final int GLFW_KEY_WORLD_2        = 162;

	// Function keys
	public static final int GLFW_KEY_ESCAPE         = 256;
	public static final int GLFW_KEY_ENTER          = 257;
	public static final int GLFW_KEY_TAB            = 258;
	public static final int GLFW_KEY_BACKSPACE      = 259;
	public static final int GLFW_KEY_INSERT         = 260;
	public static final int GLFW_KEY_DELETE         = 261;
	public static final int GLFW_KEY_RIGHT          = 262;
	public static final int GLFW_KEY_LEFT           = 263;
	public static final int GLFW_KEY_DOWN           = 264;
	public static final int GLFW_KEY_UP             = 265;
	public static final int GLFW_KEY_PAGE_UP        = 266;
	public static final int GLFW_KEY_PAGE_DOWN      = 267;
	public static final int GLFW_KEY_HOME           = 268;
	public static final int GLFW_KEY_END            = 269;
	public static final int GLFW_KEY_CAPS_LOCK      = 280;
	public static final int GLFW_KEY_SCROLL_LOCK    = 281;
	public static final int GLFW_KEY_NUM_LOCK       = 282;
	public static final int GLFW_KEY_PRINT_SCREEN   = 283;
	public static final int GLFW_KEY_PAUSE          = 284;
	public static final int GLFW_KEY_F1             = 290;
	public static final int GLFW_KEY_F2             = 291;
	public static final int GLFW_KEY_F3             = 292;
	public static final int GLFW_KEY_F4             = 293;
	public static final int GLFW_KEY_F5             = 294;
	public static final int GLFW_KEY_F6             = 295;
	public static final int GLFW_KEY_F7             = 296;
	public static final int GLFW_KEY_F8             = 297;
	public static final int GLFW_KEY_F9             = 298;
	public static final int GLFW_KEY_F10            = 299;
	public static final int GLFW_KEY_F11            = 300;
	public static final int GLFW_KEY_F12            = 301;
	public static final int GLFW_KEY_F13            = 302;
	public static final int GLFW_KEY_F14            = 303;
	public static final int GLFW_KEY_F15            = 304;
	public static final int GLFW_KEY_F16            = 305;
	public static final int GLFW_KEY_F17            = 306;
	public static final int GLFW_KEY_F18            = 307;
	public static final int GLFW_KEY_F19            = 308;
	public static final int GLFW_KEY_F20            = 309;
	public static final int GLFW_KEY_F21            = 310;
	public static final int GLFW_KEY_F22            = 311;
	public static final int GLFW_KEY_F23            = 312;
	public static final int GLFW_KEY_F24            = 313;
	public static final int GLFW_KEY_F25            = 314;
	public static final int GLFW_KEY_KP_0           = 320;
	public static final int GLFW_KEY_KP_1           = 321;
	public static final int GLFW_KEY_KP_2           = 322;
	public static final int GLFW_KEY_KP_3           = 323;
	public static final int GLFW_KEY_KP_4           = 324;
	public static final int GLFW_KEY_KP_5           = 325;
	public static final int GLFW_KEY_KP_6           = 326;
	public static final int GLFW_KEY_KP_7           = 327;
	public static final int GLFW_KEY_KP_8           = 328;
	public static final int GLFW_KEY_KP_9           = 329;
	public static final int GLFW_KEY_KP_DECIMAL     = 330;
	public static final int GLFW_KEY_KP_DIVIDE      = 331;
	public static final int GLFW_KEY_KP_MULTIPLY    = 332;
	public static final int GLFW_KEY_KP_SUBTRACT    = 333;
	public static final int GLFW_KEY_KP_ADD         = 334;
	public static final int GLFW_KEY_KP_ENTER       = 335;
	public static final int GLFW_KEY_KP_EQUAL       = 336;
	public static final int GLFW_KEY_LEFT_SHIFT     = 340;
	public static final int GLFW_KEY_LEFT_CONTROL   = 341;
	public static final int GLFW_KEY_LEFT_ALT       = 342;
	public static final int GLFW_KEY_LEFT_SUPER     = 343;
	public static final int GLFW_KEY_RIGHT_SHIFT    = 344;
	public static final int GLFW_KEY_RIGHT_CONTROL  = 345;
	public static final int GLFW_KEY_RIGHT_ALT      = 346;
	public static final int GLFW_KEY_RIGHT_SUPER    = 347;
	public static final int GLFW_KEY_MENU           = 348;
	public static final int GLFW_KEY_LAST           = GLFW_KEY_MENU;

	// ======================================================================
	// Key Action Constants
	// ======================================================================

	public static final int GLFW_RELEASE = 0;
	public static final int GLFW_PRESS   = 1;
	public static final int GLFW_REPEAT  = 2;

	// ======================================================================
	// Modifier Key Bits
	// ======================================================================

	public static final int GLFW_MOD_SHIFT     = 0x0001;
	public static final int GLFW_MOD_CONTROL   = 0x0002;
	public static final int GLFW_MOD_ALT       = 0x0004;
	public static final int GLFW_MOD_SUPER     = 0x0008;
	public static final int GLFW_MOD_CAPS_LOCK = 0x0010;
	public static final int GLFW_MOD_NUM_LOCK  = 0x0020;

	// ======================================================================
	// Mouse Button Constants
	// ======================================================================

	public static final int GLFW_MOUSE_BUTTON_1      = 0;
	public static final int GLFW_MOUSE_BUTTON_2      = 1;
	public static final int GLFW_MOUSE_BUTTON_3      = 2;
	public static final int GLFW_MOUSE_BUTTON_4      = 3;
	public static final int GLFW_MOUSE_BUTTON_5      = 4;
	public static final int GLFW_MOUSE_BUTTON_6      = 5;
	public static final int GLFW_MOUSE_BUTTON_7      = 6;
	public static final int GLFW_MOUSE_BUTTON_8      = 7;
	public static final int GLFW_MOUSE_BUTTON_LAST   = GLFW_MOUSE_BUTTON_8;
	public static final int GLFW_MOUSE_BUTTON_LEFT   = GLFW_MOUSE_BUTTON_1;
	public static final int GLFW_MOUSE_BUTTON_RIGHT  = GLFW_MOUSE_BUTTON_2;
	public static final int GLFW_MOUSE_BUTTON_MIDDLE = GLFW_MOUSE_BUTTON_3;

	// ======================================================================
	// Joystick/Gamepad Constants
	// ======================================================================

	public static final int GLFW_JOYSTICK_1    = 0;
	public static final int GLFW_JOYSTICK_2    = 1;
	public static final int GLFW_JOYSTICK_3    = 2;
	public static final int GLFW_JOYSTICK_4    = 3;
	public static final int GLFW_JOYSTICK_5    = 4;
	public static final int GLFW_JOYSTICK_6    = 5;
	public static final int GLFW_JOYSTICK_7    = 6;
	public static final int GLFW_JOYSTICK_8    = 7;
	public static final int GLFW_JOYSTICK_9    = 8;
	public static final int GLFW_JOYSTICK_10   = 9;
	public static final int GLFW_JOYSTICK_11   = 10;
	public static final int GLFW_JOYSTICK_12   = 11;
	public static final int GLFW_JOYSTICK_13   = 12;
	public static final int GLFW_JOYSTICK_14   = 13;
	public static final int GLFW_JOYSTICK_15   = 14;
	public static final int GLFW_JOYSTICK_16   = 15;
	public static final int GLFW_JOYSTICK_LAST = GLFW_JOYSTICK_16;

	public static final int GLFW_GAMEPAD_BUTTON_A            = 0;
	public static final int GLFW_GAMEPAD_BUTTON_B            = 1;
	public static final int GLFW_GAMEPAD_BUTTON_X            = 2;
	public static final int GLFW_GAMEPAD_BUTTON_Y            = 3;
	public static final int GLFW_GAMEPAD_BUTTON_LEFT_BUMPER  = 4;
	public static final int GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER = 5;
	public static final int GLFW_GAMEPAD_BUTTON_BACK         = 6;
	public static final int GLFW_GAMEPAD_BUTTON_START        = 7;
	public static final int GLFW_GAMEPAD_BUTTON_GUIDE        = 8;
	public static final int GLFW_GAMEPAD_BUTTON_LEFT_THUMB   = 9;
	public static final int GLFW_GAMEPAD_BUTTON_RIGHT_THUMB  = 10;
	public static final int GLFW_GAMEPAD_BUTTON_DPAD_UP      = 11;
	public static final int GLFW_GAMEPAD_BUTTON_DPAD_RIGHT   = 12;
	public static final int GLFW_GAMEPAD_BUTTON_DPAD_DOWN    = 13;
	public static final int GLFW_GAMEPAD_BUTTON_DPAD_LEFT    = 14;
	public static final int GLFW_GAMEPAD_BUTTON_LAST         = GLFW_GAMEPAD_BUTTON_DPAD_LEFT;
	public static final int GLFW_GAMEPAD_BUTTON_CROSS        = GLFW_GAMEPAD_BUTTON_A;
	public static final int GLFW_GAMEPAD_BUTTON_CIRCLE       = GLFW_GAMEPAD_BUTTON_B;
	public static final int GLFW_GAMEPAD_BUTTON_SQUARE       = GLFW_GAMEPAD_BUTTON_X;
	public static final int GLFW_GAMEPAD_BUTTON_TRIANGLE     = GLFW_GAMEPAD_BUTTON_Y;

	public static final int GLFW_GAMEPAD_AXIS_LEFT_X         = 0;
	public static final int GLFW_GAMEPAD_AXIS_LEFT_Y         = 1;
	public static final int GLFW_GAMEPAD_AXIS_RIGHT_X        = 2;
	public static final int GLFW_GAMEPAD_AXIS_RIGHT_Y        = 3;
	public static final int GLFW_GAMEPAD_AXIS_LEFT_TRIGGER   = 4;
	public static final int GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER  = 5;
	public static final int GLFW_GAMEPAD_AXIS_LAST           = GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER;

	// ======================================================================
	// Error Constants
	// ======================================================================

	public static final int GLFW_NO_ERROR            = 0;
	public static final int GLFW_NOT_INITIALIZED     = 0x00010001;
	public static final int GLFW_NO_CURRENT_CONTEXT  = 0x00010002;
	public static final int GLFW_INVALID_ENUM        = 0x00010003;
	public static final int GLFW_INVALID_VALUE       = 0x00010004;
	public static final int GLFW_OUT_OF_MEMORY       = 0x00010005;
	public static final int GLFW_API_UNAVAILABLE     = 0x00010006;
	public static final int GLFW_VERSION_UNAVAILABLE = 0x00010007;
	public static final int GLFW_PLATFORM_ERROR      = 0x00010008;
	public static final int GLFW_FORMAT_UNAVAILABLE  = 0x00010009;
	public static final int GLFW_NO_WINDOW_CONTEXT   = 0x0001000A;

	// Init hints / platforms
	public static final int GLFW_PLATFORM            = 0x00050003;
	public static final int GLFW_ANGLE_PLATFORM_TYPE = 0x00050002;

	public static final int GLFW_ANY_PLATFORM   = 0x00060000;
	public static final int GLFW_PLATFORM_WIN32 = 0x00060001;
	public static final int GLFW_PLATFORM_COCOA = 0x00060002;
	public static final int GLFW_PLATFORM_WAYLAND = 0x00060003;
	public static final int GLFW_PLATFORM_X11   = 0x00060004;

	// ======================================================================
	// Window Hint Constants
	// ======================================================================

	public static final int GLFW_FOCUSED                 = 0x00020001;
	public static final int GLFW_ICONIFIED               = 0x00020002;
	public static final int GLFW_RESIZABLE               = 0x00020003;
	public static final int GLFW_VISIBLE                 = 0x00020004;
	public static final int GLFW_DECORATED               = 0x00020005;
	public static final int GLFW_AUTO_ICONIFY            = 0x00020006;
	public static final int GLFW_FLOATING                = 0x00020007;
	public static final int GLFW_MAXIMIZED               = 0x00020008;
	public static final int GLFW_CENTER_CURSOR           = 0x00020009;
	public static final int GLFW_TRANSPARENT_FRAMEBUFFER = 0x0002000A;
	public static final int GLFW_HOVERED                 = 0x0002000B;
	public static final int GLFW_FOCUS_ON_SHOW           = 0x0002000C;

	public static final int GLFW_RED_BITS           = 0x00021001;
	public static final int GLFW_GREEN_BITS         = 0x00021002;
	public static final int GLFW_BLUE_BITS          = 0x00021003;
	public static final int GLFW_ALPHA_BITS         = 0x00021004;
	public static final int GLFW_DEPTH_BITS         = 0x00021005;
	public static final int GLFW_STENCIL_BITS       = 0x00021006;
	public static final int GLFW_ACCUM_RED_BITS     = 0x00021007;
	public static final int GLFW_ACCUM_GREEN_BITS   = 0x00021008;
	public static final int GLFW_ACCUM_BLUE_BITS    = 0x00021009;
	public static final int GLFW_ACCUM_ALPHA_BITS   = 0x0002100A;
	public static final int GLFW_AUX_BUFFERS        = 0x0002100B;
	public static final int GLFW_STEREO             = 0x0002100C;
	public static final int GLFW_SAMPLES            = 0x0002100D;
	public static final int GLFW_SRGB_CAPABLE       = 0x0002100E;
	public static final int GLFW_REFRESH_RATE       = 0x0002100F;
	public static final int GLFW_DOUBLEBUFFER       = 0x00021010;

	public static final int GLFW_CLIENT_API             = 0x00022001;
	public static final int GLFW_CONTEXT_VERSION_MAJOR  = 0x00022002;
	public static final int GLFW_CONTEXT_VERSION_MINOR  = 0x00022003;
	public static final int GLFW_CONTEXT_REVISION       = 0x00022004;
	public static final int GLFW_CONTEXT_ROBUSTNESS     = 0x00022005;
	public static final int GLFW_OPENGL_FORWARD_COMPAT  = 0x00022006;
	public static final int GLFW_OPENGL_DEBUG_CONTEXT   = 0x00022007;
	public static final int GLFW_OPENGL_PROFILE         = 0x00022008;
	public static final int GLFW_CONTEXT_RELEASE_BEHAVIOR = 0x00022009;
	public static final int GLFW_CONTEXT_NO_ERROR       = 0x0002200A;
	public static final int GLFW_CONTEXT_CREATION_API   = 0x0002200B;
	public static final int GLFW_SCALE_TO_MONITOR       = 0x0002200C;

	public static final int GLFW_COCOA_RETINA_FRAMEBUFFER = 0x00023001;
	public static final int GLFW_COCOA_FRAME_NAME       = 0x00023002;
	public static final int GLFW_COCOA_GRAPHICS_SWITCHING = 0x00023003;
	public static final int GLFW_X11_CLASS_NAME         = 0x00024001;
	public static final int GLFW_X11_INSTANCE_NAME      = 0x00024002;

	// Client API
	public static final int GLFW_NO_API         = 0;
	public static final int GLFW_OPENGL_API     = 0x00030001;
	public static final int GLFW_OPENGL_ES_API  = 0x00030002;

	// Context robustness
	public static final int GLFW_NO_ROBUSTNESS          = 0;
	public static final int GLFW_NO_RESET_NOTIFICATION  = 0x00031001;
	public static final int GLFW_LOSE_CONTEXT_ON_RESET  = 0x00031002;

	// OpenGL profile
	public static final int GLFW_OPENGL_ANY_PROFILE     = 0;
	public static final int GLFW_OPENGL_CORE_PROFILE    = 0x00032001;
	public static final int GLFW_OPENGL_COMPAT_PROFILE  = 0x00032002;

	// Cursor types
	public static final int GLFW_CURSOR                 = 0x00033001;
	public static final int GLFW_STICKY_KEYS            = 0x00033002;
	public static final int GLFW_STICKY_MOUSE_BUTTONS   = 0x00033003;
	public static final int GLFW_LOCK_KEY_MODS          = 0x00033004;
	public static final int GLFW_RAW_MOUSE_MOTION       = 0x00033005;

	public static final int GLFW_CURSOR_NORMAL          = 0x00034001;
	public static final int GLFW_CURSOR_HIDDEN          = 0x00034002;
	public static final int GLFW_CURSOR_DISABLED        = 0x00034003;

	// Standard cursor shapes
	public static final int GLFW_ARROW_CURSOR           = 0x00036001;
	public static final int GLFW_IBEAM_CURSOR           = 0x00036002;
	public static final int GLFW_CROSSHAIR_CURSOR       = 0x00036003;
	public static final int GLFW_HAND_CURSOR            = 0x00036004;
	public static final int GLFW_HRESIZE_CURSOR         = 0x00036005;
	public static final int GLFW_VRESIZE_CURSOR         = 0x00036006;

	// Context release behavior
	public static final int GLFW_ANY_RELEASE_BEHAVIOR     = 0;
	public static final int GLFW_RELEASE_BEHAVIOR_FLUSH   = 0x00035001;
	public static final int GLFW_RELEASE_BEHAVIOR_NONE    = 0x00035002;

	// Context creation API
	public static final int GLFW_NATIVE_CONTEXT_API     = 0x00036001;
	public static final int GLFW_EGL_CONTEXT_API        = 0x00036002;
	public static final int GLFW_OSMESA_CONTEXT_API     = 0x00036003;

	// Connected/disconnected
	public static final int GLFW_CONNECTED    = 0x00040001;
	public static final int GLFW_DISCONNECTED = 0x00040002;

	// Joystick hat states
	public static final int GLFW_JOYSTICK_HAT_BUTTONS = 0x00050001;

	public static final int GLFW_HAT_CENTERED   = 0;
	public static final int GLFW_HAT_UP         = 1;
	public static final int GLFW_HAT_RIGHT      = 2;
	public static final int GLFW_HAT_DOWN       = 4;
	public static final int GLFW_HAT_LEFT       = 8;
	public static final int GLFW_HAT_RIGHT_UP   = GLFW_HAT_RIGHT | GLFW_HAT_UP;
	public static final int GLFW_HAT_RIGHT_DOWN = GLFW_HAT_RIGHT | GLFW_HAT_DOWN;
	public static final int GLFW_HAT_LEFT_UP    = GLFW_HAT_LEFT | GLFW_HAT_UP;
	public static final int GLFW_HAT_LEFT_DOWN  = GLFW_HAT_LEFT | GLFW_HAT_DOWN;

	// Don't care value
	public static final int GLFW_DONT_CARE = -1;

	// ======================================================================
	// Internal state: the single "window" handle for the browser canvas
	// ======================================================================

	/** The constant window handle representing the browser canvas. */
	private static final long MAIN_WINDOW_HANDLE = 1L;

	private static boolean initialized = false;
	private static String windowTitle = "";
	private static int cursorMode = GLFW_CURSOR_NORMAL;
	private static boolean stickyKeys = false;
	private static boolean stickyMouseButtons = false;
	private static boolean rawMouseMotion = false;

	// ======================================================================
	// Callback state
	// ======================================================================

	private static GLFWKeyCallbackI keyCallback;
	private static GLFWCharCallbackI charCallback;
	private static GLFWMouseButtonCallbackI mouseButtonCallback;
	private static GLFWCursorPosCallbackI cursorPosCallback;
	private static GLFWScrollCallbackI scrollCallback;
	private static GLFWWindowSizeCallbackI windowSizeCallback;
	private static GLFWFramebufferSizeCallbackI framebufferSizeCallback;
	private static GLFWWindowFocusCallbackI windowFocusCallback;
	private static GLFWWindowCloseCallbackI windowCloseCallback;
	private static GLFWWindowRefreshCallbackI windowRefreshCallback;
	private static GLFWWindowPosCallbackI windowPosCallback;
	private static GLFWWindowIconifyCallbackI windowIconifyCallback;
	private static GLFWWindowMaximizeCallbackI windowMaximizeCallback;
	private static GLFWWindowContentScaleCallbackI windowContentScaleCallback;
	private static GLFWDropCallbackI dropCallback;
	private static String[] dropNames = new String[0];
	private static GLFWErrorCallbackI errorCallback;
	private static GLFWJoystickCallbackI joystickCallback;
	private static GLFWMonitorCallbackI monitorCallback;
	private static GLFWCursorEnterCallbackI cursorEnterCallback;

	// Previous window size for resize detection
	private static int lastWidth = -1;
	private static int lastHeight = -1;
	private static boolean lastFocused = true;
	private static boolean lastMouseInside = true;
	private static double cursorX = 0.0;
	private static double cursorY = 0.0;
	private static boolean cursorInitialized = false;
	private static boolean settingCursorPosition = false;

	// ======================================================================
	// Initialization and Termination
	// ======================================================================

	/**
	 * Initializes the GLFW library. In the browser, this sets up the connection
	 * to PlatformInput and marks GLFW as initialized.
	 * 
	 * @return {@link #GLFW_TRUE} if successful, {@link #GLFW_FALSE} otherwise
	 */
	public static boolean glfwInit() {
		initialized = true;
		lastWidth = getWindowCoordinateWidth();
		lastHeight = getWindowCoordinateHeight();
		lastFocused = Display.isActive();
		lastMouseInside = Mouse.isInsideWindow();
		cursorInitialized = false;
		return true;
	}

	/**
	 * Terminates the GLFW library and clears all callbacks.
	 */
	public static void glfwTerminate() {
		keyCallback = null;
		charCallback = null;
		mouseButtonCallback = null;
		cursorPosCallback = null;
		scrollCallback = null;
		windowSizeCallback = null;
		framebufferSizeCallback = null;
		windowFocusCallback = null;
		windowCloseCallback = null;
		windowRefreshCallback = null;
		windowPosCallback = null;
		windowIconifyCallback = null;
		windowMaximizeCallback = null;
		windowContentScaleCallback = null;
		dropCallback = null;
		joystickCallback = null;
		monitorCallback = null;
		cursorEnterCallback = null;
		cursorInitialized = false;
		initialized = false;
	}

	/**
	 * Sets initialization hints.
	 */
	public static void glfwInitHint(int hint, int value) {
	}

	public static boolean glfwPlatformSupported(int platform) {
		switch (platform) {
		case GLFW_ANY_PLATFORM:
			return true;
		case GLFW_PLATFORM_WIN32:
			return PlatformRuntime.getPlatformOS() == EnumPlatformOS.WINDOWS;
		case GLFW_PLATFORM_COCOA:
			return PlatformRuntime.getPlatformOS() == EnumPlatformOS.MACOS;
		case GLFW_PLATFORM_X11:
		case GLFW_PLATFORM_WAYLAND:
			return PlatformRuntime.getPlatformOS() == EnumPlatformOS.LINUX;
		default:
			return false;
		}
	}

	/**
	 * Retrieves the GLFW version.
	 */
	public static void glfwGetVersion(IntBuffer major, IntBuffer minor, IntBuffer rev) {
		if (major != null) major.put(major.position(), GLFW_VERSION_MAJOR);
		if (minor != null) minor.put(minor.position(), GLFW_VERSION_MINOR);
		if (rev != null) rev.put(rev.position(), GLFW_VERSION_REVISION);
	}

	/**
	 * Returns the GLFW version string.
	 */
	public static String glfwGetVersionString() {
		return GLFW_VERSION_MAJOR + "." + GLFW_VERSION_MINOR + "." + GLFW_VERSION_REVISION;
	}

	// ======================================================================
	// Error Handling
	// ======================================================================

	/**
	 * Returns the last error code and optionally its description.
	 */
	public static int glfwGetError(PointerBuffer description) {
		return GLFW_NO_ERROR;
	}

	/**
	 * Sets the error callback.
	 */
	public static GLFWErrorCallback glfwSetErrorCallback(GLFWErrorCallbackI cbfun) {
		GLFWErrorCallbackI prev = errorCallback;
		errorCallback = cbfun;
		return prev == null ? null : prev instanceof GLFWErrorCallback ? (GLFWErrorCallback) prev : GLFWErrorCallback.create(prev);
	}

	// ======================================================================
	// Monitor Functions
	// ======================================================================

	/**
	 * Returns the primary monitor. In browser, returns a dummy handle.
	 */
	public static long glfwGetPrimaryMonitor() {
		return 1L;
	}

	/**
	 * Returns available monitors. In browser, only one "monitor".
	 */
	public static PointerBuffer glfwGetMonitors() {
		PointerBuffer buf = PointerBuffer.allocateDirect(1);
		buf.put(0, 1L);
		return buf;
	}

	/**
	 * Returns the position of the monitor. In browser, always (0,0).
	 */
	public static void glfwGetMonitorPos(long monitor, IntBuffer xpos, IntBuffer ypos) {
		if (xpos != null) xpos.put(xpos.position(), 0);
		if (ypos != null) ypos.put(ypos.position(), 0);
	}

	public static void glfwGetMonitorPos(long monitor, int[] xpos, int[] ypos) {
		if (xpos != null && xpos.length > 0) xpos[0] = 0;
		if (ypos != null && ypos.length > 0) ypos[0] = 0;
	}

	/**
	 * Returns the work area of the monitor.
	 */
	public static void glfwGetMonitorWorkarea(long monitor, IntBuffer xpos, IntBuffer ypos, IntBuffer width, IntBuffer height) {
		if (xpos != null) xpos.put(xpos.position(), 0);
		if (ypos != null) ypos.put(ypos.position(), 0);
		if (width != null) width.put(width.position(), Display.getWidth());
		if (height != null) height.put(height.position(), Display.getHeight());
	}

	/**
	 * Returns the physical size of the monitor in mm.
	 */
	public static void glfwGetMonitorPhysicalSize(long monitor, IntBuffer widthMM, IntBuffer heightMM) {
		// Approximate based on typical 96 DPI display
		if (widthMM != null) widthMM.put(widthMM.position(), (int)(Display.getWidth() * 25.4 / 96.0));
		if (heightMM != null) heightMM.put(heightMM.position(), (int)(Display.getHeight() * 25.4 / 96.0));
	}

	/**
	 * Returns the content scale factors for the monitor.
	 */
	public static void glfwGetMonitorContentScale(long monitor, float[] xscale, float[] yscale) {
		float dpi = Display.getDPI();
		if (xscale != null && xscale.length > 0) xscale[0] = dpi;
		if (yscale != null && yscale.length > 0) yscale[0] = dpi;
	}

	/**
	 * Returns the name of the monitor.
	 */
	public static String glfwGetMonitorName(long monitor) {
		return "Eaglercraft Display";
	}

	/**
	 * Sets the monitor callback.
	 */
	public static GLFWMonitorCallback glfwSetMonitorCallback(GLFWMonitorCallbackI cbfun) {
		GLFWMonitorCallbackI prev = monitorCallback;
		monitorCallback = cbfun;
		return prev instanceof GLFWMonitorCallback ? (GLFWMonitorCallback) prev : null;
	}

	/**
	 * Returns the available video modes for the monitor.
	 * In browser, returns only the current mode.
	 */
	public static GLFWVidMode.Buffer glfwGetVideoModes(long monitor) {
		return new GLFWVidMode.Buffer(createVidModeStruct(Display.getWidth(), Display.getHeight(), 8, 8, 8, 60));
	}

	/**
	 * Returns the current video mode of the monitor.
	 */
	public static GLFWVidMode glfwGetVideoMode(long monitor) {
		return new GLFWVidMode(createVidModeStruct(Display.getWidth(), Display.getHeight(), 8, 8, 8, 60));
	}

	private static ByteBuffer createVidModeStruct(int width, int height, int redBits, int greenBits, int blueBits,
			int refreshRate) {
		ByteBuffer buf = ByteBuffer.allocateDirect(24).order(ByteOrder.nativeOrder());
		buf.putInt(0, width);
		buf.putInt(4, height);
		buf.putInt(8, redBits);
		buf.putInt(12, greenBits);
		buf.putInt(16, blueBits);
		buf.putInt(20, refreshRate);
		return buf;
	}

	/**
	 * Sets the gamma ramp.
	 */
	public static void glfwSetGamma(long monitor, float gamma) {
	}

	// ======================================================================
	// Window Functions
	// ======================================================================

	/**
	 * Sets default window hints.
	 */
	public static void glfwDefaultWindowHints() {
	}

	/**
	 * Sets a window hint.
	 */
	public static void glfwWindowHint(int hint, int value) {
	}

	/**
	 * Sets a string window hint.
	 */
	public static void glfwWindowHintString(int hint, CharSequence value) {
	}

	/**
	 * 
	 * @param width   the desired width (informational in browser)
	 * @param height  the desired height (informational in browser)
	 * @param title   the window title
	 * @param monitor the monitor for fullscreen, or 0 for windowed
	 * @param share   the window whose context to share, or 0
	 * @return the window handle (always MAIN_WINDOW_HANDLE)
	 */
	public static long glfwCreateWindow(int width, int height, CharSequence title, long monitor, long share) {
		windowTitle = title != null ? title.toString() : "";
		lastWidth = Math.max(1, width);
		lastHeight = Math.max(1, height);
		return MAIN_WINDOW_HANDLE;
	}

	/**
	 * Destroys a window.
	 */
	public static void glfwDestroyWindow(long window) {
	}

	/**
	 * Returns whether the window should close.
	 */
	public static boolean glfwWindowShouldClose(long window) {
		return Display.isCloseRequested();
	}

	/**
	 * Sets the should-close flag.
	 */
	public static void glfwSetWindowShouldClose(long window, boolean value) {
		// In browser, close is managed by the page
	}

	/**
	 * Sets the window title.
	 */
	public static void glfwSetWindowTitle(long window, CharSequence title) {
		windowTitle = title != null ? title.toString() : "";
		Display.setTitle(windowTitle);
	}

	/**
	 * Sets the window icon.
	 */
	public static void glfwSetWindowIcon(long window, GLFWImage.Buffer images) {
	}

	/**
	 * Returns the window position (0,0).
	 */
	public static void glfwGetWindowPos(long window, IntBuffer xpos, IntBuffer ypos) {
		if (xpos != null) xpos.put(xpos.position(), 0);
		if (ypos != null) ypos.put(ypos.position(), 0);
	}

	public static void glfwGetWindowPos(long window, int[] xpos, int[] ypos) {
		if (xpos != null && xpos.length > 0) xpos[0] = 0;
		if (ypos != null && ypos.length > 0) ypos[0] = 0;
	}

	/**
	 * Sets the window position.
	 */
	public static void glfwSetWindowPos(long window, int xpos, int ypos) {
	}

	/**
	 * Returns the window size.
	 */
	public static void glfwGetWindowSize(long window, IntBuffer width, IntBuffer height) {
		if (width != null) width.put(width.position(), getWindowCoordinateWidth());
		if (height != null) height.put(height.position(), getWindowCoordinateHeight());
	}

	public static void glfwGetWindowSize(long window, int[] width, int[] height) {
		if (width != null && width.length > 0) {
			width[0] = getWindowCoordinateWidth();
		}
		if (height != null && height.length > 0) {
			height[0] = getWindowCoordinateHeight();
		}
	}

	/**
	 * Sets size limits for the window. 
	 */
	public static void glfwSetWindowSizeLimits(long window, int minwidth, int minheight, int maxwidth, int maxheight) {
	}

	/**
	 * Sets the aspect ratio constraint. 
	 */
	public static void glfwSetWindowAspectRatio(long window, int numer, int denom) {
	}

	/**
	 * Sets the window size.
	 */
	public static void glfwSetWindowSize(long window, int width, int height) {
	}

	/**
	 * Returns the framebuffer size (may differ from window size due to DPI scaling).
	 */
	public static void glfwGetFramebufferSize(long window, IntBuffer width, IntBuffer height) {
		if (width != null) width.put(width.position(), PlatformInput.getWindowWidth());
		if (height != null) height.put(height.position(), PlatformInput.getWindowHeight());
	}

	public static void glfwGetFramebufferSize(long window, int[] width, int[] height) {
		if (width != null && width.length > 0) width[0] = PlatformInput.getWindowWidth();
		if (height != null && height.length > 0) height[0] = PlatformInput.getWindowHeight();
	}

	/**
	 * Returns the frame extents (all 0s).
	 */
	public static void glfwGetWindowFrameSize(long window, IntBuffer left, IntBuffer top, IntBuffer right, IntBuffer bottom) {
		if (left != null) left.put(left.position(), 0);
		if (top != null) top.put(top.position(), 0);
		if (right != null) right.put(right.position(), 0);
		if (bottom != null) bottom.put(bottom.position(), 0);
	}

	public static void glfwGetWindowFrameSize(long window, int[] left, int[] top, int[] right, int[] bottom) {
		if (left != null && left.length > 0) {
			left[0] = 0;
		}
		if (top != null && top.length > 0) {
			top[0] = 0;
		}
		if (right != null && right.length > 0) {
			right[0] = 0;
		}
		if (bottom != null && bottom.length > 0) {
			bottom[0] = 0;
		}
	}

	/**
	 * Returns the content scale for the window.
	 */
	public static void glfwGetWindowContentScale(long window, float[] xscale, float[] yscale) {
		float dpi = Display.getDPI();
		if (xscale != null && xscale.length > 0) xscale[0] = dpi;
		if (yscale != null && yscale.length > 0) yscale[0] = dpi;
	}

	/**
	 * Returns the window opacity.
	 */
	public static float glfwGetWindowOpacity(long window) {
		return 1.0f;
	}

	/**
	 * Sets the window opacity.
	 */
	public static void glfwSetWindowOpacity(long window, float opacity) {
	}

	/**
	 * Iconifies (minimizes) the window.
	 */
	public static void glfwIconifyWindow(long window) {
	}

	/**
	 * Restores the window from iconification.
	 */
	public static void glfwRestoreWindow(long window) {
	}

	/**
	 * Maximizes the window.
	 */
	public static void glfwMaximizeWindow(long window) {
	}

	/**
	 * Shows the window.
	 */
	public static void glfwShowWindow(long window) {
	}

	/**
	 * Hides the window.
	 */
	public static void glfwHideWindow(long window) {
	}

	/**
	 * Requests focus for the window.
	 */
	public static void glfwFocusWindow(long window) {
	}

	/**
	 * Requests attention for the window.
	 */
	public static void glfwRequestWindowAttention(long window) {
	}

	/**
	 * Returns the monitor for fullscreen, or NULL for windowed.
	 */
	public static long glfwGetWindowMonitor(long window) {
		return Display.isFullscreen() ? 1L : 0L;
	}

	/**
	 * Sets the monitor (enter/exit fullscreen).
	 */
	public static void glfwSetWindowMonitor(long window, long monitor, int xpos, int ypos, int width, int height, int refreshRate) {
		if (monitor != 0L && !Display.isFullscreen()) {
			Display.toggleFullscreen();
		} else if (monitor == 0L && Display.isFullscreen()) {
			Display.toggleFullscreen();
		}
	}

	/**
	 * Returns a window attribute.
	 */
	public static int glfwGetWindowAttrib(long window, int attrib) {
		switch (attrib) {
			case GLFW_FOCUSED:
				return Display.isActive() ? GLFW_TRUE : GLFW_FALSE;
			case GLFW_ICONIFIED:
				return GLFW_FALSE;
			case GLFW_RESIZABLE:
				return GLFW_TRUE;
			case GLFW_VISIBLE:
				return GLFW_TRUE;
			case GLFW_DECORATED:
				return GLFW_FALSE;
			case GLFW_FLOATING:
				return GLFW_FALSE;
			case GLFW_MAXIMIZED:
				return GLFW_FALSE;
			case GLFW_TRANSPARENT_FRAMEBUFFER:
				return GLFW_FALSE;
			case GLFW_HOVERED:
				return Mouse.isInsideWindow() ? GLFW_TRUE : GLFW_FALSE;
			case GLFW_FOCUS_ON_SHOW:
				return GLFW_TRUE;
			case GLFW_CLIENT_API:
				return GLFW_OPENGL_ES_API;
			case GLFW_CONTEXT_VERSION_MAJOR:
				return 3; // WebGL2 ~ OpenGL ES 3.0
			case GLFW_CONTEXT_VERSION_MINOR:
				return 0;
			case GLFW_OPENGL_PROFILE:
				return GLFW_OPENGL_ANY_PROFILE;
			default:
				return 0;
		}
	}

	/**
	 * Sets a window attribute.
	 */
	public static void glfwSetWindowAttrib(long window, int attrib, int value) {
	}

	/**
	 * Sets the user pointer for the window.
	 */
	private static long windowUserPointer = 0L;

	public static void glfwSetWindowUserPointer(long window, long pointer) {
		windowUserPointer = pointer;
	}

	/**
	 * Returns the user pointer for the window.
	 */
	public static long glfwGetWindowUserPointer(long window) {
		return windowUserPointer;
	}

	// ======================================================================
	// Window Callbacks
	// ======================================================================

	public static GLFWWindowPosCallback glfwSetWindowPosCallback(long window, GLFWWindowPosCallbackI cbfun) {
		GLFWWindowPosCallbackI prev = windowPosCallback;
		windowPosCallback = cbfun;
		return prev instanceof GLFWWindowPosCallback ? (GLFWWindowPosCallback) prev : null;
	}

	public static GLFWWindowSizeCallback glfwSetWindowSizeCallback(long window, GLFWWindowSizeCallbackI cbfun) {
		GLFWWindowSizeCallbackI prev = windowSizeCallback;
		windowSizeCallback = cbfun;
		lastWidth = -1;
		lastHeight = -1;
		return prev instanceof GLFWWindowSizeCallback ? (GLFWWindowSizeCallback) prev : null;
	}

	public static GLFWWindowCloseCallback glfwSetWindowCloseCallback(long window, GLFWWindowCloseCallbackI cbfun) {
		GLFWWindowCloseCallbackI prev = windowCloseCallback;
		windowCloseCallback = cbfun;
		return prev instanceof GLFWWindowCloseCallback ? (GLFWWindowCloseCallback) prev : null;
	}

	public static GLFWWindowRefreshCallback glfwSetWindowRefreshCallback(long window, GLFWWindowRefreshCallbackI cbfun) {
		GLFWWindowRefreshCallbackI prev = windowRefreshCallback;
		windowRefreshCallback = cbfun;
		return prev instanceof GLFWWindowRefreshCallback ? (GLFWWindowRefreshCallback) prev : null;
	}

	public static GLFWWindowFocusCallback glfwSetWindowFocusCallback(long window, GLFWWindowFocusCallbackI cbfun) {
		GLFWWindowFocusCallbackI prev = windowFocusCallback;
		windowFocusCallback = cbfun;
		return prev instanceof GLFWWindowFocusCallback ? (GLFWWindowFocusCallback) prev : null;
	}

	public static GLFWWindowIconifyCallback glfwSetWindowIconifyCallback(long window, GLFWWindowIconifyCallbackI cbfun) {
		GLFWWindowIconifyCallbackI prev = windowIconifyCallback;
		windowIconifyCallback = cbfun;
		return prev instanceof GLFWWindowIconifyCallback ? (GLFWWindowIconifyCallback) prev : null;
	}

	public static GLFWWindowMaximizeCallback glfwSetWindowMaximizeCallback(long window, GLFWWindowMaximizeCallbackI cbfun) {
		GLFWWindowMaximizeCallbackI prev = windowMaximizeCallback;
		windowMaximizeCallback = cbfun;
		return prev instanceof GLFWWindowMaximizeCallback ? (GLFWWindowMaximizeCallback) prev : null;
	}

	public static GLFWFramebufferSizeCallback glfwSetFramebufferSizeCallback(long window, GLFWFramebufferSizeCallbackI cbfun) {
		GLFWFramebufferSizeCallbackI prev = framebufferSizeCallback;
		framebufferSizeCallback = cbfun;
		return prev instanceof GLFWFramebufferSizeCallback ? (GLFWFramebufferSizeCallback) prev : null;
	}

	public static GLFWWindowContentScaleCallback glfwSetWindowContentScaleCallback(long window, GLFWWindowContentScaleCallbackI cbfun) {
		GLFWWindowContentScaleCallbackI prev = windowContentScaleCallback;
		windowContentScaleCallback = cbfun;
		return prev instanceof GLFWWindowContentScaleCallback ? (GLFWWindowContentScaleCallback) prev : null;
	}

	// ======================================================================
	// Event Processing
	// ======================================================================

	/**
	 * Processes all pending events. This is the main event loop driver.
	 * Dispatches callbacks based on state changes detected from PlatformInput.
	 */
	public static void glfwPollEvents() {
		if (!initialized) return;

		// Check for window resize
		int currentWidth = getWindowCoordinateWidth();
		int currentHeight = getWindowCoordinateHeight();
		if (currentWidth != lastWidth || currentHeight != lastHeight) {
			lastWidth = currentWidth;
			lastHeight = currentHeight;
			if (windowSizeCallback != null) {
				windowSizeCallback.invoke(MAIN_WINDOW_HANDLE, currentWidth, currentHeight);
			}
			if (framebufferSizeCallback != null) {
				framebufferSizeCallback.invoke(MAIN_WINDOW_HANDLE, PlatformInput.getWindowWidth(), PlatformInput.getWindowHeight());
			}
		}

		// Check for focus change
		boolean currentFocused = Display.isActive();
		if (currentFocused != lastFocused) {
			lastFocused = currentFocused;
			if (windowFocusCallback != null) {
				windowFocusCallback.invoke(MAIN_WINDOW_HANDLE, currentFocused);
			}
		}

		boolean currentMouseInside = Mouse.isInsideWindow();
		if (currentMouseInside != lastMouseInside) {
			lastMouseInside = currentMouseInside;
			if (cursorEnterCallback != null) {
				cursorEnterCallback.invoke(MAIN_WINDOW_HANDLE, currentMouseInside);
			}
		}

		// Process keyboard events
		while (Keyboard.next()) {
			int eaglerKey = Keyboard.getEventKey();
			boolean state = Keyboard.getEventKeyState();
			char character = Keyboard.getEventCharacter();
			int glfwKey = KeyboardConstants.getGLFWKeyFromEagler(eaglerKey);
			int action = state ? (Keyboard.isRepeatEvent() ? GLFW_REPEAT : GLFW_PRESS) : GLFW_RELEASE;
			int mods = getCurrentKeyMods();

			if (keyCallback != null && glfwKey != 0) {
				keyCallback.invoke(MAIN_WINDOW_HANDLE, glfwKey, eaglerKey, action, mods);
			}

			if (charCallback != null && state && character != '\0' && character >= 32) {
				charCallback.invoke(MAIN_WINDOW_HANDLE, character);
			}

			if (charModsCallback != null && state && character != '\0' && character >= 32) {
				charModsCallback.invoke(MAIN_WINDOW_HANDLE, character, mods);
			}
		}

		// Process mouse events
		while (Mouse.next()) {
			if (!isCursorLocked() && syncCursorPositionFromMouseEvent() && cursorPosCallback != null) {
				cursorPosCallback.invoke(MAIN_WINDOW_HANDLE, cursorX, cursorY);
			}
			int button = Mouse.getEventButton();
			boolean state = Mouse.getEventButtonState();

			if (button >= 0 && mouseButtonCallback != null) {
				mouseButtonCallback.invoke(MAIN_WINDOW_HANDLE, button,
						state ? GLFW_PRESS : GLFW_RELEASE, getCurrentKeyMods());
			}

			int dWheel = Mouse.getEventDWheel();
			if (dWheel != 0 && scrollCallback != null) {
				scrollCallback.invoke(MAIN_WINDOW_HANDLE, 0.0, dWheel > 0 ? 1.0 : -1.0);
			}
		}

		// Cursor position update
		if (cursorPosCallback != null) {
			if (pollCursorPosition()) {
				cursorPosCallback.invoke(MAIN_WINDOW_HANDLE, cursorX, cursorY);
			}
		}

		// Check close requested
		if (Display.isCloseRequested() && windowCloseCallback != null) {
			windowCloseCallback.invoke(MAIN_WINDOW_HANDLE);
		}
	}

	/**
	 * Waits until events are available and processes them.
	 */
	public static void glfwWaitEvents() {
		PlatformRuntime.sleep(0);
		glfwPollEvents();
	}

	/**
	 * Waits until events are available or timeout, then processes them.
	 */
	public static void glfwWaitEventsTimeout(double timeout) {
		if (timeout > 0.0) {
			int millis = (int) Math.ceil(timeout * 1000.0);
			PlatformRuntime.sleep(Math.max(1, Math.min(millis, 1000)));
		} else {
			PlatformRuntime.sleep(0);
		}
		glfwPollEvents();
	}

	/**
	 * Posts an empty event to wake up waitEvents.
	 */
	public static void glfwPostEmptyEvent() {
	}

	// ======================================================================
	// Input Functions
	// ======================================================================

	/**
	 * Returns an input mode value.
	 */
	public static int glfwGetInputMode(long window, int mode) {
		switch (mode) {
			case GLFW_CURSOR:
				return cursorMode;
			case GLFW_STICKY_KEYS:
				return stickyKeys ? GLFW_TRUE : GLFW_FALSE;
			case GLFW_STICKY_MOUSE_BUTTONS:
				return stickyMouseButtons ? GLFW_TRUE : GLFW_FALSE;
			case GLFW_RAW_MOUSE_MOTION:
				return rawMouseMotion ? GLFW_TRUE : GLFW_FALSE;
			default:
				return 0;
		}
	}

	/**
	 * Sets an input mode value.
	 */
	public static void glfwSetInputMode(long window, int mode, int value) {
		switch (mode) {
			case GLFW_CURSOR:
				cursorMode = value;
				if (value == GLFW_CURSOR_DISABLED) {
					Mouse.setGrabbed(true);
				} else {
					Mouse.setGrabbed(false);
				}
				break;
			case GLFW_STICKY_KEYS:
				stickyKeys = value == GLFW_TRUE;
				break;
			case GLFW_STICKY_MOUSE_BUTTONS:
				stickyMouseButtons = value == GLFW_TRUE;
				break;
			case GLFW_RAW_MOUSE_MOTION:
				rawMouseMotion = value == GLFW_TRUE;
				break;
		}
	}

	/**
	 * Returns whether raw mouse motion is supported.
	 */
	public static boolean glfwRawMouseMotionSupported() {
		return true; // Pointer Lock API provides raw-ish motion??
	}

	/**
	 * Returns the localized name of a key.
	 */
	public static String glfwGetKeyName(int key, int scancode) {
		int eaglerKey = KeyboardConstants.getEaglerKeyFromGLFW(key);
		if (eaglerKey != 0) {
			return KeyboardConstants.getKeyName(eaglerKey);
		}
		return null;
	}

	/**
	 * Returns the scancode of a key.
	 */
	public static int glfwGetKeyScancode(int key) {
		return KeyboardConstants.getEaglerKeyFromGLFW(key);
	}

	/**
	 * Returns the state of a keyboard key.
	 */
	public static int glfwGetKey(long window, int key) {
		int eaglerKey = KeyboardConstants.getEaglerKeyFromGLFW(key);
		return Keyboard.isKeyDown(eaglerKey) ? GLFW_PRESS : GLFW_RELEASE;
	}

	/**
	 * Returns the state of a mouse button.
	 */
	public static int glfwGetMouseButton(long window, int button) {
		return Mouse.isButtonDown(button) ? GLFW_PRESS : GLFW_RELEASE;
	}

	/**
	 * Returns the cursor position.
	 */
	public static void glfwGetCursorPos(long window, double[] xpos, double[] ypos) {
		if (!isCursorLocked()) {
			syncCursorPositionFromMouseState();
		} else if (!cursorInitialized) {
			syncCursorPositionFromMouseState();
		}
		if (xpos != null && xpos.length > 0) xpos[0] = cursorX;
		if (ypos != null && ypos.length > 0) ypos[0] = cursorY;
	}

	/**
	 * Sets the cursor position.
	 */
	public static void glfwSetCursorPos(long window, double xpos, double ypos) {
		cursorX = clampCursorX(xpos);
		cursorY = clampCursorY(ypos);
		cursorInitialized = true;
		if (!settingCursorPosition) {
			settingCursorPosition = true;
			try {
				Mouse.setCursorPosition((int) cursorX, getMouseYFromGLFWY(cursorY));
			} finally {
				settingCursorPosition = false;
			}
		}
	}

	/**
	 * Creates a custom cursor. In browser, returns a dummy handle.
	 */
	public static long glfwCreateCursor(GLFWImage image, int xhot, int yhot) {
		return 1L; 
	}

	/**
	 * Creates a standard cursor shape.
	 */
	public static long glfwCreateStandardCursor(int shape) {
		return shape; 
	}

	/**
	 * Destroys a cursor.
	 */
	public static void glfwDestroyCursor(long cursor) {
	}

	/**
	 * Sets the cursor for the window.
	 */
	public static void glfwSetCursor(long window, long cursor) {
		if (cursor == GLFW_HAND_CURSOR || cursor == 0x00036004) {
			PlatformInput.showCursor(EnumCursorType.HAND);
		} else if (cursor == GLFW_IBEAM_CURSOR || cursor == 0x00036002) {
			PlatformInput.showCursor(EnumCursorType.TEXT);
		} else {
			PlatformInput.showCursor(EnumCursorType.DEFAULT);
		}
	}

	// ======================================================================
	// Input Callbacks
	// ======================================================================

	public static GLFWKeyCallback glfwSetKeyCallback(long window, GLFWKeyCallbackI cbfun) {
		GLFWKeyCallbackI prev = keyCallback;
		keyCallback = cbfun;
		return prev instanceof GLFWKeyCallback ? (GLFWKeyCallback) prev : null;
	}

	public static GLFWCharCallback glfwSetCharCallback(long window, GLFWCharCallbackI cbfun) {
		GLFWCharCallbackI prev = charCallback;
		charCallback = cbfun;
		return prev instanceof GLFWCharCallback ? (GLFWCharCallback) prev : null;
	}

	private static GLFWCharModsCallbackI charModsCallback;

	public static GLFWCharModsCallback glfwSetCharModsCallback(long window, GLFWCharModsCallbackI cbfun) {
		GLFWCharModsCallbackI prev = charModsCallback;
		charModsCallback = cbfun;
		return prev instanceof GLFWCharModsCallback ? (GLFWCharModsCallback) prev : null;
	}

	public static GLFWMouseButtonCallback glfwSetMouseButtonCallback(long window, GLFWMouseButtonCallbackI cbfun) {
		GLFWMouseButtonCallbackI prev = mouseButtonCallback;
		mouseButtonCallback = cbfun;
		return prev instanceof GLFWMouseButtonCallback ? (GLFWMouseButtonCallback) prev : null;
	}

	public static GLFWCursorPosCallback glfwSetCursorPosCallback(long window, GLFWCursorPosCallbackI cbfun) {
		GLFWCursorPosCallbackI prev = cursorPosCallback;
		cursorPosCallback = cbfun;
		return prev instanceof GLFWCursorPosCallback ? (GLFWCursorPosCallback) prev : null;
	}

	public static GLFWCursorEnterCallback glfwSetCursorEnterCallback(long window, GLFWCursorEnterCallbackI cbfun) {
		GLFWCursorEnterCallbackI prev = cursorEnterCallback;
		cursorEnterCallback = cbfun;
		return prev instanceof GLFWCursorEnterCallback ? (GLFWCursorEnterCallback) prev : null;
	}

	public static GLFWScrollCallback glfwSetScrollCallback(long window, GLFWScrollCallbackI cbfun) {
		GLFWScrollCallbackI prev = scrollCallback;
		scrollCallback = cbfun;
		return prev instanceof GLFWScrollCallback ? (GLFWScrollCallback) prev : null;
	}

	public static GLFWDropCallback glfwSetDropCallback(long window, GLFWDropCallbackI cbfun) {
		GLFWDropCallbackI prev = dropCallback;
		dropCallback = cbfun;
		return prev instanceof GLFWDropCallback ? (GLFWDropCallback) prev : null;
	}

	private static double getPlatformCursorX() {
		return clampCursorX(framebufferToWindowX(Mouse.getX()));
	}

	private static double getPlatformCursorY() {
		return getGLFWYFromMouseY(Mouse.getY());
	}

	private static void syncCursorPositionFromMouseState() {
		cursorX = getPlatformCursorX();
		cursorY = getPlatformCursorY();
		cursorInitialized = true;
	}

	private static boolean syncCursorPositionFromMouseEvent() {
		int x = Mouse.getEventX();
		int y = Mouse.getEventY();
		if (x < 0 || y < 0) {
			return false;
		}
		double nextX = clampCursorX(framebufferToWindowX(x));
		double nextY = getGLFWYFromMouseY(y);
		if (!cursorInitialized || nextX != cursorX || nextY != cursorY) {
			cursorX = nextX;
			cursorY = nextY;
			cursorInitialized = true;
			return true;
		}
		return false;
	}

	private static double getGLFWYFromMouseY(int y) {
		return clampCursorY(framebufferToWindowY(Display.getHeight() - y - 1));
	}

	private static int getMouseYFromGLFWY(double y) {
		return Display.getHeight() - (int) clampFramebufferY(windowToFramebufferY(clampCursorY(y))) - 1;
	}

	private static double clampCursorX(double x) {
		int w = getWindowCoordinateWidth();
		if (w <= 1) {
			return 0.0;
		}
		return x < 0.0 ? 0.0 : (x >= w ? (double) (w - 1) : x);
	}

	private static double clampCursorY(double y) {
		int h = getWindowCoordinateHeight();
		if (h <= 1) {
			return 0.0;
		}
		return y < 0.0 ? 0.0 : (y >= h ? (double) (h - 1) : y);
	}

	private static int getWindowCoordinateWidth() {
		return Math.max(1, PlatformInput.getWindowWidth());
	}

	private static int getWindowCoordinateHeight() {
		return Math.max(1, PlatformInput.getWindowHeight());
	}

	private static double framebufferToWindowX(double x) {
		int framebufferWidth = Display.getWidth();
		if (framebufferWidth <= 1) {
			return 0.0;
		}
		return x * (double)getWindowCoordinateWidth() / (double)framebufferWidth;
	}

	private static double framebufferToWindowY(double y) {
		int framebufferHeight = Display.getHeight();
		if (framebufferHeight <= 1) {
			return 0.0;
		}
		return y * (double)getWindowCoordinateHeight() / (double)framebufferHeight;
	}

	private static double windowToFramebufferY(double y) {
		int windowHeight = getWindowCoordinateHeight();
		if (windowHeight <= 1) {
			return 0.0;
		}
		return y * (double)Display.getHeight() / (double)windowHeight;
	}

	private static double clampFramebufferY(double y) {
		int h = Display.getHeight();
		if (h <= 1) {
			return 0.0;
		}
		return y < 0.0 ? 0.0 : (y >= h ? (double)(h - 1) : y);
	}

	private static boolean pollCursorPosition() {
		if (!cursorInitialized) {
			syncCursorPositionFromMouseState();
			return true;
		}

		if (isCursorLocked()) {
			int dx = Mouse.getDX();
			int dy = Mouse.getDY();
			if (dx == 0 && dy == 0) {
				return false;
			}
			cursorX += dx;
			cursorY -= dy;
			return true;
		}

		double x = getPlatformCursorX();
		double y = getPlatformCursorY();
		if (x == cursorX && y == cursorY) {
			return false;
		}
		cursorX = x;
		cursorY = y;
		return true;
	}

	private static boolean isCursorLocked() {
		return cursorMode == GLFW_CURSOR_DISABLED || Mouse.isActuallyGrabbed();
	}

	static String getDropName(long names, int index) {
		return index >= 0 && index < dropNames.length ? dropNames[index] : "";
	}

	public static GLFWJoystickCallback glfwSetJoystickCallback(GLFWJoystickCallbackI cbfun) {
		GLFWJoystickCallbackI prev = joystickCallback;
		joystickCallback = cbfun;
		return prev instanceof GLFWJoystickCallback ? (GLFWJoystickCallback) prev : null;
	}

	// ======================================================================
	// Joystick Functions
	// ======================================================================

	/**
	 * Returns whether the specified joystick is present.
	 */
	public static boolean glfwJoystickPresent(int jid) {
		return jid == GLFW_JOYSTICK_1 && PlatformInput.gamepadIsValid();
	}

	/**
	 * Returns the name of the specified joystick.
	 */
	public static String glfwGetJoystickName(int jid) {
		if (jid == GLFW_JOYSTICK_1 && PlatformInput.gamepadIsValid()) {
			return PlatformInput.gamepadGetName();
		}
		return null;
	}

	/**
	 * Returns whether the joystick has a gamepad mapping.
	 */
	public static boolean glfwJoystickIsGamepad(int jid) {
		return glfwJoystickPresent(jid);
	}

	/**
	 * Returns the GUID of the joystick.
	 */
	public static String glfwGetJoystickGUID(int jid) {
		return glfwJoystickPresent(jid) ? "eaglercraft-gamepad-0" : null;
	}

	/**
	 * Returns the gamepad name.
	 */
	public static String glfwGetGamepadName(int jid) {
		return glfwGetJoystickName(jid);
	}

	public static boolean glfwGetGamepadState(int jid, GLFWGamepadState state) {
		if (state == null || !glfwJoystickIsGamepad(jid)) {
			return false;
		}
		for (int i = 0; i <= GLFW_GAMEPAD_AXIS_LAST; ++i) {
			state.axes(i, 0.0f);
		}
		for (int i = 0; i <= GLFW_GAMEPAD_BUTTON_LAST; ++i) {
			state.buttons(i, (byte) GLFW_RELEASE);
		}
		return true;
	}

	/**
	 * Updates gamepad mappings from a string.
	 */
	public static boolean glfwUpdateGamepadMappings(String string) {
		return true;
	}

	// ======================================================================
	// Clipboard Functions
	// ======================================================================

	/**
	 * Returns the clipboard string.
	 */
	public static String glfwGetClipboardString(long window) {
		return EagRuntime.getClipboard();
	}

	/**
	 * Sets the clipboard string.
	 */
	public static void glfwSetClipboardString(long window, CharSequence string) {
		EagRuntime.setClipboard(string != null ? string.toString() : "");
	}

	public static void glfwSetClipboardString(long window, ByteBuffer string) {
		String decoded = MemoryUtil.memUTF8(string);
		EagRuntime.setClipboard(decoded != null ? decoded : "");
	}

	// ======================================================================
	// Time Functions
	// ======================================================================

	/**
	 * Returns the current time in seconds since GLFW init.
	 */
	public static double glfwGetTime() {
		return EagRuntime.steadyTimeMillis() / 1000.0;
	}

	/**
	 * Sets the time.
	 */
	public static void glfwSetTime(double time) {
		// Browser limitation: cannot set time base
	}

	/**
	 * Returns the timer value in nanoseconds.
	 */
	public static long glfwGetTimerValue() {
		return EagRuntime.nanoTime();
	}

	/**
	 * Returns the timer frequency (ticks per second).
	 */
	public static long glfwGetTimerFrequency() {
		return 1_000_000_000L; // nanoseconds
	}

	// ======================================================================
	// Context Functions
	// ======================================================================

	/**
	 * Makes the OpenGL context current. In browser, always current.
	 */
	public static void glfwMakeContextCurrent(long window) {
	}

	/**
	 * Returns the window whose context is current.
	 */
	public static long glfwGetCurrentContext() {
		return MAIN_WINDOW_HANDLE;
	}

	/**
	 * Swaps the front and back buffers.
	 */
	public static void glfwSwapBuffers(long window) {
		Display.update();
	}

	/**
	 * Sets the swap interval (V-Sync).
	 */
	public static void glfwSwapInterval(int interval) {
		Display.setVSync(interval > 0);
	}

	/**
	 * Returns whether an extension is supported.
	 */
	public static boolean glfwExtensionSupported(String extension) {
		return false;
	}

	/**
	 * Returns the process address for an OpenGL function.
	 */
	public static long glfwGetProcAddress(String procname) {
		return procname != null ? (procname.hashCode() | 1L) : 0L;
	}

	/**
	 * Returns whether Vulkan is supported. Not in browser.
	 */
	public static boolean glfwVulkanSupported() {
		return false;
	}

	// ======================================================================
	// Helper Methods
	// ======================================================================

	/**
	 * Constructs the current modifier key bitmask from keyboard state.
	 */
	private static int getCurrentKeyMods() {
		int mods = 0;
		if (Keyboard.isKeyDown(KeyboardConstants.KEY_LSHIFT) || Keyboard.isKeyDown(KeyboardConstants.KEY_RSHIFT)) {
			mods |= GLFW_MOD_SHIFT;
		}
		if (Keyboard.isKeyDown(KeyboardConstants.KEY_LCONTROL) || Keyboard.isKeyDown(KeyboardConstants.KEY_RCONTROL)) {
			mods |= GLFW_MOD_CONTROL;
		}
		if (Keyboard.isKeyDown(KeyboardConstants.KEY_LMENU) || Keyboard.isKeyDown(KeyboardConstants.KEY_RMENU)) {
			mods |= GLFW_MOD_ALT;
		}
		return mods;
	}

	/**
	 * Returns the main window handle constant.
	 */
	public static long getMainWindowHandle() {
		return MAIN_WINDOW_HANDLE;
	}
}
