package com.mojang.authlib;

import java.util.HashMap;
import java.util.Map;

public enum UserType {
    LEGACY("legacy"),
    MOJANG("mojang");

    private static final Map<String, UserType> BY_NAME = new HashMap<String, UserType>();
    private final String name;

    private UserType(final String name) {
        this.name = name;
    }

    public static UserType byName(final String name) {
        return BY_NAME.get(name.toLowerCase());
    }

    public String getName() {
        return name;
    }

    static {
        for (final UserType type : UserType.values()) {
            BY_NAME.put(type.name, type);
        }
    }
}
