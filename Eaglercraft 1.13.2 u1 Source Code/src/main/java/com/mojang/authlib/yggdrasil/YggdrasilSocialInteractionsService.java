package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.MinecraftClientException;
import com.mojang.authlib.exceptions.MinecraftClientHttpException;
import com.mojang.authlib.minecraft.SocialInteractionsService;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import com.mojang.authlib.yggdrasil.response.PrivilegesResponse;
import com.mojang.authlib.yggdrasil.response.PrivilegesResponse.Privileges.Privilege;
import java.net.Proxy;
import java.net.URL;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

public class YggdrasilSocialInteractionsService implements SocialInteractionsService {
    private static final long BLOCKLIST_REQUEST_COOLDOWN_SECONDS = 120;
    private static final UUID ZERO_UUID = new UUID(0, 0);

    private final URL routePrivileges;
    private final URL routeBlocklist;

    private final MinecraftClient minecraftClient;
    private boolean serversAllowed;
    private boolean realmsAllowed;
    private boolean chatAllowed;
    private boolean telemetryAllowed;
    @Nullable
    private Instant nextAcceptableBlockRequest;

    @Nullable
    private Set<UUID> blockList;

    public YggdrasilSocialInteractionsService(final String accessToken, final Proxy proxy, final Environment env) throws AuthenticationException {
        this.minecraftClient = new MinecraftClient(accessToken, proxy);
        routePrivileges = HttpAuthenticationService.constantURL(env.getServicesHost() + "/privileges");
        routeBlocklist = HttpAuthenticationService.constantURL(env.getServicesHost() + "/privacy/blocklist");
        checkPrivileges();
    }

    @Override
    public boolean serversAllowed() {
        return serversAllowed;
    }

    @Override
    public boolean realmsAllowed() {
        return realmsAllowed;
    }

    @Override
    public boolean chatAllowed() {
        return chatAllowed;
    }

    @Override
    public boolean telemetryAllowed() {
        return telemetryAllowed;
    }

    @Override
    public boolean isBlockedPlayer(final UUID playerID) {
        if (playerID.equals(ZERO_UUID)) {
            return false;
        }

        if (blockList == null) {
            blockList = fetchBlockList();
            if (blockList == null) {
                return false;
            }
        }

        return blockList.contains(playerID);
    }

    @Nullable
    private Set<UUID> fetchBlockList() {
        if (nextAcceptableBlockRequest != null && nextAcceptableBlockRequest.isAfter(Instant.now())) {
            return null;
        }
        nextAcceptableBlockRequest = Instant.now().plusSeconds(BLOCKLIST_REQUEST_COOLDOWN_SECONDS);
        try {
            final BlockListResponse response = minecraftClient.get(routeBlocklist, BlockListResponse.class);
            return response.getBlockedProfiles();
        }
        catch (final MinecraftClientHttpException e) {
            //TODO: Look at the error type and response code. Retry if 5xx
            //TODO: Handle when status is 401 (Unathorized) -> Refresh token/login again.
            return null;
        } catch (final MinecraftClientException e) {
            //Low level error, IO problems or JSON parsing error
            //TODO: Retry if SERVICE_UNAVAILABLE error
            return null;
        }
    }

    private void checkPrivileges() throws AuthenticationException {
        try {
            final PrivilegesResponse response = minecraftClient.get(routePrivileges, PrivilegesResponse.class);
            //Optionals so we don't crash if attribute is not present.
            chatAllowed = response.getPrivileges().getOnlineChat().map(Privilege::isEnabled).orElse(false);
            serversAllowed = response.getPrivileges().getMultiplayerServer().map(Privilege::isEnabled).orElse(false);
            realmsAllowed = response.getPrivileges().getMultiplayerRealms().map(Privilege::isEnabled).orElse(false);
            telemetryAllowed = response.getPrivileges().getTelemetry().map(Privilege::isEnabled).orElse(false);
        } catch (MinecraftClientHttpException e) {
            //TODO: Handle when status is 401 (Unauthorized) -> Refresh token/login again.
            throw e.toAuthenticationException();
        } catch (final MinecraftClientException e) {
            //Low level error, IO problems or JSON parsing error
            //TODO: Retry if SERVICE_UNAVAILABLE error
            throw e.toAuthenticationException();
        }
    }
}
