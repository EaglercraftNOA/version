package com.mojang.authlib.yggdrasil.request;

public class ValidateRequest {
    private String clientToken;
    private String accessToken;

    public ValidateRequest(final String accessToken, final String clientToken) {
        this.clientToken = clientToken;
        this.accessToken = accessToken;
    }
}
