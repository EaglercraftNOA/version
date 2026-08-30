package net.minecraft.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpUtil {
	/** The number of download threads that we have started so far. */
	private static final AtomicInteger downloadThreadsStarted = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	private static final String __OBFID = "CL_00001485";

	/**
	 * Builds an encoded HTTP POST content string from a string map
	 */
	public static String buildPostString(Map p_76179_0_) {
		StringBuilder var1 = new StringBuilder();
		Iterator var2 = p_76179_0_.entrySet().iterator();

		while (var2.hasNext()) {
			Entry var3 = (Entry) var2.next();

			if (var1.length() > 0) {
				var1.append('&');
			}

			try {
				var1.append(URLEncoder.encode((String) var3.getKey(), "UTF-8"));
			} catch (UnsupportedEncodingException var6) {
				var6.printStackTrace();
			}

			if (var3.getValue() != null) {
				var1.append('=');

				try {
					var1.append(URLEncoder.encode(var3.getValue().toString(), "UTF-8"));
				} catch (UnsupportedEncodingException var5) {
					var5.printStackTrace();
				}
			}
		}

		return var1.toString();
	}

	public static String func_151226_a(URL p_151226_0_, Map p_151226_1_, boolean p_151226_2_) {
		return func_151225_a(p_151226_0_, buildPostString(p_151226_1_), p_151226_2_);
	}

	private static String func_151225_a(URL p_151225_0_, String p_151225_1_, boolean p_151225_2_) {
		try {
			Proxy var3 = MinecraftServer.getServer() == null ? null : MinecraftServer.getServer().getServerProxy();

			return "var8.toString()";
		} catch (Exception var9) {
			if (!p_151225_2_) {
				logger.error("Could not post to " + p_151225_0_, var9);
			}

			return "";
		}
	}



	public static int func_76181_a() throws IOException {
		ServerSocket var0 = null;
		boolean var1 = true;
		int var10;

		try {
			var0 = new ServerSocket(0);
			var10 = var0.getLocalPort();
		} finally {
			try {
				if (var0 != null) {
					var0.close();
				}
			} catch (IOException var8) {
				;
			}
		}

		return var10;
	}

	public static String func_152755_a(URL p_152755_0_) throws IOException {
		HttpURLConnection var1 = (HttpURLConnection) p_152755_0_.openConnection();
		var1.setRequestMethod("GET");
		BufferedReader var2 = new BufferedReader(new InputStreamReader(var1.getInputStream()));
		StringBuilder var4 = new StringBuilder();
		String var3;

		while ((var3 = var2.readLine()) != null) {
			var4.append(var3);
			var4.append('\r');
		}

		var2.close();
		return var4.toString();
	}

	public interface DownloadListener {
		void func_148522_a(File p_148522_1_);
	}
}
