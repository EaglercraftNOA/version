package com.mojang.authlib.minecraft.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientException.ErrorType;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Objects;
import java.util.UUID;

public class ObjectMapper {

    private final Gson gson;

    public ObjectMapper(final Gson gson) {
        this.gson = Objects.requireNonNull(gson);
    }

    public <T> T readValue(String value, Class<T> type) {
        try {
            return gson.fromJson(value, type);
        } catch (JsonParseException e) {
            throw new MinecraftClientException(ErrorType.JSON_ERROR, "Failed to read value " + value, e);
        }
    }

    public String writeValueAsString(final Object entity) {
        try {
            return gson.toJson(entity);
        } catch (RuntimeException e) {
            throw new MinecraftClientException(ErrorType.JSON_ERROR, "Failed to write value", e);
        }
    }

    public static ObjectMapper create() {
        return new ObjectMapper(new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create());
    }
}
