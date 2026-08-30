/*
 * Copyright 2014 The Netty Project
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

package io.netty.channel;

import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;

/**
 * A {@link ChannelFactory} that instantiates a new {@link Channel} by invoking its default constructor reflectively.
 */
public class ReflectiveChannelFactory<T extends Channel> implements ChannelFactory<T> {

    private final Class<? extends T> clazz;

    public ReflectiveChannelFactory(Class<? extends T> clazz) {
        if (clazz == null) {
            throw new NullPointerException("clazz");
        }
        this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newChannel() {
        if (clazz == NioSocketChannel.class) {
            return (T) new NioSocketChannel();
        }
        if (clazz == NioServerSocketChannel.class) {
            return (T) new NioServerSocketChannel();
        }
        if (clazz == NioDatagramChannel.class) {
            return (T) new NioDatagramChannel();
        }
        throw new ChannelException("No TeaVM-safe Channel factory registered for " + clazz);
    }

    @Override
    public String toString() {
        return StringUtil.simpleClassName(clazz) + ".class";
    }
}
