package com.mojang.authlib;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseUserAuthentication implements UserAuthentication {
    private static final Logger LOGGER = LogManager.getLogger();

    protected static final String STORAGE_KEY_PROFILE_NAME = "displayName";
    protected static final String STORAGE_KEY_PROFILE_ID = "uuid";
    protected static final String STORAGE_KEY_PROFILE_PROPERTIES = "profileProperties";
    protected static final String STORAGE_KEY_USER_NAME = "username";
    protected static final String STORAGE_KEY_USER_ID = "userid";
    protected static final String STORAGE_KEY_USER_PROPERTIES = "userProperties";

    private final AuthenticationService authenticationService;
    private final PropertyMap userProperties = new PropertyMap();
    private String userid;
    private String username;
    private String password;
    private GameProfile selectedProfile;
    private UserType userType;

    protected BaseUserAuthentication(final AuthenticationService authenticationService) {
        Validate.notNull(authenticationService);
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean canLogIn() {
        return !canPlayOnline() && StringUtils.isNotBlank(getUsername()) && StringUtils.isNotBlank(getPassword());
    }

    @Override
    public void logOut() {
        password = null;
        userid = null;
        setSelectedProfile(null);
        getModifiableUserProperties().clear();
        setUserType(null);
    }

    @Override
    public boolean isLoggedIn() {
        return getSelectedProfile() != null;
    }

    @Override
    public void setUsername(final String username) {
        if (isLoggedIn() && canPlayOnline()) {
            throw new IllegalStateException("Cannot change username whilst logged in & online");
        }

        this.username = username;
    }

    @Override
    public void setPassword(final String password) {
        if (isLoggedIn() && canPlayOnline() && StringUtils.isNotBlank(password)) {
            throw new IllegalStateException("Cannot set password whilst logged in & online");
        }

        this.password = password;
    }

    protected String getUsername() {
        return username;
    }

    protected String getPassword() {
        return password;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void loadFromStorage(final Map<String, Object> credentials) {
        logOut();

        setUsername(String.valueOf(credentials.get(STORAGE_KEY_USER_NAME)));

        if (credentials.containsKey(STORAGE_KEY_USER_ID)) {
            userid = String.valueOf(credentials.get(STORAGE_KEY_USER_ID));
        } else {
            userid = username;
        }

        if (credentials.containsKey(STORAGE_KEY_USER_PROPERTIES)) {
            try {
                final List<Map<String, String>> list = (List<Map<String, String>>) credentials.get(STORAGE_KEY_USER_PROPERTIES);

                for (final Map<String, String> propertyMap : list) {
                    final String name = propertyMap.get("name");
                    final String value = propertyMap.get("value");
                    final String signature = propertyMap.get("signature");

                    if (signature == null) {
                        getModifiableUserProperties().put(name, new Property(name, value));
                    } else {
                        getModifiableUserProperties().put(name, new Property(name, value, signature));
                    }
                }
            } catch (final Throwable t) {
                LOGGER.warn("Couldn't deserialize user properties", t);
            }
        }

        if (credentials.containsKey(STORAGE_KEY_PROFILE_NAME) && credentials.containsKey(STORAGE_KEY_PROFILE_ID)) {
            final GameProfile profile = new GameProfile(UUIDTypeAdapter.fromString(String.valueOf(credentials.get(STORAGE_KEY_PROFILE_ID))), String.valueOf(credentials.get(STORAGE_KEY_PROFILE_NAME)));
            if (credentials.containsKey(STORAGE_KEY_PROFILE_PROPERTIES)) {
                try {
                    final List<Map<String, String>> list = (List<Map<String, String>>) credentials.get(STORAGE_KEY_PROFILE_PROPERTIES);
                    for (final Map<String, String> propertyMap : list) {
                        final String name = propertyMap.get("name");
                        final String value = propertyMap.get("value");
                        final String signature = propertyMap.get("signature");

                        if (signature == null) {
                            profile.getProperties().put(name, new Property(name, value));
                        } else {
                            profile.getProperties().put(name, new Property(name, value, signature));
                        }
                    }
                } catch (final Throwable t) {
                    LOGGER.warn("Couldn't deserialize profile properties", t);
                }
            }
            setSelectedProfile(profile);
        }
    }

    @Override
    public Map<String, Object> saveForStorage() {
        final Map<String, Object> result = new HashMap<String, Object>();

        if (getUsername() != null) {
            result.put(STORAGE_KEY_USER_NAME, getUsername());
        }
        if (getUserID() != null) {
            result.put(STORAGE_KEY_USER_ID, getUserID());
        } else if (getUsername() != null) {
            result.put(STORAGE_KEY_USER_NAME, getUsername());
        }

        if (!getUserProperties().isEmpty()) {
            final List<Map<String, String>> properties = new ArrayList<Map<String, String>>();
            for (final Property userProperty : getUserProperties().values()) {
                final Map<String, String> property = new HashMap<String, String>();
                property.put("name", userProperty.getName());
                property.put("value", userProperty.getValue());
                property.put("signature", userProperty.getSignature());
                properties.add(property);
            }
            result.put(STORAGE_KEY_USER_PROPERTIES, properties);
        }

        final GameProfile selectedProfile = getSelectedProfile();
        if (selectedProfile != null) {
            result.put(STORAGE_KEY_PROFILE_NAME, selectedProfile.getName());
            result.put(STORAGE_KEY_PROFILE_ID, selectedProfile.getId());

            final List<Map<String, String>> properties = new ArrayList<Map<String, String>>();
            for (final Property profileProperty : selectedProfile.getProperties().values()) {
                final Map<String, String> property = new HashMap<String, String>();
                property.put("name", profileProperty.getName());
                property.put("value", profileProperty.getValue());
                property.put("signature", profileProperty.getSignature());
                properties.add(property);
            }

            if (!properties.isEmpty()) {
                result.put(STORAGE_KEY_PROFILE_PROPERTIES, properties);
            }
        }

        return result;
    }

    protected void setSelectedProfile(final GameProfile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    @Override
    public GameProfile getSelectedProfile() {
        return selectedProfile;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();

        result.append(getClass().getSimpleName());
        result.append("{");

        if (isLoggedIn()) {
            result.append("Logged in as ");
            result.append(getUsername());

            if (getSelectedProfile() != null) {
                result.append(" / ");
                result.append(getSelectedProfile());
                result.append(" - ");

                if (canPlayOnline()) {
                    result.append("Online");
                } else {
                    result.append("Offline");
                }
            }
        } else {
            result.append("Not logged in");
        }

        result.append("}");

        return result.toString();
    }

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    @Override
    public String getUserID() {
        return userid;
    }

    @Override
    public PropertyMap getUserProperties() {
        if (isLoggedIn()) {
            final PropertyMap result = new PropertyMap();
            result.putAll(getModifiableUserProperties());
            return result;
        } else {
            return new PropertyMap();
        }
    }

    protected PropertyMap getModifiableUserProperties() {
        return userProperties;
    }

    @Override
    public UserType getUserType() {
        if (isLoggedIn()) {
            return userType == null ? UserType.LEGACY : userType;
        } else {
            return null;
        }
    }

    protected void setUserType(final UserType userType) {
        this.userType = userType;
    }

    protected void setUserid(final String userid) {
        this.userid = userid;
    }
}
