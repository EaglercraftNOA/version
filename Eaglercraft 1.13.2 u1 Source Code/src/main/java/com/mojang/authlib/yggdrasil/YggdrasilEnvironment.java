package com.mojang.authlib.yggdrasil;

import com.mojang.authlib.Environment;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public enum YggdrasilEnvironment implements Environment {

    PROD(
        "https://authserver.mojang.com",
        "https://api.mojang.com",
        "https://sessionserver.mojang.com",
        "https://api.minecraftservices.com"
    ),
    STAGING(
        "https://yggdrasil-auth-staging.mojang.com",
        "https://api-staging.mojang.com",
        "https://yggdrasil-auth-session-staging.mojang.zone",
        "https://api-staging.minecraftservices.com"
    );

    private final String authHost;
    private final String accountsHost;
    private final String sessionHost;
    private final String servicesHost;

    YggdrasilEnvironment(final String authHost, final String accountsHost, final String sessionHost, final String servicesHost) {
        this.authHost = authHost;
        this.accountsHost = accountsHost;
        this.sessionHost = sessionHost;
        this.servicesHost = servicesHost;
    }

    public String getAuthHost() {
        return authHost;
    }

    public String getAccountsHost() {
        return accountsHost;
    }

    public String getSessionHost() {
        return sessionHost;
    }

    public String getServicesHost() {
        return servicesHost;
    }

    public String getName() {
        return this.name();
    }

    @Override
    public String asString() {
        return "authHost='" + authHost + "', accountsHost='" + accountsHost + "', sessionHost='" + sessionHost + "', servicesHost='" + servicesHost + "', name='" + getName() + "'";
    }

    public static Optional<YggdrasilEnvironment> fromString(@Nullable String value) {
        return Stream
                   .of(YggdrasilEnvironment.values())
                   .filter(env -> value != null && value.equalsIgnoreCase(env.name()))
                   .findFirst();
    }
}
