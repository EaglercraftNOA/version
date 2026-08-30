package com.mojang.authlib;

import com.mojang.authlib.properties.PropertyMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.UUID;

public class GameProfile {
    private final UUID id;
    private final String name;
    private final PropertyMap properties = new PropertyMap();
    private boolean legacy;

    /**
     * Constructs a new Game Profile with the specified ID and name.
     * <p />
     * Either ID or name may be null/empty, but at least one must be filled.
     *
     * @param id Unique ID of the profile
     * @param name Display name of the profile
     * @throws java.lang.IllegalArgumentException Both ID and name are either null or empty
     */
    public GameProfile(final UUID id, final String name) {
        if (id == null && StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Name and ID cannot both be blank");
        }

        this.id = id;
        this.name = name;
    }

    /**
     * Gets the unique ID of this game profile.
     * <p />
     * This may be null for partial profile data if constructed manually.
     *
     * @return ID of the profile
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the display name of this game profile.
     * <p />
     * This may be null for partial profile data if constructed manually.
     *
     * @return Name of the profile
     */
    public String getName() {
        return name;
    }

    /**
     * Returns any known properties about this game profile.
     *
     * @return Modifiable map of profile properties.
     */
    public PropertyMap getProperties() {
        return properties;
    }

    /**
     * Checks if this profile is complete.
     * <p />
     * A complete profile has no empty fields. Partial profiles may be constructed manually and used as input to methods.
     *
     * @return True if this profile is complete (as opposed to partial)
     */
    public boolean isComplete() {
        return id != null && StringUtils.isNotBlank(getName());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GameProfile that = (GameProfile) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("name", name)
            .append("properties", properties)
            .append("legacy", legacy)
            .toString();
    }

    public boolean isLegacy() {
        return legacy;
    }
}
