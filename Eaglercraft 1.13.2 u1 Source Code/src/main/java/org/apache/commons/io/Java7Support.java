package org.apache.commons.io;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.io.IOException;

/**
 * Java7 feature detection and reflection based feature access.
 * <p/>
 * Taken from maven-shared-utils, only for private usage until we go full java7
 */
class Java7Support {

    private static final boolean IS_JAVA7;

    static {
        // The browser runtime does not expose java.nio.file, so the Java 7
        // NIO-2 reflective probe is skipped and Java7Support reports that
        // the symlink helpers are unavailable.
        IS_JAVA7 = false;
    }

    /**
     * Invokes java7 isSymbolicLink
     * @param file The file to check
     * @return true if the file is a symbolic link
     */
    public static boolean isSymLink(File file) {
        return false;
    }

    /**
     * Reads the target of a symbolic link
     * @param symlink The symlink to read
     * @return The location the symlink is pointing to
     * @throws IOException Upon failure
     */

    public static File readSymbolicLink(File symlink)
            throws IOException {
        return symlink;
    }


    /**
     * Indicates if a symlunk target exists
     * @param file The symlink file
     * @return true if the target exists
     * @throws IOException upon error
     */
    private static boolean exists(File file)
            throws IOException {
        return file.exists();
    }

    /**
     * Creates a symbolic link
     * @param symlink The symlink to create
     * @param target Where it should point
     * @return The newly created symlink
     * @throws IOException upon error
     */
    public static File createSymbolicLink(File symlink, File target)
            throws IOException {
        return symlink;

    }

    /**
     * Performs a nio delete
     *
     * @param file the file to delete
     * @throws IOException Upon error
     */
    public static void delete(File file)
            throws IOException {
        if (!file.delete() && file.exists()) {
            throw new IOException("Could not delete: " + file);
        }
    }

    /**
     * Indicates if the current vm has java7 lubrary support
     * @return true if java7 library support
     */
    public static boolean isAtLeastJava7() {
        return IS_JAVA7;
    }

}
