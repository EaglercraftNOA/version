package com.mojang.authlib;

import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.properties.PropertyMap;

import java.util.Map;

public interface UserAuthentication {
    /**
     * Checks if enough details are provided to attempt authentication.
     * <p />
     * The exact details required may depend on the service, but generally Username & Password should suffice.
     * Attempting to call {@link #logIn()} when this method returns false will guarantee a failure. You may use
     * this method to check if you can attempt a log in without altering the current state of the authentication.
     *
     * @return True if authentication may be attempted in this state
     */
    boolean canLogIn();

    /**
     * Attempts authentication with the currently set details.
     * <p />
     * If {@link #canLogIn()} returned false, this method is guaranteed to fail. However, an appropriate exception
     * will be raised informing you as to why it failed. The exact required credentials to authenticate varies on
     * the service being used, but generally {@link #setUsername(String) username} and {@link #setPassword(String) password} are a safe
     * bet to log a user in.
     * <p />
     * If the user is {@link #isLoggedIn() already logged in} this method will <b>not</b> fail early and will continue
     * to reauthenticate the user. If the user is attempting to log in with a legacy username ("Steve")
     * and that username is valid but migrated to a Mojang account ("steve@minecraft.net"), a {@link com.mojang.authlib.exceptions.UserMigratedException}
     * will be thrown.
     *
     * @throws com.mojang.authlib.exceptions.AuthenticationUnavailableException Thrown when the servers return a malformed response, or are otherwise unavailable
     * @throws com.mojang.authlib.exceptions.InvalidCredentialsException Thrown when the specified credentials are invalid
     * @throws com.mojang.authlib.exceptions.UserMigratedException Thrown when attempting to authenticate with a {@link #setUsername(String) username} that has been migrated to an email address
     * @throws com.mojang.authlib.exceptions.AuthenticationException Generic exception indicating that we could not authenticate the user
     */
    void logIn() throws AuthenticationException;

    /**
     * Logs this user out, clearing any local credentials.
     */
    void logOut();

    /**
     * Checks if the user is currently logged in.
     *
     * @return True if the user is logged in
     */
    boolean isLoggedIn();

    /**
     * Checks if the user {@link #isLoggedIn() is logged in}, has a valid {@link #getSelectedProfile() game profile} and has validated
     * their session online.
     *
     * @return True if the user is allowed to play online
     */
    boolean canPlayOnline();

    /**
     * Gets a list of valid {@link GameProfile GameProfiles} for this user.
     * <p />
     * Calling this method whilst the user is not {@link #isLoggedIn() logged in} will always return null.
     * If the result of this method is an empty array or null and the user is logged in, the user is considered to not have purchased the game but
     * may be allowed to play demo mode.
     *
     * @return An array of available game profiles, or null.
     */
    GameProfile[] getAvailableProfiles();

    /**
     * Gets the currently selected {@link GameProfile} for this user.
     * <p />
     * Calling this method whilst the user is not {@link #isLoggedIn() logged in} or has no {@link #getAvailableProfiles() available profiles} will always return null.
     *
     * @return Users currently selected Game Profile
     */
    GameProfile getSelectedProfile();

    /**
     * Attempts to select the specified {@link GameProfile}.
     * <p />
     * The user must be {@link #isLoggedIn() logged in}, have no {@link #getSelectedProfile() currently selected game profile} and the specified profile must
     * be retrieved from {@link #getAvailableProfiles()}.
     *
     * @param profile The game profile to select.
     * @throws java.lang.IllegalArgumentException Profile is null or did not come from {@link #getAvailableProfiles()}
     * @throws com.mojang.authlib.exceptions.AuthenticationException User is not currently {@link #isLoggedIn() logged in},
     * or already has a {@link #getSelectedProfile() selected profile},
     * or the authentication service did not allow the profile change
     * @throws com.mojang.authlib.exceptions.AuthenticationUnavailableException Thrown when the servers return a malformed response, or are otherwise unavailable
     */
    void selectGameProfile(GameProfile profile) throws AuthenticationException;

    /**
     * Tries to load any stored details that may be used for authentication from a given Map.
     * <p />
     * This may be used to load an approximation of the current state from a past {@link com.mojang.authlib.UserAuthentication} with {@link #saveForStorage()}.
     *
     * @param credentials Map to load credentials or state from
     */
    void loadFromStorage(Map<String, Object> credentials);

    /**
     * Saves any known credentials to a Map and returns the result.
     * <p />
     * This may be used to save an approximation of the current state for a future {@link com.mojang.authlib.UserAuthentication} with {@link #loadFromStorage(java.util.Map)}.
     *
     * @return Map containing any saved credentials and state for storage
     */
    Map<String, Object> saveForStorage();

    /**
     * Sets the username to authenticate with for the next {@link #logIn()} call.
     * <p />
     * You may not call this method whilst the user is {@link #isLoggedIn() logged in}.
     *
     * @param username Username to authenticate with
     * @throws java.lang.IllegalStateException User is already logged in
     */
    void setUsername(String username);

    /**
     * Sets the password to authenticate with for the next {@link #logIn()} call.
     * <p />
     * You may not call this method with a non-null and non-empty string whilst the user is {@link #isLoggedIn() logged in}.
     *
     * @param password Password to authenticate with
     * @throws java.lang.IllegalStateException User is already logged in and the password is non-null & non-empty
     */
    void setPassword(String password);

    /**
     * Gets an authenticated token for use in authenticated API calls.
     *
     * @return Authenticated token for the current user, or null if not logged in.
     */
    String getAuthenticatedToken();

    /**
     * Gets the unique ID of the currently logged in user.
     * <p />
     * This method will return null if the user is not logged in.
     *
     * @return Unique ID of the currently logged in user, or null if not logged in
     */
    String getUserID();

    /**
     * Gets a Multimap of properties bound to the currently logged in user.
     * <p />
     * This method will return an empty Multimap if the user is not logged in.
     * <p />
     * The returned Multimap will ignore any changes.
     *
     * @return Multimap of user properties.
     */
    PropertyMap getUserProperties();

    /**
     * Gets the type of the currently logged in user.
     * <p />
     * This method will return null if the user is not logged in.
     *
     * @return Type of current logged in user, or null.
     */
    UserType getUserType();
}
