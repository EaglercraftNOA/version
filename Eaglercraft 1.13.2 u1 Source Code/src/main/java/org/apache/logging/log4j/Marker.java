package org.apache.logging.log4j;

public class Marker {
	private final String name;
	private final Marker parent;

	Marker(String name, Marker parent) {
		this.name = name;
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public Marker getParent() {
		return parent;
	}
}
