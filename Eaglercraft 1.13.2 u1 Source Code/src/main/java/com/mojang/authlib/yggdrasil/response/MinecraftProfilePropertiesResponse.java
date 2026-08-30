package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;

import java.util.UUID;

public class MinecraftProfilePropertiesResponse extends Response {
    private UUID id;
    private String name;
    private PropertyMap properties;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public PropertyMap getProperties() {
        return properties;
    }
}
