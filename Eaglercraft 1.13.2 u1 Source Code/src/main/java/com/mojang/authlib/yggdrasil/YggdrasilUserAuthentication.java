package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.HttpUserAuthentication;
import com.mojang.authlib.Environment;
import com.mojang.authlib.UserType;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.yggdrasil.request.AuthenticationRequest;
import com.mojang.authlib.yggdrasil.request.RefreshRequest;
import com.mojang.authlib.yggdrasil.request.ValidateRequest;
import com.mojang.authlib.yggdrasil.response.AuthenticationResponse;
import com.mojang.authlib.yggdrasil.response.RefreshResponse;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.authlib.yggdrasil.response.User;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class YggdrasilUserAuthentication extends HttpUserAuthentication {
    private static final Logger LOGGER = LogManager.getLogger();

    private final URL routeAuthenticate;
    private final URL routeRefresh;
    private final URL routeValidate;
    private final URL routeInvalidate;
    private final URL routeSignout;

    private static final String STORAGE_KEY_ACCESS_TOKEN = "accessToken";

    private final Agent agent;
    private GameProfile[] profiles;
    private final String clientToken;
    private String accessToken;
    private boolean isOnline;

    public YggdrasilUserAuthentication(final YggdrasilAuthenticationService authenticationService, final String clientToken, final Agent agent) {
        this(authenticationService, clientToken, agent, YggdrasilEnvironment.PROD);
    }

    public YggdrasilUserAuthentication(final YggdrasilAuthenticationService authenticationService, final String clientToken, final Agent agent, Environment env) {
        super(authenticationService);
        this.clientToken = clientToken;
        this.agent = agent;

        LOGGER.info("Environment: " + env.getName(), ". AuthHost: " + env.getAuthHost());
        this.routeAuthenticate = HttpAuthenticationService.constantURL(env.getAuthHost() + "/authenticate");
        this.routeRefresh = HttpAuthenticationService.constantURL(env.getAuthHost() + "/refresh");
        this.routeValidate = HttpAuthenticationService.constantURL(env.getAuthHost() + "/validate");
        this.routeInvalidate = HttpAuthenticationService.constantURL(env.getAuthHost() + "/invalidate");
        this.routeSignout = HttpAuthenticationService.constantURL(env.getAuthHost() + "/signout");
    }

    @Override
    public boolean canLogIn() {
        return !canPlayOnline() && StringUtils.isNotBlank(getUsername()) && (StringUtils.isNotBlank(getPassword()) || StringUtils.isNotBlank(getAuthenticatedToken()));
    }

    @Override
    public void logIn() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }

        if (StringUtils.isNotBlank(getAuthenticatedToken())) {
            logInWithToken();
        } else if (StringUtils.isNotBlank(getPassword())) {
            logInWithPassword();
        } else {
            throw new InvalidCredentialsException("Invalid password");
        }
    }

    protected void logInWithPassword() throws AuthenticationException {
        if (StringUtils.isBlank(getUsername())) {
            throw new InvalidCredentialsException("Invalid username");
        }
        if (StringUtils.isBlank(getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        LOGGER.info("Logging in with username & password");

        final AuthenticationRequest request = new AuthenticationRequest(getAgent(), getUsername(), getPassword(), clientToken);
        final AuthenticationResponse response = getAuthenticationService().makeRequest(routeAuthenticate, request, AuthenticationResponse.class);

        if (!response.getClientToken().equals(clientToken)) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }

        if (response.getSelectedProfile() != null) {
            setUserType(response.getSelectedProfile().isLegacy() ? UserType.LEGACY : UserType.MOJANG);
        } else if (ArrayUtils.isNotEmpty(response.getAvailableProfiles())) {
            setUserType(response.getAvailableProfiles()[0].isLegacy() ? UserType.LEGACY : UserType.MOJANG);
        }

        final User user = response.getUser();

        if (user != null && user.getId() != null) {
            setUserid(user.getId());
        } else {
            setUserid(getUsername());
        }

        isOnline = true;
        accessToken = response.getAccessToken();
        profiles = response.getAvailableProfiles();
        setSelectedProfile(response.getSelectedProfile());
        getModifiableUserProperties().clear();

        updateUserProperties(user);
    }

    protected void updateUserProperties(final User user) {
        if (user == null) {
            return;
        }

        if (user.getProperties() != null) {
            getModifiableUserProperties().putAll(user.getProperties());
        }
    }

    protected void logInWithToken() throws AuthenticationException {
        if (StringUtils.isBlank(getUserID())) {
            if (StringUtils.isBlank(getUsername())) {
                setUserid(getUsername());
            } else {
                throw new InvalidCredentialsException("Invalid uuid & username");
            }
        }
        if (StringUtils.isBlank(getAuthenticatedToken())) {
            throw new InvalidCredentialsException("Invalid access token");
        }

        LOGGER.info("Logging in with access token");

        if (checkTokenValidity()) {
            LOGGER.debug("Skipping refresh call as we're safely logged in.");
            isOnline = true;
            return;
        }

        final RefreshRequest request = new RefreshRequest(getAuthenticatedToken(), clientToken);
        final RefreshResponse response = getAuthenticationService().makeRequest(routeRefresh, request, RefreshResponse.class);

        if (!response.getClientToken().equals(clientToken)) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }

        if (response.getSelectedProfile() != null) {
            setUserType(response.getSelectedProfile().isLegacy() ? UserType.LEGACY : UserType.MOJANG);
        } else if (ArrayUtils.isNotEmpty(response.getAvailableProfiles())) {
            setUserType(response.getAvailableProfiles()[0].isLegacy() ? UserType.LEGACY : UserType.MOJANG);
        }

        if (response.getUser() != null && response.getUser().getId() != null) {
            setUserid(response.getUser().getId());
        } else {
            setUserid(getUsername());
        }

        isOnline = true;
        accessToken = response.getAccessToken();
        profiles = response.getAvailableProfiles();
        setSelectedProfile(response.getSelectedProfile());
        getModifiableUserProperties().clear();

        updateUserProperties(response.getUser());
    }

    protected boolean checkTokenValidity() throws AuthenticationException {
        final ValidateRequest request = new ValidateRequest(getAuthenticatedToken(), clientToken);
        try {
            getAuthenticationService().makeRequest(routeValidate, request, Response.class);
            return true;
        } catch (final AuthenticationException ignored) {
            return false;
        }
    }

    @Override
    public void logOut() {
        super.logOut();

        accessToken = null;
        profiles = null;
        isOnline = false;
    }

    @Override
    public GameProfile[] getAvailableProfiles() {
        return profiles;
    }

    @Override
    public boolean isLoggedIn() {
        return StringUtils.isNotBlank(accessToken);
    }

    @Override
    public boolean canPlayOnline() {
        return isLoggedIn() && getSelectedProfile() != null && isOnline;
    }

    @Override
    public void selectGameProfile(final GameProfile profile) throws AuthenticationException {
        if (!isLoggedIn()) {
            throw new AuthenticationException("Cannot change game profile whilst not logged in");
        }
        if (getSelectedProfile() != null) {
            throw new AuthenticationException("Cannot change game profile. You must log out and back in.");
        }
        if (profile == null || !ArrayUtils.contains(profiles, profile)) {
            throw new IllegalArgumentException("Invalid profile '" + profile + "'");
        }

        final RefreshRequest request = new RefreshRequest(getAuthenticatedToken(), clientToken, profile);
        final RefreshResponse response = getAuthenticationService().makeRequest(routeRefresh, request, RefreshResponse.class);

        if (!response.getClientToken().equals(clientToken)) {
            throw new AuthenticationException("Server requested we change our client token. Don't know how to handle this!");
        }

        isOnline = true;
        accessToken = response.getAccessToken();
        setSelectedProfile(response.getSelectedProfile());
    }

    @Override
    public void loadFromStorage(final Map<String, Object> credentials) {
        super.loadFromStorage(credentials);

        accessToken = String.valueOf(credentials.get(STORAGE_KEY_ACCESS_TOKEN));
    }

    @Override
    public Map<String, Object> saveForStorage() {
        final Map<String, Object> result = super.saveForStorage();

        if (StringUtils.isNotBlank(getAuthenticatedToken())) {
            result.put(STORAGE_KEY_ACCESS_TOKEN, getAuthenticatedToken());
        }

        return result;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public String getSessionToken() {
        if (isLoggedIn() && getSelectedProfile() != null && canPlayOnline()) {
            return String.format("token:%s:%s", getAuthenticatedToken(), getSelectedProfile().getId());
        } else {
            return null;
        }
    }

    @Override
    public String getAuthenticatedToken() {
        return accessToken;
    }

    public Agent getAgent() {
        return agent;
    }

    @Override
    public String toString() {
        return "YggdrasilAuthenticationService{" +
            "agent=" + agent +
            ", profiles=" + Arrays.toString(profiles) +
            ", selectedProfile=" + getSelectedProfile() +
            ", username='" + getUsername() + '\'' +
            ", isLoggedIn=" + isLoggedIn() +
            ", userType=" + getUserType() +
            ", canPlayOnline=" + canPlayOnline() +
            ", accessToken='" + accessToken + '\'' +
            ", clientToken='" + clientToken + '\'' +
            '}';
    }

    @Override
    public YggdrasilAuthenticationService getAuthenticationService() {
        return (YggdrasilAuthenticationService) super.getAuthenticationService();
    }
}
