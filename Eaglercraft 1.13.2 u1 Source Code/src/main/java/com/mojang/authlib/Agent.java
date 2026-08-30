package com.mojang.authlib;

public class Agent {
    public static final Agent MINECRAFT = new Agent("Minecraft", 1);
    public static final Agent SCROLLS = new Agent("Scrolls", 1);

    private final String name;
    private final int version;

    public Agent(final String name, final int version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "Agent{" +
            "name='" + name + '\'' +
            ", version=" + version +
            '}';
    }
}
