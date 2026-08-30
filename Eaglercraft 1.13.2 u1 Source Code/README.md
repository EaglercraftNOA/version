# Eaglercraft 1.13.2

### Java 17 is recommended for compiling to TeaVM

### Java 8 or greater is required for the desktop runtime

**Most Java IDEs will allow you to import this repository as a gradle project for compiling it to JavaScript.**

Java must be added to your PATH!

**To compile the web client:**
1. In the folder `target_teavm_javascript` run `MakeOfflineDownload` (or the `target_teavm_javascript:makeMainOfflineDownload` gradle task)
2. Check the "javascript" folder


**To use the desktop runtime:**
1. Run `StartDesktopRuntime` in `target_lwjgl_desktop` (or the `target_lwjgl_desktop:eaglercraftDebugRuntime` gradle task)

**To compile WASM-GC:**
1. Run `MakeWASMClientBundle` in `target_teavm_wasm_gc` (or the `target_teavm_wasm_gc:makeMainWasmClientBundle` gradle task)


**OPTIONAL:**

Run `CompileBootstrapJS` in `target_teavm_wasm_gc` to regenerate bootstrap.js

Run `CompileLoaderWASM` in `target_teavm_wasm_gc` to generate loader.wasm (requires emscripten)

**To setup a multiplayer server:**

Template: https://github.com/arlenh7/Eag-1.13-TestServer

To make a server for 1.13 you have to use the plugin `EaglerXServer` by lax1dude which you can download [here](https://github.com/lax1dude/eaglerxserver/releases/download/v1.1.0/EaglerXServer.jar). Although this was made to work with Bungeecord, Velocity and Spigot this was only tested in Bungeecord and Velocity so Spigot might not be supported. You can also join servers that use Forks of Bungeecord, as 1.13 supports them.

### Installation

Install [EaglerXServer](https://github.com/lax1dude/eaglerxserver/releases/download/v1.1.0/EaglerXServer.jar) and drop the .jar file into your plugins folder of your bungee server. Set your inject address in the config.yml to the same bind address as your bungee server, and restart the server. If needed, enable ip forwarding and choose a header. The most recommende header is X-Real-IP as that is the one that works the best. Bungeecord should be configured normally with online mode being false. It is recommended to setup your server as 1.13 but ViaVersion might work too.

Configuration files and other plugin data will be written in `plugins/EaglerXServer`

For more information about EaglerXServer and how to setup a multiplayer server look at the [readme here](https://github.com/lax1dude/eaglerxserver/blob/main/README.md)
