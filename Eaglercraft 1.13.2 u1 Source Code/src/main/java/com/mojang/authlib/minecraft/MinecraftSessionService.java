package com.mojang.authlib.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;

import java.net.InetAddress;
import java.util.Map;

public interface MinecraftSessionService {
    /**
     * Attempts to join the specified Minecraft server.
     * <p />
     * The {@link com.mojang.authlib.GameProfile} used to join with may be partial, but the exact requirements will vary on
     * authentication service. If this method returns without throwing an exception, the join was successful and a subsequent call to
     * {@link #hasJoinedServer(GameProfile, String, InetAddress)} will return true.
     *
     * @param profile Partial {@link com.mojang.authlib.GameProfile} to join as
     * @param authenticationToken The {@link com.mojang.authlib.UserAuthentication#getAuthenticatedToken() authenticated token} of the user
     * @param serverId The random ID of the server to join
     * @throws com.mojang.authlib.exceptions.AuthenticationUnavailableException Thrown when the servers return a malformed response, or are otherwise unavailable
     * @throws com.mojang.authlib.exceptions.InvalidCredentialsException Thrown when the specified authenticationToken is invalid
     * @throws com.mojang.authlib.exceptions.AuthenticationException Generic exception indicating that we could not authenticate the user
     */
    void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException;

    /**
     * Checks if the specified user has joined a Minecraft server.
     * <p />
     * The {@link com.mojang.authlib.GameProfile} used to join with may be partial, but the exact requirements will vary on
     * authentication service.
     *
     * @param user Partial {@link GameProfile} to check for
     * @param serverId The random ID of the server to check for
     * @param address The address connected from
     * @throws com.mojang.authlib.exceptions.AuthenticationUnavailableException Thrown when the servers return a malformed response, or are otherwise unavailable
     * @return Full game profile if the user had joined, otherwise null
     */
    GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException;

    /**
     * Gets a map of all known textures from a {@link com.mojang.authlib.GameProfile}.
     * <p />
     * If a profile contains invalid textures, they will not be returned. If a profile contains no textures, an empty map will be returned.
     *
     * @param profile Game profile to return textures from.
     * @param requireSecure If true, requires the payload to be recent and securely fetched.
     * @return Map of texture types to textures.
     * @throws com.mojang.authlib.minecraft.InsecureTextureException If requireSecure is true and the data is insecure
     */
    Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure);

    /**
     * Fills a profile with all known properties from the session service.
     * <p />
     * The profile must have an ID. If no information is found, nothing will be done.
     *
     * @param profile Game profile to fill with properties.
     * @param requireSecure If you require verifiable correct data.
     * @return Filled profile for the previous user.
     */
    GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure);
}
