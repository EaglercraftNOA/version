package net.minecraft.util.text.translation;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class LanguageMap {
	/**
	 * Pattern that matches numeric variable placeholders in a resource string, such
	 * as "%d", "%3$d", "%.2f"
	 */
	private static final Pattern NUMERIC_VARIABLE_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");

	/**
	 * A Splitter that splits a string on the first "=". For example, "a=b=c" would
	 * split into ["a", "b=c"].
	 */
	private static final Splitter EQUAL_SIGN_SPLITTER = Splitter.on('=').limit(2);

	/** Is the private singleton instance of StringTranslate. */
	private static final LanguageMap instance = new LanguageMap();
	static LanguageMap fallbackInstance = null;
	private final Map<String, String> languageList = Maps.<String, String>newHashMap();

	/**
	 * The time, in milliseconds since epoch, that this instance was last updated
	 */
	private long lastUpdateTimeInMilliseconds;

	public LanguageMap() {
	}

	public static void initClient() {
		try (InputStream inputstream = EagRuntime.getResourceStream("/assets/minecraft/lang/en_us.lang")) {
			initServer(IOUtils.readLines(inputstream, StandardCharsets.UTF_8));
			fallbackInstance = new LanguageMap();
			fallbackInstance.replaceWith(instance.languageList);
			SingleplayerServerController.updateLocale(dump());
		} catch (IOException e) {
			EagRuntime.debugPrintStackTrace(e);
		}
	}

	public static void initServer(List<String> strs) {
		instance.languageList.clear();
		for (int i = 0, l = strs.size(); i < l; ++i) {
			String s = strs.get(i);
			if (!s.isEmpty() && s.charAt(0) != 35) {
				String[] astring = (String[]) Iterables.toArray(EQUAL_SIGN_SPLITTER.split(s), String.class);
				if (astring != null && astring.length == 2) {
					String s1 = astring[0];
					String s2 = NUMERIC_VARIABLE_PATTERN.matcher(astring[1]).replaceAll("%s"); // TODO: originally
																								// "%$1s"
																								// but must be "%s" to
																								// work with TeaVM
																								// (why?)
					instance.languageList.put(s1, s2);
				}
			}
		}

		instance.lastUpdateTimeInMilliseconds = EagRuntime.steadyTimeMillis();
	}

	/**
	 * Return the StringTranslate singleton instance
	 */
	static LanguageMap getInstance() {
		return instance;
	}

	/**
	 * Replaces all the current instance's translations with the ones that are
	 * passed in.
	 */
	public static synchronized void replaceWith(Map<String, String> p_135063_0_) {
		instance.languageList.clear();
		instance.languageList.putAll(p_135063_0_);
		instance.lastUpdateTimeInMilliseconds = EagRuntime.steadyTimeMillis();
	}

	/**
	 * Translate a key to current language.
	 */
	public synchronized String translateKey(String key) {
		return this.tryTranslateKey(key);
	}

	/**
	 * Translate a key to current language applying String.format()
	 */
	public synchronized String translateKeyFormat(String key, Object... format) {
		String s = this.tryTranslateKey(key);

		try {
			return HString.format(s, format);
		} catch (IllegalFormatException var5) {
			return "Format error: " + s;
		}
	}

	/**
	 * Tries to look up a translation for the given key; spits back the key if no
	 * result was found.
	 */
	private String tryTranslateKey(String key) {
		String s = this.languageList.get(key);
		return s == null ? key : s;
	}

	/**
	 * Returns true if the passed key is in the translation table.
	 */
	public synchronized boolean isKeyTranslated(String key) {
		return this.languageList.containsKey(key);
	}

	/**
	 * Gets the time, in milliseconds since epoch, that this instance was last
	 * updated
	 */
	public long getLastUpdateTimeInMilliseconds() {
		return this.lastUpdateTimeInMilliseconds;
	}

	public static List<String> dump() {
		List<String> ret = new ArrayList(instance.languageList.size());
		for (Entry<String, String> etr : instance.languageList.entrySet()) {
			ret.add(etr.getKey() + "=" + etr.getValue());
		}
		return ret;
	}
}
