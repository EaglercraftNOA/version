package com.mojang.authlib;

import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static java.util.Arrays.asList;

public class EnvironmentParser {
    @Nullable
    private static String environmentOverride;

    public static void setEnvironmentOverride(final String override) {
        environmentOverride = override;
    }

    private static final String PROP_PREFIX = "minecraft.api.";
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String PROP_ENV = PROP_PREFIX + "env";
    public static final String PROP_AUTH_HOST = PROP_PREFIX + "auth.host";
    public static final String PROP_ACCOUNT_HOST = PROP_PREFIX + "account.host";
    public static final String PROP_SESSION_HOST = PROP_PREFIX + "session.host";
    public static final String PROP_SERVICES_HOST = PROP_PREFIX + "services.host";

    public static Optional<Environment> getEnvironmentFromProperties() {
        final String envName = environmentOverride != null ? environmentOverride : System.getProperty(PROP_ENV);
        final Optional<Environment> env = YggdrasilEnvironment
                                              .fromString(envName)
                                              .map(Environment.class::cast);

        return env.isPresent() ? env : fromHostNames();

    }

    private static Optional<Environment> fromHostNames() {

        final String auth = System.getProperty(PROP_AUTH_HOST);
        final String account = System.getProperty(PROP_ACCOUNT_HOST);
        final String session = System.getProperty(PROP_SESSION_HOST);
        final String services = System.getProperty(PROP_SERVICES_HOST);

        if (auth != null && account != null && session != null) {
            return Optional.of(Environment.create(auth, account, session, services, "properties"));
        } else if (auth != null || account != null || session != null) {
            LOGGER.info("Ignoring hosts properties. All need to be set: " +
                            asList(PROP_AUTH_HOST, PROP_ACCOUNT_HOST, PROP_SESSION_HOST));
        }
        return Optional.empty();

    }

}
