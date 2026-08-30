package com.mojang.text2speech;

public class Narrator {
	private static final Narrator INSTANCE = new Narrator();

	public static Narrator getNarrator() {
		return INSTANCE;
	}

	public boolean active() {
		return false;
	}

	public void say(String text, boolean interrupt) {
	}

	public void clear() {
	}

	public void destroy() {
	}
}
