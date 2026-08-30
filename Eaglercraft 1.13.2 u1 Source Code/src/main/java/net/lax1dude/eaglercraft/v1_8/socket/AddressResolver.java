/*
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.socket;

import com.google.common.net.HostAndPort;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerAddress;

public class AddressResolver {

	private static final int DEFAULT_MINECRAFT_PORT = 25565;

	public static String resolveURI(ServerData input) {
		return resolveURI(input.serverIP);
	}

	public static String resolveURI(ServerAddress input) {
		String host = input.getHost();
		if(isWebSocketURI(host)) {
			return normalizeWebSocketURI(host);
		}
		return resolveURI(host, input.getPort());
	}
	
	public static String resolveURI(String input) {
		return resolveURI(input, DEFAULT_MINECRAFT_PORT);
	}

	public static String resolveURI(String input, int defaultPort) {
		input = input.trim();
		if(isWebSocketURI(input)) {
			return normalizeWebSocketURI(input);
		}

		String suffix = "";
		int i = firstSuffixIndex(input);
		if(i != -1) {
			suffix = input.substring(i);
			input = input.substring(0, i);
		}

		String host = input;
		int port = defaultPort;
		try {
			HostAndPort hostAndPort = HostAndPort.fromString(input).withDefaultPort(defaultPort);
			host = hostAndPort.getHost();
			port = hostAndPort.getPort();
		}catch(IllegalArgumentException ex) {
		}

		StringBuilder ret = new StringBuilder();
		ret.append(EagRuntime.requireSSL() ? "wss://" : "ws://");
		ret.append(host);
		if(port > 0) {
			ret.append(':').append(port);
		}
		ret.append(suffix);
		return ret.toString();
	}

	public static ServerAddress resolveAddressFromURI(String input) {
		input = input.trim();
		String uri = resolveURI(input);
		int port = extractPort(uri);
		return ServerAddress.createRaw(uri, port);
	}

	public static boolean isWebSocketURI(String input) {
		if(input == null) {
			return false;
		}
		String lc = input.trim().toLowerCase();
		return lc.startsWith("ws://") || lc.startsWith("wss://");
	}

	private static String normalizeWebSocketURI(String input) {
		return input.trim();
	}

	private static int extractPort(String uri) {
		String lc = uri.toLowerCase();
		int start;
		int defaultPort;
		if(lc.startsWith("ws://")) {
			start = 5;
			defaultPort = 80;
		}else if(lc.startsWith("wss://")) {
			start = 6;
			defaultPort = 443;
		}else {
			start = 0;
			defaultPort = DEFAULT_MINECRAFT_PORT;
		}
		String authority = uri.substring(start);
		int i = firstSuffixIndex(authority);
		if(i != -1) {
			authority = authority.substring(0, i);
		}
		try {
			return HostAndPort.fromString(authority).withDefaultPort(defaultPort).getPort();
		}catch(IllegalArgumentException ex) {
			return defaultPort;
		}
	}

	private static int firstSuffixIndex(String input) {
		int ret = -1;
		for(int i = 0, l = input.length(); i < l; ++i) {
			char c = input.charAt(i);
			if(c == '/' || c == '?' || c == '#') {
				ret = i;
				break;
			}
		}
		return ret;
	}
	
}
