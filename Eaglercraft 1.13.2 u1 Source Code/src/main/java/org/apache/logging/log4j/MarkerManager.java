package org.apache.logging.log4j;

public final class MarkerManager {
	private MarkerManager() {
	}

	public static Marker getMarker(String name) {
		return new Marker(name, null);
	}

	public static Marker getMarker(String name, Marker parent) {
		return new Marker(name, parent);
	}
}
