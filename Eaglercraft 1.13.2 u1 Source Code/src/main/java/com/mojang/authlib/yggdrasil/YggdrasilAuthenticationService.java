package com.mojang.authlib.yggdrasil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.Environment;
import com.mojang.authlib.EnvironmentParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InsufficientPrivilegesException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserMigratedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilAuthenticationService extends HttpAuthenticationService {

    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    private final String clientToken;
    private final Gson gson;
    private final Environment environment;

    public YggdrasilAuthenticationService(final Proxy proxy) {
        this(proxy, determineEnvironment());
    }

    public YggdrasilAuthenticationService(final Proxy proxy, final Environment environment) {
        this(proxy, null, environment);
    }

    public YggdrasilAuthenticationService(final Proxy proxy, @Nullable final String clientToken) {
        this(proxy, clientToken, determineEnvironment());
    }

    public YggdrasilAuthenticationService(final Proxy proxy, @Nullable final String clientToken, final Environment environment) {
        super(proxy);
        this.clientToken = clientToken;
        this.environment = environment;
        final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(GameProfile.class, new GameProfileSerializer());
        builder.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
        builder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
        builder.registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer());
        gson = builder.create();
        LOGGER.info("Environment: " + environment.asString());
    }

    private static Environment determineEnvironment() {
        return EnvironmentParser
                   .getEnvironmentFromProperties()
                   .orElse(YggdrasilEnvironment.PROD);
    }

    @Override
    public UserAuthentication createUserAuthentication(final Agent agent) {
        if (clientToken == null) {
            throw new IllegalStateException("Missing client token");
        }
        return new YggdrasilUserAuthentication(this, clientToken, agent, environment);
    }

    @Override
    public MinecraftSessionService createMinecraftSessionService() {
        return new YggdrasilMinecraftSessionService(this, environment);
    }

    @Override
    public GameProfileRepository createProfileRepository() {
        return new YggdrasilGameProfileRepository(this, environment);
    }

    protected <T extends Response> T makeRequest(final URL url, final Object input, final Class<T> classOfT) throws AuthenticationException {
        return makeRequest(url, input, classOfT, null);
    }

    protected <T extends Response> T makeRequest(final URL url, final Object input, final Class<T> classOfT, @Nullable final String authentication) throws AuthenticationException {
        try {
            final String jsonResult = input == null ? performGetRequest(url, authentication) : performPostRequest(url, gson.toJson(input), "application/json");
            final T result = gson.fromJson(jsonResult, classOfT);

            if (result == null) {
                return null;
            }

            if (StringUtils.isNotBlank(result.getError())) {
                if ("UserMigratedException".equals(result.getCause())) {
                    throw new UserMigratedException(result.getErrorMessage());
                } else if ("ForbiddenOperationException".equals(result.getError())) {
                    throw new InvalidCredentialsException(result.getErrorMessage());
                } else if ("InsufficientPrivilegesException".equals(result.getError())) {
                    throw new InsufficientPrivilegesException(result.getErrorMessage());
                } else {
                    throw new AuthenticationException(result.getErrorMessage());
                }
            }

            return result;
        } catch (final IOException | IllegalStateException | JsonParseException e) {
            throw new AuthenticationUnavailableException("Cannot contact authentication server", e);
        }
    }

    private static class GameProfileSerializer implements JsonSerializer<GameProfile>, JsonDeserializer<GameProfile> {
        @Override
        public GameProfile deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
            final JsonObject object = (JsonObject) json;
            final UUID id = object.has("id") ? context.<UUID>deserialize(object.get("id"), UUID.class) : null;
            final String name = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : null;
            return new GameProfile(id, name);
        }

        @Override
        public JsonElement serialize(final GameProfile src, final Type typeOfSrc, final JsonSerializationContext context) {
            final JsonObject result = new JsonObject();
            if (src.getId() != null) {
                result.add("id", context.serialize(src.getId()));
            }
            if (src.getName() != null) {
                result.addProperty("name", src.getName());
            }
            return result;
        }
    }

    public YggdrasilSocialInteractionsService createSocialInteractionsService(final String accessToken) throws AuthenticationException {
        return new YggdrasilSocialInteractionsService(accessToken, getProxy(), environment);
    }
}
