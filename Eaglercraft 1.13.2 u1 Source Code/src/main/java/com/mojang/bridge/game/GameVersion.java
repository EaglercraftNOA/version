package com.mojang.bridge.game;

import java.util.Date;

/**
 * An object representing a version of the game (typically, the one being played right now).
 */
public interface GameVersion {
    /**
     * A unique identified for this version.
     *
     * <p>The only guarantees about this string are that it will be 100 characters or less, and that it will always
     * be unique to a version. Do not make any other assumptions on the format, as it may change at any time.</p>
     *
     * @return A unique identifier for this Version, 100 characters or less.
     */
    String getId();

    /**
     * A human readable name of this version.
     *
     * <p>This may not be unique to two versions, do not use it for anything programmatic - only for displaying to
     * humans in lieu of any other data represented by this class.</p>
     *
     * <p>The format of this may change, it may be duplicated across multiple versions (such as in development, multiple
     * builds have the same name), or sometimes it may be plain wrong (for example, the old april fools "2.0"
     * version). </p>
     *
     * @return A human readable name of this version
     */
    String getName();

    /**
     * A human readable release target for this version.
     *
     * <p>A "release target" is the target version that this hopes to be. For example, multiple snapshots previewing
     * features for "1.14" may return "1.14", as well as the actual "1.14" version itself.</p>
     *
     * <p>This value is suitable for grouping versions based on the same release type, and helping to disambiguate
     * snapshots. The format contained within this string is still not guaranteed, and should not be used for anything
     * other than checking for equality with another version's release target.</p>
     *
     * @return A name of a release that this version is targeted towards.
     */
    String getReleaseTarget();

    /**
     * The format of world data that is used by this version.
     *
     * <p>This number is not unique, but is guaranteed to be comparable. Two versions with the same number are
     * compatible, a version with a lower number will be upgraded and a version with a higher number may suffer
     * data-loss.</p>
     *
     * <p>This number is also not contiguous, and will often skip whole ranges.</p>
     *
     * @return A numeric format number of worlds used by this version.
     */
    int getWorldVersion();

    /**
     * The format of the network protocol that is used by this version.
     *
     * <p>This number is not unique, but is guaranteed to be comparable. Two versions with the same number are
     * compatible, a version with a lower number will be upgraded and a version with a higher number may suffer
     * data-loss.</p>
     *
     * <p>This number is also not contiguous, and will often skip whole ranges.</p>
     *
     * @return A numeric format number of the network protocol used by this version.
     */
    int getProtocolVersion();

    /**
     * @deprecated Use {@link #getPackVersion(PackType)} for version that distinguishes between pack types
     * @return
     */
    @Deprecated
    default int getPackVersion() {
        return getPackVersion(PackType.RESOURCE);
    }

    /**
     * The format of the resource or data packs used by this version.
     *
     * <p>This number is not unique, but is guaranteed to be comparable. Two versions with the same number are
     * compatible, a version with a lower number will be outdated and a version with a higher number may not be
     * understood at all.</p>
     *
     * <p>This number is also not contiguous, and may skip whole ranges.</p>
     *
     * @return A numeric format number of the pack format used by this version.
     */
    default int getPackVersion(PackType packType) {
        return getPackVersion();
    }

    /**
     * The date &amp; time that this version was built.
     *
     * <p>This is useful for having a rough idea of when a version was released. The actual release time of a version
     * isn't well defined, as "release" has various different stages and meanings, so the build time is close enough.</p>
     *
     * <p>Note that you can't accurately sort versions by this field, as a patch for 1.13 may be released after 1.14
     * (for example), which should ideally be sorted as "1.13, 1.13.1, 1.14" but this field will sort it as
     * "1.13", "1.14", "1.13.1".</p>
     *
     * @return The time that this version was built.
     */
    Date getBuildTime();

    /**
     * Whether or not this version is considered stable.
     *
     * <p>Typically, full releases will be marked stable and snapshots/pre-releases/internal previews will not.</p>
     *
     * @return True if this version is stable.
     */
    boolean isStable();
}
