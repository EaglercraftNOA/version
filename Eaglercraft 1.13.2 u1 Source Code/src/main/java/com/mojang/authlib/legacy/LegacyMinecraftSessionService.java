package com.mojang.authlib.legacy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.mojang.authlib.HttpAuthenticationService.buildQuery;
import static com.mojang.authlib.HttpAuthenticationService.concatenateURL;
import static com.mojang.authlib.HttpAuthenticationService.constantURL;

@Deprecated
public class LegacyMinecraftSessionService extends HttpMinecraftSessionService {
    private static final String BASE_URL = "http://session.minecraft.net/game/";
    private static final URL JOIN_URL = constantURL(BASE_URL + "joinserver.jsp");
    private static final URL CHECK_URL = constantURL(BASE_URL + "checkserver.jsp");

    protected LegacyMinecraftSessionService(final LegacyAuthenticationService authenticationService) {
        super(authenticationService);
    }

    @Override
    public void joinServer(final GameProfile profile, final String authenticationToken, final String serverId) throws AuthenticationException {
        final Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("user", profile.getName());
        arguments.put("sessionId", authenticationToken);
        arguments.put("serverId", serverId);

        final URL url = concatenateURL(JOIN_URL, buildQuery(arguments));

        try {
            final String response = getAuthenticationService().performGetRequest(url);

            if (!"OK".equals(response)) {
                throw new AuthenticationException(response);
            }
        } catch (final IOException e) {
            throw new AuthenticationUnavailableException(e);
        }
    }

    @Override
    public GameProfile hasJoinedServer(final GameProfile user, final String serverId, final InetAddress address) throws AuthenticationUnavailableException {
        final Map<String, Object> arguments = new HashMap<String, Object>();

        arguments.put("user", user.getName());
        arguments.put("serverId", serverId);

        final URL url = concatenateURL(CHECK_URL, buildQuery(arguments));

        try {
            final String response = getAuthenticationService().performGetRequest(url);

            return "YES".equals(response) ? user : null;
        } catch (final IOException e) {
            throw new AuthenticationUnavailableException(e);
        }
    }

    @Override
    public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(final GameProfile profile, final boolean requireSecure) {
        return new HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture>();
    }

    @Override
    public GameProfile fillProfileProperties(final GameProfile profile, final boolean requireSecure) {
        return profile;
    }

    @Override
    public LegacyAuthenticationService getAuthenticationService() {
        return (LegacyAuthenticationService) super.getAuthenticationService();
    }
}
