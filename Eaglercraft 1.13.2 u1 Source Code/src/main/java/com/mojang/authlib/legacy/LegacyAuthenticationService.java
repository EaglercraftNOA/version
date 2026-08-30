package com.mojang.authlib.legacy;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import org.apache.commons.lang3.Validate;

import java.net.Proxy;

@Deprecated
public class LegacyAuthenticationService extends HttpAuthenticationService {
    /**
     * Constructs a new AuthenticationService using the legacy service.
     * <p />
     * The legacy authentication service only supports the Minecraft {@link Agent}.
     *
     * @param proxy Proxy to route all HTTP(s) requests through.
     * @throws java.lang.IllegalArgumentException Proxy is null
     */
    protected LegacyAuthenticationService(final Proxy proxy) {
        super(proxy);
    }

    /**
     * Creates a relevant {@link com.mojang.authlib.UserAuthentication} using the legacy servers.
     * <p />
     * The legacy authentication service only supports the Minecraft {@link Agent}.
     *
     * @param agent Game agent to authenticate for
     * @throws java.lang.IllegalArgumentException Agent is null or not allowed for this AuthenticationService
     * @return New user authenticator
     */
    @Override
    public LegacyUserAuthentication createUserAuthentication(final Agent agent) {
        Validate.notNull(agent);
        if (agent != Agent.MINECRAFT) {
            throw new IllegalArgumentException("Legacy authentication cannot handle anything but Minecraft");
        }
        return new LegacyUserAuthentication(this);
    }

    @Override
    public LegacyMinecraftSessionService createMinecraftSessionService() {
        return new LegacyMinecraftSessionService(this);
    }

    @Override
    public GameProfileRepository createProfileRepository() {
        throw new UnsupportedOperationException("Legacy authentication service has no profile repository");
    }
}
