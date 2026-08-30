/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TreeMap;

/**
 * Retrieves the version information of available Netty artifacts.
 * <p>
 * This class retrieves the version information from {@code META-INF/io.netty.versions.properties}, which is
 * generated in build time.  Note that it may not be possible to retrieve the information completely, depending on
 * your environment.
 * </p>
 */
public final class Version {

    private static final String PROP_VERSION = ".version";
    private static final String PROP_BUILD_DATE = ".buildDate";
    private static final String PROP_COMMIT_DATE = ".commitDate";
    private static final String PROP_SHORT_COMMIT_HASH = ".shortCommitHash";
    private static final String PROP_LONG_COMMIT_HASH = ".longCommitHash";
    private static final String PROP_REPO_STATUS = ".repoStatus";

    /**
     * Retrieves the version information of Netty artifacts.
     *
     * @return A {@link Map} whose keys are Maven artifact IDs and whose values are {@link Version}s
     */
    public static Map<String, Version> identify() {
        return identify(null);
    }

    /**
     * Retrieves the version information of Netty artifacts using the specified loader token.
     *
     * @return A {@link Map} whose keys are Maven artifact IDs and whose values are {@link Version}s
     */
    public static Map<String, Version> identify(Object classLoader) {
        return new TreeMap<String, Version>();
    }

    private static long parseIso8601(String value) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(value).getTime();
        } catch (ParseException ignored) {
            return 0;
        }
    }

    /**
     * Prints the version information to {@link System#err}.
     */
    public static void main(String[] args) {
        for (Version v: identify().values()) {
            System.err.println(v);
        }
    }

    private final String artifactId;
    private final String artifactVersion;
    private final long buildTimeMillis;
    private final long commitTimeMillis;
    private final String shortCommitHash;
    private final String longCommitHash;
    private final String repositoryStatus;

    private Version(
            String artifactId, String artifactVersion,
            long buildTimeMillis, long commitTimeMillis,
            String shortCommitHash, String longCommitHash, String repositoryStatus) {
        this.artifactId = artifactId;
        this.artifactVersion = artifactVersion;
        this.buildTimeMillis = buildTimeMillis;
        this.commitTimeMillis = commitTimeMillis;
        this.shortCommitHash = shortCommitHash;
        this.longCommitHash = longCommitHash;
        this.repositoryStatus = repositoryStatus;
    }

    public String artifactId() {
        return artifactId;
    }

    public String artifactVersion() {
        return artifactVersion;
    }

    public long buildTimeMillis() {
        return buildTimeMillis;
    }

    public long commitTimeMillis() {
        return commitTimeMillis;
    }

    public String shortCommitHash() {
        return shortCommitHash;
    }

    public String longCommitHash() {
        return longCommitHash;
    }

    public String repositoryStatus() {
        return repositoryStatus;
    }

    @Override
    public String toString() {
        return artifactId + '-' + artifactVersion + '.' + shortCommitHash +
               ("clean".equals(repositoryStatus)? "" : " (repository: " + repositoryStatus + ')');
    }
}
