/*
 * Copyright 2012 The Netty Project
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
package io.netty.channel.socket.nio;

import io.netty.channel.ChannelException;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.util.internal.SocketUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketOption;
import java.net.SocketException;
import java.net.StandardSocketOptions;
import java.nio.channels.DatagramChannel;
import java.util.Enumeration;

/**
 * The default {@link NioDatagramChannelConfig} implementation.
 */
class NioDatagramChannelConfig extends DefaultDatagramChannelConfig {

    private static final SocketOption<Integer> IP_MULTICAST_TTL = StandardSocketOptions.IP_MULTICAST_TTL;
    private static final SocketOption<NetworkInterface> IP_MULTICAST_IF = StandardSocketOptions.IP_MULTICAST_IF;
    private static final SocketOption<Boolean> IP_MULTICAST_LOOP = StandardSocketOptions.IP_MULTICAST_LOOP;

    private final DatagramChannel javaChannel;

    NioDatagramChannelConfig(NioDatagramChannel channel, DatagramChannel javaChannel) {
        super(channel, javaChannel.socket());
        this.javaChannel = javaChannel;
    }

    @Override
    public int getTimeToLive() {
        return (Integer) getOption0(IP_MULTICAST_TTL);
    }

    @Override
    public DatagramChannelConfig setTimeToLive(int ttl) {
        setOption0(IP_MULTICAST_TTL, ttl);
        return this;
    }

    @Override
    public InetAddress getInterface() {
        NetworkInterface inf = getNetworkInterface();
        if (inf != null) {
            Enumeration<InetAddress> addresses = SocketUtils.addressesFromNetworkInterface(inf);
            if (addresses.hasMoreElements()) {
                return addresses.nextElement();
            }
        }
        return null;
    }

    @Override
    public DatagramChannelConfig setInterface(InetAddress interfaceAddress) {
        try {
            setNetworkInterface(NetworkInterface.getByInetAddress(interfaceAddress));
        } catch (SocketException e) {
            throw new ChannelException(e);
        }
        return this;
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return (NetworkInterface) getOption0(IP_MULTICAST_IF);
    }

    @Override
    public DatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
        setOption0(IP_MULTICAST_IF, networkInterface);
        return this;
    }

    @Override
    public boolean isLoopbackModeDisabled() {
        return (Boolean) getOption0(IP_MULTICAST_LOOP);
    }

    @Override
    public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
        setOption0(IP_MULTICAST_LOOP, loopbackModeDisabled);
        return this;
    }

    @Override
    public DatagramChannelConfig setAutoRead(boolean autoRead) {
        super.setAutoRead(autoRead);
        return this;
    }

    @Override
    protected void autoReadCleared() {
        ((NioDatagramChannel) channel).clearReadPending0();
    }

    private <T> T getOption0(SocketOption<T> option) {
        try {
            return javaChannel.getOption(option);
        } catch (Exception e) {
            throw new ChannelException(e);
        }
    }

    private <T> void setOption0(SocketOption<T> option, T value) {
        try {
            javaChannel.setOption(option, value);
        } catch (Exception e) {
            throw new ChannelException(e);
        }
    }
}
