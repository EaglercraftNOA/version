# Eaglercraft 1.12
(README ripped from 1.8)

### Java 17 is recommended for compiling to TeaVM

### Java 8 or greater is required for the desktop runtime

**Most Java IDEs will allow you to import this repository as a gradle project for compiling it to JavaScript.**

Java must be added to your PATH!

**To compile the web client:**
1. Run `CompileEPK`
2. Run `CompileJS` (or the `generateJavaScript` gradle task in your IDE)
3. Check the "javascript" folder

**To compile an offline download:**
1. Run `CompileEPK`
2. Run `CompileJS` (or the `generateJavaScript` gradle task in your IDE)
3. Run `MakeOfflineDownload`
4. Check the "javascript" folder

**To use the desktop runtime:**
1. Open a terminal and run `./gradlew runclient`
2. Run/Debug the client with the included "eaglercraftDebugRuntime" configuration

**To setup a multiplayer server:**

Dead simple test server: https://github.com/catfoolyou/EagsTestServer

To make a server for Eaglercraft 1.12 the recommended software to use is EaglercraftXBungee ("EaglerXBungee") which is included [here](https://github.com/lax1dude/eagl3rxbungee/blob/main/EaglerXBungee-Latest.jar). This is a plugin designed to be used with BungeeCord to allow Eaglercraft players to join your BungeeCord server. It is assumed that the reader already knows what BungeeCord is and has a working server set up that is joinable via java edition. If you don't know what BungeeCord is, please research the topic yourself first before continuing. Waterfall and FlameCord have also been tested, but EaglerXBungee was natively compiled against BungeeCord.

There is an experimental velocity plugin available [here](https://github.com/lax1dude/eagl3rxbungee/blob/main/EaglerXVelocity-Latest.jar) but it is still in development and not recommended for public servers, so be sure to check for updates regularly if you use it. Configuration files are basically identical to EaglercraftXBungee so its safe to just directy copy in your old EaglercraftXBungee config files to the `plugins/eaglerxvelocity` folder and they should work with a minimal number of edits if you are migrating your network from BungeeCord to Velocity.

**Warning:** Both EaglerXBungee and EaglerXVelocity perform a lot of reflection that will inevitably break after a while when BungeeCord or Velocity is updated upstream. Both plugins will display the precise build number of BungeeCord and Velocity that has been tested by the developers and known to be compatible with EaglerXBungee and EaglerXVelocity when the proxy first starts up. If you are experiencing issues, try checking the BungeeCord or Velocity website for old versions and find the closest version number to whatever the current compatible version number is that is printed by EaglerXBungee/EaglerXVelocity, it will probably fix whatever missing functions the error messages are complaining about.

### Installation

Obtain the latest version of the EaglerXBungee JAR file (it can be downloaded in the 1.8 client from the "Multiplayer" screen) and place it in the "plugins" folder of your BungeeCord server. It's recommended to only join native Minecraft 1.12.2 servers through an EaglerXBungee server but plugins like ProtocolSupport have allowed some people to join newer servers too.

Configuration files and other plugin data will be written in `plugins/EaglercraftXBungee`

For more information about EaglerXBungee/Velocity and how to setup a multiplayer server look at the 1.8 readme [here](https://git.eaglercraft.rip/eaglercraft/eaglercraft-1.8#making-a-server)

**Note:** 
This is eclipse-specific problem, just import it into IntelliJ as a Gradle project (NOT as an Eclipse project!) and it will work fine

If you are trying to use the desktop runtime on Linux, make sure you add the "desktopRuntime" folder to the `LD_LIBRARY_PATH` environment variable of the Java process. This should be done automatically by the Eclipse project's default run configuration, but it might not work properly on every system ~~or when the Eclipse project is imported into IntelliJ.~~

The source codes of EaglercraftXBungee and EaglercraftXVelocity are not included here.
