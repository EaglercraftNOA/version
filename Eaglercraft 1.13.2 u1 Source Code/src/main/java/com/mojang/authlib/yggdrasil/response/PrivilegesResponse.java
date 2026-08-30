package com.mojang.authlib.yggdrasil.response;

import java.util.Optional;

public class PrivilegesResponse extends Response {
/*
{
    "privileges": {
        "onlineChat": {
            "enabled": true
        },
        "multiplayerServer": {
            "enabled": true
        },
        "multiplayerRealms": {
            "enabled": true
        },
        "telemetry": {
            "enabled": true
        }
    }
}
*/
    private Privileges privileges = new Privileges();

    public Privileges getPrivileges() {
        return privileges;
    }

    public class Privileges {
        private Privilege onlineChat = new Privilege();
        private Privilege multiplayerServer = new Privilege();
        private Privilege multiplayerRealms = new Privilege();
        private Privilege telemetry = new Privilege();

        public Optional<Privilege> getOnlineChat() {
            return Optional.ofNullable(onlineChat);
        }

        public Optional<Privilege>  getMultiplayerServer() {
            return Optional.ofNullable(multiplayerServer);
        }

        public Optional<Privilege>  getMultiplayerRealms() {
            return Optional.ofNullable(multiplayerRealms);
        }

        public Optional<Privilege> getTelemetry() {
            return Optional.ofNullable(telemetry);
        }

        public class Privilege {
            private boolean enabled;

            public boolean isEnabled() {
                return enabled;
            }
        }
    }
}
