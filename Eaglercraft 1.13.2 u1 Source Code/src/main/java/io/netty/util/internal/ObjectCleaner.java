/*
 * Copyright 2017 The Netty Project
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
package io.netty.util.internal;

/**
 * Allows a way to register some {@link Runnable} that will executed once there are no references to an {@link Object}
 * anymore.
 */
public final class ObjectCleaner {

    /**
     * Register the given {@link Object} for which the {@link Runnable} will be executed once there are no references
     * to the object anymore.
     *
     * This should only be used if there are no other ways to execute some cleanup once the Object is not reachable
     * anymore because it is not a cheap way to handle the cleanup.
     */
    public static void register(Object object, Runnable cleanupTask) {
        ObjectUtil.checkNotNull(object, "object");
        ObjectUtil.checkNotNull(cleanupTask, "cleanupTask");
        // TeaVM/WASM GC cannot block on ReferenceQueue or start cleaner threads.
        // Browser memory is reclaimed by the runtime; explicit release paths still run normally.
    }

    public static int getLiveSetCount() {
        return 0;
    }

    private ObjectCleaner() {
        // Only contains a static method.
    }
}
