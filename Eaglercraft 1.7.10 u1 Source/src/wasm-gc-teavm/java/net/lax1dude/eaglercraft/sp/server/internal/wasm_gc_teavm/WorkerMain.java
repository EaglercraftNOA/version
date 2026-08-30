/*
 * Copyright (c) 2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.sp.server.internal.wasm_gc_teavm;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.internal.wasm_gc_teavm.WASMGCClientConfigAdapter;
import net.lax1dude.eaglercraft.sp.ipc.IPCPacket15Crashed;
import net.lax1dude.eaglercraft.sp.ipc.IPCPacketFFProcessKeepAlive;
import net.lax1dude.eaglercraft.sp.server.EaglerIntegratedServerWorker;
import net.lax1dude.eaglercraft.sp.server.internal.ServerPlatformSingleplayer;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.interop.Import;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

import java.io.PrintStream;

public class WorkerMain {

	public static void _main() {
		PrintStream systemOut = System.out;
		PrintStream systemErr = System.err;
		try {
			PlatformRuntime.setThreadName("IntegratedServer");
			systemOut.println("WorkerMain: [INFO] eaglercraftx worker thread is starting...");
			JSObject startArgs = getEaglerXOpts();
			systemOut.println("WorkerMain: [INFO] reading configuration");
			((WASMGCClientConfigAdapter)WASMGCClientConfigAdapter.instance).loadNative(startArgs);
			systemOut.println("WorkerMain: [INFO] initializing server runtime");
			ServerPlatformSingleplayer.initializeContext();
			systemOut.println("WorkerMain: [INFO] starting worker thread");
			EaglerIntegratedServerWorker.serverMain();
		}catch(Throwable t) {
			System.setOut(systemOut);
			System.setErr(systemErr);
			System.err.println("WorkerMain: [ERROR] uncaught exception thrown!");
			EagRuntime.debugPrintStackTraceToSTDERR(t);
			EaglerIntegratedServerWorker.sendIPCPacket(new IPCPacket15Crashed("UNCAUGHT EXCEPTION CAUGHT IN WORKER PROCESS!\\n\\n" + EagRuntime.getStackTrace(t)));
			EaglerIntegratedServerWorker.sendIPCPacket(new IPCPacketFFProcessKeepAlive(IPCPacketFFProcessKeepAlive.EXITED));
		}finally {
			System.err.println("WorkerMain: [ERROR] eaglercraftx worker thread has exited");
		}
	}

	@JSFunctor
	private static interface WorkerArgumentsPacketHandler extends JSObject {
		public void onMessage(String msg);
	}

	@Import(module = "platformRuntime", name = "getEaglercraftXOpts")
	private static native JSObject getEaglerXOpts();

	@Async
	private static native String getStartArgs();

	private static void getStartArgs(final AsyncCallback<String> cb) {
		ServerPlatformSingleplayer.register();
		// Send ready signal to client after registration
		postMessage();
		cb.complete(getStartArgs());
	}

	@JSBody(params = {}, script = "postMessage({ ch: '__WORKER_READY__', dat: null });")
	private static native void postMessage();

}